package dev.mmunki.error422.net;

import dev.mmunki.error422.Error422;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ControlPayload(Action action, int durationSeconds, long seed) implements CustomPacketPayload {
    public static final Type<ControlPayload> TYPE = new Type<>(Error422.id("control"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ControlPayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> {
                buffer.writeByte(payload.action.ordinal());
                buffer.writeVarInt(payload.durationSeconds);
                buffer.writeLong(payload.seed);
            },
            buffer -> new ControlPayload(
                    Action.values()[buffer.readUnsignedByte()],
                    buffer.readVarInt(),
                    buffer.readLong()));

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(TYPE, STREAM_CODEC);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Action {
        START,
        STOP
    }
}
