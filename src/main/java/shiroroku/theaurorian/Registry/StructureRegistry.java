package shiroroku.theaurorian.Registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import shiroroku.theaurorian.TheAurorian;
import shiroroku.theaurorian.World.Structure.DarkstoneDungeonStructure;
import shiroroku.theaurorian.World.Structure.IgnoreBlockStructureProcessor;
import shiroroku.theaurorian.World.Structure.MoonTempleStructure;
import shiroroku.theaurorian.World.Structure.ReplaceAirStructureProcessor;
import shiroroku.theaurorian.World.Structure.SingleTemplateStructure;

public class StructureRegistry {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, TheAurorian.MODID);
    public static final DeferredRegister<StructurePieceType> PIECE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, TheAurorian.MODID);
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSOR_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, TheAurorian.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<DarkstoneDungeonStructure>> DARKSTONE_DUNGEON_TYPE =
            STRUCTURE_TYPES.register("darkstone_dungeon", () -> type(DarkstoneDungeonStructure.CODEC));
    public static final DeferredHolder<StructurePieceType, StructurePieceType> DARKSTONE_DUNGEON_PIECE =
            PIECE_TYPES.register("darkstone_dungeon", () -> DarkstoneDungeonStructure.DarkstoneDungeonPiece::load);

    public static final DeferredHolder<StructureType<?>, StructureType<MoonTempleStructure>> MOON_TEMPLE_TYPE =
            STRUCTURE_TYPES.register("moon_temple", () -> type(MoonTempleStructure.CODEC));
    public static final DeferredHolder<StructurePieceType, StructurePieceType> MOON_TEMPLE_PIECE =
            PIECE_TYPES.register("moon_temple", () -> MoonTempleStructure.MoonTemplePiece::load);

    public static final DeferredHolder<StructureType<?>, StructureType<SingleTemplateStructure>> SINGLE_TEMPLATE_TYPE =
            STRUCTURE_TYPES.register("single_template", () -> type(SingleTemplateStructure.CODEC));
    public static final DeferredHolder<StructurePieceType, StructurePieceType> SINGLE_TEMPLATE_PIECE =
            PIECE_TYPES.register("single_template", () -> SingleTemplateStructure.SingleTemplatePiece::load);

    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<IgnoreBlockStructureProcessor>> IGNORE_BLOCK =
            PROCESSOR_TYPES.register("ignore_block", () -> processor(IgnoreBlockStructureProcessor.CODEC));
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<ReplaceAirStructureProcessor>> REPLACE_AIR =
            PROCESSOR_TYPES.register("replace_air", () -> processor(ReplaceAirStructureProcessor.CODEC));

    private static <S extends Structure> StructureType<S> type(MapCodec<S> codec) {
        return () -> codec;
    }

    private static <P extends StructureProcessor> StructureProcessorType<P> processor(MapCodec<P> codec) {
        return () -> codec;
    }

    public static void register(IEventBus bus) {
        STRUCTURE_TYPES.register(bus);
        PIECE_TYPES.register(bus);
        PROCESSOR_TYPES.register(bus);
    }
}
