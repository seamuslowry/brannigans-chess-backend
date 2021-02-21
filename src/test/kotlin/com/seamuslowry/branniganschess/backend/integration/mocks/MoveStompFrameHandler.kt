package com.seamuslowry.branniganschess.backend.integration.mocks

import com.seamuslowry.branniganschess.backend.models.Move
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import java.lang.reflect.Type

class MoveStompFrameHandler(private val messages: MutableList<Move>) : StompFrameHandler {
    override fun getPayloadType(headers: StompHeaders): Type = Move::class.java

    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        messages.add(payload as Move)
    }
}