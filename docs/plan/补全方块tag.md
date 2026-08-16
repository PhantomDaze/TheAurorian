# 为 mod 方块/物品补全合适的 tag

> 状态：**已完成**（2026-08-16）
> 日期：2026-08-16
> MC 版本：1.20.1 (forge 47.4.10)

## 背景

mod 现有 tag 极少，且项目实际有完整 datagen（`DataGenBlocksTags`/`DataGenItemsTags`），但 provider 覆盖不全，许多方块/物品缺标准 tag。

## 完成的工作

通过补充两个 datagen provider（而非手写静态 JSON，因项目用 datagen 生成 `src/generated/resources`）：

### DataGenBlocksTags.java 新增
- `mineable/hoe`：silentwood_leaves、weeping_willow_leaves（原版树叶用锄）
- `small_flowers`：petunia、lavender_block（蜜蜂/剑/花盆判定）
- `minecraft:dirt`：补 aurorian_grass_light
- `minecraft:sand`：moon_sand
- `minecraft:logs`：通过 `addTag(LOGS_THAT_BURN)` 链接（让 logs 父 tag 生效）
- `mineable/axe`：补 silentwood_sapling/torch、weeping_willow_sapling、mushroom/mushroom_stem/mushroom_small
- `mineable/pickaxe`：补 crystal、mushroom_crystal、urn
- `mineable/shovel`：补 aurorian_grass_light
- `needs_stone_tool`：补 deepslate_cerulean_ore、deepslate_moonstone_ore
- `stairs`：补 darkstone_stairs、moon_temple_stairs
- `fences`：补 silentwood_fence（原仅有 wooden_fences）
- forge block tags：`glass`、`glass_panes`、`sand`、`ores/cerulean`、`ores/moonstone`、`ores_in_ground/{stone,deepslate}`、`storage_blocks/{aurorian_steel,cerulean,moonstone,coal}`、`cobblestone/normal`，并让 `forge:ores`/`forge:storage_blocks` 父 tag 通过子 tag 引用

### DataGenItemsTags.java 新增
- 木制品 item 衍生（用 `copy` 从 block tag）：logs/logs_that_burn/planks/slabs/stairs/walls/fences/wooden_fences/wooden_slabs/wooden_stairs/leaves/saplings/flowers/small_flowers/sand/dirt —— **此前 item 侧完全缺失**
- forge 子 tag：`ingots/{aurorian_steel,aurorianite,cerulean,crystalline,moonstone,umbra}`、`nuggets/{aurorian_steel,cerulean,moonstone}`、`storage_blocks/{aurorian_steel,cerulean,moonstone,coal}`、`ores/{cerulean,moonstone}`、`ores_in_ground/{stone,deepslate}`、`coal`、`seeds/{lavender,silkberry}`、`crops/{lavender,silkberry}`
- 父 tag 通过子 tag 引用（ingots/nuggets/storage_blocks/ores），符合 forge 约定

### 删除的手写文件
- `main/resources/data/forge/tags/items/ingots|nuggets|storage_blocks/{cerulean,moonstone,aurorian_steel}.json`（9 个，改由 datagen 生成）
- `main/resources/data/minecraft/tags/blocks/climbable.json`（datagen 已生成）

## 验证
- ✅ `compileJava` 通过（仅原有 deprecation 警告）
- ✅ `runData` 成功，生成 65 个文件（新增约 58 个 tag 文件）
- ✅ 所有生成 tag JSON 格式有效
- ✅ 抽查归类正确（mineable/needs_stone_tool/logs/planks/ingots 父子链等）

## 遗留问题（非本次任务，需单独处理）

### processResources duplicate（预存，阻塞完整 build）
- `src/main/resources/assets/theaurorian/models/item/crystalline_sprite.json`（手动维护，"Match Crystalline Sprite rendering to upstream"）与 `src/generated/resources/.../crystalline_sprite.json`（datagen 生成）同名，processResources 重跑时报 duplicate。
- 两文件内容仅末尾换行差异。
- 长期潜伏（processResources UP-TO-DATE 时不触发），任何 main/resources 改动都会暴露。
- **建议**：给 `build.gradle` 的 `processResources` 块加 `duplicatesStrategy = DuplicatesStrategy.EXCLUDE`，或删除 main 的手写版本（若 generated 版本已满足渲染需求，需验证）。
- 本次 runData 用 `-x processResources` 绕过验证，datagen 本身不受影响。

