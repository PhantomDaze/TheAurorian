package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Inventory BE base. Item handler is exposed via {@link shiroroku.theaurorian.Registry.CapabilityRegistry}
 * (RegisterCapabilitiesEvent), not legacy LazyOptional getCapability overrides.
 */
public abstract class AbstractInventoryBlockEntity extends BlockEntity {

    private final ItemStackHandler itemHandler = createItemHandler();

    public AbstractInventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract ItemStackHandler createItemHandler();

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    /**
     * 按面暴露物品栏。side == null 表示内部访问，始终返回完整背包。
     * 子类默认不做分面限制（getSlotsForFace 返回 null），保持旧行为。
     */
    public IItemHandler getItemHandler(@Nullable Direction side) {
        if (side == null) {
            return itemHandler;
        }
        int[] slots = getSlotsForFace(side);
        if (slots == null) {
            return itemHandler;
        }
        return createSidedHandler(slots, side);
    }

    /**
     * 返回指定面可见的槽位索引；返回 null 表示不做分面限制（保持旧行为）。
     */
    @Nullable
    protected int[] getSlotsForFace(Direction side) {
        return null;
    }

    /**
     * 是否允许通过指定面把物品放进槽位。
     */
    protected boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
        return itemHandler.isItemValid(slot, stack);
    }

    /**
     * 是否允许通过指定面取出槽位物品。
     */
    protected boolean canTakeItemThroughFace(int slot, Direction side) {
        return true;
    }

    /**
     * 把内部 ItemStackHandler 包装成按面限定的 IItemHandler。
     */
    private IItemHandler createSidedHandler(int[] slots, Direction side) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return slots.length;
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return itemHandler.getStackInSlot(slots[slot]);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                int index = slots[slot];
                return canPlaceItemThroughFace(index, stack, side) ? itemHandler.insertItem(index, stack, simulate) : stack;
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                int index = slots[slot];
                return canTakeItemThroughFace(index, side) ? itemHandler.extractItem(index, amount, simulate) : ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                return itemHandler.getSlotLimit(slots[slot]);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return canPlaceItemThroughFace(slots[slot], stack, side);
            }
        };
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("items")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("items"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("items", itemHandler.serializeNBT(registries));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        this.saveAdditional(nbt, registries);
        return nbt;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void updateClient() {
        if (level != null) {
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
        }
    }
}
