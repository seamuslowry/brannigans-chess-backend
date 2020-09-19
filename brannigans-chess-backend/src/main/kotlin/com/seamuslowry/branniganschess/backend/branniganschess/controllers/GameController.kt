package com.seamuslowry.branniganschess.backend.branniganschess.controllers

import com.seamuslowry.branniganschess.backend.branniganschess.models.*
import com.seamuslowry.branniganschess.backend.branniganschess.repos.GameRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.MoveRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PieceRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PlayerRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GameController(
        private val gameRepository: GameRepository,
        private val pieceRepository: PieceRepository,
        private val moveRepository: MoveRepository,
        private val playerRepository: PlayerRepository
) {

    @GetMapping("/1")
    fun testOne(): ResponseEntity<Game> {
        var game = Game("12345")

        game = gameRepository.save(game)
        return ResponseEntity.ok(game)
    }

    @GetMapping("/2")
    fun testTwo(): ResponseEntity<Player> {
        val player = playerRepository.save(Player("username"))
        return ResponseEntity.ok(player)
    }

    @GetMapping("/3")
    fun testThree(): ResponseEntity<Piece> {
        var game = Game("321")

        game = gameRepository.save(game)

        var piece = Piece(PieceType.QUEEN, PieceColor.WHITE, game)

        piece = pieceRepository.save(piece)
        return ResponseEntity.ok(piece)
    }

    @GetMapping("/4")
    fun testFour(): ResponseEntity<Move> {
        var game = Game("321")

        game = gameRepository.save(game)

        var piece = Piece(PieceType.QUEEN, PieceColor.WHITE, game)

        piece = pieceRepository.save(piece)

        var move = Move(piece, 0, 0, 1,1)

        move = moveRepository.save(move)

        return ResponseEntity.ok(move)
    }
}