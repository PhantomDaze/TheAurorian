package shiroroku.theaurorian.World.Structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Registry.StructureRegistry;

/**
 * Only places template blocks where the target is currently air, leaving the
 * existing terrain untouched elsewhere. Replicates the 1.12
 * {@code BlockFillProcessor(Blocks.AIR)} used by the weeping willow tree.
 */
public class ReplaceAirStructureProcessor extends StructureProcessor {

    public static final MapCodec<ReplaceAirStructureProcessor> CODEC = MapCodec.unit(ReplaceAirStructureProcessor::new);
    public static final ReplaceAirStructureProcessor INSTANCE = new ReplaceAirStructureProcessor();

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader pLevel, BlockPos pPos, BlockPos pPivot, StructureTemplate.StructureBlockInfo pOriginalInfo, StructureTemplate.StructureBlockInfo pModifiedInfo, StructurePlaceSettings pSettings) {
        return pLevel.getBlockState(pModifiedInfo.pos()).isAir() ? pModifiedInfo : null;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureRegistry.REPLACE_AIR.get();
    }
}
