# Changelog

## 1.1.0

- Rebranded the mod as **Horror Minecraft** and replaced the title logo.
- Renamed the package and Maven group from `dev.mmunki` to `tw.mmunki`.
- Added a silent randomized 2 to 5 minute onset before the first symptom.
- Replaced the title-screen panorama with full-screen falling code rain.
- Removed the fixed top-left error/status text from the home screen.
- Added a custom ERROR422 entity that stalks, teleports, whispers and vanishes.
- Added whisper, entity and jumpscare anomalies with new sounds and overlays.
- Added terrifying random sounds to button clicks.
- Expanded jar verification for the new package, entity, texture and mixins.
- Added onset tests; the JUnit suite now has 16 passing tests.

## 1.0.0

- First release for Minecraft 26.2 on Fabric.
- Deterministic five-phase ERROR422 session.
- Client overlay, HUD corruption and title-screen corruption.
- Server chat errors, sounds, lightning, effects, titles and finale.
- Safe mode by default; block swaps, inventory scramble and lightning damage
  are opt-in.
- `/error422` commands and optional Mod Menu settings screen.
