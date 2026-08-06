package shiroroku.theaurorian.Blocks.BossSpawner;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.Entities.Boss.MoonQueenEntity;
import shiroroku.theaurorian.Entities.DungeonKeeper.DungeonKeeperEntity;
import shiroroku.theaurorian.Registry.BlockEntityRegistry;
import shiroroku.theaurorian.TheAurorian;
import shiroroku.theaurorian.Util.ModUtil;

import java.util.Locale;

public class BossSpawnerBlockEntity extends BlockEntity {

    public EntityType<?> bossEntity = null;
    public static final int spawnDistance = 16;

    public BossSpawnerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.boss_spawner.get(), pPos, pBlockState);
    }

    private boolean isNearPlayer() {
        return ModUtil.hasNearbyPlayerAbove(level,
                worldPosition.getX() + 0.5D,
                worldPosition.getY(),
                worldPosition.getZ() + 0.5D,
                spawnDistance,
                player -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)
                        && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(player));
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos,
                                                      BlockState blockState, T t) {
        if (level.isClientSide) {
            return;
        }
        if (t instanceof BossSpawnerBlockEntity spawner
                && spawner.bossEntity != null
                && spawner.isNearPlayer()) {
            spawner.spawnBoss();
        }
    }

    public void setBoss(EntityType<?> pType) {
        bossEntity = pType;
        setChanged();
    }

    public void spawnBoss() {
        if (bossEntity == null || level == null || level.isClientSide) {
            return;
        }

        int nearbyPlayers = level.getEntitiesOfClass(Player.class,
                new AABB(worldPosition, worldPosition.offset(1, 1, 1))
                        .inflate(spawnDistance * 2)).size();
        TheAurorian.LOGGER.debug("Boss spawn nearby players: {}", nearbyPlayers);

        LivingEntity boss = (LivingEntity) bossEntity.spawn(
                (ServerLevel) level,
                null,
                null,
                null,
                worldPosition.above(),
                MobSpawnType.STRUCTURE,
                false,
                false);
        if (boss == null) {
            TheAurorian.LOGGER.warn("Could not spawn boss {} at {}",
                    bossEntity,
                    worldPosition);
            return;
        }

        if (boss instanceof DungeonKeeperEntity keeper) {
            keeper.ensureBossEquipment();
        } else if (boss instanceof MoonQueenEntity queen) {
            queen.ensureBossEquipment();
        }

        if (nearbyPlayers > 1) {
            scaleAttribute(boss, Attributes.MOVEMENT_SPEED,
                    nearbyPlayers * CommonConfig.boss_speed_per_player.get() + 1.0D);
            scaleAttribute(boss, Attributes.ATTACK_DAMAGE,
                    nearbyPlayers * CommonConfig.boss_damage_per_player.get() + 1.0D);
            scaleAttribute(boss, Attributes.MAX_HEALTH,
                    nearbyPlayers * CommonConfig.boss_health_per_player.get() + 1.0D);
            boss.setHealth(boss.getMaxHealth());
        }
        level.destroyBlock(worldPosition, false);
    }

    private static void scaleAttribute(LivingEntity entity,
                                       net.minecraft.world.entity.ai.attributes.Attribute attribute,
                                       double multiplier) {
        var instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(instance.getValue() * multiplier);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        String bossKey = null;
        if (tag.contains("boss")) {
            bossKey = tag.getString("boss");
        } else if (tag.contains("containedboss")) {
            bossKey = mapLegacyBossName(tag.getString("containedboss"));
        }
        bossEntity = resolveBoss(bossKey);
    }

    private EntityType<?> resolveBoss(String rawId) {
        if (rawId == null || rawId.isEmpty()) {
            return null;
        }
        ResourceLocation id = ResourceLocation.tryParse(rawId);
        if (id == null) {
            TheAurorian.LOGGER.warn("Boss spawner at {} has malformed boss id: {}",
                    worldPosition,
                    rawId);
            return null;
        }
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(id);
        if (type == null) {
            TheAurorian.LOGGER.warn("Boss spawner at {} has unknown boss id: {}",
                    worldPosition,
                    rawId);
        }
        return type;
    }

    private static String mapLegacyBossName(String raw) {
        if (raw == null) {
            return null;
        }
        return switch (raw.toLowerCase(Locale.ROOT)) {
            case "spider", "spiderboss", "dungeon_spider" ->
                    "theaurorian:dungeon_spider";
            case "moonqueen", "moonqueenboss", "moon_queen" ->
                    "theaurorian:moon_queen";
            case "keeper", "runestonedungeonkeeper", "dungeon_keeper" ->
                    "theaurorian:dungeon_keeper";
            default -> raw.contains(":") ? raw : "theaurorian:" + raw;
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        if (bossEntity != null) {
            ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(bossEntity);
            if (key != null) {
                tag.putString("boss", key.toString());
            }
        }
        super.saveAdditional(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
