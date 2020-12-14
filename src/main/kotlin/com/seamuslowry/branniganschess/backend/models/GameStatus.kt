package com.seamuslowry.branniganschess.backend.models

enum class GameStatus {
    WAITING_FOR_PLAYERS,
    WAITING_FOR_WHITE,
    WAITING_FOR_BLACK,
    WHITE_TURN,
    BLACK_TURN,
    WHITE_CHECK,
    BLACK_CHECK,
    WHITE_PROMOTION,
    BLACK_PROMOTION,
    WHITE_VICTORY,
    BLACK_VICTORY,
    STALEMATE,
}