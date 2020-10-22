package com.seamuslowry.branniganschess.backend.controllers

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.services.GameService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("games")
class GameController(
        private val gameService: GameService
) {

    @PostMapping("/create")
    @ApiOperation("Creates a game", response = Game::class)
    @ApiResponses(
            ApiResponse(code = 200, message =  "The game was created."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun createGame(): ResponseEntity<Game> = ResponseEntity.ok(gameService.createGame())

    @GetMapping("/", "")
    @ApiOperation("Retrieves a page of games")
    @ApiResponses(
            ApiResponse(code = 200, message =  "Successfully retrieved the page of games."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun getGames(@RequestParam(required = false) active: Boolean?,
                 @PageableDefault(size = 10, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<Game>> = ResponseEntity.ok(gameService.findAllBy(active, pageable))

    @GetMapping("/user")
    @PreAuthorize("authentication.principal.subject == #principal.subject")
    fun getUser(@AuthenticationPrincipal principal: Jwt): ResponseEntity<String> = ResponseEntity.ok(principal.subject)

    @SubscribeMapping("/status/{gameId}")
    fun getCurrentStatus(@DestinationVariable gameId: Long): String {
        val game = gameService.getById(gameId)
        return game.status.toString()
    }
}