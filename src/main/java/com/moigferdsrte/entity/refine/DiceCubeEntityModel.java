package com.moigferdsrte.entity.refine;

import com.moigferdsrte.Entrance;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;


public class DiceCubeEntityModel extends EntityModel<DiceCubeEntity> {

    private final ModelPart dice;

    public static final EntityModelLayer LAYER = new EntityModelLayer(Identifier.of(Entrance.MOD_ID, "dice_entity"), "main");
    public static final EntityModelLayer STICKY_LAYER = new EntityModelLayer(Identifier.of(Entrance.MOD_ID, "sticky_dice_entity"), "main");

    public DiceCubeEntityModel(ModelPart root) {
        this.dice = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData dice = modelPartData.addChild("dice", ModelPartBuilder.create().uv(0, 0).cuboid(2.5f, -2.5f, -2.5f, 5.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, 16.5F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(DiceCubeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        dice.render(matrices, vertices, light, overlay, color);
    }
}
