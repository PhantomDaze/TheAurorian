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

public class BossSpawnerBlockEntity extends BlockEntity {

    public EntityType<?> bossEntity;
    public static final int spawnDistance = 16;

    public BossSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.boss_spawner.get(), pos, state);
    }

    private boolean isNearPlayer() {
        return ModUtil.hasNearbyPlayerAbove(level, worldPosition.getX() + 0.5D, worldPosition.getY(),
                worldPosition.getZ() + 0.5D, spawnDistance,
                player -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)
                        && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(player));
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (!level.isClientSide && blockEntity instanceof BossSpawnerBlockEntity spawner
                && spawner.bossEntity != null && spawner.isNearPlayer()) {
            spawner.spawnBoss();
        }
    }

    public void setBoss(EntityType<?> type) {
        this.bossEntity = type;
    }

    public void spawnBoss() {
        if (bossEntity == null || level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        int nearbyPlayers = level.getEntitiesOfClass(Player.class,
                new AABB(worldPosition).inflate(spawnDistance * 2)).size();
        LivingEntity boss = (LivingEntity) bossEntity.spawn(serverLevel, (CompoundTag) null, null,
                worldPosition.above(), MobSpawnType.STRUCTURE, false, false);
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
                    boss.getAttribute(Attributes.MOVEMENT_SPEED).getValue()
                            * (nearbyPlayers * CommonConfig.boss_speed_per_player.get() + 1));
            boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                    boss.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                            * (nearbyPlayers * CommonConfig.boss_damage_per_player.get() + 1));
            boss.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                    boss.getAttribute(Attributes.MAX_HEALTH).getValue()
                            * (nearbyPlayers * CommonConfig.boss_health_per_player.get() + 1));
            boss.setHealth(boss.getMaxHealth());
        }
        level.destroyBlock(worldPosition, false);
    }

    @Override
    public void load(CompoundTag tag) {
        String bossKey = tag.contains("boss") ? tag.getString("boss")
                : tag.contains("containedboss") ? mapLegacyBossName(tag.getString("containedboss")) : null;
        this.bossEntity = null;
        if (bossKey != null && !bossKey.isEmpty()) {
            ResourceLocation id = ResourceLocation.tryParse(bossKey);
            EntityType<?> type = id == null ? null : ForgeRegistries.ENTITY_TYPES.getValue(id);
            if (type != null && type != EntityType.PIG) {
                this.bossEntity = type;
            } else {
                TheAurorian.LOGGER.warn("Boss spawner at {} has unknown boss id: {}", worldPosition, bossKey);
            }
        }
        super.load(tag);
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
    protected void saveAdditional(CompoundTag tag) {
        if (bossEntity != null) {
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(bossEntity);
            if (id != null) {
                tag.putString("boss", id.toString());
            }
        }
        super.saveAdditional(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
