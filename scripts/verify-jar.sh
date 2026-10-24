#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

VERSION="${1:-26.2}"
JAR="${2:-build/libs/error422-1.0.0+${VERSION}.jar}"

if [ ! -f "$JAR" ]; then
    echo "error: jar not found: $JAR" >&2
    exit 1
fi

echo "verifying $JAR"

require_entry() {
    if ! unzip -Z1 "$JAR" | grep -qx "$1"; then
        echo "error: missing jar entry: $1" >&2
        exit 1
    fi
}

require_entry "fabric.mod.json"
require_entry "error422.mixins.json"
require_entry "assets/error422/icon.png"
require_entry "assets/error422/lang/en_us.json"
require_entry "dev/mmunki/error422/Error422Mod.class"
require_entry "dev/mmunki/error422/client/Error422Client.class"
require_entry "dev/mmunki/error422/client/Error422ModMenu.class"
require_entry "dev/mmunki/error422/mixin/HudMixin.class"
require_entry "dev/mmunki/error422/mixin/TitleScreenMixin.class"

python3 - "$JAR" "$VERSION" <<'PY'
import json
import sys
import zipfile

jar_path, version = sys.argv[1], sys.argv[2]
with zipfile.ZipFile(jar_path) as jar:
    metadata = json.loads(jar.read("fabric.mod.json"))
    mixins = json.loads(jar.read("error422.mixins.json"))
    icon = jar.read("assets/error422/icon.png")

assert metadata["id"] == "error422", metadata["id"]
assert metadata["version"] == "1.0.0", metadata["version"]
assert metadata["environment"] == "*", metadata["environment"]
assert metadata["depends"]["java"] == ">=25"
assert metadata["depends"]["fabricloader"] == ">=0.20.1"
assert metadata["depends"]["minecraft"] == f">={version} <{version.rsplit('.', 1)[0]}.{int(version.rsplit('.', 1)[1]) + 1}"
assert "dev.mmunki.error422.Error422Mod" in metadata["entrypoints"]["main"]
assert "dev.mmunki.error422.client.Error422Client" in metadata["entrypoints"]["client"]
assert "dev.mmunki.error422.client.Error422ModMenu" in metadata["entrypoints"]["modmenu"]
assert mixins["required"] is True
assert mixins["package"] == "dev.mmunki.error422.mixin"
assert "HudMixin" in mixins["client"]
assert "TitleScreenMixin" in mixins["client"]
assert icon[:8] == b"\x89PNG\r\n\x1a\n"
assert int.from_bytes(icon[16:20], "big") == 128
assert int.from_bytes(icon[20:24], "big") == 128
print("fabric.mod.json, mixins and icon: ok")
PY

for entrypoint in \
    dev.mmunki.error422.Error422Mod \
    dev.mmunki.error422.client.Error422Client \
    dev.mmunki.error422.client.Error422ModMenu; do
    if ! javap -classpath "$JAR" "$entrypoint" | grep -q "public ${entrypoint}();"; then
        echo "error: $entrypoint has no public no-arg constructor" >&2
        exit 1
    fi
done
echo "entrypoints: ok"

MC_JAR="${HOME}/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-deobf/${VERSION}/minecraft-merged-deobf-${VERSION}.jar"
if [ -f "$MC_JAR" ]; then
    if ! javap -p -classpath "$MC_JAR" net.minecraft.client.gui.Hud | grep -q "private void extractPlayerHealth(net.minecraft.client.gui.GuiGraphicsExtractor);"; then
        echo "error: Hud.extractPlayerHealth target not found" >&2
        exit 1
    fi
    if ! javap -p -classpath "$MC_JAR" net.minecraft.client.gui.Hud | grep -q "private void extractFood(net.minecraft.client.gui.GuiGraphicsExtractor, net.minecraft.world.entity.player.Player, int, int);"; then
        echo "error: Hud.extractFood target not found" >&2
        exit 1
    fi
    if ! javap -classpath "$MC_JAR" net.minecraft.client.gui.screens.TitleScreen | grep -q "public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor, int, int, float);"; then
        echo "error: TitleScreen.extractRenderState target not found" >&2
        exit 1
    fi
    echo "mixin targets: ok"
else
    echo "warning: Minecraft deobf jar not found, mixin target check skipped" >&2
fi

sha256sum "$JAR"
echo "verification passed"
