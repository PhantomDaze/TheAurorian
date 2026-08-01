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

## Layer 3 — Visible client demo (watchable)

Not headless. You stand in the world, look forward, and run:

```text
/ta demo              # full playlist (~17 cases), stage builds in front of you
/ta demo pause 100    # slower (~5s between cases); default 80t ≈ 4s
/ta demo next         # skip the current wait
/ta demo stop         # abort and clean the stage
/ta demo from 5       # start at case index 5
```

Requirements: op/permission 2, singleplayer or server with cheats.  
Each case: title card → build stage in your view cone → action → pass/fail subtitle → long pause while the camera stays locked on the focus.

Cases include machines, fog wall, mushroom bounce, crops, keyholes, Queen’s Chipper, 3 bosses, living parade, scrapper/MF, silentwood pick levels, locator cycle, slime boots, portal frame.

Code: `src/main/java/shiroroku/theaurorian/Demo/`

## Port plan status

Content port **Phase 0–10 + G closed** per `docs/port-plan.md`（§8.1–8.7 全勾；§9 仅 D26 TCon/CT/ConArm `exempt`）。

## Optional human playtest（不阻塞完成判定）

自动化已覆盖逻辑/资源/可重复演示；下列仅手感：

- 生存模式完整三地牢通关节奏  
- 多人 Boss 缩放手感（代码路径已有 per-player 缩放）  
- `/locate` 大地图散布观感  
- Spectral 半透明 / Aurora 像素级观感  

See `docs/port-plan.md` §8.8.

## CI sketch

```yaml
- uses: actions/setup-java@v4
  with: { distribution: temurin, java-version: 17 }
- run: ./gradlew test runGameTestServer --no-daemon
```
