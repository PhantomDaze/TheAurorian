# The Aurorian — 1.12.2 vs 1.19.2 全量对比

> 生成日期：2026-07-31  
> 当前分支：`1.19.2` @ 工作区 `/opt/MDEV/Aurorian`  
> 上游参考：`upstream/` ← `https://github.com/shiroroku/TheAurorian` 分支 `1.12.2`（shallow clone，已在 `.gitignore`）  
> 版本标记：上游 `1.12.2-1.2` · 当前 `1.19.2-2.7-playtest`

---

## 0. 一句话结论

1.19.2 是**可 playtest 的重写移植**，不是逐文件搬迁。  
基础设施（维度、传送门、注册、机器、主材料线、Runestone 地牢）已通；  
**Darkstone / Moon Temple / Umbra Tower、2 个 Boss、被动生物、农业、大量装饰方块与兼容层**仍缺。  
以 Java LOC 计约完成 **30%**；以可玩主线闭环计约 **45–55%**（仅 Runestone 线闭环）。

---

## 1. 元数据

| 项 | 上游 1.12.2 | 当前 1.19.2 |
|----|-------------|-------------|
| 模组版本 | `1.12.2-1.2` | `1.19.2-2.7-playtest` |
| MC | 1.12.2 | 1.19.2 |
| Forge | 14.23.5.2847 | 43.4.6 |
| 映射 | MCP（FG3） | Parchment `1.19.2-2022.11.27` |
| Java | 8 | 17 |
| 包名 | `com.shiroroku.theaurorian` | `shiroroku.theaurorian` |
| 附加包 | `com.fluke.worleycaves`（内嵌洞穴） | 无 |
| 依赖 | JEI, Patchouli, Mantle, TConstruct, CraftTweaker, ConArm | Curios（硬依赖）, JEI, Geckolib |
| 图鉴 | Patchouli 书 `the_aurorian_guide` | 自研 `Mirror of Guidance` |
| 饰品 | 无 Curios（物品直接） | Curios 槽位 |
| 状态 | 完整发布版 | README 自述未完成 |

---

## 2. 体量总表

| 指标 | 上游 1.12.2 | 当前 1.19.2 | 比率 (cur/up) |
|------|------------:|------------:|--------------:|
| Java 文件 | 302 | 144 | **48%** |
| Java LOC | 28,237 | 8,406 | **30%** |
| 源树文件总数 | 1,725 | 1,063 | 62% |
| JSON | 672 | 689 | 103%* |
| PNG 贴图 | 659 | 202 | **31%** |
| NBT 结构 | 55 | 3 | **5%** |
| mcmeta | 26 | 17 | 65% |
| 音效 ogg | 6 | 0 | **0%** |
| 语言文件 | 3 (en/es/zh `.lang`) | 1 (en_us.json) | — |
| 语言键约 | ~350 (en) | 231 | 66% |
| 配方 JSON | 121 | 167 | 138%* |
| Patchouli JSON | 124 | 0（改 Mirror 12 节点） | — |
| 方块 blockstate | 99 | 65 | 66% |
| 物品模型 | 152 | 189 | 124%* |

\* JSON/配方/物品模型当前更高，因 1.19 拆分 data/assets、datagen 生成方块物品模型、slab/wall/deepslate 等新变体，**不代表内容更全**。

### 2.1 扩展名清单

**当前 `src/`**

| 扩展名 | 数量 |
|--------|-----:|
| json | 689 |
| png | 202 |
| java | 144 |
| mcmeta | 17 |
| nbt | 3 |
| toml / cfg / LICENSE | 少量 |

**上游 `upstream/src/`**

| 扩展名 | 数量 |
|--------|-----:|
| json | 672 |
| png | 659 |
| java | 302 |
| nbt | 55 |
| mcmeta | 26 |
| ogg | 6 |
| lang | 3 |

### 2.2 贴图分布

| 目录 | 上游 PNG | 当前 PNG |
|------|--------:|--------:|
| blocks / block | 112 | 65 |
| items / item | 121 | 110 |
| entity | 19 | 11 |
| armor / models/armor | 10 | 10 |
| gui | 2 | 5 |
| **合计** | **~659**（含 tcon/conarm 等） | **202** |

---

## 3. 代码结构

### 3.1 包树对比

