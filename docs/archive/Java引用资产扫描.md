# Java 引用资产扫描（1.21.1 / 1.20.1 / 1.19.2）

状态：**已完成（只读审计）**（2026-11-20）
性质：**只读**。除本文档外未修改任何仓库文件，未 commit / push / reset / clean，未运行 gradle，未启动客户端/服务端，未触碰 25565 端口。
动机：`scripts/validate_resources.py` 只校验 JSON ↔ JSON 的引用闭合（A–I 类），**不检查 Java 里写死的资源路径，也不检查贴图 PNG 是否存在**；本扫描专门补这一类盲区。

基线：

| 树 | 分支 | HEAD |
|---|---|---|
| `Aurorian`（MC 1.21.1 / NeoForge） | `1.21.1` | `f57b39a1` |
| `TheAurorian-1.20.1f`（MC 1.20.1 / Forge） | `1.20.1` | `3041ba73` |
| `TheAurorian-1.19.2`（MC 1.19.2 / Forge） | `1.19.2` | `5b2d9fc6` |

包名 `shiroroku.theaurorian`，modid `theaurorian`。所有路径均按**大小写敏感的精确匹配**判定。

> 范围约定：只扫描 `src/main/java`。`Aurorian/.claude/worktrees/` 下的副本为 agent 工作副本，**故意排除**。上一轮已确认无问题的「blockstate/model/texture JSON 互相引用闭合」「渲染层正确性」不重复审计。

---

## 1. 扫描方法

临时脚本（不提交）：

- `/tmp/asset_sweep/sweep.py`：第一遍，宽松提取。
- `/tmp/asset_sweep/refine.py`：第二遍，按引用形态分类、解析命名空间、判定文件存在。
- `/tmp/asset_sweep/texcheck.py`：逐条核对贴图引用。
- `/tmp/asset_sweep/final_sweep.py`：按扩展名/目录前缀全量兜底扫描。

覆盖的引用形态：

| 形态 | 提取规则 | 路径解析 |
|---|---|---|
| `new ResourceLocation(NS, "x")` | 命名空间参数为 `MODID`/`TheAurorian.MODID` | `assets/theaurorian/<x>` / `data/theaurorian/<x>` |
| `ResourceLocation.fromNamespaceAndPath(NS, "x")`（1.20.1 及以上） | 同上 | 同上 |
| `modLoc("x")` | 一律本模组命名空间 | `assets/theaurorian/<x>` |
| `new ResourceLocation("x")` / `ResourceLocation.parse("x")` | 无命名空间 → `minecraft` | 仅当 `x` 以 `textures/`、`entity/` 等目录开头时按文件检查 |
| 裸字符串字面量 | 以 `textures/ models/ blockstates/ sounds/ particles/ structures/ loot_table(s)/ recipes/ recipe/ advancements/ enchantment(s)/ shaders/ atlases/ fonts/ lang/` 开头或以 `.png .json .ogg .nbt .mcmeta .obj .glsl .vsh .fsh` 结尾 | 依次尝试 `assets/theaurorian/`、`data/theaurorian/` |
| 拼接式路径 | 全量 grep `ResourceLocation(... + ...)`、`MODID + ":textures/..."`、`"block/molten_" + name` 等 | 人工展开变量取值后核对（见 §2.3） |
| 语言键 | `item.|block.|entity.|message.|string.|gui.|container.|enchantment.|effect.|key.|tooltip.|subtitles.|death.|advancement.|biome.|dimension.|itemGroup.|fluid.|painting.|jukebox_song.|trim_material.` 前缀 | `assets/theaurorian/lang/en_us.json` |
| 派生资产（源码里没有字面量路径） | 材质名 → 盔甲层贴图；配方/结构/特征 ID → data JSON | 按版本约定推导，见 §2.2 |

判定标签：

- **确定使用**：调用链可追到已注册物品/方块/实体/数据对象，客户端或服务端一定读取该文件。
- **可能使用**：引用存在，但受配置/条件/数据驱动分支控制，或为 DataGen 输入。
- **死代码**：无调用者、被 `@Deprecated` 取代、或只存在于注释/历史常量。

---

## 2. 命中统计

### 2.1 字面量候选（`refine.py` 去重后）

| 树 | 候选总数 | 可直接映射到文件 | 非文件标识符 | 外部命名空间 | Java 中的 lang 键 |
|---|---:|---:|---:|---:|---:|
| 1.21.1 `Aurorian` | 142 | 54（贴图 32 + 图集 3 + DataGen 19） | 67 | 2 | 19 |
| 1.20.1 `TheAurorian-1.20.1f` | 146 | 54（贴图 32 + 图集 3 + DataGen 19） | 73 | 0 | 19 |
| 1.19.2 `TheAurorian-1.19.2` | 133 | 50（贴图 32 + 图集 3 + DataGen 15） | 62 | 2 | 19 |

