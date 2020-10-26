package com.seamuslowry.branniganschess.backend.controllers

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.services.PlayerService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("players")
class PlayerController(
        private val playerService: PlayerService
) {
    @GetMapping("/games")
    @ApiOperation("Gets all games for the logged in player", response = Game::class, responseContainer = "List")
    @ApiResponses(
            ApiResponse(code = 200, message =  "Successfully retrieved the list of games."),
            ApiResponse(code = 404, message =  "The player does not exist."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun getGames(@AuthenticationPrincipal principal: Jwt,
                  @RequestParam(required = false) color: PieceColor?,
                  @RequestParam(required = false) active: Boolean?)
            : ResponseEntity<Iterable<Game>> {
        return ResponseEntity.ok(playerService.getGames(principal.subject , color, active))
    }

    @GetMapping("/signup/google")
    @ApiOperation("Signs up a player using their Google login", response = Player::class)
    @ApiResponses(
        ApiResponse(code = 200, message =  "Successfully signed the player up."),
        ApiResponse(code = 400, message =  "Player has already signed up with google."),
        ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun signupWithGoogle(@AuthenticationPrincipal principal: Jwt)
        : ResponseEntity<Player> {
        return ResponseEntity.ok(playerService.googleSignUp(principal.subject))
    }
}