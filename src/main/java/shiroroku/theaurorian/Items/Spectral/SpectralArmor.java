package shiroroku.theaurorian.Items.Spectral;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Items.BaseAurorianArmor;
import shiroroku.theaurorian.TheAurorian;

import java.util.List;

public class SpectralArmor extends BaseAurorianArmor {

    public SpectralArmor(ArmorMaterial pMaterial, ArmorItem.Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    /**
     * Spectral armor is rendered by {@link SpectralArmorLayer} with a translucent
     * render type. Pointing the vanilla armor layer at a fully transparent
     * texture stops it from double-rendering the piece opaque.
     */
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return TheAurorian.MODID + ":textures/models/armor/spectral_transparent.png";
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, Spectral.appendHoverText(pTooltipComponents), pIsAdvanced);
    }
}