```
上游 com.shiroroku.theaurorian          当前 shiroroku.theaurorian
├── Blocks (65)                         ├── Blocks (10 + 子包 25)
├── Compat                              │   ├── AurorianFurnace, BossSpawner, Crystal
│   ├── Conarm (2)                      │   ├── MoonlightForge, Scrapper
│   ├── CraftTweaker (6)                │   ├── SilentwoodChest, SilentwoodCraftingTable
│   ├── JEI (5)                         ├── Compat/Curios, Compat/JEI
│   └── TinkersConstruct (3)            ├── Config (Client/Common)
├── Enchantments (2)                    ├── DataGen (6)          ← 1.19 新增
├── Entities                            ├── Enchantments (2)
│   ├── Boss (17)                       ├── Entities
│   ├── Hostile (25)                    │   ├── AurorianArrow, CrystallineBeam
│   ├── Passive (12)                    │   ├── DungeonKeeper (+AI), DungeonSlime
│   └── Projectiles (10)                │   ├── Hollow, UndeadKnight
├── Items (68)                          ├── Items
├── Misc / Network / Particles          │   ├── AurorianSteel, Loot, MirrorOfGuidance
├── Recipes (4)                         │   ├── Moonstone, Spectral
├── Registry (12)                       ├── Mixin/Client
├── TileEntities (17)                   ├── Portal
├── Util (5)                            ├── Registry (9)
└── World                               ├── Renderers (Aurora 等)
    ├── Biomes (7)                      ├── Util
    ├── Feature (8)                     └── World/Feature (仅 SilentwoodTree)
    └── Structures (6)
+ com.fluke.worleycaves (4, ~3710 LOC)
```

### 3.2 Java LOC / 文件数 by 区域

| 区域 | 上游 files | 上游 LOC | 当前 files | 当前 LOC | 备注 |
|------|----------:|---------:|----------:|---------:|------|
| Blocks (+TE 在 1.12) | 65+17 | ~6.8k | 35 | 2,263 | TE 并入 Blocks 子包 |
| Items | 68 | 4,558 | 48 | 2,242 | |
| Entities | 64 | 6,480 | 22 | 1,158 | **最大缺口** |
| World (+Struct/Biome/Feat) | 28 | 3,078 | 1 | 33 | 世界生成几乎全改 datapack |
| Registry | 12 | 943 | 9 | 631 | |
| Compat | 16 | 657 | 4 | 193 | 无 TCon/CT/ConArm |
| Enchantments | 2 | 179 | 2 | 56 | |
| Util / 其它 | — | — | — | — | |
| Worley Caves 内嵌 | 4 | 3,710 | 0 | 0 | **整包缺失** |
| DataGen / Mixin / Portal / Config | 0 | 0 | 有 | ~1.2k | 1.19 架构新增 |
| Mirror of Guidance | 0 | 0 | 4 | — | 替代 Patchouli |

### 3.3 当前包文件数明细

| 包 | 文件数 |
|----|------:|
| `.../Items` + 子包 | 48 |
| `.../Blocks` + 子包 | 35 |
| `.../Entities` + 子包 | 22 |
| `.../Registry` | 9 |
| `.../DataGen` | 6 |
| 根 + Events | 5 |
| `.../Util` | 4 |
| `.../Compat` | 4 |
| `.../Renderers` | 3 |
| `.../Config` | 2 |
| `.../Portal` | 2 |
| `.../Enchantments` | 2 |
| `.../World/Feature` | 1 |
| `.../Mixin` | 1 |

### 3.4 上游包文件数明细（核心）

| 包 | 文件数 |
|----|------:|
| `.../Items` | 68 |
| `.../Blocks` | 65 |
| `.../Entities/Hostile` | 25 |
| `.../Entities/Boss` | 17 |
| `.../TileEntities` | 17 |
| `.../Entities/Passive` | 12 |
| `.../Registry` | 12 |
| `.../Entities/Projectiles` | 10 |
| `.../World/Feature` | 8 |
| `.../World` + Biomes + Structures | 7+7+6 |
| Compat 合计 | 16 |
| worleycaves | 4 |

---

## 4. 注册内容对比

### 4.1 方块（blockstate 名，规范化去下划线后比对）

| | 数量 |
|--|-----:|
| 上游 blockstates | 99 |
| 当前 blockstates | 65 |
| 规范化后共有 | ~40 |
| 仅上游 | ~59 |
| 仅当前（含 rename/新变体） | ~25 |

#### 当前已有方块（注册名）