## 不确定项（已决策）
- **moonstone 当 ingot**：历史已设为 forge:ingots/moonstone，保持，未加 gems（避免重复归类）。
- **aurorian_deepslate 不算 cobbled deepslate**：跳过 forge:cobblestone/deepslate。
- **geode 加进 needs_stone_tool**：原 provider 已加，保持（虽 geode copy IRON_ORE，但作为水晶矿需石镐合理）。

---

## 追加任务（2026-08-16）：掉落物去重

用户要求：树叶掉落物应像其他树叶一样可能掉木棍和树苗/不掉；剪刀原样采集树叶和草。

### 修改（DataGenBlocksLoot.java）
1. **weeping_willow_leaves**：原实现无条件掉自身（任何工具都掉），且第二池掉 sap 但缺剪刀/精准条件。改为与 silentwood_leaves 一致：
   - 剪刀/精准采集 → 原样掉 `weeping_willow_leaves`
   - 否则 → 概率掉 `weeping_willow_sap`（fortune 0.05-0.1）+ 概率掉 `silentwood_stick`×1-2（fortune 0.02-0.1），可能两者都不掉
2. **aurorian_tallgrass / aurorian_tallgrass_light**：新增 `dropGrassLike` 辅助方法。
   - 剪刀/精准采集 → 原样掉草自身（此前掉 plant_fiber）
   - 否则 → 12.5% 概率掉 `plant_fiber`（带 fortune uniform_bonus_count 2，与原版 grass 一致）
3. **bright_bulb / lavender_block / petunia / silkberry_block**：保持剪刀掉自身/作物的现有行为不变。

### 关键点
- 使用 `createSelfDropDispatchTable`（weeping_willow），与原版树叶掉落逻辑一致。
- `ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 2)`（parchment 映射名，非 `uniformBonusCount`）。
- 查询 parchment 映射确认方法名：`/tmp/parchdata/parchment.json`。

### 验证
- ✅ compileJava 通过
- ✅ runData 生成，3 个 loot 文件更新（weeping_willow_leaves, aurorian_tallgrass, aurorian_tallgrass_light）
- ✅ 生成的 JSON 结构正确（树叶 alternatives + 木棍池；草 two-pool shears/silk + 概率纤维）

---

## 追加任务（2026-08-16）：地牢定位器修复

审计 DungeonLocatorItem 发现两个 bug，已修复：

### Bug 1：方向粒子死代码（已修）
- **原状**：`use()` 在 `!isClientSide`（服务端）块内调用 `spawnDirectionParticles`，而该方法开头 `if (!level.isClientSide) return;` —— 服务端立即返回，粒子永不生成。玩家右键定位器只听到音效、看到耐久消耗，没有任何方向视觉提示。
- **修复**：新增网络通道 `Network/LocatorNetwork.java`（SimpleChannel），服务端检测到结构后通过 `PacketDistributor.PLAYER` 把地牢 BlockPos 发给使用者客户端；客户端在 `Items/DungeonLocatorClient.java`（@OnlyIn(CLIENT)）生成 CLOUD 方向粒子（复刻原逻辑）。在 `TheAurorian` 构造时 `LocatorNetwork.register()` 注册通道。

### Bug 2：searchRadius 单位错误（已修）
- **原状**：`searchRadius()` 返回 `density*4*16`（如 Darkstone=384），但 `findNearestMapStructure` 的 radius 参数单位是「结构 spacing 格点的环状搜索最大半径」，不是 block。乘 16 后搜索范围比预期大 16 倍（≈12 万 chunk），单位语义错误、范围失控。
- **修复**：去掉 `*16`，改为格点单位（Darkstone 6 / Moontemple 4 / Runestone 2）。配合 structure_set spacing=32 chunk，覆盖 2×512~6×512 block 范围，足够找到附近地牢。

### 新增文件
- `Network/LocatorNetwork.java`：SimpleChannel + S2C `DungeonDirectionMessage(BlockPos)`
- `Items/DungeonLocatorClient.java`：客户端粒子（@OnlyIn(CLIENT)，避免服务端类加载 Minecraft）

### 修改文件
- `Items/DungeonLocatorItem.java`：删除死代码 `spawnDirectionParticles`，改为发网络包；修正 `searchRadius` 单位；清理无用 import（BlockPos/ParticleTypes/Mth）
- `TheAurorian.java`：注册 `LocatorNetwork`

### 验证
- ✅ compileJava 通过
- ✅ runData 通过（-x processResources 绕过预存 crystalline_sprite duplicate，与本次无关）
- ⚠️ 完整 `build` 仍因预存的 crystalline_sprite.json main/generated 重复而失败（非本次引入，待单独处理）
