package com.seamuslowry.branniganschess.backend.websocket

import com.seamuslowry.branniganschess.backend.config.WebSocketConfig.Companion.MESSAGE_PREFIX
import com.seamuslowry.branniganschess.backend.models.Game
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import javax.persistence.PostPersist
import javax.persistence.PostUpdate

@Component
class GameStatusHandler(
        @Autowired
        private val socket: SimpMessagingTemplate
) {
    @PostPersist
    @PostUpdate
    fun onAfterSave(game: Game) {
        socket.convertAndSend("$MESSAGE_PREFIX/status/${game.id}", game)
    }
}