package shiroroku.theaurorian.Blocks.MoonlightForge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import shiroroku.theaurorian.Blocks.AbstractModContainerMenu;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.MenuRegistry;

public class MoonlightForgeMenu extends AbstractModContainerMenu {

    private final MoonlightForgeBlockEntity blockEntity;

    public MoonlightForgeMenu(int containerId, BlockPos pos, Inventory playerInventory, Player playerIn) {
        super(MenuRegistry.moonlight_forge.get(), containerId, 3);
        blockEntity = (MoonlightForgeBlockEntity) playerIn.getCommandSenderWorld().getBlockEntity(pos);
        if (blockEntity != null) {
            var handler = blockEntity.getItemHandler();
            addSlot(new SlotItemHandler(handler, 0, 22, 35));
            addSlot(new SlotItemHandler(handler, 1, 84, 35));
            addSlot(new SlotItemHandler(handler, 2, 142, 35));
        }
        addPlayerSlots(new InvWrapper(playerInventory));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, BlockRegistry.moonlight_forge.get());
    }

    public float craftingProgress() {
        if (blockEntity.cachedRecipe == null) {
            return 0;
        }
        int time = blockEntity.getCraftingTime(blockEntity.cachedRecipe);
        return time == 0 ? 0 : (float) blockEntity.craftingProgress / time;
    }

    public boolean isCrafting() {
        return blockEntity.isCrafting();
    }

    public boolean canSeeMoon() {
        return blockEntity.canSeeMoon();
    }
}
