package com.seamuslowry.branniganschess.backend.controllers

import com.seamuslowry.branniganschess.backend.dtos.AdditionalPlayerInfo
import com.seamuslowry.branniganschess.backend.dtos.ChangeNameDto
import com.seamuslowry.branniganschess.backend.dtos.PlayerStatInfo
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.services.PlayerService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("players")
class PlayerController(
        private val playerService: PlayerService
) {
    @GetMapping("/games/{playerId}")
    @ApiOperation("Gets all games for the logged in player")
    @ApiResponses(
            ApiResponse(code = 200, message =  "Successfully retrieved the list of games."),
            ApiResponse(code = 404, message =  "The player does not exist."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun getGames(@PathVariable playerId: Long,
                 @RequestParam(required = false) color: PieceColor?,
                 @RequestParam(name = "status", defaultValue = "") statuses: List<GameStatus>,
                 @PageableDefault(size = 10, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<Game>> {
        return ResponseEntity.ok(playerService.getGames(playerId, color, statuses, pageable))
    }

    @GetMapping("/stats/{playerId}")
    @ApiOperation("Gets all stat information for the provided player")
    @ApiResponses(
        ApiResponse(code = 200, message =  "Successfully retrieved the stat information."),
        ApiResponse(code = 404, message =  "The player does not exist."),
        ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun getStats(@PathVariable playerId: Long): ResponseEntity<PlayerStatInfo> {
        return ResponseEntity.ok(playerService.getStats(playerId))
    }

    @PostMapping("/auth")
    @ApiOperation("Retrieves the player that corresponds to the authenticated id", response = Player::class)
    @ApiResponses(
        ApiResponse(code = 200, message =  "Successfully retrieved the player."),
        ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun authenticate(authentication: Authentication, @RequestBody additionalPlayerInfo: AdditionalPlayerInfo): ResponseEntity<Player> = ResponseEntity.ok(playerService.authenticatePlayer(authentication.name, additionalPlayerInfo))

    @PostMapping("/name")
    @ApiOperation("Changes the logged in player's name", response = Player::class)
    @ApiResponses(
        ApiResponse(code = 200, message =  "Successfully changed the player's name."),
        ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun changeName(authentication: Authentication, @RequestBody newName: ChangeNameDto): ResponseEntity<Player> = ResponseEntity.ok(playerService.changeName(authentication.name, newName.name))

    @PostMapping("/join/{gameId}")
    @ApiOperation("Adds the authenticated user to the specified game", response = Game::class)
    @ApiResponses(
        ApiResponse(code = 200, message =  "Successfully joined the game."),
        ApiResponse(code = 409, message =  "Cannot join the game as requested."),
        ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun join(authentication: Authentication,
             @PathVariable gameId: Long,
             @RequestParam(required = false) color: PieceColor?)
        : ResponseEntity<Game> = ResponseEntity.ok(playerService.joinGame(gameId, authentication.name, color))

    @PostMapping("/leave/{gameId}")
    @ApiOperation("Removes the authenticated user to the specified game", response = Game::class)
    @ApiResponses(
        ApiResponse(code = 200, message =  "Successfully left the game."),
        ApiResponse(code = 409, message =  "Cannot leave the game."),
        ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun leave(authentication: Authentication,
              @PathVariable gameId: Long)
        : ResponseEntity<Game> = ResponseEntity.ok(playerService.leaveGame(gameId, authentication.name))

    @PostMapping("/resign/{gameId}")
    @ApiOperation("Resigns the authenticated user from the specified game", response = Game::class)
    @ApiResponses(
        ApiResponse(code = 200, message =  "Successfully resigned the game."),
        ApiResponse(code = 409, message =  "Cannot resign the game."),
        ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun resign(authentication: Authentication,
        @PathVariable gameId: Long)
        : ResponseEntity<Game> = ResponseEntity.ok(playerService.resignGame(gameId, authentication.name))
}