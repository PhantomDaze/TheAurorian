# The Aurorian

> **Port notice:** This repository is a community port maintained by **PhantomDaze**. I am the porter/maintainer, not the original author of The Aurorian. The original mod, code, assets, and designs belong to **Shiroroku (Elise)** and the original project contributors. Please see [`upstream/`](upstream/) and [`upstream/LICENSE.txt`](upstream/LICENSE.txt) for the upstream source and attribution.

**1.20.1 port status:** Forge 1.20.1 (47.4.10) build compiles; the resource gate, JUnit tests, and all 22 GameTests pass.
This port is based on the completed 1.19.2 content port (Phases 0–10 + G).
Optional later work: TCon/ConArm/CT compatibility, custom Gecko boss meshes, freeplay polish, and client smoke testing (`runClient` / `/ta demo`).

The Aurorian is a Minecraft mod that adds a new dimension with new bosses, tools, and other items to use and explore.

### Major changes from 1.12 (via 1.19.2) to 1.20.1

- Custom in-game guide, the Mirror of Guidance
- Curios is a requirement, with new wearables
- Dungeons are randomly spread instead of placed on a grid axis
- Boss player-count and difficulty scaling
- New dungeon layouts and greater variety
- Umbra sword and Crystalline sword reworks
- Scrapper returns materials based on durability
- Mystical Barriers became Fog Walls
- Lush underground areas with mushroom caves and related features
- Chimneys reduce fuel use of Aurorian Furnaces
- New Aurorian Steel recipe
- Weeping Willow biome restored, including trees, drips, and bell
- Dungeon loot follows material lines (Runestone → Aurorianite, Darkstone → Umbra, Moon Temple → Crystalline)
- New rendering features, including auroras and moon movement

### Verify / demo

```bash
# Java 17
./gradlew test                 # datapack integrity + unit tests
./gradlew runGameTestServer    # 22 headless functional GameTests
# In the client with cheats enabled:
/ta demo                       # 17 watchable cases in front of you
```

See [`README.zh-CN.md`](README.zh-CN.md) for the Chinese README.

See [`docs/port-plan.md`](docs/port-plan.md), [`docs/diff.md`](docs/diff.md), [`docs/testing.md`](docs/testing.md), and [`docs/asset-remap.md`](docs/asset-remap.md) for porting documentation.
