package com.aviumauctores.pioneers.model;

import java.util.List;

public record State(String updatedAt,
                    String gameId,
                    List<ExpectedMove> expectedMoves,
                    Point3D robber
) {
}
