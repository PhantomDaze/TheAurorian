package shiroroku.theaurorian.Entities.DungeonKeeper.AI;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import shiroroku.theaurorian.Entities.DungeonKeeper.DungeonKeeperEntity;
import shiroroku.theaurorian.Registry.EnchantRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;

public class KeeperMeleeGoal extends MeleeAttackGoal {

    private final DungeonKeeperEntity mob;

    public KeeperMeleeGoal(DungeonKeeperEntity keeper) {
        super(keeper, 1D, false);
        this.mob = keeper;
    }

    private boolean isInRange() {
        float range = 4;
        return mob.getTarget() != null && mob.distanceTo(mob.getTarget()) <= range;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && isInRange();
    }

    @Override
    public void start() {
        super.start();
        ItemStack sword = new ItemStack(ItemRegistry.moonstone_sword.get());
        if (mob.level() instanceof ServerLevel sl) {
            var reg = sl.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            sword.enchant(reg.getHolderOrThrow(Enchantments.KNOCKBACK), 2);
            sword.enchant(reg.getHolderOrThrow(EnchantRegistry.LIGHTNING), 3);
        }
        mob.setItemInHand(InteractionHand.MAIN_HAND, sword);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && isInRange();
    }

    @Override
    public void stop() {
        super.stop();
        mob.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemRegistry.silentwood_bow.get()));
    }
}
