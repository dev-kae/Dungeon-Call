package com.zenith.network.packet;

import java.io.Serializable;
import java.util.UUID;

public record PlayerData (
    UUID id,
    String name,
    float x,
    float y
) implements Serializable {
}
