package br.com.zenith.network.packet;

import java.io.Serializable;

public record Packet(
        PacketType type,
        Object data
) implements Serializable {
}
