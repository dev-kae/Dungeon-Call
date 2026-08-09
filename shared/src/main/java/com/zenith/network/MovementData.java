package com.zenith.network;

import java.io.Serializable;

public record MovementData(
    float dx,
    float dy
) implements Serializable {
}
