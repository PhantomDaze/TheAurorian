package shiroroku.theaurorian.DataGen;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;

import java.util.List;
import java.util.function.Function;
import net.minecraft.world.level.ItemLike;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DataGenBlocksLoot extends LootTableProvider {

    public DataGenBlocksLoot(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK)
        ), registries);
    }

    private static class Blocks extends BlockLootSubProvider {

        /**
         * Shears drop condition: vanilla shears + every mod sickle (which are ShearsItem
         * subclasses). Listed explicitly because the c:tools/shear tag is not fully
         * populated in the loot provider's tag registry at datagen time.
         * Keep in sync when adding a new sickle.
         */
        private final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(
                Items.SHEARS,
                ItemRegistry.aurorian_stone_sickle.get(),
                ItemRegistry.moonstone_sickle.get(),
                ItemRegistry.silentwood_sickle.get()));
        private final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH;
        private final LootItemCondition.Builder HAS_NO_SHEARS_OR_SILK_TOUCH;

        protected Blocks(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
            this.HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(this.hasSilkTouch());
            this.HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();
        }

        /**
         * Parent createShearsOnlyDrop is static and always matches vanilla shears only;
         * build the same table with our HAS_SHEARS so sickles also harvest plants.
         */
        private LootTable.Builder shearsOnlyDrop(ItemLike pItem) {
            return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(pItem).when(this.HAS_SHEARS)));
        }

        /**
         * Vanilla-style grass: shears/sickle collect plant_fiber as-is (guaranteed),
         * otherwise a small chance of wheat seeds, like vanilla grass.
         */
        private LootTable.Builder grassDrop(Block pBlock) {
            HolderLookup.RegistryLookup<Enchantment> lookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(
                    LootItem.lootTableItem(ItemRegistry.plant_fiber.get()).when(this.HAS_SHEARS).otherwise(
                            this.applyExplosionDecay(pBlock, LootItem.lootTableItem(Items.WHEAT_SEEDS)
                                    .when(LootItemRandomChanceCondition.randomChance(0.125F))
                                    .apply(ApplyBonusCount.addUniformBonusCount(lookup.getOrThrow(Enchantments.FORTUNE), 2))))));
        }

        /**
         * Same for leaves: shears or silk touch must also accept sickles.
         */
        @Override
        protected LootTable.Builder createSilkTouchOrShearsDispatchTable(Block pBlock, LootPoolEntryContainer.Builder<?> pWrapped) {
            return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(pBlock).when(this.HAS_SHEARS_OR_SILK_TOUCH).otherwise(pWrapped)));
        }

        private static final float[] NORMAL_LEAVES_STICK_CHANCES = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};

        /**
         * Vanilla {@link #createLeavesDrops(Block, Block, float...)} hardcodes its shears
         * condition to vanilla shears only, which would make a sickle drop both the leaf
         * block and sticks. Rebuild it so both the leaf and stick pools share our
         * HAS_SHEARS_OR_SILK_TOUCH condition.
         */
        private LootTable.Builder leavesDrops(Block pLeavesBlock, Block pSaplingBlock, float... pChances) {
            HolderLookup.RegistryLookup<Enchantment> lookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            return this.createSilkTouchOrShearsDispatchTable(
                    pLeavesBlock,
                    this.applyExplosionCondition(pLeavesBlock, LootItem.lootTableItem(pSaplingBlock)
                            .when(BonusLevelTableCondition.bonusLevelFlatChance(lookup.getOrThrow(Enchantments.FORTUNE), pChances))))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .when(this.HAS_NO_SHEARS_OR_SILK_TOUCH)
                            .add(this.applyExplosionDecay(pLeavesBlock, LootItem.lootTableItem(Items.STICK)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                                    .when(BonusLevelTableCondition.bonusLevelFlatChance(lookup.getOrThrow(Enchantments.FORTUNE), NORMAL_LEAVES_STICK_CHANCES))));
        }

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
            // Flowers drop as-is, but only with shears/sickle (per item descriptions).
            // Grass: shears/sickle always drop plant_fiber; otherwise a small chance of wheat seeds.
            this.add(BlockRegistry.aurorian_tallgrass.get(), this.grassDrop(BlockRegistry.aurorian_tallgrass.get()));
            this.add(BlockRegistry.aurorian_tallgrass_light.get(), this.grassDrop(BlockRegistry.aurorian_tallgrass_light.get()));
            this.add(BlockRegistry.bright_bulb.get(), this.shearsOnlyDrop(BlockRegistry.bright_bulb.get()));
            this.add(BlockRegistry.geode.get(), block -> createOreDrop(block, BlockRegistry.crystal.get().asItem()));
            this.add(BlockRegistry.lavender_block.get(), this.shearsOnlyDrop(ItemRegistry.lavender.get()));
            this.add(BlockRegistry.petunia.get(), this.shearsOnlyDrop(BlockRegistry.petunia.get()));
            this.add(BlockRegistry.silentwood_slab.get(), b -> createSlabItemTable(b));
            this.add(BlockRegistry.silkberry_block.get(), this.shearsOnlyDrop(ItemRegistry.silkberry.get()));
            this.dropSelf(BlockRegistry.aurorian_cobblestone_stairs.get());
            this.dropSelf(BlockRegistry.aurorian_deepslate_stairs.get());
            this.dropSelf(BlockRegistry.aurorian_furnace.get());
            this.dropSelf(BlockRegistry.chimney.get());
            // Boss spawner is unbreakable and drops nothing, like the vanilla spawner.
            this.add(BlockRegistry.boss_spawner.get(), b -> LootTable.lootTable());
            // Portal / fog barrier are unbreakable and drop nothing.
            this.add(BlockRegistry.aurorian_portal.get(), b -> LootTable.lootTable());
            this.add(BlockRegistry.fog_wall.get(), b -> LootTable.lootTable());
            this.dropSelf(BlockRegistry.crystal.get());
            this.dropSelf(BlockRegistry.darkstone_stairs.get());
            this.dropSelf(BlockRegistry.moon_gem.get());
            this.add(BlockRegistry.mushroom.get(), block -> createSingleItemTable(BlockRegistry.mushroom_small.get().asItem()));
            this.add(BlockRegistry.mushroom_stem.get(), block -> createSingleItemTable(BlockRegistry.mushroom_small.get().asItem()));
            this.dropSelf(BlockRegistry.mushroom_small.get());
            this.dropSelf(BlockRegistry.moonlight_forge.get());
            this.dropSelf(BlockRegistry.moon_temple_bars.get());
            this.dropSelf(BlockRegistry.moon_temple_stairs.get());
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
            this.add(BlockRegistry.silentwood_leaves.get(), block -> this.leavesDrops(block, BlockRegistry.silentwood_sapling.get(), NORMAL_LEAVES_SAPLING_CHANCES));
            this.dropSelf(BlockRegistry.silentwood_log.get());
            this.dropSelf(BlockRegistry.silentwood_sapling.get());
            this.dropSelf(BlockRegistry.silentwood_stairs.get());
            this.add(BlockRegistry.weeping_willow_leaves.get(), block -> this.leavesDrops(block, BlockRegistry.weeping_willow_sapling.get(), NORMAL_LEAVES_SAPLING_CHANCES));
            this.dropSelf(BlockRegistry.weeping_willow_log.get());
            this.dropSelf(BlockRegistry.weeping_willow_sapling.get());
            this.dropSelf(BlockRegistry.weeping_willow_stairs.get());
            this.dropSelf(BlockRegistry.aurorian_deepslate_wall.get());
            this.dropSelf(BlockRegistry.aurorian_cobblestone_wall.get());
            // ! dont forget to add to function below too <3
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            java.util.List<Block> all = new java.util.ArrayList<>();
            BlockRegistry.BLOCKS.getEntries().forEach(h -> all.add(h.get()));
            BlockRegistry.BLOCKS_GEN.getEntries().forEach(h -> all.add(h.get()));
            BlockRegistry.BLOCKS_GEN_NL.getEntries().forEach(h -> all.add(h.get()));
            BlockRegistry.BLOCKS_GEN_NL_PLANT.getEntries().forEach(h -> all.add(h.get()));
            // urn keeps a hand-authored loot table in src/main/resources (weighted ruin loot);
            // exclude it so datagen does not generate (and thus override) one.
            all.remove(BlockRegistry.urn.get());
            return all;
        }
    }
}