「非文件标识符」包括：注册名、`ModelLayerLocation`、数据包 tag 键、`AttributeModifier` 的 `curio/...` ID、`ResourceKey` 的数据 ID、配方/音效/菜单/方块实体类型 ID、ItemModel 的 predicate 名（`blocking`、`pulling`、`charging` 等）。这些不落地为资产文件，不计入缺陷。

「外部命名空间」：`Aurorian` 与 `TheAurorian-1.19.2` 各有 2 条原版命名空间贴图（`textures/entity/sheep/sheep.png`、`sheep_fur.png`，见 §2.3）；1.20.1 的羊贴图为模组自有并在库内。

### 2.2 派生资产（人工/脚本按版本约定核对，源码中无字面量）

| 类别 | 1.21.1 | 1.20.1 | 1.19.2 | 结论 |
|---|---|---|---|---|
| 盔甲层贴图 `textures/models/armor/<material>_layer_<1|2>.png` | 12 个文件 | 12 + 1 个遗留 | 12 | 全部命中，见 §3.2 |
| 战利品表（`chests/ruins/common`、`chests/runestone/*`、`chests/moontemple/*`、`chests/darkstone/*`） | 10/10 | 10/10 | 10/10 | 全部命中 |
| 结构 JSON（`darkstone_dungeon`、`moon_temple`、`runestone_dungeon`） | 3/3 | 3/3 | 3/3 | 全部命中 |
| 结构模板 NBT（`weepingwillow/willow_l1,l2,s1,s2,s3`、`runestone/upstream_ref/*`） | 全命中 | 全命中 | 全命中 | 全部命中 |
| `configured_feature`（`silentwood_tree`、`weeping_willow_tree`、`mushroom_tree`） | **2/3** | **2/3** | 3/3 | **缺 `mushroom_tree`，见 §3.1** |
| 维度 `dimension/the_aurorian.json` | 命中 | 命中 | 命中 | 全部命中 |
| 粒子（`particles/aurorian_slime.json`、`weeping_willow_drip.json` 及其贴图） | 命中 | 命中 | 命中 | 全部命中 |
| 声音事件（`music`、`weepingwillowbell` 及 6 个 `.ogg`） | 命中 | 命中 | 命中 | 全部命中；validator I 类已覆盖，仅复核 |
| 图集 `atlases/blocks.json`（`block/moonlight_forge_overlay`） | 命中 | 命中 | 不适用（用 `TextureStitchEvent`） | 全部命中 |
| DataGen 物品贴图（`ITEMS_GEN*` 116 项） | 112 直连 + 4 茶水走 `item/tea` 父模型 | 同左 | 同左 | 全部命中 |
| DataGen 方块贴图（`BLOCKS_GEN*`） | 58/58 | 58/58 | 59/59 | 全部命中 |
| 流体贴图 `block/molten_<name>.png` 与 `_flow` | 6/6 | 6/6 | 6/6 | 全部命中 |

### 2.3 拼接式路径展开结果

| 位置 | 拼接逻辑 | 实际取值 | 结果 |
|---|---|---|---|
| `EventsClient.java:189,194`（1.21.1）/ `Registry/MoltenFluidType.java:43,48`（1.20.1/1.19.2） | `"block/molten_" + name (+ "_flow")` | `cerulean`、`moonstone`、`aurorian_steel` | 6 个贴图三树均存在 |
| `Items/Spectral/SpectralArmorLayer.java:48` | `"textures/models/armor/spectral_layer_" + (LEGS ? 2 : 1) + ".png"` | layer_1、layer_2 | 三树均存在 |
| `Items/Spectral/SpectralArmor.java` | `MODID + ":textures/models/armor/spectral_transparent.png"` | — | 三树均存在 |
| `Registry/MaterialTiers.java`（1.21.1 `:64`；1.20.1 `:82`；1.19.2 `:69`） | 盔甲材质名 → 默认层路径 | 见 §3.2 | 三树均存在 |
| `World/Structure/RunestoneDungeonStructure.java` `REF = "theaurorian:runestone/upstream_ref/"` | `+ quadrant + "v2"` | `bl/br/tl/tr` | 20/20 NBT 三树均存在 |
| `World/Feature/WeepingWillowTreeFeature.java:56` | `"weepingwillow/" + pool[...]` | `willow_l1/l2/s1/s2/s3` | 三树均存在 |
| `DataGen/DataGenItems.java` / `DataGenBlocks.java` | 注册名 → `item/`、`block/` 贴图 | 全部注册项 | 见 §2.2 |
| `World/Feature/AurorianTreeGrowers.java:41`（1.21.1）/ `MushroomTreeGrower.java:15`（1.20.1） | `"mushroom_tree"`（无变量，但为数据 ID） | `theaurorian:mushroom_tree` | **缺失，见 §3.1** |

