package com.seamuslowry.branniganschess.backend.models

import javax.persistence.*

@Entity
class Game (
        var uuid: String = "",
        @ManyToOne
        var whitePlayer: Player? = null,
        @ManyToOne
        var blackPlayer: Player? = null,
        @ManyToOne
        var winner: Player? = null,
        @Enumerated(EnumType.STRING)
        var status: GameStatus = GameStatus.WHITE_TURN,
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1
)