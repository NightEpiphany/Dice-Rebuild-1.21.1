package com.moigferdsrte.network.payload;

import com.moigferdsrte.Entrance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record GambleCompetePayload(Map<Byte, Byte> game) implements CustomPayload {

    public static final CustomPayload.Id<GambleCompetePayload> ID = new CustomPayload.Id<>(
            Identifier.of(Entrance.MOD_ID, "gamble_compete")
    );

    public static final PacketCodec<PacketByteBuf, GambleCompetePayload> CODEC = PacketCodec.ofStatic(
            (buf, payload) -> {
                buf.writeVarInt(payload.game.size());
                for (var entry : payload.game.entrySet()) {
                    buf.writeByte(entry.getKey());
                    buf.writeByte(entry.getValue());
                }
            },
            buf -> {
                int gameSize = buf.readVarInt();
                Map<Byte, Byte> game = new HashMap<>(gameSize);
                for (int i = 0; i < gameSize; i++) {
                    game.put(buf.readByte(), buf.readByte());
                }
                return new GambleCompetePayload(game);
            }
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
