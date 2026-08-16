package shiroroku.theaurorian.DataGen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.Tags;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataGenBlocksLoot extends LootTableProvider {

    public DataGenBlocksLoot(PackOutput output) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK)
        ));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        // skip strict validation for mod-owned tables
    }

    private static class Blocks extends BlockLootSubProvider {

        protected Blocks() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        // mojang pls
        private static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
        private static final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Tags.Items.SHEARS));// sickles will only work with this :c they reference shears item instead of tag like what???
        private static final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);
        private static final LootItemCondition.Builder HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();

        @Override
        protected void generate() {
            // AUTO GENERATED
            BlockRegistry.BLOCKS_GEN.getEntries().stream().map(Supplier::get).forEach(this::dropSelf);

            // CUSTOM
            this.add(BlockRegistry.aurorian_coal_ore.get(), block -> createOreDrop(block, ItemRegistry.aurorian_coal.get()));
            this.add(BlockRegistry.aurorian_cobblestone_slab.get(), b -> createSlabItemTable(b));
            this.add(BlockRegistry.aurorian_deepslate_slab.get(), b -> createSlabItemTable(b));
            this.add(BlockRegistry.aurorian_grass.get(), block -> createSingleItemTableWithSilkTouch(block, BlockRegistry.aurorian_dirt.get()));
            this.add(BlockRegistry.aurorian_grass_light.get(), block -> createSingleItemTable(block));
            this.add(BlockRegistry.aurorian_stone.get(), block-> createSingleItemTableWithSilkTouch(block, BlockRegistry.aurorian_cobblestone.get()));
            this.add(BlockRegistry.aurorian_glass.get(), b -> createSilkTouchOnlyTable(b));
            this.add(BlockRegistry.moon_glass.get(), b -> createSilkTouchOnlyTable(b));
            this.add(BlockRegistry.aurorian_glass_pane.get(), b -> createSilkTouchOnlyTable(b));
            this.add(BlockRegistry.moon_glass_pane.get(), b -> createSilkTouchOnlyTable(b));
            this.add(BlockRegistry.aurorian_tallgrass.get(), dropGrassLike(BlockRegistry.aurorian_tallgrass.get(), ItemRegistry.plant_fiber.get()));
            this.add(BlockRegistry.aurorian_tallgrass_light.get(), dropGrassLike(BlockRegistry.aurorian_tallgrass_light.get(), ItemRegistry.plant_fiber.get()));
            this.add(BlockRegistry.bright_bulb.get(), dropWithSickleOrShears(BlockRegistry.bright_bulb.get()));
            this.add(BlockRegistry.geode.get(), block -> createOreDrop(block, BlockRegistry.crystal.get().asItem()));
            this.add(BlockRegistry.lavender_block.get(), dropWithSickleOrShears(ItemRegistry.lavender.get()));
            this.add(BlockRegistry.petunia.get(), dropWithSickleOrShears(BlockRegistry.petunia.get()));
            this.add(BlockRegistry.silentwood_slab.get(), b -> createSlabItemTable(b));
            this.add(BlockRegistry.silkberry_block.get(), dropWithSickleOrShears(ItemRegistry.silkberry.get()));
            this.dropSelf(BlockRegistry.aurorian_cobblestone_stairs.get());
            this.dropSelf(BlockRegistry.aurorian_deepslate_stairs.get());
            this.dropSelf(BlockRegistry.aurorian_furnace.get());
            this.dropSelf(BlockRegistry.chimney.get());
            this.dropSelf(BlockRegistry.crystal.get());
            this.dropSelf(BlockRegistry.darkstone_stairs.get());
            this.dropSelf(BlockRegistry.moon_gem.get());
            this.add(BlockRegistry.mushroom.get(), block -> createSingleItemTable(BlockRegistry.mushroom_small.get().asItem()));
            this.add(BlockRegistry.mushroom_stem.get(), block -> createSingleItemTable(BlockRegistry.mushroom_small.get().asItem()));
            this.dropSelf(BlockRegistry.mushroom_small.get());
            this.dropSelf(BlockRegistry.moonlight_forge.get());
            this.dropSelf(BlockRegistry.moon_temple_bars.get());
            this.dropSelf(BlockRegistry.umbra_stone_roof_stairs.get());
            this.dropSelf(BlockRegistry.peridotite_smooth_stairs.get());
            this.dropSelf(BlockRegistry.aurorian_stone_brick_stairs.get());
            this.dropSelf(BlockRegistry.aurorian_stone_stairs.get());
            this.dropSelf(BlockRegistry.runestone_bars.get());
            this.dropSelf(BlockRegistry.runestone_stairs.get());
            this.dropSelf(BlockRegistry.scrapper.get());
            this.dropSelf(BlockRegistry.silentwood_chest.get());
            this.dropSelf(BlockRegistry.silentwood_crafting_table.get());
            this.dropSelf(BlockRegistry.silentwood_fence.get());
            this.dropSelf(BlockRegistry.silentwood_torch.get());
            this.dropSelf(BlockRegistry.moon_torch.get());
            this.dropSelf(BlockRegistry.silentwood_ladder.get());
            this.add(BlockRegistry.lavender_crop.get(), this.createCropDrops(BlockRegistry.lavender_crop.get(), ItemRegistry.lavender_seeds.get(), ItemRegistry.lavender.get(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(BlockRegistry.lavender_crop.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(net.minecraft.world.level.block.CropBlock.AGE, 7))));
            this.add(BlockRegistry.silkberry_crop.get(), this.createCropDrops(BlockRegistry.silkberry_crop.get(), ItemRegistry.silkberry_seeds.get(), ItemRegistry.silkberry.get(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(BlockRegistry.silkberry_crop.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(net.minecraft.world.level.block.CropBlock.AGE, 7))));
            this.add(BlockRegistry.silentwood_leaves.get(), block -> createSelfDropDispatchTable(BlockRegistry.silentwood_leaves.get(), HAS_SHEARS_OR_SILK_TOUCH,
                    applyExplosionCondition(BlockRegistry.silentwood_leaves.get(), LootItem.lootTableItem(BlockRegistry.silentwood_sapling.get()))
                            .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.05F, 0.0625F, 0.083333336F, 0.1F)))
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                            .add(applyExplosionDecay(BlockRegistry.silentwood_leaves.get(), LootItem.lootTableItem(ItemRegistry.silentwood_stick.get())
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                                    .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F)))));
            this.dropSelf(BlockRegistry.silentwood_log.get());
            this.dropSelf(BlockRegistry.silentwood_sapling.get());
            this.dropSelf(BlockRegistry.silentwood_stairs.get());
            this.add(BlockRegistry.weeping_willow_leaves.get(), block -> createSelfDropDispatchTable(BlockRegistry.weeping_willow_leaves.get(), HAS_SHEARS_OR_SILK_TOUCH,
                    applyExplosionCondition(BlockRegistry.weeping_willow_leaves.get(), LootItem.lootTableItem(ItemRegistry.weeping_willow_sap.get()))
                            .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.05F, 0.0625F, 0.083333336F, 0.1F)))
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                            .add(applyExplosionDecay(BlockRegistry.weeping_willow_leaves.get(), LootItem.lootTableItem(ItemRegistry.silentwood_stick.get())
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                                    .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F)))));
            this.dropSelf(BlockRegistry.weeping_willow_log.get());
            this.dropSelf(BlockRegistry.weeping_willow_sapling.get());
            this.dropSelf(BlockRegistry.weeping_willow_stairs.get());
            this.dropSelf(BlockRegistry.aurorian_deepslate_wall.get());
            this.dropSelf(BlockRegistry.aurorian_cobblestone_wall.get());
            // ! dont forget to add to function below too <3
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            // AUTO GENERATED
            List<Block> gen = new ArrayList<>(BlockRegistry.BLOCKS_GEN.getEntries().stream().map(Supplier::get).toList());

            // CUSTOM
            gen.add(BlockRegistry.aurorian_coal_ore.get());
            gen.add(BlockRegistry.aurorian_cobblestone_slab.get());
            gen.add(BlockRegistry.aurorian_cobblestone_stairs.get());
            gen.add(BlockRegistry.aurorian_cobblestone_wall.get());
            gen.add(BlockRegistry.aurorian_deepslate_slab.get());
            gen.add(BlockRegistry.aurorian_deepslate_stairs.get());
            gen.add(BlockRegistry.aurorian_deepslate_wall.get());
            gen.add(BlockRegistry.aurorian_furnace.get());
            gen.add(BlockRegistry.aurorian_glass.get());
            gen.add(BlockRegistry.aurorian_glass_pane.get());
            gen.add(BlockRegistry.aurorian_grass.get());
            gen.add(BlockRegistry.aurorian_grass_light.get());
            gen.add(BlockRegistry.aurorian_stone.get());
            gen.add(BlockRegistry.aurorian_stone_brick.get());
            gen.add(BlockRegistry.aurorian_stone_brick_stairs.get());
            gen.add(BlockRegistry.aurorian_stone_stairs.get());
            gen.add(BlockRegistry.aurorian_tallgrass.get());
            gen.add(BlockRegistry.aurorian_tallgrass_light.get());
            gen.add(BlockRegistry.bright_bulb.get());
            gen.add(BlockRegistry.chimney.get());
            gen.add(BlockRegistry.crystal.get());
            gen.add(BlockRegistry.darkstone_stairs.get());
            gen.add(BlockRegistry.geode.get());
            gen.add(BlockRegistry.lavender_block.get());
            gen.add(BlockRegistry.moon_glass.get());
            gen.add(BlockRegistry.moon_glass_pane.get());
            gen.add(BlockRegistry.moon_gem.get());
            gen.add(BlockRegistry.mushroom.get());
            gen.add(BlockRegistry.mushroom_crystal.get());
            gen.add(BlockRegistry.mushroom_small.get());
            gen.add(BlockRegistry.mushroom_stem.get());
            gen.add(BlockRegistry.moon_sand.get());
            gen.add(BlockRegistry.moon_torch.get());
            gen.add(BlockRegistry.moonlight_forge.get());
            gen.add(BlockRegistry.moon_temple_bars.get());
            gen.add(BlockRegistry.peridotite.get());
            gen.add(BlockRegistry.peridotite_smooth.get());
            gen.add(BlockRegistry.peridotite_smooth_stairs.get());
            gen.add(BlockRegistry.petunia.get());
            gen.add(BlockRegistry.runestone_bars.get());
            gen.add(BlockRegistry.runestone_stairs.get());
            gen.add(BlockRegistry.scrapper.get());
            gen.add(BlockRegistry.silentwood_chest.get());
            gen.add(BlockRegistry.silentwood_crafting_table.get());
            gen.add(BlockRegistry.silentwood_fence.get());
            gen.add(BlockRegistry.silentwood_leaves.get());
            gen.add(BlockRegistry.silentwood_log.get());
            gen.add(BlockRegistry.silentwood_sapling.get());
            gen.add(BlockRegistry.silentwood_slab.get());
            gen.add(BlockRegistry.silentwood_stairs.get());
            gen.add(BlockRegistry.silentwood_torch.get());
            gen.add(BlockRegistry.silentwood_ladder.get());
            gen.add(BlockRegistry.silkberry_block.get());
            gen.add(BlockRegistry.lavender_crop.get());
            gen.add(BlockRegistry.silkberry_crop.get());
            gen.add(BlockRegistry.umbra_stone.get());
            gen.add(BlockRegistry.umbra_stone_cracked.get());
            gen.add(BlockRegistry.umbra_stone_roof_tiles.get());
            gen.add(BlockRegistry.umbra_stone_roof_stairs.get());
            gen.add(BlockRegistry.weeping_willow_leaves.get());
            gen.add(BlockRegistry.weeping_willow_log.get());
            gen.add(BlockRegistry.weeping_willow_sapling.get());
            gen.add(BlockRegistry.weeping_willow_stairs.get());
            gen.add(BlockRegistry.aurorian_farm_tile.get());
            gen.add(BlockRegistry.aurorian_coal_block.get());
            gen.add(BlockRegistry.aurorian_steel_block.get());
            gen.add(BlockRegistry.cerulean_block.get());
            gen.add(BlockRegistry.moonstone_block.get());
            return gen;
        }

        private Function<Block, LootTable.Builder> dropWithSickleOrShears(ItemLike drops) {
            return block -> LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(HAS_SHEARS).add(LootItem.lootTableItem(drops)));
        }

        // 草类（aurorian_tallgrass 等）：剪刀/精准采集原样掉草自身；否则按原版概率掉纤维（带时运加成）
        private Function<Block, LootTable.Builder> dropGrassLike(Block self, ItemLike drops) {
            return block -> LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .when(HAS_SHEARS_OR_SILK_TOUCH)
                            .add(LootItem.lootTableItem(self)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                            .when(net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance(0.125F))
                            .add(applyExplosionDecay(self, LootItem.lootTableItem(drops)
                                    .apply(net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 2)))));
        }
    }
}
