package com.seamuslowry.branniganschess.backend.websocket

import com.seamuslowry.branniganschess.backend.config.WebSocketConfig.Companion.MESSAGE_PREFIX
import com.seamuslowry.branniganschess.backend.models.Move
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import javax.persistence.PostPersist
import javax.persistence.PostUpdate

@Component
class SharedMovesHandler(
        @Autowired
        private val socket: SimpMessagingTemplate
) {
    @PostPersist
    @PostUpdate
    fun onAfterSave(move: Move) {
        if (move.takenPiece != null) socket.convertAndSend("$MESSAGE_PREFIX/moves/${move.movingPiece.gameId}", move)
    }
}