# Automated Tests

Two layers: **datapack integrity** (no game) and **in-world GameTests** (headless server).

## Quick commands

```bash
# Use Java 17 (required; system Java 26 breaks Gradle 8.1)
export JAVA_HOME=/usr/lib/jvm/zulu-17   # or your JDK 17 path
export PATH="$JAVA_HOME/bin:$PATH"

# 1) Datapack / asset gate + JUnit unit tests
./gradlew test

# 2) In-world functional GameTests (Forge GameTest server)
./gradlew runGameTestServer

# Both
./gradlew test runGameTestServer
```

After adding blocks/items/entities:

```bash
./gradlew runData
./gradlew test runGameTestServer
```

## Layer 1 — Content integrity (no Minecraft)

| ID | Category | Checks |
|----|----------|--------|
| A | Language | en/zh/es key parity |
| B | Blocks & items | registrations, blockstates, models, lang, required IDs |
| C | Entities | lang, loot, spawn eggs for living |
| D | Structures | NBT minima, structure/set links, templates |
| E | Worldgen | biomes+music, features, dimension multi_noise |
| F | Recipes & chests | volume, MF/scrapper, item ID refs |
| G | Advancements | tree, boss OR-logic, lang |
| H | Mirror | nodes, graph, lang |
| I | Sounds & particles | ogg + registries |
| J | Tags & generated | tag/loot volume |
| K | JSON / pack | parse-all, mods.toml |

- Script: `scripts/validate_resources.py` (`./gradlew validateResources`)
- JUnit: `src/test/java/.../content/PortContentCategoriesTest.java`

## Layer 2 — In-world GameTests (functional)

Run configuration: `gameTestServer` in `build.gradle`  
Entry: `src/main/java/shiroroku/theaurorian/GameTests/AurorianGameTests.java`  
Templates: `data/theaurorian/structures/gametest/*.nbt`

| Batch | Coverage |
|-------|----------|
| **blocks** | Core machines place + BE; fog wall repel math; mushroom bounce |
| **agriculture** | Crops require aurorian farm tile + sky; silkberry same |
| **dungeon** | Keyhole opens gates with correct key; rejects wrong key; Queen’s Chipper breaks dungeon blocks only |
| **boss** | Boss spawner spawns Keeper / Moon Queen / Spider and consumes block; boss combat attributes |
| **entities** | All living types spawn; undead knight livable |
| **machines** | Scrapper prereqs (crystal+input) + recipe registry; Moonlight Forge moon/day gate + recipe registry |
| **items** | Silentwood pickaxe harvest levels 0→3; locator dungeon cycle; Keeper’s Bow type; slime boots cancel fall>3 + bounce |
| **portal** | Portal + frame place; dimension key present |
| **registry** | Critical items/blocks resolve in-world |

**22 required tests** — last run: all passed.

### Server-safe loading fixes (needed for GameTest)

GameTest is a dedicated server. The following were split so the mod loads without client classes:

- `EntityRegistry` — no renderer imports; client registration in `EntityClientRegistry` (`Dist.CLIENT`)
- `MirrorOGItem` — opens UI via `DistExecutor` + `MirrorOGClient`
- `SilentwoodChestBlockItem` — BEWLR via `SilentwoodChestClientExt` (client only)
- Client-only mods (AppleSkin / Neat / Effortless Building) → `compileOnly` so they are not on the GameTest classpath

## Out of scope / still manual

- Full portal dimension hop with real player connection
- Multiplayer boss HP scaling with 2+ real players (spawner path unit-tested with 0–1 nearby)
- Structure `/locate` generation over large worlds
- Client-only rendering (spectral translucency, aurora)

See also `docs/port-plan.md` §8.8.

## CI sketch

```yaml
- uses: actions/setup-java@v4
  with: { distribution: temurin, java-version: 17 }
- run: ./gradlew test runGameTestServer --no-daemon
```
