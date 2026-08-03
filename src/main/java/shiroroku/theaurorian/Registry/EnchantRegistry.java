package shiroroku.theaurorian.Registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import shiroroku.theaurorian.TheAurorian;

public class EnchantRegistry {

    public static final ResourceKey<Enchantment> LIGHTNING = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "lightning"));
    public static final ResourceKey<Enchantment> LIGHTNING_RESISTANCE = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "lightning_resistance"));

    // Note: enchantments are now data-driven via datapack json under data/theaurorian/enchantment/
    // Effects applied via event handlers (see EventsForge and custom logic ported from old subclasses)

}
