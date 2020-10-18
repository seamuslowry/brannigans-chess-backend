package com.seamuslowry.branniganschess.backend.models

import com.seamuslowry.branniganschess.backend.websocket.GameStatusHandler
import javax.persistence.*

@Entity
@EntityListeners(GameStatusHandler::class)
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