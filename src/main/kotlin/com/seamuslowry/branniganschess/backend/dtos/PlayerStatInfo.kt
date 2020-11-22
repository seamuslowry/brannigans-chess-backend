package com.seamuslowry.branniganschess.backend.dtos

data class PlayerStatInfo (
    val whiteGames: Long = 0,
    val blackGames: Long = 0,
    val whiteWins: Long = 0,
    val blackWins: Long = 0,
    val whiteDraws: Long = 0,
    val blackDraws: Long = 0
)