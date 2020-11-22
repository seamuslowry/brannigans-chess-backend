package com.seamuslowry.branniganschess.backend.dtos

data class PlayerStatInfo (
    val whiteGames: Long,
    val blackGames: Long,
    val whiteWins: Long,
    val blackWins: Long,
    val whiteDraws: Long,
    val blackDraws: Long
)