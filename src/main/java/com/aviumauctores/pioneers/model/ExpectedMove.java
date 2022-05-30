package com.aviumauctores.pioneers.model;

import java.util.List;

public record ExpectedMove(
        String action,
        List<String> player) {
}
