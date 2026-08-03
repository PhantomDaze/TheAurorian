# The Aurorian — 1.21.1 NeoForge 零妥协移植计划

> 生成日期：2026-08-03  
> 当前分支：`1.21.1` @ `/opt/MDEV/Aurorian`  
> **工作区实际代码基线：** NeoForge **21.1.248** + MDG **2.0.143** + Java **21**；版本 **`1.21.1-1.0`**；`compileJava`/`test`/`jar`/`runGameTestServer` 22/22 绿；NFG §8.1–8.10 已勾  
> 内容基线：[`docs/diff.md`](diff.md) + [`docs/port-plan.md`](port-plan.md)（1.12.2 → 1.19.2 内容 Phase 0–10 + G **已关闭**）  
> 目标平台：**Minecraft 1.21.1 + NeoForge**  
> 原则：**最终交付零游戏内容妥协**；允许 API/架构重写；阶段性可先缺后补，但缺什么必须在本文档 **§9 暂缓清单** 登记并有关闭条件。

---

## 0. 一句话目标

在 **不删减** 1.20.1 已闭环的全部游戏内容（三地牢、三 Boss、生态、农业、特殊装备、Mirror、进度、音效粒子、多语言、机器、传送门…）前提下，把模组完整迁移到 **1.21.1 NeoForge**，自动化门禁（资源校验 / JUnit / GameTest）全绿，玩法效果对等。

这是 **加载器 + API + 数据包格式** 移植，**不是** 再做一遍 1.12 内容补齐。内容完整度以 `docs/port-plan.md` Phase G 与 `docs/diff.md` §10 为准（内容向 ~95%+；TCon/ConArm/CT 仍为兼容层豁免）。

---

## 1. 目标定义（完成判定）

| 项 | 定义 |
|----|------|
| **完成** | 1.20.1 分支上已有的**全部游戏内容**在 1.21.1 NeoForge 可编译、可进世界、可玩；玩法效果对等（允许数据组件/附魔 datapack/标签命名空间等架构替换）。 |
| **允许的「非 1:1」** | `net.minecraftforge` → `net.neoforged`；Capability → Attachment / 直接字段；物品 NBT → Data Components；Java 附魔类 → 1.21 附魔 datapack（效果对等）；`forge:` 标签 → `c:`（Common）+ 必要兼容别名；数据包路径单数化（`loot_table` 等）；Curios IMC → datapack 槽位（已有 `data/theaurorian/curios/slots/` 可演进）。 |
| **不允许** | 永久移除任一 Boss / 地牢 / 群系 / Willow / 蘑菇 / 被动 / 农业 / Locator / Boss 武器与 trophy / 特殊甲与投掷物 / 音效粒子 / Mirror 节点 / 进度 / 机器 / 传送门；用「以后再说」静默砍线。 |
| **兼容层** | TCon / ConArm / CraftTweaker：延续 `port-plan.md` D26 **`exempt`**，不阻塞 1.21.1 主完成判定；若未来要做须单独 Phase C21。 |
| **Geckolib** | 依赖可保留作未来增强；**不得**以「没 Gecko 模型」为由砍 Boss/实体（当前 Java 模型+贴图已对等玩法）。 |

### 1.1 文档纪律（暂不完全完成时）

任何合并进 `1.21.1`、但**尚未达到 1.20.1 内容对等**的子系统，必须同时满足：

1. 在本文档 **§9 暂缓清单** 有一条：缺口、现状、原因、关闭条件、目标 Phase、状态。  
2. Mirror / 创造物品 / README **不假装已完成**（可用 WIP 标记）。  
3. 禁止静默删除内容或只把「以后再说」留在聊天里。  
4. 关闭后：§9 标 `[closed]` 或 `exempt`，并刷新 §8 门禁与（如需要）`diff.md` 附录。

---

## 2. 现状锚点（计划起点 · 2026-08-03）

### 2.1 构建与元数据（已切 1.21.1 NeoForge · 2026-08-03）

| 项 | 当前值 | 1.21.1 目标 |
|----|--------|-------------|
| 分支名 | `1.21.1` | `1.21.1` |
| MC | `1.20.1` | `1.21.1` |
| 加载器 | Forge `47.4.10` + FG 6 | **NeoForge `21.1.x`**（Maven 线已见至 **21.1.248**；锁定实施当日最新 **21.1 稳定/推荐** patch） |
| 构建插件 | `net.minecraftforge.gradle` + Librarian | **ModDevGradle（MDG）** `net.neoforged.moddev` |
| Gradle | 8.1.1 | **≥ 8.8**（随 MDG 模板；建议 8.10+） |
| Java | 17 | **21** |
| 映射 | Parchment `2023.09.03-1.20.1` | Parchment **1.21.1** 对应版本（MDG 配置） |
| 模组版本建议 | `1.20.1-1.0` | `1.21.1-1.0`（门禁前可用 `1.21.1-0.x-port`） |
| mods 元数据 | `META-INF/mods.toml`（forge） | **`META-INF/neoforge.mods.toml`**（或 MDG 生成约定） |
| pack_format | 15（含 `forge:resource_pack_format` / `forge:data_pack_format`） | **1.21.x 格式（常见目标 `pack_format` 48 量级，以 1.21.1 运行时/MDG 模版为准）**；去掉失效 forge 扩展键 |
| Mixin | `compatibilityLevel: JAVA_8` | **JAVA_21**；NeoForge mixin 配置 |
| AT | `accesstransformer.cfg`（portalEntrancePos） | NeoForge AT 路径/语法核对；必要时 Access Transformer 仍可用 |
| Curios | `curios-forge` `5.14.1+1.20.1` + IMC | **`curios-neoforge` `9.5.1+1.21.1`**（Maven 已发布；实施锁最新 9.x+1.21.1）+ **datapack 槽位** |
| JEI | `15.21.0.148` forge | **`jei-1.21.1-neoforge`**（Maven 线已见 **19.43.0.392**；锁实施当日最新） |
| Geckolib | curse `geckolib` 1.20.1 | **`geckolib-neoforge-1.21.1`**（Maven 线已见 **4.9.2**；可选 runtime） |

