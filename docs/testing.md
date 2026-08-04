# Automated Tests

Two layers: **datapack integrity** (no game) and **in-world GameTests** (headless server).

## Quick commands

```bash
# Use Java 21 (NeoForge 1.21.1 / MDG)
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk   # or your JDK 21 path
export PATH="$JAVA_HOME/bin:$PATH"

# 1) Datapack / asset gate + JUnit unit tests (MDG unitTest mode)
./gradlew test

# 2) In-world functional GameTests (NeoForge GameTest server)
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
| K | JSON / pack | parse-all, neoforge.mods.toml (template or generated) |

- Script: `scripts/validate_resources.py` (`./gradlew validateResources`)
- JUnit: `src/test/java/.../content/PortContentCategoriesTest.java`

## Layer 2 — In-world GameTests (functional)

Run configuration: `gameTestServer` in `build.gradle`  
Entry: `src/main/java/shiroroku/theaurorian/GameTests/AurorianGameTests.java`  
Templates: `data/theaurorian/structure/gametest/*.nbt`

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

**22 required tests** — last run (1.21.1 NeoForge 21.1.248): all passed.

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

## Layer 4 — Structure layout client harness (pre-launch)

No in-game commands. Used to inspect dungeon/temple piece alignment after structure-port changes.

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk   # or your JDK 21
./scripts/run_structure_layout_test.sh
# or IDE run config: "Structure Layout Test"
# or: ./gradlew runClientStructureLayout
```

Pre-launch (`prepareStructureLayoutTest`):

1. Deletes `run/saves/ta_structure_layout` if present.

Client JVM property `theaurorian.structureLayoutTest=true` then:

1. On title screen, creates a **new** superflat world `ta_structure_layout`  
   layers: **bedrock×1 + dirt×100 + grass×1**, difficulty **NORMAL**, creative + cheats, fixed seed.
2. After join: force-loads chunks and places  
   `runestone_dungeon` @ (0,0) · `darkstone_dungeon` @ (256,0) · `moon_temple` @ (0,256).
3. Teleports to runestone; **keeps the game open** for manual inspection.

Code: `src/main/java/shiroroku/theaurorian/DevTest/StructureLayoutTest.java`  
Scripts: `scripts/prepare_structure_layout_test.sh`, `scripts/run_structure_layout_test.sh`

## Port plan status

Content port **Phase 0–10 + G closed** per `docs/port-plan.md`（§8.1–8.7 全勾；§9 仅 D26 TCon/CT/ConArm `exempt`）。

**1.21.1 NeoForge platform port:** see [`docs/port-plan-1.21.1.md`](port-plan-1.21.1.md). Toolchain NF0–NF1 closed; compile + `./gradlew test` + **22/22 GameTest** green (2026-08-03). Remaining §9 items are mostly in-world hand feel, Curios IMC cleanup, and JEI deprecation polish — not silent content cuts.

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
  with: { distribution: temurin, java-version: 21 }
- run: ./gradlew test runGameTestServer --no-daemon
```
