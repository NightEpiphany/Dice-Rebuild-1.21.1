package com.moigferdsrte.entity.extension;


import com.moigferdsrte.Entrance;
import com.moigferdsrte.entity.refine.RefineRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GamblerEntityRenderer extends IllagerEntityRenderer<GamblerEntity> {

    private static final Identifier TEXTURE = Identifier.of(Entrance.MOD_ID, "textures/entity/illager/gambler.png");

    private static final Identifier TEXTURE_ANGRY = Identifier.of(Entrance.MOD_ID, "textures/entity/illager/gambler_angry.png");

    public GamblerEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new IllagerEntityModel<>(ctx.getPart(RefineRegistry.GAMBLER_LAYER)), 0.5f);
        this.addFeature(new GamblerDiceFeatureRenderer(this, new CarriedDiceCubeEntityModel(ctx.getPart(CarriedDiceCubeEntityModel.LAYER))));
        this.addFeature(new HeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()){
            public void render(
                    MatrixStack matrixStack,
                    VertexConsumerProvider vertexConsumerProvider,
                    int i,
                    GamblerEntity gamblerEntity,
                    float f,
                    float g,
                    float h,
                    float j,
                    float k,
                    float l
            ) {
                if (gamblerEntity.getState() != IllagerEntity.State.CROSSED) {
                    super.render(matrixStack, vertexConsumerProvider, i, gamblerEntity, f, g, h, j, k, l);
                }
            }
        });

        this.addFeature(new VillagerHeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()){
            public void render(
                    MatrixStack matrixStack,
                    VertexConsumerProvider vertexConsumerProvider,
                    int i,
                    GamblerEntity gamblerEntity,
                    float f,
                    float g,
                    float h,
                    float j,
                    float k,
                    float l)
            {
                if (gamblerEntity.getState() == IllagerEntity.State.CROSSED){
                    super.render(matrixStack, vertexConsumerProvider, i, gamblerEntity, f, g, h, j, k, l);
                }
            }
        });
    }

    @Override
    public Identifier getTexture(GamblerEntity entity) {
        return entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }
}
