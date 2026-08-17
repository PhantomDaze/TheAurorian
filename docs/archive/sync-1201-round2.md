# 同步 1.20.1 最近修复到 1.21.1（第二轮）

上一轮同步审计见 `docs/archive/sync-1201-recent10.md`（覆盖到 `68b8b23f`）。本次覆盖 1.20.1 在 `68b8b23f` 之后的提交（`d4417ba1..3bac0e07`，共 23 条）。

## 1.20.1 新提交核对

### 已在 1.21.1 同步（无需处理）
| commit | 主题 | 1.21.1 对应 |
|---|---|---|
| 8398f679 | Prevent surface structures in lakes/water | ✅ e88993a3 |
| 0ae73301 | Ruined houses single-template | ✅ `structure/ruined_house.nbt` + `worldgen/structure/ruined_house.json` |
| 92494473 | Darkstone boss room one floor + loot | ✅ cd938d7c |
| 55cd2edd | Moon temple chests + key fragments | ✅ e57d0587 |
| f6adf569 | Runestone tower loot chests | ✅ 1.21.1 移植时已带 loot |
| 80b443c8 | Re-save graveyard structure | ⚠️ 同一 nbt，视觉确认即可，无需改代码 |
| 2bb2cb04 | Tinkers material colors to assets | ✅ 1ed51fcf（assets/tinkering/materials，颜色一致） |
| 742d0fa2 | Weeping willow planks/stairs recipes | ✅ `recipe/shaped/weeping_willow_stairs.json` + `recipe/shapeless/weeping_willow_planks.json`（1.21.1 用单数 `recipe/`） |
| b048b79b | Tags via datagen | ✅ ac713046（1.21.1 用 `c:` 命名空间，已重建） |
| 74ee6516 | bump version 1.0.6 | ⚠️ 1.21.1 版本号独立（1.21.1-1.0.5），不动 |
| 0569d19c | Track crystalline_sprite item model | ✅ `src/generated/.../crystalline_sprite.json` 已存在且内容一致 |
| ef4be938 | Weeping willow + tallgrass loot | ⚠️ 1.21.1 设计不同（见下） |

### 本次已同步修复
| commit | 主题 | 1.21.1 处理 |
|---|---|---|
| d4417ba1 | Mirror of Guidance item lighting | ✅ RenderUtil 加 `Lighting.setupForFlatItems/3DItems` |
| 3bac0e07 | Rename 暗石武库 → 暗石地牢 (zh_cn) | ✅ zh_cn.json 4 处重命名（dungeon_darkstone.desc/name、boss_loot.desc、umbra.desc） |
| f154a8a3 | Move ores node off moon temple | ✅ ores.json (40,40) → (160,-40)，避开 moon_temple 重叠 |
| abcd7159 | Close unclosed $(br2) tags | ✅ 6moonlightforge.json + 2therunestonedungeon.json 各补 `)` |
| 85ff40b9 | Ranged attack origins (sprite/keeper) | ✅ CrystallineBeamEntity 起点改包围盒中心；CrystallineSpriteEntity 去掉 d3*0.2 抛物补偿；DungeonKeeperEntity 新增 `performRangedAttack` override（用 `getArrow`，箭起点设头部） |
| 706ebc67 | Locator particle via network + search radius | ✅ 新建 `Network/` 包：`LocatorDungeonDirectionPayload` + `DungeonLocatorClient` + `LocatorClientPayloadHandler` + `NetworkHandler`（NeoForge `PayloadRegistrar`）；DungeonLocatorItem 删死代码 `spawnDirectionParticles`，改 `PacketDistributor.sendToPlayer`；searchRadius 去掉 `*16` |
| e4ab1050 | Weeping willow leaves persistent | ✅ WeepingWillowLeavesBlock 构造器 `registerDefaultState(PERSISTENT=true)` |
| 00658fe5 | Dungeon slime collision box | ⚠️ **不同步**：1.20.1 的 bug 源自 Forge 对 `Slime#getDimensions` 的 patch（乘 `0.255*getSize()`），1.21.1 NeoForge 无此 patch，`Entity.getDimensions` 直接返回 `sized(0.52,0.52)`，无 4x 偏小 bug。保留现状 |

### 设计差异（保留 1.21.1 设计）
- **ef4be938 tallgrass loot**：1.20.1 修复后 shears/silk 掉 `theaurorian:aurorian_tallgrass`（草方块本身）；1.21.1 当前 shears/sickle 掉 `theaurorian:plant_fiber`。1.21.1 的设计在 5edecd2f 中明确「tallgrass: shears/sickle always drop plant_fiber」，是**有意为之**（sickle 收割草纤维而非草方块）。**不同步** 1.20.1 的 tallgrass 行为。
- **ef4be938 weeping_willow_leaves stick pool**：1.20.1 加 `silentwood_stick`；1.21.1 已有 `minecraft:stick` 的 stick pool。1.21.1 用 vanilla stick（weeping willow 无专属 stick 物品），保留现状。

## 验证
- `./gradlew compileJava` 通过。
- `./gradlew validateResources validateRunestoneLayout validateDungeonLayout` 全部通过（0 warning）。
- `./gradlew check`（含 JUnit 内容校验 + validate_resources + validateRunestoneLayout）全部 PASSED，BUILD SUCCESSFUL。

## 涉及文件
- `src/main/resources/assets/theaurorian/lang/zh_cn.json`
- `src/main/resources/data/theaurorian/mirror_of_guidance/ores.json`
- `src/main/resources/assets/theaurorian/patchouli_books/the_aurorian_guide/zh_cn/entries/aurorianbasics/6moonlightforge.json`
- `src/main/resources/assets/theaurorian/patchouli_books/the_aurorian_guide/zh_cn/entries/modprogression/2therunestonedungeon.json`
- `src/main/java/shiroroku/theaurorian/Util/RenderUtil.java`
- `src/main/java/shiroroku/theaurorian/Entities/CrystallineBeam/CrystallineBeamEntity.java`
- `src/main/java/shiroroku/theaurorian/Entities/CrystallineSprite/CrystallineSpriteEntity.java`
- `src/main/java/shiroroku/theaurorian/Entities/DungeonKeeper/DungeonKeeperEntity.java`
- `src/main/java/shiroroku/theaurorian/Blocks/WeepingWillowLeavesBlock.java`
- `src/main/java/shiroroku/theaurorian/Items/DungeonLocatorItem.java`
- `src/main/java/shiroroku/theaurorian/Network/`（新增包：DungeonLocatorClient、LocatorDungeonDirectionPayload、LocatorClientPayloadHandler、NetworkHandler）
