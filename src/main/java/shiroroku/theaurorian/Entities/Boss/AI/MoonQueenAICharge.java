package shiroroku.theaurorian.Entities.Boss.AI;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import shiroroku.theaurorian.Entities.Boss.MoonQueenEntity;
import shiroroku.theaurorian.Registry.ItemRegistry;

import java.util.EnumSet;

/**
 * Moon Queen charge attack. Winds up, dashes at the player, blocks with a
 * shield during the dash, and chain-charges when low on health if she misses.
 */
public class MoonQueenAICharge extends Goal {

    private final MoonQueenEntity entity;
    private int chargeTime = 0;
    private final int maxChargeTime = 20;
    private int chargeCooldown = 0;
    private int chargeWindup = 0;
    private final double chargeSpeed = 3.5D;
    private final int minDistance = 2;
    private final int maxDistance = 20;
    private double targetX;
    private double targetY;
    private double targetZ;
    private final int attackReach = 2;
    private final boolean chainCharge;
    private int maxChainCharges = 0;
    private int queuedChainCharges = 0;

    public MoonQueenAICharge(MoonQueenEntity attacker, boolean chainCharge) {
        this.entity = attacker;
        this.chainCharge = chainCharge;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.TARGET));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        if (this.entity.getTarget() != null) {
            float dist = this.entity.distanceTo(this.entity.getTarget());
            if (dist >= this.minDistance && dist <= this.maxDistance) {
                if (this.entity.hasLineOfSight(this.entity.getTarget())) {
                    if (this.chargeCooldown == 0) {
                        return true;
                    } else if (this.chargeCooldown > 0) {
                        this.chargeCooldown--;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.entity.getNavigation().stop();
        this.entity.setCharging(true);
        this.startChargeAttack();
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.entity.getNavigation().isDone() || this.entity.getTarget() != null) {
            if (this.entity.getTarget() != null) {
                if (this.entity.hasLineOfSight(this.entity.getTarget())) {
                    if (this.chargeCooldown == 0) {
                        return true;
                    }
                }
            } else {
                if (this.chargeCooldown == 0) {
                    return true;
                }
            }
        }
        this.entity.setCharging(false);
        return false;
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();

        if (this.chargeTime > 0) {
            this.chargeTime--;
        }

        // A tick before we charge we set the location, so the player has a chance to dodge.
        if (this.chargeWindup == 1) {
            this.setChargeLocation(this.entity.getTarget());
        }

        if (this.chargeWindup == 0) {
            this.entity.getLookControl().setLookAt(this.targetX, this.targetY + this.entity.getEyeHeight(), this.targetZ, 30.0F, 30.0F);
            this.entity.setWindingUpCharge(false);
            this.entity.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.chargeSpeed);

            if (this.entity.getTarget() != null) {
                if (this.entity.distanceTo(this.entity.getTarget()) <= this.attackReach) {
                    this.finishChargeAttack(target);
                } else if (this.entity.getNavigation().isDone()) {
                    this.finishChargeAttack(null);
                }
            } else if (this.entity.getNavigation().isDone()) {
                this.finishChargeAttack(null);
            }
        } else {
            if (target != null) {
                this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
            this.entity.setWindingUpCharge(true);
            this.chargeWindup--;
        }
    }

    private void startChargeAttack() {
        this.chargeTime = this.maxChargeTime;

        // Block with a shield while charging
        this.entity.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ItemRegistry.moonstone_shield.get()));
        this.entity.startUsingItem(InteractionHand.OFF_HAND);
        this.chargeWindup = 40 - this.entity.getRandom().nextInt(10);

        if (this.chainCharge) {
            double healthScale = this.entity.getHealth() / this.entity.getMaxHealth();
            // Q2: explicit upstream half-open intervals
            if (healthScale >= 0.75) {
                this.maxChainCharges = 0;
            } else if (healthScale >= 0.50 && healthScale < 0.75) {
                this.maxChainCharges = 1;
            } else if (healthScale >= 0.25 && healthScale < 0.50) {
                this.maxChainCharges = 2;
            } else if (healthScale >= 0 && healthScale < 0.25) {
                this.maxChainCharges = 3;
            } else {
                this.maxChainCharges = 0;
            }
        }
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
        this.entity.stopUsingItem();
    }

    private void finishChargeAttack(LivingEntity target) {
        this.chargeCooldown = 60 - this.entity.getRandom().nextInt(10);
        this.entity.stopUsingItem();
        if (target != null) {
            this.entity.setChargeHit(true);
            this.entity.doHurtTarget(target);
            target.setDeltaMovement(target.getDeltaMovement().add(0, 0.25, 0));
        } else if (this.chainCharge) {
            this.handleChainCharge();
        }
        this.entity.setCharging(false);
    }

    private void handleChainCharge() {
        if (this.maxChainCharges != 0) {
            if (this.queuedChainCharges != 0) {
                this.chargeCooldown = 5;
                this.queuedChainCharges--;
            } else {
                this.queuedChainCharges = this.maxChainCharges;
            }
        }
    }

    private void setChargeLocation(LivingEntity target) {
        if (target != null) {
            this.targetX = target.getX();
            this.targetY = target.getY();
            this.targetZ = target.getZ();
        }
    }
}
