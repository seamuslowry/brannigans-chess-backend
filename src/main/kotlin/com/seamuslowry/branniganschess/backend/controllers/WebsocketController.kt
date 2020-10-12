package com.seamuslowry.branniganschess.backend.controllers

import com.seamuslowry.branniganschess.backend.services.GameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable

import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller

@Controller
class WebsocketController(
        @Autowired val gameService: GameService
) {
    @SubscribeMapping("/status/{gameId}")
    fun getInitialStatus(@DestinationVariable gameId: Long): String {
        val game = gameService.getById(gameId)
        return game.status.toString()
    }
}