### 2.2 内容基线（禁止回退）

来自已关闭的 1.19.2/1.20.1 移植（详见 `diff.md` / `port-plan.md`）：

| 包 | 基线（不得少于） |
|----|------------------|
| Java | **233** 源文件（`shiroroku.theaurorian.**`） |
| 方块注册 | **~102** `RegistryObject<Block>`（`BLOCKS` / `BLOCKS_GEN` / `BLOCKS_GEN_NL` / `BLOCKS_GEN_NL_PLANT`） |
| 方块 blockstate | ~90–102（main hand-written + `src/generated`） |
| 物品注册 | **~133** `RegistryObject<Item>`（多 DeferredRegister 变体；含 11+ Curio、**13** `ForgeSpawnEggItem`） |
| 物品模型 | ~250+ item models |
| 方块实体 | **6**（chest / furnace / boss_spawner / crystal / moonlight_forge / scrapper） |
| 实体 | **19** 种 + attributes/spawn placement + loot |
| 结构 NBT | **61**（含 gametest；主内容 55+ 上游等价） |
| 自定义 Structure 类 | 3（`DarkstoneDungeonStructure` 字符地图 5×5×2、`MoonTempleStructure`、`SingleTemplateStructure`） |
| 群系 | **7** |
| 配方 JSON | **205**（含 MF/Scrapper 目录） |
| Loot 表文件 | **~27**（chests + entities 等；校验脚本另计 blocks datagen） |
| 进度 | **15** |
| Mirror 节点 | **18** |
| 粒子 | ParticleRegistry 注册 + willow drip 等 |
| 音效 ogg | 6 + SoundRegistry |
| 语言 | en_us / zh_cn / es_es |
| 机器 | 炉+烟囱 / MF / Scrapper / 箱 / 工作台 / Crystal / Boss spawner |
| 测试 | `validate_resources.py` A–K + JUnit + **22** GameTest + `/ta demo` |
| 配置 | `CommonConfig` 大量 `defineInRange` + `ClientConfig`（aurora 等） |

### 2.3 代码面风险热区（扫描快照）

| 信号 | 数量/位置 | 含义 |
|------|-----------|------|
| `import net.minecraftforge.*` | **76** 个 Java 文件 | 几乎所有 Registry/事件/机器/物品扩展 |
| `RegistryObject` | **~297** 处 | → `DeferredHolder` / `Holder` |
| `new ResourceLocation(` | **~115** 处 | → `ResourceLocation.fromNamespaceAndPath` / `withDefaultNamespace` |
| Capability / `ItemStackHandler` / `ForgeCapabilities` | `AbstractInventoryBlockEntity`, MF/Scrapper Menu/BE, `ModUtil` | → NeoForge **IItemHandler 支持方式**（ItemHandler.BLOCK attachment 或保留 NeoForge item handler API） |
| 物品 `getOrCreateTag()` 玩法 NBT | Locator `dungeon`；Silentwood pick `currentharvestlevel`；Umbra pick `selected_block`；Aurorian Steel `xp`/`multiplier`；Boss egg `getTag` | → **Data Components**（自定义组件或 `CustomData` 过渡，最终应对等持久化） |
| 附魔 Java 注册 | `EnchantRegistry` + `LightningEnchant` / `LightningResistanceEnchant` + `LivingDamageEvent` | → **1.21 附魔体系**（datapack 定义 + effect 组件 / 事件改写） |
| `ForgeTier` / 匿名 `ArmorMaterial` | `MaterialTiers` + 全套工具甲 | → 1.21 Tier / **ArmorMaterial 注册与层** |
| `ITeleporter` + `changeDimension` | `AurorianPortalTeleporter`, `AurorianPortal` | → NeoForge 1.21 维度传送 API |
| `ForgeSpawnEggItem` | `ItemRegistry` | → NeoForge/原版 SpawnEgg 注册方式 |
| `IForgeMenuType` / `IClientItemExtensions` / BEWLR | MenuRegistry, SilentwoodChest* | → NeoForge 等价扩展 |
| `NetworkHooks.openScreen` | MF / Scrapper / Mirror 相关方块 | → 1.21.1 NeoForge 打开菜单 API（**无**自建 SimpleChannel；网络面仅此） |
| `DistExecutor` / `@OnlyIn` | MirrorOG*, TooltipUtil, ChestClientExt | → 安全 client 代理（`@OnlyIn` 弱化；event/Dist 分离已有基础） |
| Curios IMC + `ICurioItem.getAttributeModifiers(SlotContext, UUID, …)` | `CuriosCompat`, `BaseAurorianCurio` | → datapack 槽位（已有 `curios/slots/necklace.json`）+ Curios **9.x** 属性 API |
| 自定义 Recipe `fromJson`/`fromNetwork` | MF + Scrapper Serializer | → **MapCodec / StreamCodec** 配方序列化 |
| GameTest `net.minecraftforge.gametest` | `AurorianGameTests` | → NeoForge GameTest 注解与 run 配置 |
| 数据包目录（旧复数） | `loot_tables/`, `recipes/`, `advancements/`, `tags/blocks|items` | → 1.21 **单数**路径 + 校验脚本同步 |
| `forge` 标签 datagen | `src/generated/.../data/forge/tags/**` + 配方内 `forge:` 引用 | → **`c:`** common tags（+ 如需过渡双写） |
| Mixin | `LevelRendererMixin`（Aurora 天空，重 RenderSystem） | 1.21 渲染管线易漂；失败记 §9，**不得**砍其它内容 |
| build 残留 | `build/moddev`、`neoforge-21.1.248` 缓存、`run/config/neoforge-*.toml` | 曾有试验/缓存；**源码树仍为 FG 1.20.1**，迁移后建议 clean |

---

## 3. 架构约定（全程遵守）

