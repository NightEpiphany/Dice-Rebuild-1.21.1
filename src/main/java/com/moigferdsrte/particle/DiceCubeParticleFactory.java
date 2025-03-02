package com.moigferdsrte.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class DiceCubeParticleFactory extends SpriteBillboardParticle {

    private static final Random RANDOM = Random.create();
    private final SpriteProvider spriteProvider;
    private float defaultAlpha = 1.0F;

    DiceCubeParticleFactory(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.5 - RANDOM.nextDouble(), velocityY, 0.5 - RANDOM.nextDouble());
        this.velocityMultiplier = 0.96F;
        this.gravityStrength = -0.1F;
        this.ascending = true;
        this.spriteProvider = spriteProvider;
        this.velocityY *= 0.2F;
        if (velocityX == 0.0 && velocityZ == 0.0) {
            this.velocityX *= 0.1F;
            this.velocityZ *= 0.1F;
        }

        this.scale *= 0.75F;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.collidesWithWorld = false;
        this.setSpriteForAge(spriteProvider);
        if (this.isInvisible()) {
            this.setAlpha(0.0F);
        }
    }

    @Override
    public int getBrightness(float tint) {
        float f = ((float)this.age + tint) / (float)this.maxAge;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        int i = super.getBrightness(tint);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        j += (int)(f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
        if (this.isInvisible()) {
            this.alpha = 0.0F;
        } else {
            this.alpha = MathHelper.lerp(0.05F, this.alpha, this.defaultAlpha);
        }
    }

    @Override
    protected void setAlpha(float alpha) {
        super.setAlpha(alpha);
        this.defaultAlpha = alpha;
    }

    private boolean isInvisible() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        return clientPlayerEntity != null
                && clientPlayerEntity.getEyePos().squaredDistanceTo(this.x, this.y, this.z) <= 9.0
                && minecraftClient.options.getPerspective().isFirstPerson()
                && clientPlayerEntity.isUsingSpyglass();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class DiceFactory implements ParticleFactory<DiceCubeParticle> {
        private final SpriteProvider spriteProvider;

        public DiceFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(
                DiceCubeParticle diceCubeParticle, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i
        ) {
            DiceCubeParticleFactory particle = new DiceCubeParticleFactory(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            particle.setColor(diceCubeParticle.getRed(), diceCubeParticle.getGreen(), diceCubeParticle.getBlue());
            particle.setAlpha(diceCubeParticle.getAlpha());
            return particle;
        }
    }

    public static void init(){}
}
