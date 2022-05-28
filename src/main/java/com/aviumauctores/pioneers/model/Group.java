package com.aviumauctores.pioneers.model;

import java.util.List;

public record Group(
        String createdAt,

        String updatedAt,

        String _id,

        List<String> members
) {
}
