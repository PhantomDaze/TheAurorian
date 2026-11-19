# 运行时缺陷修复：羊战利品表 + 结构 NBT 属性类型

状态：**已完成**（2026-11-19）
目标树：`/opt/DEV/MDEV/minecraft-mods/aurorian/TheAurorian-1.19.2`
环境：MC 1.19.2 / Forge 43.4.6 / JDK 17 (`/usr/lib/jvm/zulu-17`)

## 缺陷 1：aurorian_sheep 战利品表整表解析失败

### 根因（已从 1.19.2 映射源码确认）

`net.minecraft.advancements.critereon.EntitySubPredicate.Types.TYPES` 在 1.19.2 只有：
`any / lightning / fishing_hook / player / slime / cat / frog`（无 `sheep`）。
原表 16 处 `type_specific.type = minecraft:sheep`，反序列化直接抛
`JsonSyntaxException: Unknown sub-predicate type: minecraft:sheep`，整表注册失败。

### 选定方案：`nbt` 谓词（保留按颜色分流）

- `EntityPredicate` 在 1.19.2 **确实有 `nbt` 字段**，`NbtPredicate.fromJson`
  用 `TagParser.parseTag` 解析 JSON 字符串形式的 SNBT；
  `matches(Entity)` 对 `entity.saveWithoutId(new CompoundTag())` 做部分匹配。
- `Sheep.addAdditionalSaveData` 恒写 `"Color"` 为 byte（`getColor().getId()`），
  顺序与 DyeColor 一致（white=0 … black=15），因此 `{Color:<id>b}` 等价于原颜色条件。
- 未采用“简化成只掉白羊毛”的退路：1.19.2 具备 `nbt` 能力，保留原设计更忠实。

### 改动（16 处，逐行）

`src/main/resources/data/theaurorian/loot_tables/entities/aurorian_sheep.json`
（行号为改后；改前行号为 49/55/…/139）

| 行 | 颜色 | 改前 predicate | 改后 predicate |
|---|---|---|---|
| 51 | white | `{"type_specific":{"type":"minecraft:sheep","color":"white"}}` | `{"nbt":"{Color:0b}"}` |
| 57 | orange | …`orange` | `{"nbt":"{Color:1b}"}` |
| 63 | magenta | …`magenta` | `{"nbt":"{Color:2b}"}` |
| 69 | light_blue | …`light_blue` | `{"nbt":"{Color:3b}"}` |
| 75 | yellow | …`yellow` | `{"nbt":"{Color:4b}"}` |
| 81 | lime | …`lime` | `{"nbt":"{Color:5b}"}` |
| 87 | pink | …`pink` | `{"nbt":"{Color:6b}"}` |
| 93 | gray | …`gray` | `{"nbt":"{Color:7b}"}` |
| 99 | light_gray | …`light_gray` | `{"nbt":"{Color:8b}"}` |
| 105 | cyan | …`cyan` | `{"nbt":"{Color:9b}"}` |
| 111 | purple | …`purple` | `{"nbt":"{Color:10b}"}` |
| 117 | blue | …`blue` | `{"nbt":"{Color:11b}"}` |
| 123 | brown | …`brown` | `{"nbt":"{Color:12b}"}` |
| 129 | green | …`green` | `{"nbt":"{Color:13b}"}` |
| 135 | red | …`red` | `{"nbt":"{Color:14b}"}` |
| 141 | black | …`black` | `{"nbt":"{Color:15b}"}` |

### 同版本全量检查

- `loot_tables/` 下 `type_specific` 仅此 16 处，全部为 `minecraft:sheep`；修复后为 0。
- 未发现 `entity_properties.predicate` 使用 1.20+ 字段（`components` / `slots` 均无）。

### 关键运行时细节（避免误判）

1.19.2 `Sheep.getDefaultLootTable()`（Forge 映射源码）：
- **未剪毛** → 返回原版 `BuiltInLootTables.SHEEP_*`；
- **已剪毛** → 返回 `this.getType().getDefaultLootTable()`，对 mod 实体即
  `theaurorian:entities/aurorian_sheep`。

因此只有 `Sheared:1b` 的极光羊才会走 mod 表。剪毛的原版羊只掉羊肉；
本次剪毛极光羊实测掉「对应颜色羊毛 + 羊肉」，可证明 mod 表被使用且颜色分流生效。

## 缺陷 2：结构 NBT `waterlogged` 为 ByteTag

### 扫描结果（全量 61 个 NBT）

共 **43 处**、**18 个文件**，**全部为 `TAG_Byte` 且值 0**，无 int/long 等其它类型错误：

| 文件 | 处数 |
|---|---|
| darkstone/darkstone_bossroom_back.nbt | 2 |
| darkstone/darkstone_end.nbt | 4 |
| moontemple/moontemplev2_center.nbt | 2 |
| moontemple/moontemplev2_left.nbt | 7 |
| moontemple/moontemplev2_right.nbt | 6 |
| moontemple/moontemplev2_room.nbt | 2 |
| runestone/upstream_ref/runestonetower_base_tlv2.nbt | 1 |
| runestone/upstream_ref/runestonetower_base_trv2.nbt | 1 |
| runestone/upstream_ref/runestonetower_floor_2_blv2.nbt | 2 |
| runestone/upstream_ref/runestonetower_floor_2_brv2.nbt | 2 |
| runestone/upstream_ref/runestonetower_floor_2_tlv2.nbt | 2 |
| runestone/upstream_ref/runestonetower_floor_2_trv2.nbt | 2 |
| runestone/upstream_ref/runestonetower_floor_blv2.nbt | 1 |
| runestone/upstream_ref/runestonetower_floor_brv2.nbt | 1 |
| runestone/upstream_ref/runestonetower_floor_tlv2.nbt | 1 |
| runestone/upstream_ref/runestonetower_floor_trv2.nbt | 1 |
| runestone/upstream_ref/runestonetower_top_blv2.nbt | 3 |
| runestone/upstream_ref/runestonetower_top_brv2.nbt | 3 |

