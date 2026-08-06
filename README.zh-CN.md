# The Aurorian 中文移植版

> **移植说明：** 本仓库是由 **PhantomDaze** 维护的社区移植版。我是本项目的移植者和维护者，不是《The Aurorian》的原作者。原始模组的代码、资源、美术设计及相关内容归 **Shiroroku（Elise）** 和原项目贡献者所有。请查看 [`upstream/`](upstream/) 和 [`upstream/LICENSE.txt`](upstream/LICENSE.txt) 了解上游源码及署名信息。

## 项目简介

《The Aurorian》是一款 Minecraft 模组，加入了新的维度、首领、工具以及许多可探索和使用的新物品。

当前工作区为 **Minecraft 1.21.1 + NeoForge 21.1.248**（ModDevGradle 2.0.143，Java 21）。内容基于已完成的 1.19.2 → 1.20.1 内容移植。

## 当前状态

- NeoForge 1.21.1 版本可以编译并打包。
- 资源完整性检查通过。
- 内容量保持

## 主要变化

从 1.12 版本经 1.19.2 / 1.20.1 移植到 1.21.1 的主要变化包括：

- 加入自定义游戏内指南「指引之镜」。
- Curios 成为必需依赖，并加入新的可穿戴物品。
- 地牢改为随机分布，不再沿网格轴线生成。
- 根据玩家数量调整首领难度。
- 地牢拥有新的布局和更多变体。
- 重制本影剑和水晶剑。
- Scrapper 会根据物品耐久度返还材料。
- 将神秘屏障改为雾墙。
- 地下区域更加丰富，加入蘑菇洞穴及相关内容。
- 烟囱可以降低极光熔炉的燃料消耗。
- 更新极光钢的配方。
- 恢复垂柳群系，包括树木、滴水和钟声效果。
- 地牢战利品遵循材料路线：符文石 → 极光石，暗石 → 本影，月神殿 → 水晶。
- 加入新的渲染效果，包括极光和月亮运动。
- **平台：** Forge → NeoForge；数据包路径单数化；Common 标签 `c:`；物品 Data Components；附魔 datapack 化。

## 验证与演示

```bash
# Java 21
./gradlew test                 # 数据包完整性和单元测试
./gradlew runGameTestServer    # 22 个无头功能 GameTest
# 在开启作弊的客户端中执行：
/ta demo                       # 在面前展示 17 个可观察演示案例
```

## 署名与许可

本项目的移植工作由 **PhantomDaze** 完成和维护。

《The Aurorian》的原始作品由 **Shiroroku（Elise）** 及原项目贡献者创作。原始项目的版权和许可信息请以 [`upstream/LICENSE.txt`](upstream/LICENSE.txt) 为准；本仓库的许可信息见 [`LICENSE`](LICENSE)。