---

## 3. 缺陷清单

### 3.1 一级：确认缺失且会导致可见功能异常

#### D1. `mushroom_tree` 配置特征（configured_feature）在 1.21.1 与 1.20.1 缺失

| 项 | 内容 |
|---|---|
| 树 | `Aurorian`（1.21.1）、`TheAurorian-1.20.1f`（1.20.1） |
| 代码位置（1.21.1） | `World/Feature/AurorianTreeGrowers.java:38-42` → `Optional.of(key("mushroom_tree"))`；被 `Registry/BlockRegistry.java:148` 的 `mushroom_small`（`SaplingBlock(AurorianTreeGrowers.MUSHROOM, ...)`）使用 |
| 代码位置（1.20.1） | `World/Feature/MushroomTreeGrower.java:14-15` → `ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(MODID, "mushroom_tree"))`；被 `Registry/BlockRegistry.java:152` 的 `mushroom_small` 使用 |
| 期望文件 | `src/main/resources/data/theaurorian/worldgen/configured_feature/mushroom_tree.json` |
| 现状 | 1.19.2 存在该文件（内容 `{"type": "theaurorian:mushroom_tree", "config": {}}`）；1.21.1 / 1.20.1 的 `worldgen/configured_feature/` 目录中**没有**它 |
| 对照证据 | 三树 `configured_feature/` 列表：仅有 1.19.2 含 `mushroom_tree.json`；三树 `MushroomTreeFeature.java`（77 行，完整实现）与 `mushroom_small` 注册均存在 |
| 影响 | `mushroom_small` 树苗可正常获得并放置，但右键催熟/自然生长时，生长器查不到对应的 `ConfiguredFeature`，**永远长不出蘑菇树**；不崩溃、无日志，属于静默功能失效 |
| 置信度 | **确定使用**（两树的树苗都直接绑定该 growth key；1.21.1 的 `MushroomTreeGrower` 已是弃用壳类，真正生效的是 `AurorianTreeGrowers`） |
| 性质 | 跨版本移植遗漏（1.19.2 → 1.20.1/1.21.1 时未带数据文件） |

> 说明：这不是贴图类「紫黑方块」问题，而是数据包资产缺失导致的玩法功能失效，仍属于「Java 引用但 JSON 校验覆盖不到」的盲区：validator 的 E 类只检查 `placed_feature → configured_feature`、`biome → placed_feature` 的 JSON 闭合，不会检查 Java 里写死的 `ConfiguredFeature` key。

### 3.2 已知盔甲贴图：当前工作区已存在（但未纳入 git）

上一轮报告（`docs/archive/` 中相关审计）记录 1.20.1 缺：

- `textures/models/armor/aurorian_slime_layer_1.png`
- `textures/models/armor/spiked_layer_1.png`

本次复查结果（2026-11-20）：

| 文件 | 1.21.1 | 1.20.1 | 1.19.2 | 1.20.1 文件状态 |
|---|---|---|---|---|
| `aurorian_slime_layer_1.png` | 存在 | **存在** | 存在 | 321 B，PNG 64×32 RGBA，md5 `2803810f8826`，三树完全一致 |
| `spiked_layer_1.png` | 存在 | **存在** | 存在 | 562 B，PNG 64×32 RGBA，md5 `89a29af80f3b`，三树完全一致 |

但这两个文件在 1.20.1 是**未跟踪文件**：

```
$ git -C TheAurorian-1.20.1f status --porcelain
?? src/main/resources/assets/theaurorian/textures/models/armor/aurorian_slime_layer_1.png
?? src/main/resources/assets/theaurorian/textures/models/armor/spiked_layer_1.png
```

文件 mtime 为 `2026-11-20 00:44:21`（同目录其余盔甲贴图为 `2026-08-03 13:48`）。

结论：

- 以**当前工作区**为准，1.20.1 已不存在该缺失，玩家不会再看到紫黑盔甲；
- 但两文件**未 commit**，任何 `git clean` / 干净检出 / 打包流程若只取已跟踪内容，缺陷会原样复现；
- 置信度：**确定使用**（`MaterialTiers.armorBuilder` 的 `getName()` 返回 `theaurorian:<name>`，`ArmorItem` 默认解析到 `textures/models/armor/<name>_layer_1.png`；`slime_boots` = BOOTS、`spiked_chestplate` = CHESTPLATE，均走 layer 1）。

