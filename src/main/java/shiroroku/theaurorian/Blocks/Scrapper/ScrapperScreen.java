package shiroroku.theaurorian.Blocks.Scrapper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import shiroroku.theaurorian.TheAurorian;

public class ScrapperScreen extends AbstractContainerScreen<ScrapperMenu> {

    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "textures/gui/scrapper.png");

    public ScrapperScreen(ScrapperMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

        float rotation = (Minecraft.getInstance().level.getGameTime() + pPartialTick) * 4 * (this.menu.isCrafting() ? 1 : 0);

        graphics.pose().pushPose();
        graphics.pose().translate(relX + 69, relY + 37, 0);
        graphics.pose().translate(8, 8, 0);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(rotation));
        graphics.pose().translate(-8, -8, 0);
        graphics.blit(GUI, 0, 0, 176, 55, 16, 16);
        graphics.pose().popPose();

        graphics.pose().pushPose();
        graphics.pose().translate(relX + 91, relY + 37, 0);
        graphics.pose().translate(8, 8, 0);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(-rotation));
        graphics.pose().translate(-8, -8, 0);
        graphics.blit(GUI, 0, 0, 176, 55, 16, 16);
        graphics.pose().popPose();

        if (this.menu.isCrafting()) {
            graphics.pose().pushPose();
            graphics.pose().translate(relX + 86, relY + 35, 0);
            graphics.blit(GUI, 0, 0, 176, 0, 5, (int) (21 * this.menu.craftingProgress()));
            graphics.pose().popPose();
        }
    }
}