**Runestone：** `runestone`, `runestone_bars`, `runestone_gate`, `runestone_gate_keyhole`, `runestone_gate_loot_keyhole`, `runestone_lamp`, `runestone_smooth`, `runestone_stairs`  
**Darkstone：** `darkstone`, `darkstone_chipped`, `darkstone_gate`, `darkstone_gate_keyhole`, `darkstone_lamp`, `darkstone_pillar`, `darkstone_stairs`  
**Moon Temple：** `moon_temple_bricks`, `moon_temple_bars`, `moon_temple_bricks_smooth`, `moon_temple_gate`, `moon_temple_gate_keyhole`, `moon_temple_interior_gate`, `moon_temple_interior_gate_keyhole`, `moon_temple_lamp`, `moon_temple_stairs`  
**自然/木材：** `aurorian_cobblestone`(+slab/stairs/wall), `aurorian_deepslate`(+slab/stairs/wall), `aurorian_dirt`, `aurorian_grass`, `aurorian_stone`, `silentwood_*` (fence/leaves/log/planks/sapling/slab/stairs/chest/crafting_table)  
**植物：** `aurorian_tallgrass`, `bright_bulb`, `lavender_block`, `petunia`, `silkberry_block`  
**矿：** `aurorian_coal_ore`, `cerulean_ore`, `deepslate_cerulean_ore`, `moonstone_ore`, `deepslate_moonstone_ore`, `geode`  
**机器/其它：** `aurorian_furnace`, `boss_spawner`, `moonlight_forge`, `scrapper`, `chimney`, `crystal`, `fog_wall`, `moon_gem`, `aurorian_portal`, `aurorian_portal_frame`

#### 仅上游有、当前缺失的方块（玩法相关优先）

| 类别 | 缺失 ID（1.12 名） |
|------|-------------------|
| 农业 | `lavendercrop`, `silkberrycrop`, `aurorianfarmtile` |
| Weeping Willow | `weepingwillowleaves/log/planks/stairs/sapling` |
| 蘑菇 | `mushroom`, `mushroomcrystal`, `mushroomsmall`, `mushroomstem` |
| Umbra 建筑 | `umbrastone`, `umbrastonecracked`, `umbrastonerooftiles`(+stairs) |
| 装饰/玻璃 | `aurorianglass`(+pane), `moonglass`(+pane), `aurorianstonebrick`, `moonsand`, `moontorch`, `silentwoodladder/torch`, `urn` |
| 材料块 | `auroriancoalblock`, `auroriansteelblock`, `ceruleanblock`, `moonstoneblock` |
| 石头变体 | `peridotite`(+smooth/stairs), `auroriangrasslight`, `auroriantallgrasslight` |
| Boss 刷怪 | 分体 `bossspawnerkeeper/moonqueen/spider`（当前合并为通用 `boss_spawner`） |
| 流体（TCon） | `tamoltenauroriansteel/cerulean/moonstone`, `tamoonwater`, `ceruleanbucket` |
| 其它 | `runestonelootgate`（当前仅 keyhole）, `mysticalbarrier`（→ 已改名 `fog_wall`） |

#### 仅当前新增（1.19 向）

- Deepslate 套件 + deepslate 矿  
- cobble slab/wall、silentwood fence/slab  
- 通用 `boss_spawner`、`fog_wall`、`bright_bulb`  
- darkstone 命名重整（`chipped`/`pillar`）

### 4.2 物品

当前 `ItemRegistry` 注册 **~103** 个非方块物品（含 spawn egg）；物品模型 189（含方块物品与 bow/shield 状态模型）。

#### 当前物品分类

| 类 | 内容 |
|----|------|
| 材料 | coal/nugget, steel ingot/nugget, aurorianite/crystalline/umbra ingot+scrap, cerulean/moonstone ingot/nugget, plant_fiber, spectral_silk, lavender, cup, stick |
| 钥匙 | runestone_key, runestone_loot_key, darkstone_key, moon_temple_key, moon_temple_interior_key, moon_temple_key_fragment, lockpicks |
| 食物/茶 | silkberry, jam, sandwich, strange_meat, lavender_bread, 4 种茶 (bright_bulb/lavender/petunia/silkberry) |
| 工具线 | Silentwood / Aurorian Stone / Moonstone 全套；Steel 全套；Aurorianite 斧镐剑铲；Umbra 镐/大剑；Crystalline 镐/剑；镰刀；弓；盾 |
| 护甲 | Cerulean, Knight, Spectral, Aurorian Steel；Umbra 仅胸甲 |
| 饰品 Curio | amulet_of_chroma, emerald/ruby/sapphire/keepers amulet |
| 特殊 | mirror_of_guidance, living_divining_rod, absorption_orb |
| 弹药 | cerulean_arrow, crystal_arrow |
| 蛋 | keeper, slime, hollow, undead_knight |

