package com.moigferdsrte.particle;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;

public class DiceCubeParticle implements ParticleEffect {

    private final ParticleType<DiceCubeParticle> type;
    private final int color;

    public DiceCubeParticle(ParticleType<DiceCubeParticle> type, int color) {
        this.type = type;
        this.color = color;
    }

    public static MapCodec<DiceCubeParticle> createCodec(ParticleType<DiceCubeParticle> type) {
        return Codecs.ARGB.<DiceCubeParticle>xmap(color -> new DiceCubeParticle(type, color), diceCubeParticle -> diceCubeParticle.color).fieldOf("color");
    }

    public static PacketCodec<? super ByteBuf, DiceCubeParticle> createPacketCodec(ParticleType<DiceCubeParticle> type) {
        return PacketCodecs.INTEGER.xmap(color -> new DiceCubeParticle(type, color), particleEffect -> particleEffect.color);
    }

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }

    public float getRed() {
        return (float) ColorHelper.Argb.getRed(this.color) / 255.0F;
    }

    public float getGreen() {
        return (float)ColorHelper.Argb.getGreen(this.color) / 255.0F;
    }

    public float getBlue() {
        return (float)ColorHelper.Argb.getBlue(this.color) / 255.0F;
    }

    public float getAlpha() {
        return (float)ColorHelper.Argb.getAlpha(this.color) / 255.0F;
    }

    public static DiceCubeParticle create(ParticleType<DiceCubeParticle> type, int color) {
        return new DiceCubeParticle(type, color);
    }
}