> 注：委托描述中 runestone 记为 12 文件 18 处；实测为 12 文件 **20 处**，
> 总数 23+20=43，与总体 43 一致（按实测修复）。

### 工具与验证

新增 `scripts/scan_structure_property_types.py`（复用 `remap_structure_nbt.py` 的 NBT 读写器）。

每文件断言：
1. **改前 round-trip 字节一致**：解析后原样重序列化 == 原解压字节；不满足则拒绝修改。
2. 仅替换 `palette[*].Properties` 中 byte 0/1 → `"false"`/`"true"`；其它类型只报告。
3. **增量核算**：TAG_Byte 负载 1 字节，TAG_String 负载 `2+len`；
   每处 +6（"false"）或 +5（"true"）。本次 43×6=**258 字节**，逐文件校验通过。
4. **最强断言**：改前树 → 内存应用预期改动 → 重新序列化 → 与写后文件解压字节逐字节相等；通过。
5. 写后重新解析，确认目标属性均为 `TAG_String`。

### 来源判定：**本项目 1.19.2 树自身引入，不是上游**

- 1.12 upstream（`Aurorian/upstream/src/main/resources/assets/theaurorian/structures`）：
  61 个 NBT 扫描 0 处；且原版静木箱调色板只有 `facing`，根本没有 `waterlogged`。
- 1.20.1 参考树：61 个 NBT 扫描 0 处。
- 旧 `Aurorian` 树（singular `structure/`）与 worktree 副本：0 处。
- 本树 `git rev-list` 逐版本追踪 `darkstone_end.nbt`：
  - `871e28e1` → 0 处；
  - `bf467fd2`（Port 1.20.1 fixes to 1.19.2; bump to 2.7.4）→ 4 处。
  → ByteTag 是本树该次移植改写时引入。

## 复核（运行时）

服务端参数：`server-port=25801`、`level-name=smoke_world`、`level-type=minecraft:flat`、
`max-tick-time=-1`、`eula=true`、RCON 25802。通过前台 Python 驱动启动、
RCON 执行命令、`stop` 干净关闭（`server_rc=0`，`BUILD SUCCESSFUL`）。

- 启动：`Done (4.379s)!`
- `Couldn't parse loot table` 计数：**0**
- `Unknown sub-predicate` 计数：**0**
- `Unable to read property` / `waterlogged` 计数：**0**
- `place template`：moontemplev2_center / left、darkstone_end、
  runestone/upstream_ref/runestonetower_top_blv2 全部 `Loaded template`。
- 羊（`Sheared:1b`）：
  - white (`Color:0b`) → `minecraft:white_wool` ×1 + `minecraft:mutton` ×1
  - red (`Color:14b`) → `minecraft:red_wool` ×1 + `minecraft:mutton` ×1
- 测试世界已删除（`run/server` 整体为本次新建，已移除）。

## 命令与退出码

| 命令 | EXIT |
|---|---|
| `python3 scripts/scan_structure_property_types.py src/main/resources/data/theaurorian/structures`（改前） | 0（43 处） |
| 同上 `--apply` | 0 |
| 同上（改后） | 0（0 处） |
| `sh ./gradlew --offline processResources classes`（JAVA_HOME=zulu-17） | 0 |
| `timeout 520 python3 /tmp/drive_1192.py`（首次未剪毛） | 0（但不走 mod 表，结论作废） |
| 同上（`Sheared:1b` 重测） | 0；server_rc=0 |

## 未解决 / 说明

- **未剪毛极光羊仍使用原版 `SHEEP_*` 表**，mod 的 `theaurorian:entities/aurorian_sheep`
  只在其剪毛时生效。这是 1.19.2 `Sheep.getDefaultLootTable()` 的既有语义，
  不属于本次两个缺陷，未改动；如需“永远使用 mod 表”，需在 `AurorianSheepEntity`
  覆写 `getDefaultLootTable()`（会影响掉落设计，建议单独评审）。
- mod 表在剪毛羊身上仍掉羊毛，与“剪毛后不掉毛”的直觉冲突；属于现有表设计，未改动。
- 其它 NBT 非字符串属性：全量扫描为 0，无遗留。
- 本机 `ss` 不可用，无法主动观察 25565 监听；全程未向 25565 发送任何操作，
  仅启动/停止自建 25801 服务端（已确认无残留进程/监听）。

## 声明

未 commit / push / stash / reset / checkout；未改 `build.gradle` / `gradle.properties` /
`settings.gradle`；未触碰 1.20.1 参考树与 `Aurorian` 树；未动 25565 上进程；
工作区既有未提交改动全部保留。
