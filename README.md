# The Aurorian

**1.19.2 port status:** content complete (Phases 0–10 + G per `docs/port-plan.md`).  
Optional later: TCon/ConArm/CT compat (exempt), custom Gecko boss meshes, freeplay polish.

A Minecraft mod that adds a new dimension with new bosses, tools, and other neat items to use and explore.

### Major changes from 1.12 to 1.19

- Custom ingame guide, the Mirror of Guidance
- Curios is a requirement, new wearables
- Dungeons are now randomly spread, no longer on a grid axis
- Boss player count & difficulty scaling
- All dungeons have new layouts and more variety
- Umbra sword reworked; Crystalline sword reworked
- Scrapper returns materials based on durability
- Mystical Barriers are now Fog Walls
- Underground is lush (mushroom caves and related features)
- Chimneys reduce fuel use of Aurorian Furnaces
- Aurorian Steel has a new recipe
- Weeping willow biome **restored** (forest + trees + drip/bell)
- Dungeon loot sticks to material lines (Runestone→Aurorianite, Darkstone→Umbra, Moon Temple→Crystalline)
- New rendering (auroras, moon movement)

### Verify / demo

```bash
# Java 17
./gradlew test                 # datapack integrity + unit tests
./gradlew runGameTestServer    # 22 headless functional GameTests
# In client with cheats:
/ta demo                       # 17 watchable cases in front of you
```

See `docs/port-plan.md`, `docs/diff.md`, `docs/testing.md`, `docs/asset-remap.md`.