#### 仅上游有的重要物品

| 物品 | 说明 |
|------|------|
| `locator` / DungeonLocator | 地牢定位器 |
| `keepersbow` | Keeper Boss 武器 |
| `queenschipper` | Moon Queen 武器 |
| `moonshield` | Moon Queen 相关盾 |
| `trophies` (keeper/moonqueen/spider) | Boss 奖杯 |
| `aurorianslimeboots`, `spikedchestplate` | 特殊护甲 |
| `stickyspiker`, `webbing`, `crystallinesprite` | 蜘蛛线道具/召唤 |
| `theaurorianguide` | Patchouli 书（→ Mirror） |
| `bepsi`, `debugger` | 彩蛋/调试 |
| `lavenderseeds` + 作物相关 | 农业 |
| 被动掉落食物 | bacon, pork, slimeball, soullessflesh, silkshroomstew, weepingwillowsap |
| `darkamulet` | 命名不同（≈ chroma/其它） |

#### 当前新增/重做物品

- `mirror_of_guidance`（替代 Patchouli）  
- Curios 多属性护符拆分（emerald/ruby/sapphire/chroma）  
- `aurorianite_shovel`（1.12 无）  
- `umbra_chestplate` + `umbra_greatsword`（1.12 为 umbrasword，已 rework）  
- 茶拆成多种；spawn eggs  

### 4.3 实体

| 类型 | 上游 | 当前 | 状态 |
|------|------|------|------|
| **Boss: Keeper** | ✅ KeeperEntity | ✅ DungeonKeeperEntity | 已移植 |
| **Boss: Moon Queen** | ✅ MoonQueenEntity | ❌ | **缺失** |
| **Boss: Spider** | ✅ SpiderEntity (dungeon) | ❌ | **缺失** |
| Hollow / DisturbedHollow | ✅ | ✅ HollowEntity | 有 |
| Undead Knight | ✅ | ✅ | 有（装备逻辑在） |
| Aurorian Slime / Dungeon Slime | ✅ | ✅ DungeonSlime | 有 |
| Moon Acolyte | ✅ | ❌ | 缺失 |
| Crystalline Sprite | ✅ | ❌ | 缺失 |
| Spirit | ✅ | ❌ | 缺失 |
| Spiderling | ✅ | ❌ | 缺失 |
| 被动: Pig / Rabbit / Sheep | ✅ | ❌ | **全部缺失** |
| 投射: Cerulean/Crystal Arrow | ✅ | ✅ | 有 |
| Crystalline Beam | ✅ | ✅ | 有 |
| Sticky Spiker / Webbing | ✅ | ❌ | 缺失 |

**当前实体注册名：** `cerulean_arrow`, `crystal_arrow`, `crystalline_beam`, `dungeon_keeper`, `dungeon_slime`, `hollow`, `undead_knight`（7 种）

**上游实体 loot 表：** 12 个 entity loot；当前 **0** 个 entity loot JSON。

### 4.4 方块实体 / 机器

| 机器 | 上游 | 当前 |
|------|:----:|:----:|
| Aurorian Furnace + Chimney | ✅ | ✅ |
| Moonlight Forge | ✅ | ✅ + JEI |
| Scrapper | ✅ | ✅ + JEI |
| Silentwood Chest | ✅ | ✅ |
| Silentwood Workbench | ✅ | ✅ |
| Crystal TE | ✅ | ✅ |
| Boss Spawner TE | ✅（分类型） | ✅（通用 + NBT boss id） |

### 4.5 附魔

| | 上游 | 当前 |
|--|------|------|
| Lightning | LightningDamage | LightningEnchant |
| Lightning Resistance | LightningResistance | LightningResistanceEnchant |

### 4.6 生物群系

| 上游 (7) | 当前 (3) |
|----------|----------|
| AurorianForest | aurorian_forest |
| AurorianForestHills | — |
| AurorianPlains | aurorian_plains |
| AurorianLakes | — |
| AurorianOvergrowth | — |
| WeepingWillowForest | —（README：暂移除） |
| （+ rough 变体思路） | aurorian_rough_forest |

