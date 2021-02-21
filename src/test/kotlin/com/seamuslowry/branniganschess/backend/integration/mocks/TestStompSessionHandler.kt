package com.seamuslowry.branniganschess.backend.integration.mocks

import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter


class TestStompSessionHandler : StompSessionHandlerAdapter() {
    override fun handleException(session: StompSession, command: StompCommand?, headers: StompHeaders, payload: ByteArray, exception: Throwable) {
        exception.printStackTrace()
    }
}