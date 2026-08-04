package shiroroku.theaurorian.Blocks.BossSpawner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.phys.Vec3;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.Entities.Boss.MoonQueenEntity;
import shiroroku.theaurorian.Entities.DungeonKeeper.DungeonKeeperEntity;
import shiroroku.theaurorian.Registry.BlockEntityRegistry;
import shiroroku.theaurorian.TheAurorian;
import shiroroku.theaurorian.Util.ModUtil;

public class BossSpawnerBlockEntity extends BlockEntity {

    public EntityType<?> bossEntity = null;
    public static final int spawnDistance = 16;

    public BossSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.boss_spawner.get(), pos, state);
    }

    private boolean isNearPlayer() {
        return ModUtil.hasNearbyPlayerAbove(level,
                worldPosition.getX() + 0.5D, worldPosition.getY(), worldPosition.getZ() + 0.5D,
                spawnDistance,
                (player) -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player) && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(player));
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState blockState, T t) {
        if (level.isClientSide) {
            return;
        }
        if (t instanceof BossSpawnerBlockEntity spawner) {
            if (spawner.bossEntity != null && spawner.isNearPlayer()) {
                spawner.spawnBoss();
            }
        }
    }

    public void setBoss(EntityType<?> type) {
        bossEntity = type;
        setChanged();
    }

    public void spawnBoss() {
        if (bossEntity == null || this.level == null || this.level.isClientSide) {
            return;
        }

        int nearbyPlayers = level.getEntitiesOfClass(Player.class,
                new AABB(worldPosition.getCenter(), worldPosition.offset(1, 1, 1).getCenter()).inflate(spawnDistance * 2)).size();
        TheAurorian.LOGGER.debug("Boss spawn nearby players: {}", nearbyPlayers);

        LivingEntity boss = (LivingEntity) bossEntity.spawn(
                (ServerLevel) this.level,
                worldPosition.above(),
                MobSpawnType.STRUCTURE
        );
        if (boss == null) {
            return;
        }
        if (boss instanceof DungeonKeeperEntity keeper) {
            keeper.ensureBossEquipment();
        } else if (boss instanceof MoonQueenEntity queen) {
            queen.ensureBossEquipment();
        }
        if (nearbyPlayers > 1) {
            boss.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(
                    boss.getAttribute(Attributes.MOVEMENT_SPEED).getValue() * ((nearbyPlayers * CommonConfig.boss_speed_per_player.get()) + 1));
            boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                    boss.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * ((nearbyPlayers * CommonConfig.boss_damage_per_player.get()) + 1));
            boss.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                    boss.getAttribute(Attributes.MAX_HEALTH).getValue() * ((nearbyPlayers * CommonConfig.boss_health_per_player.get()) + 1));
            boss.setHealth(boss.getMaxHealth());
        }
        this.level.destroyBlock(this.worldPosition, false);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        String bossKey = null;
        if (tag.contains("boss")) {
            bossKey = tag.getString("boss");
        } else if (tag.contains("containedboss")) {
            // 1.12 / unremapped structure NBT
            bossKey = mapLegacyBossName(tag.getString("containedboss"));
        }
        if (bossKey != null && !bossKey.isEmpty()) {
            ResourceLocation id = ResourceLocation.tryParse(bossKey);
            if (id != null) {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
                // get() falls back to pig for unknown ids on some mappings — reject pig unless explicitly requested
                if (type != null && type != EntityType.PIG) {
                    this.bossEntity = type;
                } else {
                    TheAurorian.LOGGER.warn("Boss spawner at {} has unknown boss id: {}", worldPosition, bossKey);
                    this.bossEntity = null;
                }
            }
        }
    }

    private static String mapLegacyBossName(String raw) {
        if (raw == null) {
            return null;
        }
        return switch (raw.toLowerCase(java.util.Locale.ROOT)) {
            case "spider", "spiderboss", "dungeon_spider" -> "theaurorian:dungeon_spider";
            case "moonqueen", "moonqueenboss", "moon_queen" -> "theaurorian:moon_queen";
            case "keeper", "runestonedungeonkeeper", "dungeon_keeper" -> "theaurorian:dungeon_keeper";
            default -> raw.contains(":") ? raw : "theaurorian:" + raw;
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (bossEntity != null) {
            ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(bossEntity);
            if (key != null) {
                tag.putString("boss", key.toString());
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        this.saveAdditional(nbt, registries);
        return nbt;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