其余盔甲层复核（三树一致，全部存在）：

| 材质 | layer_1 | layer_2 | 使用部件 |
|---|---|---|---|
| `aurorian_steel` | ✓ | ✓ | 全套 |
| `cerulean` | ✓ | ✓ | 全套 |
| `knight` | ✓ | ✓ | 全套 |
| `spectral` | ✓ | ✓ | 全套 + 自定义透明层 |
| `aurorian_slime` | ✓ | 不需要 | 仅 `slime_boots` |
| `spiked` | ✓ | 不需要 | 仅 `spiked_chestplate` |
| `umbra` | ✓ | 不需要 | 仅 `umbra_chestplate` |

### 3.3 二级：确认缺失但影响轻微

**无。**

### 3.4 三级：死代码 / 遗留（不构成缺失缺陷）

| 项 | 树 | 位置 | 说明 |
|---|---|---|---|
| `textures/models/armor/aurorianslimeboots.png` | 1.20.1 | 资源文件 | 1.12 时代命名的遗留贴图，**没有任何 Java 代码或 JSON 引用**；1.21.1 / 1.19.2 已无此文件。属于「多余文件」而非「缺失文件」 |
| `World/Feature/MushroomTreeGrower.java` | 1.21.1 | `@Deprecated` 空壳类 | 注释 `Prefer AurorianTreeGrowers#MUSHROOM`，无调用者；不影响资产，但与 D1 同源，建议一并清理 |
| `WeepingWillowTreeFeature` 的 NBT 文件名猜测 | — | — | 早期猜测的 `weepingwillowtree*.nbt` 不存在属误报；真实池为 `willow_l1/l2/s1/s2/s3`，三树均存在 |
| 注释中的资源路径 | 三树 | — | 未发现仅存在于注释里的资源引用（`grep '^\s*//.*(textures/|models/|\.png|\.ogg)'` 无命中） |
| 语言键 `.desc` | 三树 | `Util/TooltipUtil.java:26` | 动态拼 `<descriptionId>.desc`，但由 `ComponentUtils.isTranslationResolvable` 守卫，缺失不会显示原始键，不构成缺陷 |

### 3.5 语言键复核

Java 中硬编码的 19 个 lang 键（含 `itemGroup.theaurorian`、`string.theaurorian.*`、`mirror_of_guidance.*.<ns>.<path>.name/.desc` 等）三树全部存在；`UrnBlock.getDescriptionId()` 拼出的 `block.theaurorian.urn`、`AurorianWallTorchBlock` 经 `Util.makeDescriptionId` 生成的 `block.theaurorian.silentwood_wall_torch` / `moon_wall_torch` 也均存在。`MirrorDataLoader` 的 18 个镜像条目键在每棵树中都能解析。

---

## 4. 跨树不一致清单（按影响排序）

1. **`mushroom_tree` 配置特征**：1.19.2 有，1.21.1 / 1.20.1 无；而三树 Java 代码都引用它 → **D1，唯一确认的缺失缺陷**。
2. **羊贴图来源不同**：1.20.1 使用模组自有 `textures/entity/aurorian_sheep.png` 与 `aurorian_sheep_fur.png`（均存在）；1.21.1 / 1.19.2 使用原版 `textures/entity/sheep/sheep.png` 与 `sheep_fur.png`（`ResourceLocation.parse` 单参数 → `minecraft` 命名空间）。**无缺失资产**，但三版本外观/可定制性不一致。
3. **浅色草方块贴图布局不同**：1.20.1 / 1.21.1 的 DataGen 使用 `block/aurorian_grass_light_top + _side`；1.19.2 的 `cubeBottomTop` 把 `block/aurorian_grass_light` 本身当侧面贴图。各自树内所需文件都存在（1.19.2 **没有** `_side`，也不引用它）。
4. **`aurorianslimeboots.png` 仅存在于 1.20.1**，且无引用（死资产）。
5. **图集配置方式不同**：1.20.1 / 1.21.1 有 `atlases/blocks.json`；1.19.2 无 `atlases/`，改由 `EventsClient` 的 `TextureStitchEvent.Pre.addSprite` 注册 `entity/silentwood_chest/*`。属版本机制差异，非缺陷。
6. **数据目录单复数按版本切换**（预期差异）：1.21.1 用 `structure/`、`loot_table/`、`enchantment/`；1.20.1 / 1.19.2 用 `structures/`、`loot_tables/`、`enchantments/`（后两者附魔为代码注册，无数据文件）。