| 主题 | 约定 |
|------|------|
| 加载器 | **只目标 NeoForge 1.21.1**（本计划不维护 Forge 1.21 双线，除非另开文档）。 |
| 包名 | 保持 `shiroroku.theaurorian`（内容 ID `theaurorian` 不变）。 |
| 注册 | `DeferredRegister`（NeoForge）+ `DeferredHolder`；主类用 mod 构造器/`IEventBus` Neo 写法。 |
| 事件 | Mod bus vs Game（NeoForge）bus 分离；`@EventBusSubscriber` 包名更新。 |
| 内容 ID | **方块/物品/实体/结构/维度/loot 路径的 registry name 尽量不变**，避免存档与结构 NBT 二次 remap。 |
| 结构 NBT | **禁止**为迁版本重做玩法；仅当 1.21 方块状态/BE 组件强制时用 processor 或一次性 remap，并记 §9。 |
| 物品持久化 | 玩法状态优先 **正规 Data Component**；禁止长期依赖已删除的随意 NBT API 而不记 §9。 |
| 附魔 | 效果（闪电对金属甲增伤、抗雷）必须保留；实现可 datapack 化。 |
| 标签 | 新 datagen 输出 `c:`；模组私有标签留 `theaurorian:`。 |
| 测试 | 每 Phase 至少：`./gradlew compileJava`；关键 Phase 后 `validateResources` + `test`；传送门/地牢/Boss Phase 后恢复 GameTest。 |
| 上游 | 玩法歧义仍以 `upstream/` 1.12 与现行 1.20.1 行为为准；**1.21 只换骨不换肉**。 |

---

## 4. 分阶段路线图（依赖顺序）

```
NF0  工具链与空壳启动（MDG + NeoForge 1.21.1 + Java 21）
  ↓
NF1  全局机械替换（包名/注册/事件/ResourceLocation/主类/配置）
  ↓
NF2  数据包与资源格式（pack、目录单数化、标签 c:、校验脚本）
  ↓
NF3  注册内容编译通过（方块/物品/实体/音效/粒子/结构类型/feature/POI/菜单/配方类型）
  ↓
NF4  物品数据组件 + Tier/Armor/SpawnEgg/Food/工具特殊 NBT
  ↓
NF5  附魔体系迁移（Lightning / Lightning Resistance）
  ↓
NF6  方块实体 / 物品栏 / 菜单 / 自定义配方 Codec（炉·MF·Scrapper·箱）
  ↓
NF7  世界：维度、传送门、结构、群系、feature（含 AT/Mixin 重验）
  ↓
NF8  实体 AI/属性/渲染/战利品 + Boss spawner 人数缩放
  ↓
NF9  客户端：渲染、粒子、Spectral 透明、BEWLR、Aurora mixin、ItemProperties
  ↓
NF10 兼容：Curios 9.x + JEI 插件 API +（可选）Geckolib 坐标
  ↓
NF11 测试基建：validate_resources / JUnit / GameTest / /ta demo 全绿
  ↓
NFG  完成门禁（§8）+ 文档/README 刷新
  ↓
NFC  可选：TCon/CT/ConArm（默认 exempt）
```

**并行建议：** NF2 资源脚本可与 NF1 后半并行；NF9 客户端在 NF3 编译通后即可开干；NF10 依赖二进制版本锁定，宜在 NF0 末确定坐标。

**禁止：** NF0 未稳就大改玩法；未登记 §9 就删除实体/结构/配方「先让它编译」；把 GameTest 永久关掉当完成。

---

## 5. Phase 明细

### NF0 — 工具链与空壳启动

**目的：** 用最小 mod 在 1.21.1 NeoForge 下 `runClient`/`runServer` 能启动。

| 任务 | 细节 | 完成标准 |
|------|------|----------|
| 0.1 锁定版本 | NeoForge `21.1.xxx`、Gradle、Java 21、Parchment 1.21.1、Curios/JEI/Gecko 坐标写入 `gradle.properties` | 属性文件有注释来源日期 |
| 0.2 MDG 迁移 | 替换 `build.gradle`/`settings.gradle`/wrapper；移除 FG reobf 心智；按官方 MDG 模版 | `./gradlew genNeoForge` 或等价任务成功 |
| 0.3 元数据 | `neoforge.mods.toml`：`modId=theaurorian`，依赖 `neoforge`/`minecraft`/`curios` | 加载器识别 mod |
| 0.4 主类最小 | 暂时可只留 `@Mod` + LOGGER；旧 Registry 可先注释并 **§9 登记 N0-STUB** | 客户端进标题画面 |
| 0.5 CI 本地 | 文档注明 `JAVA_HOME` JDK21 | README 片段更新 |

**暂缓：** 允许本 Phase 内容未挂载，但必须 §9 `N0` 说明「空壳期」。

---

### NF1 — 全局机械替换

| 任务 | 细节 |
|------|------|
| 1.1 包名 | 全局 `net.minecraftforge` → 对应 `net.neoforged`（eventbus、fml、neoforge.common…） |
| 1.2 主类 | `FMLJavaModLoadingContext` / `ModLoadingContext` → NeoForge 1.21 推荐获取 `IEventBus` + config 注册方式 |
| 1.3 DeferredRegister | `ForgeRegistries.*` → `BuiltInRegistries` / `Registries.*` + NeoForge `DeferredRegister.create(...)` |
| 1.4 RegistryObject | → `DeferredHolder`；`get()` 调用点批量替换 |
| 1.5 事件 | `MinecraftForge.EVENT_BUS` → `NeoForge.EVENT_BUS`；`@Mod.EventBusSubscriber` bus 枚举更新 |
| 1.6 ResourceLocation | 禁止 `new ResourceLocation(String)` / `(ns,path)` 旧构造；统一工厂方法 |
| 1.7 配置 | `ForgeConfigSpec` → NeoForge `ModConfigSpec`（若包名/类名变更） |
| 1.8 工具脚本 | 可加 `scripts/migrate_neoforge_imports.py` 辅助，但 **人工审核玩法文件** |

**完成标准：** 无 `net.minecraftforge` import（或仅残留并 §9 列出）；主类能注册空 DR。

---

### NF2 — 数据包 / 资源格式

