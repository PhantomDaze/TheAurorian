package shiroroku.theaurorian.Registry;

import net.minecraft.core.registries.Registries;

import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import shiroroku.theaurorian.Blocks.MoonlightForge.MoonlightForgeMenu;
import shiroroku.theaurorian.Blocks.Scrapper.ScrapperMenu;
import shiroroku.theaurorian.TheAurorian;

public class MenuRegistry {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, TheAurorian.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<MoonlightForgeMenu>> moonlight_forge = MENUS.register("moonlight_forge", () -> IMenuTypeExtension.create((id, playerInv, data) -> new MoonlightForgeMenu(id, data.readBlockPos(), playerInv, playerInv.player)));
    public static final DeferredHolder<MenuType<?>, MenuType<ScrapperMenu>> scrapper = MENUS.register("scrapper", () -> IMenuTypeExtension.create((id, playerInv, data) -> new ScrapperMenu(id, data.readBlockPos(), playerInv, playerInv.player)));

}
