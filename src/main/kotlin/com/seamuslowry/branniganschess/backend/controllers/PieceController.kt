package com.seamuslowry.branniganschess.backend.controllers

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import com.seamuslowry.branniganschess.backend.utils.Utils
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("pieces")
class PieceController(
        private val pieceService: PieceService,
        private val gameService: GameService
) {
    @GetMapping("/{gameUuid}")
    @ApiOperation("Gets all pieces for a given game", response = Piece::class, responseContainer = "List")
    @ApiResponses(
            ApiResponse(code = 200, message =  "Successfully retrieved the list of pieces."),
            ApiResponse(code = 404, message =  "The game does not exist."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun getPieces(@PathVariable gameUuid: String,
                  @RequestParam(name = "color", defaultValue = "") colors: List<PieceColor>,
                  @RequestParam(required = false) status: PieceStatus?
    )
            : ResponseEntity<Iterable<Piece>> {
        val game = gameService.getByUuid(gameUuid)
        return ResponseEntity.ok(pieceService.findAllBy(game, colors, status))
    }

    @PostMapping("/promote/{pawnId}/{type}")
    @ApiOperation("Promotes a piece in the given game", response = Piece::class, responseContainer = "List")
    @ApiResponses(
            ApiResponse(code = 200, message =  "Successfully promoted the piece."),
            ApiResponse(code = 400, message =  "You are not allowed to promote that piece."),
            ApiResponse(code = 404, message =  "The piece does not exist."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun promotePiece(
        @PathVariable pawnId: Long,
        @PathVariable type: PieceType
    ): ResponseEntity<Piece> {
        val piece = pieceService.getById(pawnId)
        val game = gameService.getById(piece.gameId)
        val promotedPiece = pieceService.promote(piece, game, type)
        gameService.updateGameStatusForNextPlayer(promotedPiece.gameId, Utils.getOpposingColor(promotedPiece.color))
        return ResponseEntity.ok(promotedPiece)
    }
}