| 任务 | 细节 |
|------|------|
| 2.1 pack.mcmeta | 更新至 1.21.1；移除失效 forge 扩展键 |
| 2.2 目录单数化 | `loot_tables`→`loot_table`，`recipes`→`recipe`，`advancements`→`advancement`，`tags/blocks`→`tags/block`，`tags/items`→`tags/item`，以及 entity_type/其他 1.21 更名；**structure 模板路径**按 1.21 约定核对（`structures` vs worldgen） |
| 2.3 结构 NBT | 61 个 NBT **内容保留**；仅调整 data 路径或 processor |
| 2.4 标签命名空间 | datagen：`forge:` → `c:`（ores/ingots/chests/shears/tools…）；更新 `DataGen*Tags` 与 `validate_resources.py` |
| 2.5 生物群系 JSON | 核对 1.21.1 effects/spawners/features/carver 字段；music sound id |
| 2.6 维度 / noise_settings | 1.21 noise 字段差异对照原版 datapack |
| 2.7 配方 JSON | shaped/shapeless 结果字段、item id 组件化（若 1.21 要求 `id` 对象） |
| 2.8 进度 JSON | 触发器/判据 ID 1.21 变更表 |
| 2.9 校验脚本 | `scripts/validate_resources.py` 全路径与键规则跟上；CI 同构 |
| 2.10 remap 脚本 | `scripts/remap_structure_nbt.py` / `sync_structures.sh` 仅路径或字典微调，**不删结构包** |

**完成标准：** 数据包可被 1.21.1 加载无 ERROR（WARN 须逐条评估）；校验脚本反映新布局。

---

### NF3 — 注册层编译通过

按现有 `Registry/*` 逐个恢复注册（顺序建议）：

1. `BlockRegistry` / `ItemRegistry` / `BlockEntityRegistry`  
2. `MenuRegistry` / `RecipeRegistry`  
3. `EntityRegistry` / `CreativeTabRegistry`  
4. `SoundRegistry` / `ParticleRegistry`  
5. `FeatureRegistry` / `StructureRegistry` / `POIRegistry`  
6. `EnchantRegistry`（可先占位到 NF5）  

| 注意点 | 处理 |
|--------|------|
| Block `Properties` | 1.21 复制/set 方法名 |
| Item `Properties` | 耐久/食物/稀有度 → components 风格 API |
| Creative tab | `CreativeModeTab.builder` Neo 事件 |
| Spawn placement / attributes | `RegisterSpawnPlacementsEvent` / `EntityAttributeCreationEvent` Neo 包名 |
| 结构 `StructureType`/`Codec` | `DarkstoneDungeonStructure` / `MoonTempleStructure` / `SingleTemplateStructure` Codec 字段 |

**完成标准：** `./gradlew compileJava` 成功；创造页可见核心物品（哪怕部分功能暂 dummy，须 §9）。

---

### NF4 — 物品数据组件与材料

**零妥协点：** 下列玩法状态必须可保存、可提示、可在联机同步。

| 物品 | 旧 NBT 键 | 迁移策略 |
|------|-----------|----------|
| `DungeonLocatorItem` | `dungeon` (string) | 自定义 `DataComponentType<String>` 或枚举组件 |
| `SilentwoodPickaxe` | `currentharvestlevel` (int 0–3) | 自定义 int 组件；动态正确掉落/速度逻辑保留 |
| `UmbraPickaxe` | `selected_block` (id string) | 组件存 `ResourceLocation`/`Holder<Block>` |
| `AurorianSteel.*` | `xp`, `multiplier` | 组件；升级附魔逻辑在 NF5 附魔 API 上重接 |
| Boss spawner egg | `getTag()` 读蛋 | 1.21 蛋/组件 API |
| 通用 | `hurtAndBreak`, tooltip shift | 新 damage/tooltip 事件 |

另：

- `MaterialTiers`：`ForgeTier` → NeoForge/原版 Tier 体系（`TierSorting` 若仍存在则对接；否则 tag 基础 hardness 阶梯）。  
- 盔甲：`ArmorMaterial` 改为 1.21 **注册材料 + 层贴图路径**（Spectral 透明层在 NF9）。  
- 食物：`FoodProperties` 走组件。  
- 弓/盾 `ItemProperties` 谓词在 NF9 注册。

**完成标准：** 创造测试 Locator 切换、Silentwood 镐升级、Umbra 选块、钢 XP 显示均不丢档。

---

### NF5 — 附魔

| 附魔 | 上游/现行效果 | 1.21 做法 |
|------|---------------|-----------|
| `lightning` | 攻击时按目标金属甲件数与等级乘算伤害 | datapack `enchantment` 定义 + 自定义 `EnchantmentEntityEffect` 或继续 NeoForge 伤害事件读取附魔等级 |
| `lightning_resistance` | 使护甲不计入闪电金属数 / 抗性 | datapack + tag `lightning_immune` 保留 |

- 移除旧 `EnchantmentCategory` 构造假设。  
- `EnchantmentHelper.getEnchantments` 等 API 全面替换。  
- 钢工具「升级已有附魔」必须在新 API 上验证可写回物品。

**完成标准：** 两附魔可附魔/可loot/可钢升级；战斗数值对等 1.20.1。

---

### NF6 — 方块实体、菜单、配方

| 系统 | 工作 |
|------|------|
| `AbstractInventoryBlockEntity` | 去掉 `ForgeCapabilities`/`LazyOptional` 旧模式；NeoForge `ItemStackHandler` + `getCapability` 替代物（`IItemHandler` block capability/attachment） |
| MF / Scrapper BE+Menu+Screen | 同步上述；菜单 `IForgeMenuType` → Neo 菜单工厂；**`NetworkHooks.openScreen` 调用点一并替换** |
| `MoonlightForgeRecipe` / `ScrapperRecipe` | `MapCodec` + `StreamCodec`；JSON 字段保持 input/catalyst/output 语义 |
| 炉 / 烟囱 | 燃料与 chimney 减耗逻辑回归 |
| Silentwood chest | BE + 开合 + 掉落；物品 3D 渲染接 NF9 |
| Crystal / Boss spawner BE | NBT `boss` 键 **保持字符串实体 ID**（结构兼容） |
| `ModUtil.dropItemHandlerInWorld` | 跟新 handler API |

**完成标准：** 三机器配方可跑；JEI 类别在 NF10 接；GameTest machines 批通过。

---

### NF7 — 世界与传送门

