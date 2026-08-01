package shiroroku.theaurorian.World.Structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.StructureRegistry;

import java.util.function.Supplier;

/**
 * Skips template blocks that equal the ignored block, leaving existing terrain
 * in place. Replicates 1.12 {@code PlacementSettings.setReplacedBlock}.
 */
public class IgnoreBlockStructureProcessor extends StructureProcessor {

    public static final Codec<IgnoreBlockStructureProcessor> CODEC = Codec.unit(IgnoreBlockStructureProcessor::new);
    public static final IgnoreBlockStructureProcessor AURORIAN_STONE = new IgnoreBlockStructureProcessor(() -> BlockRegistry.aurorian_stone.get());

    private final Supplier<Block> ignored;

    public IgnoreBlockStructureProcessor() {
        this(() -> null);
    }

    public IgnoreBlockStructureProcessor(Supplier<Block> ignored) {
        this.ignored = ignored;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader pLevel, BlockPos pPos, BlockPos pPivot, StructureTemplate.StructureBlockInfo pOriginalInfo, StructureTemplate.StructureBlockInfo pModifiedInfo, StructurePlaceSettings pSettings) {
        Block ignoredBlock = ignored.get();
        if (ignoredBlock != null && pModifiedInfo.state().is(ignoredBlock)) {
            return null;
        }
        return pModifiedInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureRegistry.IGNORE_BLOCK.get();
    }
}