### 4.7 维度 / 传送门

| | 上游 | 当前 |
|--|------|------|
| 维度 | Java WorldProvider + ChunkGenerator | datapack `dimension` + `noise_settings` + multi_noise |
| 传送门方块/框 | ✅ | ✅ |
| Teleporter | AurorianTeleporter | AurorianPortalTeleporter + Shape + POI |
| 坐标缩放 | 有 | 有（DimensionType scale） |

### 4.8 结构与世界生成

#### NBT 结构件

| 结构组 | 上游 NBT 数 | 当前 NBT | 生成器类（上游） | 当前 worldgen |
|--------|----------:|----------|------------------|---------------|
| Runestone Dungeon | 20 | 2 (`runestone/top|bottom`) | RunestoneTowerWorldGenerator | ✅ structure + set + pools |
| Darkstone Dungeon | 14 | 0 | DarkstoneDungeonWorldGenerator | ❌ 仅方块 |
| Moon Temple | 11 | 0 | MoonTempleWorldGenerator | ❌ 仅方块 |
| Ruins | 3 | 1 (`ruined_house`) | RuinsWorldGenerator / Graveyard | ⚠️ 简化 1 种 |
| Umbra Tower | 2 | 0 | UmbraTowerWorldGenerator | ❌ |
| Weeping Willow 树 | 5 | 0 | WeepingWillowTreeWorldGenerator | ❌ |
| **合计** | **55** | **3** | | |

#### 上游 Feature / 生成器（Java）

- SilentwoodTree, WeepingWillowTree  
- Plant, TallGrass, Mushroom  
- UnderGround, UnderWater, Urns  
- （+ WorleyCaveGenerator 洞穴）

#### 当前 configured/placed features（datapack）

`silentwood_tree`, `aurorian_grass_patch`, `aurorian_plants`, `lavender_patch`, `forest_rock`, `ore_coal`, `ore_cerulean`, `ore_moonstone`, `ore_geode`

### 4.9 Loot

| 类型 | 上游 | 当前 |
|------|------|------|
| 宝箱 | runestone low/med/high；darkstone L/M/H；moontemple L/M/H；ruins（10） | runestone common/uncommon/rare/epic；ruined_house（5） |
| 实体 | 12 | 0 |
| 方块 | 少量手写 + urn | datagen 大量 blocks loot |

### 4.10 配方

| | 上游 | 当前 |
|--|-----:|-----:|
| 总 JSON | 121（assets/recipes） | 167（data/.../recipes） |
| shaped | — | 59 |
| shapeless | — | 20 |
| smelting | — | 7 |
| blasting | — | 6 |
| moonlight_forge | 有 | 20 |
| scrapper | 有 | 55 |

### 4.11 图鉴 / 进度

| | 上游 | 当前 |
|--|------|------|
| 指南 | Patchouli 双语书，124 JSON，分类：basics/agriculture/blocks/magical/progression | Mirror of Guidance，12 个 datapack 节点 |
| Mirror 节点 | — | aurorian, aurorian_steel, aurorianite, crafting, dungeon_darkstone, dungeon_runestone, dungeons, ore_*, ores, umbra |
| Advancements | assets 下有 | 未见独立进度树 |

### 4.12 兼容

| 模块 | 上游 | 当前 |
|------|:----:|:----:|
| JEI | ✅ | ✅ |
| Patchouli | ✅ | ❌（自研 Mirror） |
| Tinkers Construct | ✅ 流体/材料/trait | ❌ |
| Constructs Armory | ✅ | ❌ |
| CraftTweaker | ✅ MF/Scrapper | ❌ |
| Curios | ❌ | ✅ 硬依赖 |
| Geckolib | ❌ | ✅ 依赖已加（内容待用） |

### 4.13 音效 / 粒子 / 网络

| | 上游 | 当前 |
|--|------|------|
| SoundRegistry + ogg×6 | ✅ | ❌ |
| Particles（slime/willow/webbing 等） | ✅ | ❌ |
| Network packets | ✅ | ❌（更依赖原版同步） |

---

## 5. 特殊能力移植状态

（当前侧能力描述来自 `Items/**` 源码通读；上游为方法级对照。）