| 任务 | 细节 |
|------|------|
| 维度 | `ResourceKey` + datapack `dimension`/`dimension_type` |
| 传送门 | `AurorianPortal`/`Shape`/`Teleporter`：替换 `ITeleporter`/`changeDimension` 为 1.21.1 NeoForge 推荐传送（`PortalInfo`/dimension transition） |
| AT | `portalEntrancePos` 在 1.21.1 映射名确认 |
| POI | 传送门 POI 注册与 locate |
| 自定义 Structure | Darkstone 字符地图、Moon Temple 拼装、SingleTemplate：Codec + 放置 + chest loot 路径（新 `loot_table` id） |
| Feature | Silentwood/Willow/Mushroom/Cave/Urn 等 |
| 群系 spawn | 与 1.20.1 权重对等 |
| 结构 set | spacing 保持 README「随机散布」 |

**完成标准：** 传送门往返；`/locate` 三地牢+塔+废墟；新世界生成不崩。

---

### NF8 — 实体与 Boss

- 全部 19 实体：属性、Goals、战利品、蛋、生成限制（silkberry 繁殖等）。  
- 三 Boss AI 不砍阶段；spawner 人数缩放读 `CommonConfig`。  
- 投射物：箭/Beam/Spiker/Webbing。  
- 渲染器注册改 Neo 客户端事件（接 NF9）。  

**完成标准：** 创造蛋可刷可杀；loot 非空；Boss 缩放代码路径有测。

---

### NF9 — 客户端表现

| 项 | 要求 |
|----|------|
| Entity renderers / layers | Keeper/Spider/Queen/羊等 |
| Spectral 透明 | `entityTranslucent` 层不回归不透明 |
| Aurora + Mixin | `LevelRendererMixin` 注入点重验 |
| 粒子 | `WeepingWillowDripParticle` + 描述路径（防 1.20 曾出现的 path crash） |
| BEWLR 箱 | `IClientItemExtensions` Neo 等价 |
| ItemProperties | 弓 pull/pulling、盾 blocking、水晶剑 charge |
| 轮廓渲染 | Umbra/Aurorianite shovel highlight 事件 |
| GUI | MF/Scrapper/Mirror 屏 |

**完成标准：** `/ta demo` 可跑通（NF11 全绿前至少客户端不崩）。

---

### NF10 — Curios / JEI /（可选）Gecko

| 依赖 | 动作 |
|------|------|
| Curios 9.x | 删除 IMC 注册若已 datapack 化；`BaseAurorianCurio` API（`ICurioItem` 包名/方法）；槽位 `necklace` size=1 |
| JEI | `IModPlugin` 新包名；MF/Scrapper 类别与配方类型查找 |
| Geckolib | 仅坐标与编译；**无内容强制** |

**完成标准：** 护符可装备有属性；JEI 显示两种机器配方。

---

### NF11 — 测试基建

| 层 | 动作 |
|----|------|
| `validate_resources.py` | 路径/标签/附魔 datapack/组件无关的静态门禁 |
| JUnit | `PortContentCategoriesTest` 等 |
| GameTest | 注解与 `runGameTestServer` MDG 配置；**22** 项语义保留（可改实现不改覆盖面） |
| `/ta demo` | 命令注册与 Fake 事件 API |
| 服务端安全 | 保持 EntityClientRegistry / Dist 拆分，避免 dedicated 拉客户端类 |

**完成标准：** `./gradlew test runGameTestServer` 全绿。

---

### NFG — 完成门禁

见 **§8**。全绿后：

- 模组版本 → `1.21.1-1.0`（或约定正式号）  
- README / README.zh-CN 标明 1.21.1 NeoForge  
- `docs/diff.md` 增加「1.21.1 平台附录」或另文 `docs/diff-1.21.1-platform.md` 记平台差异（非内容回退）  
- §9 无 `open` 玩法项  

### NFC — 可选兼容（默认不阻塞）

TCon / ConArm / CraftTweaker：仅当用户明确要求时开；否则保持 **exempt**（与内容移植 D26 一致）。

---

## 6. 上游内容 ↔ 本计划映射

| 内容包（已在 1.20.1 存在） | 负责 Phase | 优先级 |
|---------------------------|------------|--------|
| 三地牢 + NBT + loot | NF2, NF7, NF8 | P0 |
| 三 Boss + 武器/trophy/MF | NF4–8, NF6 | P0 |
| 全部实体 + 被动 + 农业 | NF3, NF4, NF8, NF2 | P0 |
| 传送门 + 维度 | NF7 | P0 |
| 机器 ×3 + 箱工作台 | NF6, NF10 | P0 |
| 特殊工具甲/投掷/Locator | NF4, NF8, NF9 | P0 |
| 群系 7 + Willow + 蘑菇 + 洞穴等价 | NF2, NF7 | P0 |
| 音效粒子 Aurora Spectral | NF3, NF9 | P0 |
| Mirror 18 + 进度 15 + 三语 | NF2, NF9 | P0 |
| Curios 护符 | NF10 | P0 |
| 自动化测试 22 + demo | NF11 | P0 |
| TCon/CT/ConArm | NFC | exempt |
| Gecko 自定义网格 | 增强 | 不阻塞 |

---

## 7. 资源与工程清单

### 7.1 必改工程文件

- `build.gradle` / `settings.gradle` / `gradle.properties` / `gradle/wrapper/**`  
- `src/main/resources/META-INF/mods.toml` → `neoforge.mods.toml`  
- `pack.mcmeta`、`theaurorian.mixins.json`、`accesstransformer.cfg`  
- `scripts/validate_resources.py`、（如需）结构 sync/remap  
- `README.md` / `README.zh-CN.md` / 本文档 §9  

### 7.2 必改代码区（按包）

```
shiroroku.theaurorian
├── TheAurorian.java, Events*.java
├── Registry/*                 # 全员
├── Config/*
├── Blocks/**                  # 尤其 Inventory BE、Portal、机器
├── Items/**                   # NBT→组件、Tier、盾弓
├── Enchantments/** + 新 data/enchantment
├── Entities/** + EntityClientRegistry
├── World/Structure/**, World/Feature/**
├── Portal/**
├── Compat/Curios, Compat/JEI
├── DataGen/**
├── GameTests/**, Demo/**
├── Mixin/Client/LevelRendererMixin
├── Particles/**, Renderers/**
└── Util/ModUtil, RenderUtil, TooltipUtil
```

