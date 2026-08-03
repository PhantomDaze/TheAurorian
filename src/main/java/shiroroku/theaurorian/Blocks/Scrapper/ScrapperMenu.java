package shiroroku.theaurorian.Blocks.Scrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import shiroroku.theaurorian.Blocks.AbstractModContainerMenu;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.MenuRegistry;

public class ScrapperMenu extends AbstractModContainerMenu {

    private final ScrapperBlockEntity blockEntity;

    public ScrapperMenu(int containerId, BlockPos pos, Inventory playerInventory, Player playerIn) {
        super(MenuRegistry.scrapper.get(), containerId, 3);
        blockEntity = (ScrapperBlockEntity) playerIn.getCommandSenderWorld().getBlockEntity(pos);
        if (blockEntity != null) {
            var handler = blockEntity.getItemHandler();
            addSlot(new SlotItemHandler(handler, 0, 40, 37));
            addSlot(new SlotItemHandler(handler, 1, 80, 17));
            addSlot(new SlotItemHandler(handler, 2, 80, 58));
        }
        addPlayerSlots(new InvWrapper(playerInventory));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, BlockRegistry.scrapper.get());
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
}
