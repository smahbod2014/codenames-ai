package com.example.demo

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
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
    @GetMapping("/api/game")
    fun getGame(): ResponseEntity<Game> {
        val game = gameService.getGame()
        return if (game != null) {
            ResponseEntity.ok(game)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/api/game/reveal")
    fun revealTile(@RequestBody revealRequest: RevealRequest): ResponseEntity<Game> {
        val game = gameService.revealTile(revealRequest.row, revealRequest.col)
        return if (game != null) {
            ResponseEntity.ok(game)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
