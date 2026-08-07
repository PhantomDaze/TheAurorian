package shiroroku.theaurorian.Compat.TinkersConstruct;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.TheAurorian;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/**
 * Port of the 1.12 "Moonlit" Tinkers trait (and its Construct's Armory armor variant).
 * In the Aurorian dimension or overworld at night there is a chance to negate durability
 * damage entirely; in daylight there is a chance to take extra damage. Applies to both
 * tools and armor, since TConstruct 3.x armor is a tool stack.
 */
public class MoonlitModifier extends Modifier implements ToolDamageModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE);
    }

    @Override
    public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
        if (holder == null) {
            return amount;
        }
        Level level = holder.level();
        boolean moonlit = level.dimension() == TheAurorian.the_aurorian
                || (level.dimension() == Level.OVERWORLD && !level.isDay());
        float chance = CommonConfig.moonstone_damage_chance.get().floatValue();
        if (moonlit) {
            if (level.getRandom().nextFloat() < chance) {
                return 0;
            }
        } else if (level.isDay()) {
            if (level.getRandom().nextFloat() < chance) {
                return 2;
            }
        }
        return amount;
    }
}