| 物品/套装 | 当前实现细节 | 状态 |
|-----------|--------------|------|
| **Aurorianite Pickaxe** | 挖 `ORES` → 自身 Haste II 5s + 额外耐久消耗 | ✅ |
| **Aurorianite Sword** | 右键：5 格内生物+自己漂浮；10s CD；副手盾需潜行 | ✅ |
| **Aurorianite Axe** | 挖原木 → 3×3 柱状伐木（最多 256） | ✅ |
| **Aurorianite Shovel** | 可配置半径/抗性范围内的铲类方块群挖 + 方块轮廓 | ✅ **1.19 新增** |
| **Umbra Pickaxe** | 右键选定方块类型写入 NBT；仅对该类型加速（config 倍率）；潜行清除；轮廓渲染 | ✅ rework |
| **Umbra Greatsword** | 主/副手持有时持续缓慢 + 抗性（rework，原 umbrasword 右键） | ✅ rework |
| **Umbra Shield** | 使用时自伤；`onUseTick` 视线锥内生物着火 + 粒子 | ✅ |
| **Umbra Chestplate** | 潜行时动态 Thorns III；禁止原版荆棘附魔 | ✅ 1.19 向 |
| **Crystalline Sword** | 弓式充能，松手发射 `CrystallineBeamEntity`（config 速度/伤害） | ✅ rework |
| **Crystalline Pickaxe** | 挖矿时 config 概率从 `CRYSTALLINE_PICKAXE_TREASURE` tag 额外掉落 | ✅ |
| **Crystalline Shield** | 格挡时修复主手（tag 或 config `repairs_all`），消耗盾耐久 | ✅ |
| **Absorption Orb** | 副手 tick：修复主手，消耗自身；禁附魔台/书 | ✅ |
| **Living Divining Rod** | 右键：18 格非战斗生物发光（75%）；2s CD | ✅ |
| **Moonstone 全套** | `onItemDamage`：50% 免伤，白天额外 +1 伤耐久（夜间更耐用） | ✅ |
| **Aurorian Steel 全套** | 使用获得 XP → 升级件上第一个可升级附魔；阈值阈值递增；金字 tooltip | ✅ |
| **Spectral Armor** | 攻击时每件 config 概率（默认 6%）驱散一个非有益效果 | ⚠️ 能力有，**透明渲染无** |
| **Strange Meat** | 长食用；随机 5 种之一长 buff + 损耗 | ✅ |
| **Curio 护符** | 仅属性修饰（生命/移速/击退抗/攻击击退） | ✅ |
| **Silentwood 工具** | Base + 燃时；**无 1.12 特殊破坏/修理类能力** | ❌ notes |
| **Keeper's Bow** | — | ❌ |
| **Queen's Chipper** | — | ❌ |
| **Dungeon Locator** | — | ❌ |
| **Slime Boots / Spiked Chest** | — | ❌ |
| **Sticky Spiker / Webbing** | — | ❌ |

### 5.1 相关配置键（CommonConfig）

- 钢：`aurorian_steel_base_level`, multiplier  
- 月石：`moonstone_damage_chance`  
- 晶：`crystalline_pickaxe_treasure_chance`, beam 参数, shield `repairs_all`  
- 铲：aurorianite shovel radius / resistance  
- 影：umbra pickaxe speed / selection_cost  
- 灵：`spectral_armor_cleanse_chance`  
- 球：`absorption_orb_repairs_all`  
- 烟囱 / Scrapper 时长 / Boss per-player 三维缩放  
- 客户端：`enable_auroras`

---

## 6. Boss 对比

| Boss | 上游 | 当前 | 关联地牢 | 掉落/武器 |
|------|------|------|----------|-----------|
| Runestone Keeper | ✅ 完整 AI（弓/弹幕/近战）+ 模型层 | ✅ DungeonKeeper + 3 Goals + 血条 + 人数缩放 | Runestone | 上游 KeepersBow 等；当前需核对 loot |
| Moon Queen | ✅ Charge/Strafe AI + 模型 | ❌ | Moon Temple | Queen's Chipper, Moon Shield, trophy |
| Spider (Dungeon) | ✅ Hang/Leap/Spit + spiderling | ❌ | Darkstone | webbing, sticky spiker, trophy |

Boss Spawner：上游三种专用方块；当前一个通用 BE，靠 NBT/`setBoss`，**结构里需写入 boss id**。

人数缩放：当前 `CommonConfig` 支持 speed/damage/health per player（✅ README 卖点已落地）。

