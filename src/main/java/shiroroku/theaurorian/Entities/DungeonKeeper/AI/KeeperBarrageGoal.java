package shiroroku.theaurorian.Entities.DungeonKeeper.AI;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import shiroroku.theaurorian.Entities.DungeonKeeper.DungeonKeeperEntity;
import shiroroku.theaurorian.Registry.ItemRegistry;

/**
 * Low-HP barrage. Upstream: move 0.25, interval 2, radius 40, HP ≤ 35%.
 */
public class KeeperBarrageGoal<T extends DungeonKeeperEntity> extends RangedBowAttackGoal<T> {

    private final DungeonKeeperEntity keeper;
    private int attackTime = 0;

    public KeeperBarrageGoal(DungeonKeeperEntity pMob) {
        // K5: 0.25 / interval 2 / range 40
        super(pMob, 0.25D, 2, 40.0F);
        keeper = pMob;
    }

    @Override
    public void start() {
        super.start();
        keeper.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemRegistry.silentwood_bow.get()));
        attackTime = 2;
        this.keeper.startUsingItem(ProjectileUtil.getWeaponHoldingHand(keeper, item -> item instanceof BowItem));
    }

    private boolean hasLowHealth() {
        // K2: ≤ 35% (upstream)
        return keeper.getHealth() / keeper.getMaxHealth() <= 0.35f;
    }

    @Override
    public boolean canUse() {
        return hasLowHealth() && keeper.getTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return hasLowHealth() && keeper.getTarget() != null;
    }

    @Override
    public void tick() {
        super.tick();

        if (attackTime > 0) {
            attackTime--;
        }

        LivingEntity target = this.keeper.getTarget();
        if (target == null) {
            return;
        }

        boolean canSee = this.keeper.getSensing().hasLineOfSight(target);

        if (canSee) {
            keeper.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (attackTime <= 0) {
                keeper.stopUsingItem();
                // K5 interval ~2 ticks between shots
                attackTime = 2;
                keeper.performRangedAttack(target, 0.5f);
                this.keeper.startUsingItem(ProjectileUtil.getWeaponHoldingHand(keeper, item -> item instanceof BowItem));
            }
        }
    }

}
