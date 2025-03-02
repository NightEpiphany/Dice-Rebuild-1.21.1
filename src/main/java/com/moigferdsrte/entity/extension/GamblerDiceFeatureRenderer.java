package com.moigferdsrte.entity.extension;

import com.moigferdsrte.Entrance;
import com.moigferdsrte.entity.refine.DiceCubeEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class GamblerDiceFeatureRenderer extends FeatureRenderer<GamblerEntity, IllagerEntityModel<GamblerEntity>> {

    private final ModelPart diceCubeEntityModelDice;

    private final ModelPart diceCubeEntityModelBelt;

    public GamblerDiceFeatureRenderer(
            FeatureRendererContext<GamblerEntity, IllagerEntityModel<GamblerEntity>> context, CarriedDiceCubeEntityModel diceCubeEntityModel
    ) {
        super(context);
        this.diceCubeEntityModelDice = diceCubeEntityModel.dice;
        this.diceCubeEntityModelBelt = diceCubeEntityModel.belt;
    }

    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            GamblerEntity entity,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    )
    {
        matrices.push();
        ModelPart body = this.getContextModel().getPart().getChild(EntityModelPartNames.BODY);
        body.rotate(matrices);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
        float wave = MathHelper.cos(entity.getWorld().getRandom().nextFloat()) * 0.333f - 0.25f;
        float animationTick = (float)((entity.getWorld().getTime() % 628.3) + tickDelta - wave) * 0.1f;
        matrices.translate(-0.48F + MathHelper.sin(animationTick) * 0.02f, -1.375F + MathHelper.sin(animationTick) * 0.08f, 0.88F);
        this.diceCubeEntityModelDice.render(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityCutout(Identifier.of(Entrance.MOD_ID, "textures/entity/d6.png"))),
                light,
                OverlayTexture.DEFAULT_UV,
                DiceCubeEntity.getDyedColor(entity.getPreferenceColor())
        );
        matrices.translate(-0.1, 1.375f, -0.16);
        if (!entity.isInvisible()) this.diceCubeEntityModelBelt.render(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityCutout(Identifier.of(Entrance.MOD_ID, "textures/entity/d6.png"))),
                light,
                OverlayTexture.DEFAULT_UV,
                -1
        );
        matrices.pop();
    }
}
