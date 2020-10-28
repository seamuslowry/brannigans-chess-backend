package com.seamuslowry.branniganschess.backend.integration.mocks

import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import java.lang.reflect.Type

class TestStompFrameHandler<T>(private val messages: MutableList<T>) : StompFrameHandler {
    override fun getPayloadType(headers: StompHeaders): Type = String::class.java

    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        messages.add(payload as T)
    }
}