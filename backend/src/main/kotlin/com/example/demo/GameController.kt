package com.example.demo

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class GameController(private val gameService: GameService) {

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/api/game/new")
    fun newGame(): Game {
        return gameService.createNewGame()
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/api/game/{id}")
    fun getGame(@PathVariable id: String): ResponseEntity<Game> {
        val game = gameService.getGame(id)
        return if (game != null) {
            ResponseEntity.ok(game)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/api/game/{id}/new")
    fun resetGame(@PathVariable id: String): ResponseEntity<Game> {
        val game = gameService.resetGame(id)
        return if (game != null) {
            ResponseEntity.ok(game)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/api/game/{id}/reveal")
    fun revealTile(@PathVariable id: String, @RequestBody revealRequest: RevealRequest): ResponseEntity<Game> {
        val game = gameService.revealTile(id, revealRequest.row, revealRequest.col)
        return if (game != null) {
            ResponseEntity.ok(game)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