---

## 7. notes.txt 与 README 对照

### notes.txt 待办 → 本对比核实

| 待办 | 核实 |
|------|------|
| silentwood abilities | ❌ 未做 |
| spectral armor transparency | ❌ 未做 |
| dark stone dungeon and mobs | 方块✅ 结构/怪❌ |
| moon temple dungeon and mobs | 方块✅ 结构/怪❌ |
| ruin structures | 仅 ruined_house |
| farming | ❌ 无 crop/farmtile |
| dungeon locator and boss weapons | ❌ |
| aurorian portal | ✅ 已有完整传送门 |
| undead knights missing gear | ⚠️ 代码已穿骑士甲，notes 可能过时 |
| shears tag / sickle | 技术债仍在 notes |
| tool tier config 不可行 | 架构限制仍在 |
| chest 物品栏 2D | 仍用 sprite |

### README「1.12→1.19 重大变化」落地情况

| 宣称 | 状态 |
|------|------|
| Mirror of Guidance | ✅ |
| Curios 硬依赖 | ✅ |
| 地牢随机散布非网格 | ⚠️ Runestone structure_set 有；其它地牢无 |
| Boss 人数难度缩放 | ✅ 框架在 |
| 全地牢新布局更多样 | ⚠️ 仅 Runestone 新布局 |
| Umbra / Crystalline sword rework | ✅ 代码向 |
| Scrapper 按耐久退材料 | ✅ 配方量充足 |
| Mystical Barriers → Fog Walls | ✅ |
| 地下 lush 新内容 | ⚠️ 有限 features，无 worley/蘑菇群系 |
| Chimney 减燃料 | ✅ |
| Aurorian Steel 新配方 | ✅ |
| Weeping willow 暂移除 | ✅ 一致 |
| 地牢 loot 绑定材料线 | ⚠️ 设计在，Darkstone/Temple 未通 |
| Aurora / 月亮渲染 | ✅ AuroraRenderer + mixin |

---

## 8. 架构差异（移植时注意）

| 主题 | 1.12 | 1.19.2 |
|------|------|--------|
| 注册 | RegistryEvent + enum Registry | DeferredRegister |
| 世界生成 | Java ChunkGenerator/BiomeDecorator | datapack noise + biome + placed_feature |
| 结构 | 手写 WorldGenerator + NBT 拼接 | structure / template_pool / structure_set |
| 配方 | assets/recipes | data/.../recipes + RecipeSerializer 自定义 |
| 网络 | 自建 PacketRegistry | 较少自定义包 |
| 配置 | AurorianConfig/Configs | ForgeConfigSpec Client/Common |
| 数据生成 | 基本手写资源 | DataGen 生成模型/tag/block loot |
| 图鉴 | Patchouli | 自研 Mirror datapack |
| 洞穴 | 内嵌 Worley Caves | 未移植 |
| 命名 | 无下划线拼接 | snake_case |

---

## 9. 缺口优先级（相对 1.12 完整度）

### P0 — 主线闭环

1. Darkstone 地牢：NBT/pool/set + chest loot + Spider boss + 相关敌对  
2. Moon Temple 地牢：同上 + Moon Queen + Moon Acolyte  
3. Boss 掉落武器与 trophy  
4. 实体 loot 表  

### P1 — 世界与进度

5. Umbra Tower 结构  
6. Ruins/Graveyard 扩展  
7. Dungeon Locator  
8. 生物群系补全（Lakes/Overgrowth/Hills；Willow 若恢复）  
9. 被动生物（羊/猪/兔）与食物链  
10. 农业（farmtile + lavender/silkberry crop）  

### P2 — 物品与表现

11. Silentwood 特殊能力  
12. Spectral 透明渲染  
13. 缺失装饰方块（glass、torch、ladder、urn、material blocks、umbra stone）  
14. 特殊甲（slime boots、spiked chest）与投掷物  
15. 音效 / 粒子  

### P3 — 生态与兼容

16. Worley 风格洞穴或替代地下内容  
17. 蘑菇区内容  
18. TCon/CT 等（若仍需要）  
19. 多语言（zh_cn/es）  
20. Advancements  

---

## 10. 完成度量化（多口径）