---

## 5. 已复核、确认无问题的部分（简述，不展开）

- 实体渲染贴图：三树所有 `getTextureLocation` 使用的贴图（dungeon_spider、moon_queen、crystalline_beam、disturbed_hollow、dungeon_keeper 双层、dungeon_slime、hollow、moon_acolyte 双层、aurorian_pig、aurorian_rabbit、spiderling、spirit、undead_knight、silentwood_chest 三态、两种箭）全部存在。
- GUI：`gui/moonlight_forge.png`、`gui/scrapper.png`、`gui/mirror_of_guidance{,_bg,_bg_2}.png` 全部存在。
- 方块图集：`block/moonlight_forge_overlay`（`TextureAtlas.LOCATION_BLOCKS`）存在，且 `atlases/blocks.json` 正确引用。
- 流体：`block/molten_{cerulean,moonstone,aurorian_steel}[_flow]` 六张全部存在。
- 结构模板与战利品表：`weepingwillow/*`、`runestone/upstream_ref/*`、10 张 `chests/*` 全部存在。
- DataGen：116 个自动物品贴图（4 个茶水走 `item/tea` 父模型，`item/tea.json` 引用的 `item/cup_tea.png`、`item/tea.png` 均存在）；58/58/59 个自动方块贴图；玻璃板 `_top`、crop `_stage0..3` 全部存在。
- 粒子：2 个事件名、2 个 JSON、`textures/particle/aurorian_slime_ball.png` 与 `textures/item/aurorian_slime_ball.png` 全部存在。
- 声音：`sounds.json` 2 个事件 → 6 个 `.ogg` 全部存在；validator I 类已覆盖，本次仅复核。
- 渲染层注册项：`setRenderLayer` 涉及的方块/物品名全部能在注册表找到（渲染层正确性本身沿用上一轮结论，不重复审计）。

---

## 6. 未覆盖 / 局限（诚实声明）

1. 只扫描 `src/main/java`；`scripts/`、`DevTest` 之外的工具代码未纳入，`.claude/worktrees/` 明确排除。
2. 未校验 PNG 的**内容**（像素、帧数、`.mcmeta` 动画参数、纹理尺寸是否匹配模型 UV）；仅对 §3.2 两个文件做了有效性/哈希比对。
3. 未验证 DataGen **运行后的最终产物**（模型/方块状态 JSON 是运行时生成，仓库里不存在）；本审计只验证其输入贴图与父模型。
4. 动态路径只覆盖了能静态展开的拼接；完全由运行时数据决定、且无字面量前缀的路径无法穷举（本项目未发现此类）。
5. 未检查 `data/` 下 Java 未显式引用的 JSON 闭包（属 validator A–I 范围）；未检查 Forge/NeoForge tag 内容正确性（tag 不是资产）。
6. 未启动客户端，视觉表现均由路径推导；D1 的「长不出树」由代码路径推导，未做游戏内实测。
7. 1.20.1 的两个盔甲 PNG 为未跟踪文件，本审计以工作区为准；若以 `HEAD` 为准，§3.2 应重新计为一级缺陷。

---

## 7. 建议最小修复（**未实施**，仅建议）

1. **D1**：把 1.19.2 的 `data/theaurorian/worldgen/configured_feature/mushroom_tree.json` 原样复制到
   - `Aurorian/src/main/resources/data/theaurorian/worldgen/configured_feature/mushroom_tree.json`
   - `TheAurorian-1.20.1f/src/main/resources/data/theaurorian/worldgen/configured_feature/mushroom_tree.json`

   内容 `{"type": "theaurorian:mushroom_tree", "config": {}}`（`MushroomTreeFeature` 使用 `NoneFeatureConfiguration`，三树 codec 相同）。
2. **1.20.1 盔甲**：把工作区已有的 `aurorian_slime_layer_1.png`、`spiked_layer_1.png` 纳入版本管理，避免被清理/打包遗漏。
3. 可选清理：删除 1.20.1 的 `aurorianslimeboots.png`；删除 1.21.1 的弃用壳类 `World/Feature/MushroomTreeGrower.java`。
4. 可选增强 validator（超出本次范围）：新增「Java 字面量 → 文件存在性」与「`textures/**/*.png` 存在性」两类检查，可覆盖本次的手工扫描。

---

## 附：复现

```bash
python3 /tmp/asset_sweep/refine.py        # 分类统计 + 未命中列表
python3 /tmp/asset_sweep/texcheck.py      # 逐条贴图核对
python3 /tmp/asset_sweep/final_sweep.py   # 全量兜底
```
