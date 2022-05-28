package com.aviumauctores.pioneers.model;

public record Move( String _id,

                    String createdAt,

                    String gameId,

                    String action,

                    int roll,

                    String building) {
}
