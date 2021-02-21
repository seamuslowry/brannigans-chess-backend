package com.seamuslowry.branniganschess.backend.utils

import com.seamuslowry.branniganschess.backend.models.GameStatus

object Constants {
    val activeStatuses = listOf(
        GameStatus.WHITE_TURN,
        GameStatus.BLACK_TURN,
        GameStatus.BLACK_PROMOTION,
        GameStatus.WHITE_PROMOTION,
        GameStatus.BLACK_CHECK,
        GameStatus.WHITE_CHECK
    )

    val inactiveStatuses = listOf(
        GameStatus.WHITE_VICTORY,
        GameStatus.BLACK_VICTORY,
        GameStatus.STALEMATE
    )

    val openStatuses = listOf(
        GameStatus.WAITING_FOR_PLAYERS,
        GameStatus.WAITING_FOR_WHITE,
        GameStatus.WAITING_FOR_BLACK
    )

    val allStatuses = listOf(*GameStatus.values())
}