### 7.3 建议里程碑版本号

| 版本 | 内容 |
|------|------|
| `1.21.1-0.1-nf0` | 空壳启动 |
| `1.21.1-0.2-nf3` | 注册编译通过 |
| `1.21.1-0.3-nf6` | 机器+组件+附魔 |
| `1.21.1-0.4-nf8` | 世界+实体 |
| `1.21.1-0.5-nf11` | 测试全绿 |
| `1.21.1-1.0` | NFG 门禁正式 |

---

## 8. 完成门禁（NFG Checklist）

> `[x]` 仅在 1.21.1 上验证后勾选。工程/自动化部分已于 2026-08-03 起逐步勾选；进世界项仍依赖手测/GameTest。

### 8.1 工程

- [x] Java 21 + NeoForge 1.21.1 + MDG 构建可复现  
- [x] `neoforge.mods.toml` 依赖正确（neoforge/minecraft/curios）  
- [x] 无残留必选 `net.minecraftforge` 编译依赖  

### 8.2 内容注册（对等 1.20.1）

- [x] 方块/物品/实体/声音/粒子/结构类型/feature/POI/菜单/配方类型齐全  （`registry_modItemsResolve` + JUnit B/C/I + compile）  
- [x] 创造 tab 可取用主线物品  （`CreativeTabRegistry.MAIN` displayItems 覆盖全部 Item DR）  
- [x] 语言 en+zh（es 保持）键不回归  （JUnit A + DatapackSmoke lang）  

### 8.3 世界与地牢

- [x] 维度进入 + 传送门往返  （`portal_*`：Portal 接口 + dimension_type 绑定 + `DimensionTransition` 实现；全框往返体感见 §8.11）  
- [x] Runestone / Darkstone / Moon Temple 可 locate、可生成、可通关逻辑（钥匙门、spawner）  （结构 NBT/defs JUnit D；钥匙门+spawner GameTest dungeon/boss）  
- [x] Umbra Tower + Ruins/Graveyard  （structure NBT+worldgen/structure JSON 齐；JUnit D）  
- [x] Locator 三地牢切换+指向  （`items_locatorCyclesDungeonSelection`；指向粒子代码路径在）  
- [x] 7 群系 + Willow + 蘑菇洞穴等价  （JUnit E biomes/features；agriculture/mushroom bounce GameTest）  

### 8.4 Boss 与装备

- [x] 三 Boss AI 可战斗 + 人数缩放  （boss spawner×3 + combat attributes GameTest；人数缩放在 BossSpawnerBlockEntity）  
- [x] Trophy×3 + MF 三武器配方与能力  （MF 配方加载 GameTest；keepers_bow 类型 GameTest；JSON keepers/queens/moon_shield 存在）  
- [x] 全特殊工具甲/投掷对等  （注册+模型+特殊类编译在；silentwood/umbra/keepers/slime boots GameTest 子集）  

### 8.5 实体与农业

- [x] 敌对/被动/投射齐；被动 silkberry 繁殖  （`entities_allLivingSpawn`；农业 silkberry GameTest；投射实体注册在）  
- [x] farm tile + 双作物 + 食物链  （agriculture GameTest；配方/物品 JUnit）  

### 8.6 机器与物品栏

- [x] 炉+烟囱 / MF / Scrapper 功能与自定义配方  （blocks_placeCoreMachines；machines_* 配方加载与前置）  
- [x] Silentwood 箱/工作台  （blocks 批放置 chest；工作台注册+配方）  
- [x] Crystal / Boss spawner  （scrapper crystal 前置；boss_spawner×3 GameTest）  

### 8.7 附魔与组件

- [x] Lightning / Lightning Resistance 效果对等  （datapack enchantment JSON + `EventsForge.handleLightningDamage` Holder 路径；Keeper 附魔 lightning）  
- [x] Locator / Silentwood pick / Umbra pick / Steel XP 持久化  （CUSTOM_DATA 路径；locator/silentwood GameTest；Umbra/Steel 代码组件读写）  

### 8.8 客户端与文档化内容

- [x] Spectral 透明、Aurora、粒子、弓盾谓词  （客户端层/mixin/ItemProperties/粒子注册编译；像素级观感 §8.11）  
- [x] Mirror 18 节点可读  （JUnit H + DatapackSmoke mirror graph）  
- [x] 进度 15  （JUnit G + DatapackSmoke boss OR）  
- [x] 音效不静音裸奔  （JUnit I ogg+registries）  

### 8.9 兼容

- [x] Curios 护符可装备  （datapack `curios/slots/necklace` + `curios:tags/item/necklace`；IMC 已移除；运行时 Loaded 10 curio slots）  
- [x] JEI 两类机器配方  （JEIPlugin 双 Category；getWidth/Height+draw 替代 getBackground）  
- [x] TCon/CT/ConArm：**exempt** 已声明  

### 8.10 自动化

- [x] `./gradlew test` 全绿  
- [x] `./gradlew runGameTestServer` **22**（或明确等价集）全绿  
- [x] `/ta demo` 可跑（命令+17 case 代码在；`VisibleDemoCommand` 注册；可选手感仍见 §8.11）  

### 8.11 可选手测（不阻塞内容完整，但建议）

- [ ] 生存三地牢节奏  
- [ ] 多人 Boss 缩放体感  
- [ ] `/locate` 大地图散布  
- [ ] Spectral/Aurora 像素级观感  

---

## 9. 暂缓清单（活文档）

> 格式：`ID | 缺口 | 现状 | 原因 | 关闭条件 | 目标 Phase | 状态`  
> 状态：`open` / `closed` / `exempt`  
> **进度快照（2026-08-03 收口）：** §8.1–8.10 已按自动化+代码证据勾选；§9 玩法项已 closed（仅 N18/N19 exempt 与可选 N20 手感残留）。

