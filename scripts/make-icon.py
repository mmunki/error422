#!/usr/bin/env python3
import random
import struct
import zlib
from pathlib import Path

WIDTH = 128
HEIGHT = 128
FONT = {
    "4": ["10001", "10001", "10001", "11111", "00001", "00001", "00001"],
    "2": ["11111", "00001", "00001", "11111", "10000", "10000", "11111"],
}

pixels = [[(0, 0, 0, 255) for _ in range(WIDTH)] for _ in range(HEIGHT)]
rng = random.Random(422)

for y in range(HEIGHT):
    for x in range(WIDTH):
        value = rng.randrange(8, 28)
        pixels[y][x] = (value, 0, 0, 255)

for y in range(0, HEIGHT, 3):
    for x in range(WIDTH):
        r, g, b, a = pixels[y][x]
        pixels[y][x] = (min(255, r + 24), g, b, a)

for _ in range(24):
    y = rng.randrange(0, HEIGHT)
    height = rng.randrange(2, 7)
    red = rng.randrange(60, 220)
    for yy in range(y, min(HEIGHT, y + height)):
        for x in range(WIDTH):
            pixels[yy][x] = (red, 0, 0, 255)

scale = 12
text = "422"
text_width = len(text) * 5 * scale + (len(text) - 1) * scale
start_x = (WIDTH - text_width) // 2
start_y = (HEIGHT - 7 * scale) // 2

for index, char in enumerate(text):
    glyph = FONT[char]
    base_x = start_x + index * 6 * scale
    for row, line in enumerate(glyph):
        for col, bit in enumerate(line):
            if bit != "1":
                continue
            for dy in range(scale):
                for dx in range(scale):
                    x = base_x + col * scale + dx
                    y = start_y + row * scale + dy
                    if 0 <= x < WIDTH and 0 <= y < HEIGHT:
                        if rng.random() < 0.08:
                            pixels[y][x] = (255, 255, 255, 255)
                        else:
                            pixels[y][x] = (255, rng.randrange(0, 50), rng.randrange(0, 50), 255)

raw = bytearray()
for y in range(HEIGHT):
    raw.append(0)
    for x in range(WIDTH):
        raw.extend(pixels[y][x])


def chunk(kind, data):
    payload = kind + data
    return struct.pack(">I", len(data)) + payload + struct.pack(">I", zlib.crc32(payload) & 0xFFFFFFFF)


png = bytearray(b"\x89PNG\r\n\x1a\n")
png.extend(chunk(b"IHDR", struct.pack(">IIBBBBB", WIDTH, HEIGHT, 8, 6, 0, 0, 0)))
png.extend(chunk(b"IDAT", zlib.compress(bytes(raw), 9)))
png.extend(chunk(b"IEND", b""))

target = Path(__file__).resolve().parent.parent / "src/main/resources/assets/error422/icon.png"
target.parent.mkdir(parents=True, exist_ok=True)
target.write_bytes(png)
print(target)
