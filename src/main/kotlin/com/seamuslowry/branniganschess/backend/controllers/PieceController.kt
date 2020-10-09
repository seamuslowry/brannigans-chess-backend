package com.seamuslowry.branniganschess.backend.controllers

import com.seamuslowry.branniganschess.backend.dtos.PieceIdentifierDto
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.services.PieceService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("pieces")
class PieceController(
        private val pieceService: PieceService
) {
    @GetMapping("/{gameId}")
    @ApiOperation("Gets all pieces for a given game", response = Piece::class, responseContainer = "List")
    @ApiResponses(
            ApiResponse(code = 200, message =  "Successfully retrieved the list of pieces."),
            ApiResponse(code = 404, message =  "The game does not exist."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun getPieces(@PathVariable gameId: Long,
                  @RequestParam(required = false) color: PieceColor?,
                  @RequestParam(required = false) status: PieceStatus?)
            : ResponseEntity<Iterable<Piece>> {
        return ResponseEntity.ok(pieceService.findAllBy(gameId, color, status))
    }

    @PostMapping("/promote/{type}")
    @ApiOperation("Promotes a piece in the given game", response = Piece::class, responseContainer = "List")
    @ApiResponses(
            ApiResponse(code = 200, message =  "Successfully promoted the piece."),
            ApiResponse(code = 400, message =  "You are not allowed to promote that piece."),
            ApiResponse(code = 404, message =  "The piece does not exist."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun promotePiece(@PathVariable type: PieceType,
            @RequestBody pieceIdentifierDto: PieceIdentifierDto)
            : ResponseEntity<Piece> {
        return ResponseEntity.ok(pieceService.promote(pieceIdentifierDto, type))
    }
}