package rearth.oritech.client.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import rearth.oritech.block.base.block.FrameInteractionBlock;
import rearth.oritech.block.base.entity.FrameInteractionBlockEntity;
import rearth.oritech.init.BlockContent;

@Environment(EnvType.CLIENT)
public class MachineGantryRenderer implements BlockEntityRenderer<FrameInteractionBlockEntity> {
    
    private static final BlockState renderedBeam = BlockContent.FRAME_GANTRY_ARM.getDefaultState();
    
    @Override
    public void render(FrameInteractionBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        
        var state = entity.getCachedState();
        if (!state.get(FrameInteractionBlock.HAS_FRAME) || entity.getAreaMin() == null || entity.getLastTarget() == null)
            return;
        
        
        var renderedPosition = Vec3d.of(entity.getCurrentTarget());
        var time = entity.getWorld().getTime() + tickDelta;
        var passedTime = time - entity.getMoveStartedAt();
        
        // check if currently moving, otherwise we can skip those calculations
        if (time < entity.getMoveStartedAt() + entity.getMoveTime()) {
            var movementDoneAmount = passedTime / (float) entity.getMoveTime();
            var offset = Vec3d.of(entity.getCurrentTarget().subtract(entity.getLastTarget())).multiply(movementDoneAmount);
            renderedPosition = Vec3d.of(entity.getLastTarget()).add(offset);
        }
        var targetOffset = renderedPosition.subtract(Vec3d.of(entity.getPos()));
        
        matrices.push();
        matrices.translate(targetOffset.getX(), targetOffset.getY(), targetOffset.getZ());
        
        var pos = entity.getCurrentTarget(); // relevant for correct lighting, actual position is determined by matrix
        
        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(
          entity.getMachineHead(),
          pos,
          entity.getWorld(),
          matrices,
          vertexConsumers.getBuffer(RenderLayers.getBlockLayer(state)),
          false,
          entity.getWorld().random);
        
        matrices.pop();
        
        matrices.push();
        
        var length = entity.getAreaMax().getX() - entity.getAreaMin().getX() + 1;
        var target = new Vec3d(entity.getAreaMin().getX(), renderedPosition.y, renderedPosition.z).subtract(Vec3d.of(entity.getPos()));
        
        matrices.translate(target.getX(), target.getY(), target.getZ());
        matrices.scale(length, 1, 1);
        
        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(
          renderedBeam,
          pos,
          entity.getWorld(),
          matrices,
          vertexConsumers.getBuffer(RenderLayers.getBlockLayer(state)),
          false,
          entity.getWorld().random);
        
        matrices.pop();
        
        
    }
}