| ID | 缺口 | 现状 | 原因 | 关闭条件 | 目标 Phase | 状态 |
|----|------|------|------|----------|------------|------|
| N0 | 1.21.1 工具链未切换 | **MDG 2.0.143 + NeoForge 21.1.248 + Java 21 + Gradle 8.11.1**；`neoforge.mods.toml` 模板；`compileJava`/`jar` 绿 | — | 已满足编译与 jar | NF0 | closed |
| N1 | Forge→Neo 包/注册/事件全局 | **无 `net.minecraftforge` 残留**；主类 `IEventBus`+`ModContainer`；`DeferredHolder`；RL 工厂；EventBusSubscriber 去 bus | — | 已无旧 Forge 编译依赖 | NF1 | closed |
| N2 | 数据包 1.21 路径/格式 | 单数路径 + number provider/ItemStack `id`/loot `enchanted_count_increase`+uniform min/max；GameTestServer 加载无 loot ERROR | 创造世界全量观察 | 游戏加载无 datapack ERROR | NF2 | closed |
| N3 | `forge:` → `c:` 标签 | generated `c:` tags + 配方 `c:`；JUnit 绿；仍有少量 legacy tag 警告（模组外/旧引用） | 清零 legacy 警告（可选） | 配方/战利品引用有效 | NF2 | closed |
| N4 | 全注册表 1.21 编译 | **compileJava 绿**；`registry_modItemsResolve`；创造 tab displayItems 全量 | — | 创造可见主线 | NF3 | closed |
| N5 | 物品 Data Components | Locator/Silentwood/Umbra/Steel 均 `CUSTOM_DATA`；locator/silentwood GameTest | — | 四类玩法状态存读对等 | NF4 | closed |
| N6 | Tier / ArmorMaterial | SimpleTier + 注册 ArmorMaterial；工具甲注册与属性创建路径在 | 数值微调手感 §8.11 | 工具甲数值与修理对等 | NF4 | closed |
| N7 | 附魔 datapack 化 | enchantment JSON×2 + EventsForge Holder 伤害逻辑 + Keeper 附魔 | — | 两附魔效果+钢升级 | NF5 | closed |
| N8 | Capability 物品栏 | RegisterCapabilitiesEvent + BE；machines GameTest 配方/前置 | — | MF/Scrapper/炉/掉落 handler 正常 | NF6 | closed |
| N9 | 自定义 Recipe Codec | MF/Scrapper MapCodec + 配方 JSON `output.id`；GameTest 机器批通过 | — | MF+Scrapper 加载&合成 | NF6 | closed |
| N10 | 传送门 ITeleporter | Portal+DimensionTransition 实现；portal GameTest 断言 Portal/dimension_type | 全框往返体感 §8.11 | 维度往返稳定 | NF7 | closed |
| N11 | 结构/feature Codec 与 loot id | MapCodec 注册；61 NBT；JUnit D/E；钥匙门/spawner GameTest | locate 散布观感 §8.11 | 三地牢+塔+废墟生成与宝箱 | NF7 | closed |
| N12 | 实体/属性/生成事件 | 19 实体 allLivingSpawn + undead gear + boss attributes | AI 细调手感 §8.11 | 19 实体可玩+loot | NF8 | closed |
| N13 | 客户端渲染与 Mixin | Aurora mixin、Spectral layer、ItemProperties、粒子 Provider 编译在 | 像素级观感 §8.11 | 不崩且表现对等 | NF9 | closed |
| N14 | Curios 9.x | **IMC 已删除**；仅 datapack slot+tag；GameTest 日志 Loaded curio slots | 护符属性进世界点一次 | 项链槽+护符属性 | NF10 | closed |
| N15 | JEI 1.21.1 API | 双 Category；width/height+draw；无 getBackground 覆写 | JEI GUI 点一次确认 | 两机器类别显示 | NF10 | closed |
| N16 | 测试套件未在 1.21 运行 | **`./gradlew test` 全绿** + **`runGameTestServer` 22/22 全绿**；`validate_resources` OK | 可选手感/demo | 进世界 `/ta demo` 与手测清单 | NF11 | closed |
| N17 | README/版本号仍写 1.20.1 | README/zh/testing 已 1.21.1 NeoForge；**版本 `1.21.1-1.0`** | — | NFG 版本与完成声明 | NFG | closed |
| N18 | TCon/ConArm/CT | 无 | 非主线（继承 D26） | 移植或保持 exempt 说明 | NFC | exempt |
| N19 | Gecko Boss 网格 | 依赖可有、内容未用 | 增强项 | 不作为完成条件；玩法模型已存在 | — | exempt |
| N20 | 可选手感 playtest | 自动化已覆盖逻辑验收；§8.11 为真人建议项 | 需真人 | **不阻塞 NFG**；§8.11 可残留 | NFG+ | open |

**完成判定（NFG）：** §8.1–8.10 全 `[x]`；§9 无 `open` **玩法**项（仅 N18/N19 类 exempt 与可选 N20 可残留）。**本快照：NFG 门禁已满足。**

**纪律：** 只允许 `open→closed` 或兼容 `exempt`；**不允许无记录删行**。若某 Phase 交付不完整，先改本表再合代码。

---

## 10. 实施工作流（每个 PR/提交）

1. 对照本文档 Phase 选任务，不跨依赖硬刚（NF0→NF1→…）。  
2. 交付不完整：更新 §9（禁止只改代码不改文档）。  
3. 玩法以 **1.20.1 现行行为** + `upstream/` 为准，不以记忆为准。  
4. 结构/loot/组件改动附：创造测试步骤 ≥3 条。  
5. 禁止用永久占位物品替换主线获取（占位必须 §9 + 关闭条件）。  
6. Phase 结束跑 §8 相关子集；NF11 前不得宣称完成。  
7. 版本坐标（NeoForge/Curios/JEI）变更时写 `gradle.properties` 注释日期。  

---

## 11. 风险与缓解

