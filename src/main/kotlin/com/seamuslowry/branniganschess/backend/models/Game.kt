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
        // TODO remove after status update
        var winner: Player? = null,
        @Enumerated(EnumType.STRING)
        var status: GameStatus = GameStatus.WAITING_FOR_PLAYERS,
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1
) {
        fun isWhite(authId: String) = whitePlayer?.authId == authId
        fun isBlack(authId: String) = blackPlayer?.authId == authId
        fun isPlayer(authId: String) = isWhite(authId) || isBlack(authId)
}