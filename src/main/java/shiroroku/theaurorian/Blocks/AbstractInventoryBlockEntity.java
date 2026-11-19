package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public abstract class AbstractInventoryBlockEntity extends BlockEntity {

    private final ItemStackHandler itemHandler = createItemHandler();
    // 缓存的完整背包能力：side == null 或不限制分面时返回
    private LazyOptional<IItemHandler> fullItemHandler = LazyOptional.of(() -> itemHandler);
    // 按面缓存的包装能力，保证同一面每次返回同一实例
    private final Map<Direction, LazyOptional<IItemHandler>> sidedItemHandlers = new EnumMap<>(Direction.class);

    public AbstractInventoryBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    protected abstract ItemStackHandler createItemHandler();

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            // side == null 是内部访问，始终暴露完整背包
            if (side == null) {
                return getFullItemHandler().cast();
            }
            int[] slots = getSlotsForFace(side);
            if (slots == null) {
                // 默认行为：所有面暴露全部槽位（保持子类旧行为）
                return getFullItemHandler().cast();
            }
            // 每个面只创建一个包装实例并缓存，保证同一面返回稳定的 LazyOptional
            return sidedItemHandlers.computeIfAbsent(side, s -> LazyOptional.of(() -> createSidedHandler(slots, s))).cast();
        }
        return super.getCapability(cap, side);
    }

    /**
     * 返回缓存的完整背包能力。若之前已被 invalidateCaps 失效（例如区块卸载后重载），则重新建立。
     */
    private LazyOptional<IItemHandler> getFullItemHandler() {
        if (!fullItemHandler.isPresent()) {
            fullItemHandler = LazyOptional.of(() -> itemHandler);
        }
        return fullItemHandler;
    }

    @Override
    public void invalidateCaps() {
        fullItemHandler.invalidate();
        sidedItemHandlers.values().forEach(LazyOptional::invalidate);
        sidedItemHandlers.clear();
        super.invalidateCaps();
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
     * 基类只是 BlockEntity（并非 Container），所以无法用 WorldlyContainer/SidedInvWrapper。
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
    public void load(CompoundTag tag) {
        if (tag.contains("items")) {
            itemHandler.deserializeNBT(tag.getCompound("items"));
        }
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("items", itemHandler.serializeNBT());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        this.saveAdditional(nbt);
        return nbt;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void updateClient() {
        level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
    }
}