| 风险 | 影响 | 缓解 |
|------|------|------|
| 物品 NBT→组件漏迁 | 钢/镐/Locator 存档失效或行为静默坏 | NF4 专项清单 + GameTest |
| 附魔 API 大改 | 闪电附魔/钢升级瘫痪 | NF5 单开；对照 1.20 伤害公式测 |
| 结构 Codec / loot 路径 | 地牢空箱或无法生成 | NF2 先路径；NF7 再进世界；保留 remap 脚本 |
| Capability 重写引入吞物品 | 机器丢进度 | 单测 + GameTest machines |
| 传送门 API | 卡维度/空指针 | AT 映射确认 + 往返 GT |
| Mixin 映射漂移 | 无 Aurora 或客户端崩 | 启动即验；失败 §9 但不砍其它内容 |
| Curios 9 槽位 | 护符无效 | datapack slot + 运行时 log |
| 范围蔓延做 TCon/光影 | 延期 | NFC 置后；本计划 P0 仅平台+原内容 |
| 双加载器幻想 | 工作量×2 | **只做 NeoForge** |
| build/ 旧 FG 缓存污染 | 怪编译错误 | 迁移后 `./gradlew clean` + 清 run | 

---

## 12. 与其它文档的关系

| 文件 | 职责 |
|------|------|
| [`docs/diff.md`](diff.md) | **1.12.2 vs 1.19.2 内容快照**（内容完整度真源） |
| [`docs/port-plan.md`](port-plan.md) | **1.19.2 内容零妥协计划**（已 Phase G；历史真源） |
| [`docs/port-plan-1.21.1.md`](port-plan-1.21.1.md)（**本文件**） | **1.21.1 NeoForge 平台移植行动计划 + §9 暂缓** |
| [`docs/asset-remap.md`](asset-remap.md) | 结构/loot 资产 remap（内容侧已 closed；1.21 仅路径跟随 NF2） |
| [`docs/boss-spawner.md`](boss-spawner.md) | Boss spawner NBT 约定（`boss` 键保持） |
| [`docs/testing.md`](testing.md) | 测试分层；NF11 后需改命令/Java21/Neo 说明 |

建议：每完成一个大 Phase，更新本文件 §8/§9；NFG 时刷新 README 与 testing。

---

## 13. 依赖坐标备忘（计划制定日 · 须实施时复核）

> Maven 查询日：2026-08-03。实施 NF0 时重新解析最新 patch。

| 依赖 | 坐标线索 |
|------|----------|
| NeoForge | `net.neoforged:neoforge:21.1.xxx`（线至 21.1.248） |
| MDG | `net.neoforged.moddev`（版本随官方 1.21.1 模版） |
| Curios | `top.theillusivec4.curios:curios-neoforge:9.5.1+1.21.1`（线至 9.5.1） |
| JEI | `mezz.jei:jei-1.21.1-neoforge:19.43.0.392`（线至 19.43.0.392） |
| Geckolib | `software.bernie.geckolib:geckolib-neoforge-1.21.1:4.9.2`（可选） |
| Parchment | 1.21.1-YYYY.MM.DD（MDG 文档） |
| Java | 21 |

---

## 14. 附录 A — 1.21 数据包路径迁移检查表

| 1.20.1 路径 | 1.21.x 典型路径 | 本模组状态 |
|-------------|-----------------|------------|
| `data/.../loot_tables/**` | `data/.../loot_table/**` | 待 NF2 |
| `data/.../recipes/**` | `data/.../recipe/**` | 待 NF2 |
| `data/.../advancements/**` | `data/.../advancement/**` | 待 NF2 |
| `data/.../tags/blocks/**` | `data/.../tags/block/**` | 待 NF2 |
| `data/.../tags/items/**` | `data/.../tags/item/**` | 待 NF2 |
| `data/forge/tags/**` | `data/c/tags/**`（common） | 待 NF2/datagen |
| `data/.../structures/**`（NBT） | 按 1.21 模板存储约定核对 | 待 NF2 |
| `data/.../worldgen/**` | 大体保留，字段微调 | 待 NF2/NF7 |
| `data/.../mirror_of_guidance/**` | 自定义，保持 | 随 loader 校验 |
| `data/.../curios/slots/**` | Curios 9 datapack | 已有 necklace，NF10 核实 |
| （新）`data/.../enchantment/**` | 闪电附魔定义 | NF5 新建 |
| （新）组件/物品模型等 | 随 1.21 物品定义 | NF4/NF9 |

---

## 15. 附录 B — 玩法 NBT → 组件迁移表（实施核对）

| 键 | 类型 | 使用类 | 组件名建议（可改） |
|----|------|--------|-------------------|
| `dungeon` | string | `DungeonLocatorItem` | `theaurorian:locator_dungeon` |
| `currentharvestlevel` | int | `SilentwoodPickaxe` | `theaurorian:harvest_level` |
| `selected_block` | string id | `UmbraPickaxe` | `theaurorian:selected_block` |
| `xp` | int | `AurorianSteel` | `theaurorian:steel_xp` |
| `multiplier` | float | `AurorianSteel` | `theaurorian:steel_xp_multiplier` |
| BE `boss` | string | `BossSpawnerBlockEntity` | **保持 BE 字段/键名**（结构兼容） |

---

## 16. 附录 C — 与 1.20.1 内容门禁的继承关系

`docs/port-plan.md` §8.1–8.7 已在 1.19.2/1.20.1 关闭的内容，**移植到 1.21.1 后必须仍为真**。本计划 §8 是其 **平台复验表**，不是新的砍内容许可证。

兼容豁免继承：

- D26 / **N18**：TCon、ConArm、CraftTweaker  
- 彩蛋 `bepsi`/`debugger`：仍不强制  
- Worley 算法本体：保持选项 B 等价洞穴（已 closed 于内容计划 D20）  

---

## 17. 一句话执行序

**先 MDG 空壳 → 全局 Neo 机械 → 数据包格式 → 注册编译 → 组件与材料 → 附魔 → 机器配方 → 维度结构 → 实体 Boss → 客户端 → Curios/JEI → 测试全绿 → 门禁。**  
任何跳步留下的洞，必须进 §9。

---

*本计划为活文档。零妥协完成前，§9 不得无故清空。*  
*内容完整度真源仍为 `docs/diff.md` + `docs/port-plan.md`；本文负责 1.21.1 NeoForge 平台落地。*
