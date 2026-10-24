# ERROR422

A clean-room Minecraft **26.2** Fabric horror mod inspired by the ERROR422
creepypasta. It is not a port of the original All Rights Reserved NeoForge
mod; it is an original implementation of the same idea for modern Fabric.

The mod runs on both the client and the server. It works in singleplayer
(integrated server) and on a dedicated Fabric server. The client receives a
deterministic session seed from the server, so the screen corruption, chat
errors, titles and sounds follow the same schedule on both sides.

## Features

- Five-phase session: `CALM`, `UNEASY`, `CORRUPT`, `FINAL`, `ENDED`.
- Deterministic event schedule from a per-session seed.
- Red scanlines, glitch bars, hex error lines and an `ERR422` finale overlay.
- Chat errors, action-bar glitches, cave/glass/lightning sounds.
- Visual-only lightning by default; damage is opt-in.
- Nausea, darkness, pushes and title-screen corruption.
- Optional block swaps and inventory scramble (both disabled by default).
- Health and hunger HUD corruption in the late phases.
- `F8` toggles the client visual overlay without stopping the server session.
- `/error422` commands with status, start, demo, stop, reset, reload and help.
- Optional Mod Menu settings screen.

## Requirements

- Minecraft `26.2`
- Fabric Loader `0.20.1` or newer
- Fabric API `0.161.0+26.2` or newer
- Java `25`

## Build

```bash
./build.sh -Pminecraft_version=26.2 -Pfabric_api_version=0.161.0+26.2
```

The jar is written to `build/libs/error422-1.0.0+26.2.jar`.

Run the unit tests and the jar verification directly:

```bash
./build.sh -Pminecraft_version=26.2 -Pfabric_api_version=0.161.0+26.2
bash scripts/verify-jar.sh 26.2
```

## Configuration

The server reads `config/error422.json` on startup and on `/error422 reload`.
The client reads the same file for its visual settings.

```json
{
  "enabled": true,
  "sessionMinutes": 45,
  "intensity": 2,
  "clientAutoStart": true,
  "corruptHud": true,
  "corruptTitleScreen": true,
  "playStatic": true,
  "allowWorldChanges": false,
  "allowInventoryScramble": false,
  "allowDamage": false,
  "disableInCreative": true,
  "seed": 0
}
```

`seed: 0` means a random seed per player session. A non-zero seed is mixed
with the player UUID so two players do not see exactly the same events.

## Commands

| Command | Permission | Description |
| --- | --- | --- |
| `/error422` | everyone | Show your session status |
| `/error422 status` | everyone | Show your session status |
| `/error422 help` | everyone | List commands |
| `/error422 start [minutes]` | gamemaster | Start a session for yourself, or all players from console |
| `/error422 demo` | gamemaster | Start a 3 minute session |
| `/error422 stop` | gamemaster | Stop your session, or all sessions from console |
| `/error422 reset` | gamemaster | Stop all sessions and reload the config |
| `/error422 reload` | gamemaster | Reload `config/error422.json` |

## Safety

The default configuration is intentionally non-destructive:

- lightning is visual-only;
- no blocks are changed;
- the inventory is not scrambled;
- creative and spectator players are skipped;
- the mod never crashes the game or the server on purpose.

The horror comes from the client overlay, chat, sounds, titles, effects and
visual lightning. Every destructive option has to be enabled explicitly.

## Test matrix

| Check | Result |
| --- | --- |
| Gradle build on Linux, Temurin JDK 25 | PASS |
| JUnit session / phase / anomaly / config tests | PASS (14/14) |
| `scripts/verify-jar.sh 26.2` | PASS |
| Dedicated server with Fabric Loader 0.20.1 + Fabric API 0.161.0+26.2 | PASS |
| Client title screen and mixin application | PASS |
| In-world session, overlay, chat and finale | PASS |

The automated checks ran on Linux with Temurin JDK 25 against Minecraft 26.2.
The client was exercised under Xvfb; the missing system audio device reported by
OpenAL and the missing narrator native library are environment limitations, not
mod failures.

## License

MIT. See `LICENSE`. The original ERROR422 mod is All Rights Reserved and no
code or assets from it are included here.
