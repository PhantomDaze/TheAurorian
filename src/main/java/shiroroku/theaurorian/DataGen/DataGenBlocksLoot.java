package shiroroku.theaurorian.DataGen;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.Tags;
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

        private final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Tags.Items.TOOLS_SHEAR));
        private final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH;
        private final LootItemCondition.Builder HAS_NO_SHEARS_OR_SILK_TOUCH;

        protected Blocks(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
            this.HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(this.hasSilkTouch());
            this.HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();
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
            this.add(BlockRegistry.aurorian_tallgrass.get(), createShearsOnlyDrop(ItemRegistry.plant_fiber.get()));
            this.add(BlockRegistry.aurorian_tallgrass_light.get(), createShearsOnlyDrop(ItemRegistry.plant_fiber.get()));
            this.add(BlockRegistry.bright_bulb.get(), createShearsOnlyDrop(BlockRegistry.bright_bulb.get()));
            this.add(BlockRegistry.geode.get(), block -> createOreDrop(block, BlockRegistry.crystal.get().asItem()));
            this.add(BlockRegistry.lavender_block.get(), createShearsOnlyDrop(ItemRegistry.lavender.get()));
            this.add(BlockRegistry.petunia.get(), createShearsOnlyDrop(BlockRegistry.petunia.get()));
            this.add(BlockRegistry.silentwood_slab.get(), b -> createSlabItemTable(b));
            this.add(BlockRegistry.silkberry_block.get(), createShearsOnlyDrop(ItemRegistry.silkberry.get()));
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
            this.add(BlockRegistry.silentwood_leaves.get(), block -> createLeavesDrops(block, BlockRegistry.silentwood_sapling.get(), NORMAL_LEAVES_SAPLING_CHANCES));
            this.dropSelf(BlockRegistry.silentwood_log.get());
            this.dropSelf(BlockRegistry.silentwood_sapling.get());
            this.dropSelf(BlockRegistry.silentwood_stairs.get());
            this.add(BlockRegistry.weeping_willow_leaves.get(), block -> createLeavesDrops(block, BlockRegistry.weeping_willow_sapling.get(), NORMAL_LEAVES_SAPLING_CHANCES));
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
            return all;
        }
    }
}
