package shiroroku.theaurorian.Registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.theaurorian.TheAurorian;
import shiroroku.theaurorian.World.Structure.DarkstoneDungeonStructure;
import shiroroku.theaurorian.World.Structure.IgnoreBlockStructureProcessor;
import shiroroku.theaurorian.World.Structure.MoonTempleStructure;
import shiroroku.theaurorian.World.Structure.ReplaceAirStructureProcessor;
import shiroroku.theaurorian.World.Structure.RunestoneDungeonStructure;
import shiroroku.theaurorian.World.Structure.SingleTemplateStructure;

public class StructureRegistry {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, TheAurorian.MODID);
    public static final DeferredRegister<StructurePieceType> PIECE_TYPES = DeferredRegister.create(Registries.STRUCTURE_PIECE, TheAurorian.MODID);
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSOR_TYPES = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, TheAurorian.MODID);

    public static final RegistryObject<StructureType<DarkstoneDungeonStructure>> DARKSTONE_DUNGEON_TYPE = STRUCTURE_TYPES.register("darkstone_dungeon", () -> () -> DarkstoneDungeonStructure.CODEC);
    public static final RegistryObject<StructurePieceType> DARKSTONE_DUNGEON_PIECE = PIECE_TYPES.register("darkstone_dungeon", () -> DarkstoneDungeonStructure.DarkstoneDungeonPiece::load);

    public static final RegistryObject<StructureType<MoonTempleStructure>> MOON_TEMPLE_TYPE = STRUCTURE_TYPES.register("moon_temple", () -> () -> MoonTempleStructure.CODEC);
    public static final RegistryObject<StructurePieceType> MOON_TEMPLE_PIECE = PIECE_TYPES.register("moon_temple", () -> MoonTempleStructure.MoonTemplePiece::load);

    public static final RegistryObject<StructureType<RunestoneDungeonStructure>> RUNESTONE_DUNGEON_TYPE = STRUCTURE_TYPES.register("runestone_dungeon", () -> () -> RunestoneDungeonStructure.CODEC);
    public static final RegistryObject<StructurePieceType> RUNESTONE_DUNGEON_PIECE = PIECE_TYPES.register("runestone_dungeon", () -> RunestoneDungeonStructure.RunestoneDungeonPiece::load);

    public static final RegistryObject<StructureType<SingleTemplateStructure>> SINGLE_TEMPLATE_TYPE = STRUCTURE_TYPES.register("single_template", () -> () -> SingleTemplateStructure.CODEC);
    public static final RegistryObject<StructurePieceType> SINGLE_TEMPLATE_PIECE = PIECE_TYPES.register("single_template", () -> SingleTemplateStructure.SingleTemplatePiece::load);

    public static final RegistryObject<StructureProcessorType<IgnoreBlockStructureProcessor>> IGNORE_BLOCK = PROCESSOR_TYPES.register("ignore_block", () -> () -> IgnoreBlockStructureProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<ReplaceAirStructureProcessor>> REPLACE_AIR = PROCESSOR_TYPES.register("replace_air", () -> () -> ReplaceAirStructureProcessor.CODEC);

    public static void register(IEventBus bus) {
        STRUCTURE_TYPES.register(bus);
        PIECE_TYPES.register(bus);
        PROCESSOR_TYPES.register(bus);
    }
}
