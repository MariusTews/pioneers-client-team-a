package com.aviumauctores.pioneers.model;

public record State(  String updatedAt,
                      String gameId,
                      ExpectedMove expectedMoves
) {
}
