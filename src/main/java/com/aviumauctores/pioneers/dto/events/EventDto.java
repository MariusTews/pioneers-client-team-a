package com.aviumauctores.pioneers.dto.events;

public record EventDto<T>(
        String event,
        T data
) {
}
