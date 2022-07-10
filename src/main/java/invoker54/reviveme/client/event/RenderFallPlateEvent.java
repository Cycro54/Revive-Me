package invoker54.reviveme.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import invoker54.reviveme.ReviveMe;
import invoker54.reviveme.client.gui.render.CircleRender;
import invoker54.reviveme.common.capability.FallenCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;

import static invoker54.reviveme.client.event.FallScreenEvent.*;
import static net.minecraft.client.gui.AbstractGui.blit;
import static net.minecraft.client.gui.AbstractGui.fill;

@Mod.EventBusSubscriber(modid = ReviveMe.MOD_ID, value = Dist.CLIENT)
public class RenderFallPlateEvent {
    private static final Minecraft inst = Minecraft.getInstance();
    private static final DecimalFormat df = new DecimalFormat("0.0");
    private static final int progCircle = new Color(39, 235, 86, 255).getRGB();
    private static final int blackBg = new Color(0, 0, 0, 255).getRGB();

    @SubscribeEvent
    public static void renderWorldFallTimer(RenderPlayerEvent.Post event){
        if (!(event.getEntity() instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) event.getEntity();
        FallenCapability cap = FallenCapability.GetFallCap(player);
        MatrixStack stack = event.getMatrixStack();
        FontRenderer font = inst.font;
        if (player.equals(inst.player)) return;

        if (!cap.isFallen()) return;

        float f = player.getBbHeight() * 0.5f;
        stack.pushPose();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        //Getting into position
        stack.translate(0, f, 0);
        stack.mulPose(inst.getEntityRenderDispatcher().cameraOrientation());
        stack.scale(-0.025F, -0.025F, 0.025F);

        //Green circular progress
        CircleRender.drawArc(stack, 0,0,22,0,cap.GetTimeLeft(true) * 360,progCircle);

        TextureManager tManager = inst.textureManager;

        //Timer texture
        tManager.bind(Timer_TEXTURE);
        blit(stack, -16, -16, 0, 0F, 0F, 32, 32, 32, 32);
        tManager.release(Timer_TEXTURE);

        //Revive type background
        fill(stack,4,-12,12, -4, blackBg);

        //Revive type item texture
        switch (cap.getPenaltyType()) {
            case NONE:
                break;
            case HEALTH:
                tManager.bind(HEALTH_TEXTURE);
                blit(stack,4,-12,0,0F,0F,8,8,8,8);
                tManager.release(HEALTH_TEXTURE);
                break;
            case EXPERIENCE:
                tManager.bind(EXPERIENCE_TEXTURE);
                blit(stack,4,-12,0,0F,0F,8,8,8,8);
                tManager.release(EXPERIENCE_TEXTURE);
                break;
            case FOOD:
                tManager.bind(FOOD_TEXTURE);
                blit(stack,4,-12,0,0F,0F,8,8,8,8);
                tManager.release(FOOD_TEXTURE);
                break;
        }

        //Penalty txt
        String penaltyAmount = Integer.toString(cap.getPenaltyAmount());
        int txtWidth =  font.width(penaltyAmount);
        int txtHeight = 9;
        int txtColor = (cap.hasEnough(inst.player) == true ? TextFormatting.GREEN.getColor() : TextFormatting.RED.getColor());
        float scale = (font.width("0")/(float)txtWidth) * 2.5f;
        scale = Float.parseFloat(df.format(scale));
        stack.scale(scale, scale, scale);

        renderText(player, Integer.toString(cap.getPenaltyAmount()), stack, event.getBuffers(), event.getLight(), txtColor, txtWidth, txtHeight, scale);

        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        stack.popPose();
        //drawInternal(font,penaltyAmount,-txtHalfWidth/2, -9/2, txtColor, false, stack.last().pose(), event.getRenderTypeBuffer(), event.getPackedLight());
    }

    private static void renderText(PlayerEntity player, String text, MatrixStack stack, IRenderTypeBuffer buffer,
                                   int lightcoords, int txtColor, int width, int height, float scale){
        boolean flag = !player.isDiscrete();
        float f = player.getBbHeight() * 0.5f;
        int i = "deadmau5".equals(text) ? -10 : 0;
        Matrix4f matrix4f = stack.last().pose();

        //float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int j = (int)(0 * 255.0F) << 24;
        FontRenderer fontrenderer = inst.font;
        float x = -width/2;
        float y = -height/2;

        fontrenderer.drawInBatch(text, x, y, txtColor, true, matrix4f, buffer, flag, j, lightcoords);
        if (flag) {
            fontrenderer.drawInBatch(text, x, y, txtColor, true, matrix4f, buffer, false, 0, lightcoords);
        }
    }


    public static void testRender(MatrixStack stack, int x, int y, int width, int height, int colorCode){
        Matrix4f lastPos = stack.last().pose();

        if (x > width) {
            int i = x;
            x = width;
            width = i;
        }

        if (y > height) {
            int j = y;
            y = height;
            height = j;
        }

        float f3 = (float)(colorCode >> 24 & 255) / 255.0F;
        float f = (float)(colorCode >> 16 & 255) / 255.0F;
        float f1 = (float)(colorCode >> 8 & 255) / 255.0F;
        float f2 = (float)(colorCode & 255) / 255.0F;

        //System.out.println(f3);
        //System.out.println(f);
        //System.out.println(f1);
        //System.out.println(f2);

        RenderSystem.disableTexture();
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        bufferbuilder.vertex(lastPos, (float) x, (float) height, 0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.vertex(lastPos, (float) width, (float) height, 0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.vertex(lastPos, (float) width, (float) y, 0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.vertex(lastPos, (float) x, (float) y, 0F).color(f, f1, f2, f3).endVertex();

        bufferbuilder.end();
        WorldVertexBufferUploader.end(bufferbuilder);
        RenderSystem.enableTexture();

    }
}
