package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

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
