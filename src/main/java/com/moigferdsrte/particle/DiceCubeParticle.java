package com.moigferdsrte.particle;

import com.moigferdsrte.entity.refine.RefineRegistry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.ColorHelper;

import java.util.Locale;

public class DiceCubeParticle implements ParticleEffect {

    private final ParticleType<DiceCubeParticle> type;
    private final int color;

    public DiceCubeParticle(ParticleType<DiceCubeParticle> type, int color) {
        this.type = type;
        this.color = color;
    }

//    public DiceCubeParticle(int color) {
//        this.type = RefineRegistry.DICE_PARTICLE;
//        this.color = color;
//    }
//
//    public static final Codec<DiceCubeParticle> CODEC = RecordCodecBuilder.create(
//            instance -> instance.group(Codec.INT.fieldOf("color").forGetter(particleEffect -> particleEffect.color)).apply(instance, DiceCubeParticle::new)
//    );
//
//    public static final ParticleEffect.Factory<DiceCubeParticle> FACTORY = new ParticleEffect.Factory<DiceCubeParticle>() {
//        public DiceCubeParticle read(ParticleType<DiceCubeParticle> particleType, StringReader stringReader) throws CommandSyntaxException {
//            stringReader.expect(' ');
//            int i = stringReader.readInt();
//            return new DiceCubeParticle(i);
//        }
//
//        public DiceCubeParticle read(ParticleType<DiceCubeParticle> particleType, PacketByteBuf packetByteBuf) {
//            return new DiceCubeParticle(packetByteBuf.readVarInt());
//        }
//    };

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.color);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%s %d", Registries.PARTICLE_TYPE.getId(this.getType()), this.color);
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
