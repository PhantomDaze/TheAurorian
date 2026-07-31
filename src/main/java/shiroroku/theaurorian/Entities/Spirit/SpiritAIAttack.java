package shiroroku.theaurorian.Entities.Spirit;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class SpiritAIAttack extends MeleeAttackGoal {
    private final SpiritEntity zombie;
    private int raiseArmTicks;

    public SpiritAIAttack(SpiritEntity zombieIn, double speedIn, boolean longMemoryIn) {
        super(zombieIn, speedIn, longMemoryIn);
        this.zombie = zombieIn;
    }

    @Override
    public void start() {
        super.start();
        this.raiseArmTicks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.zombie.setArmsRaised(false);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.raiseArmTicks;
        this.zombie.setArmsRaised(this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < 10);
    }
}
