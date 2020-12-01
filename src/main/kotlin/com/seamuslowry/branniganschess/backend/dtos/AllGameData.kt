package com.seamuslowry.branniganschess.backend.dtos

import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.Move
import com.seamuslowry.branniganschess.backend.models.Piece

data class AllGameData(
    val game: Game,
    val moves: Iterable<Move>,
    val pieces: Iterable<Piece>
)
