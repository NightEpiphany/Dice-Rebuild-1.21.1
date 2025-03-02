package com.moigferdsrte.entity.extension;

import com.moigferdsrte.Entrance;
import com.moigferdsrte.entity.refine.DiceCubeEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CarriedDiceCubeEntityModel extends EntityModel<DiceCubeEntity> {

    public final ModelPart belt;
    public final ModelPart dice;
    private final ModelPart root;

    public static final EntityModelLayer LAYER = new EntityModelLayer(Identifier.of(Entrance.MOD_ID, "carried_dice_entity"), "main");


    public CarriedDiceCubeEntityModel(ModelPart dice) {
        this.root = dice;
        this.dice = dice.getChild("carried");
        this.belt = dice.getChild("belt");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData carried = modelPartData.addChild("carried", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -3.0F, 1.0F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 22.0F, -3.0F));
        ModelPartData belt = modelPartData.addChild("belt", ModelPartBuilder.create().uv(0, 14).cuboid(-1.0F, -1.0F, 1.0F, 10.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7418F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        root.render(matrices, vertices, light, overlay, color);
    }

    @Override
    public void setAngles(DiceCubeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}
