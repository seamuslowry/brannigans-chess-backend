package com.seamuslowry.branniganschess.backend.integration.mocks

import com.seamuslowry.branniganschess.backend.models.Game
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import java.lang.reflect.Type

class GameStompFrameHandler(private val messages: MutableList<Game>) : StompFrameHandler {
    override fun getPayloadType(headers: StompHeaders): Type = Game::class.java

    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        messages.add(payload as Game)
    }
}