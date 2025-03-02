package com.moigferdsrte.entity.refine;

import com.moigferdsrte.Entrance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;

@Environment(EnvType.CLIENT)
public class DiceCubeEntityRenderer extends EntityRenderer<DiceCubeEntity> {

    public static final Identifier TEXTURE = Identifier.of(Entrance.MOD_ID, "textures/entity/d6.png");

    protected final DiceCubeEntityModel dice;

    public DiceCubeEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        dice = new DiceCubeEntityModel(ctx.getPart(DiceCubeEntityModel.LAYER));
    }

    @Override
    public void render(DiceCubeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F + entity.getDataTracker().get(DiceCubeEntity.ROLL_FLOOR)*20));
        matrices.translate(0,-14/16f,0);
        switch (entity.getRandomPoint()){
            case 1 -> {
                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180.0F));
                matrices.translate(0, -2, 0);
            }
            case 2 -> {
                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90.0F));
                matrices.translate(0, -1, 1);
            }
            case 3 -> {
                if (!entity.isOnGround()) {
                    matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(45.0F));
                    matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(45.0F));
                    matrices.translate(-0.5, -0.5f, 0.75f);
                }else {
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
                    matrices.translate(1, -1, 0);
                }
            }
            case 4 -> {
                if (!entity.isOnGround()) {
                    matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(45.0F));
                    matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(45.0F));
                    matrices.translate(-0.5, -0.5f, 0.75f);
                }else {
                    matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(90.0F));
                    matrices.translate(-1, -1, 0);
                }
            }
            case 5 -> {
                if (!entity.isOnGround()) {
                    matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(45.0F));
                    matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(45.0F));
                    matrices.translate(-0.5, -0.5f, 0.75f);
                }else {
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
                    matrices.translate(0, -1, -1);
                }
            }
            default -> {
                if (!entity.isOnGround()) {
                    matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(45.0F));
                    matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(45.0F));
                    matrices.translate(-0.5, -0.5f, 0.75f);
                    matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(-45.0F));
                    matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-45.0F));
                    matrices.translate(0.7, -0.53f, -0.53);
                }
            }
        }
        dice.render(matrices,vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity))),light, OverlayTexture.DEFAULT_UV, DiceCubeEntity.getDyedColor(entity.getColor()));
        matrices.pop();
    }

    @Override
    public Identifier getTexture(DiceCubeEntity entity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLight(DiceCubeEntity entity, BlockPos pos) {
        return entity.getIlluminance() || entity.isOnFire() ? 15 : entity.getWorld().getLightLevel(LightType.BLOCK, pos);
    }
}
