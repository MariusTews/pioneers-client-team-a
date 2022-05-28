package com.aviumauctores.pioneers.model;
import java.util.List;


public record User(
        String createdAt,

        String updatedAt,

        String _id,

        String name,

        String status,

        String avatar,

        List<String> friends
) {
}
