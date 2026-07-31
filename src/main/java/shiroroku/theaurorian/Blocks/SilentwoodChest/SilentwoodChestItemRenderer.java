package shiroroku.theaurorian.Blocks.SilentwoodChest;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renders the silentwood chest as a 3D model in the inventory and in-hand,
 * instead of the flat 2D sprite used by the default block item model.
 */
public class SilentwoodChestItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final SilentwoodChestItemRenderer INSTANCE = new SilentwoodChestItemRenderer();

    private SilentwoodChestBlockRenderer<SilentwoodChestBlockEntity> renderer;
    private SilentwoodChestBlockEntity entity;
    private boolean initialized = false;

    public SilentwoodChestItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    private void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        Minecraft minecraft = Minecraft.getInstance();
        BlockEntityRendererProvider.Context context = new BlockEntityRendererProvider.Context(
                minecraft.getBlockEntityRenderDispatcher(),
                minecraft.getBlockRenderer(),
                minecraft.getItemRenderer(),
                minecraft.getEntityRenderDispatcher(),
                minecraft.getEntityModels(),
                minecraft.font);
        this.renderer = new SilentwoodChestBlockRenderer<>(context);
        BlockState blockState = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
        this.entity = new SilentwoodChestBlockEntity(BlockPos.ZERO, blockState);
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemTransforms.TransformType pTransformType, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        init();
        pPoseStack.pushPose();
        pPoseStack.scale(0.5F, 0.5F, 0.5F);
        this.renderer.render(this.entity, 0.0F, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        pPoseStack.popPose();
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
    }
}
