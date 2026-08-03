package shiroroku.theaurorian.Items;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import shiroroku.theaurorian.TheAurorian;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

public class BaseAurorianCurio extends BaseAurorianItem implements ICurioItem {

    private final List<SimpleAttibuteModifier> modifiers = new ArrayList<>();

    public BaseAurorianCurio(Properties properties, Holder<Attribute> attribute, AttributeModifier.Operation operation, double amt) {
        super(properties.stacksTo(1));
        modifiers.add(new SimpleAttibuteModifier(attribute, operation, amt));
    }

    public BaseAurorianCurio(Properties properties, List<SimpleAttibuteModifier> modifiers) {
        super(properties.stacksTo(1));
        this.modifiers.addAll(modifiers);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> map = LinkedHashMultimap.create();
        for (SimpleAttibuteModifier mod : modifiers) {
            map.put(mod.attribute, mod.modifier(id));
        }
        return map;
    }

    public static class SimpleAttibuteModifier {
        private final Holder<Attribute> attribute;
        private final AttributeModifier.Operation operation;
        private final double amount;

        public SimpleAttibuteModifier(Holder<Attribute> attribute, AttributeModifier.Operation operation, double amount) {
            this.attribute = attribute;
            this.operation = operation;
            this.amount = amount;
        }

        private AttributeModifier modifier(ResourceLocation baseId) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "curio/" + baseId.getPath());
            return new AttributeModifier(id, amount, operation);
        }
    }
}
