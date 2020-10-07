package com.seamuslowry.branniganschess.backend.models

enum class GameStatus {
    WAITING_FOR_PLAYERS, // unused
    WAITING_FOR_WHITE, // unused
    WAITING_FOR_BLACK, // unused
    WHITE_TURN,
    BLACK_TURN,
    WHITE_CHECK,
    BLACK_CHECK,
    WHITE_PROMOTION, // unused
    BLACK_PROMOTION, // unused
    CHECKMATE,
    STALEMATE,
}