| 口径 | 估计 | 依据 |
|------|-----:|------|
| Java LOC | **~30%** | 8.4k / 28.2k |
| Java 文件数 | **~48%** | 144 / 302 |
| 方块种类 | **~55–65%** | 65/99，含 rename |
| 物品玩法内容 | **~65–70%** | 主线工具甲齐全，缺 Boss 武与特殊件 |
| 实体 | **~25%** | 7 vs ~20+ 种 |
| 结构 NBT | **~5%** | 3 / 55 |
| 地牢主线（3 条） | **~33%** | 1 / 3 可打 |
| 生物群系 | **~40%** | 3 / 7 |
| 贴图 | **~31%** | 202 / 659 |
| 配方 | **较高** | 数量超上游，覆盖基础+机器 |
| **综合可玩移植** | **~45–55%** | playtest 可跑，内容深度不足 |
| **相对 1.12 发售完整度** | **~35–45%** | 两大地牢+生态未归 |

---

## 11. 文件级 Java 名对比（辅助）

> 类名重命名很多（`KeeperEntity`→`DungeonKeeperEntity`，`*TileEntity`→`*BlockEntity`），不能只靠 basename 判断缺失。  
> 下面「仅上游 basename」表示 1.12 有、1.19 **同名文件不存在**（可能已改名移植）。

### 上游有、当前无同名的代表性类（玩法）

- Boss: `MoonQueenEntity`, `SpiderEntity`, `KeeperEntity`（已改名存在）  
- Hostile: `MoonAcolyte*`, `CrystallineSprite*`, `Spirit*`, `Spiderling*`, `DisturbedHollow*`  
- Passive: `AurorianPig*`, `AurorianRabbit*`, `AurorianSheep*`  
- Structures: `DarkstoneDungeonWorldGenerator`, `MoonTempleWorldGenerator`, `UmbraTowerWorldGenerator`, `RuinsWorldGenerator`, `GraveyardWorldGenerator`, `RunestoneTowerWorldGenerator`  
- Items: `DungeonLocatorItem`, `KeepersBow`, `QueensChipper`, `StickySpikerItem`, `WebbingItem`, `SlimeBootsItemArmor`, `SpikedItemArmor`  
- World: 全套 Biome/ChunkGenerator/Worley  
- Compat: Tinker/ConArm/CraftTweaker 全套  

### 当前有、上游无同名的代表性类（1.19 架构/新内容）

- `MirrorOG*`, `AuroraRenderer`, `DataGen*`, `BaseAurorian*`, `FogWallBlock`  
- `AurorianiteShovel*`, `UmbraGreatsword`, `UmbraChestplate`  
- `AurorianPortalTeleporter/Shape`, `LevelRendererMixin`  
- `CuriosCompat`, `POIRegistry`, `MaterialTiers`  

---

## 12. 统计命令备忘（复现）

```bash
# 体量
find src -name '*.java' | wc -l
find upstream/src -name '*.java' | wc -l
find src -name '*.java' -print0 | xargs -0 cat | wc -l

# 结构
find src upstream/src -name '*.nbt' | sort

# 方块/物品集合
find src -path '*/blockstates/*.json' | sed 's|.*/||;s/.json//' | sort -u
ls upstream/src/main/resources/assets/theaurorian/blockstates | sed 's/.json//'
```

上游克隆：

```bash
git clone --branch 1.12.2 --single-branch --depth 1 \
  https://github.com/shiroroku/TheAurorian.git upstream
```

---

## 13. 附录：当前世界与数据目录快照

```
data/theaurorian/
├── dimension/the_aurorian.json
├── dimension_type/
├── loot_tables/chests/{ruined_house,runestone/*}
├── mirror_of_guidance/*.json          (12)
├── recipes/{blasting,moonlight_forge,scrapper,shaped,shapeless,smelting}
├── structures/{ruined_house.nbt,runestone/top|bottom.nbt}
└── worldgen/
    ├── biome/ (3)
    ├── configured_feature/ (9)
    ├── placed_feature/ (9)
    ├── noise_settings/
    ├── structure/ (runestone_dungeon, ruined_house)
    ├── structure_set/
    └── template_pool/
```

```
upstream/.../structures/
├── darkstone/     (14 nbt)
├── moontemple/    (11 nbt)
├── ruins/         (3 nbt)
├── runestonedungeon/ (20 nbt)
├── umbratower/    (2 nbt)
└── weepingwillow/ (5 nbt)
```

---

*本文件为静态快照，不随 git 自动更新。上游目录被 `.gitignore` 忽略，仅作本地玩法参考。*
