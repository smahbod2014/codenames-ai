package com.example.demo

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GameService(private val wordService: WordService) {

    private val games = mutableMapOf<String, Game>()

    fun createNewGame(): Game {
        val gameId = UUID.randomUUID().toString().take(6)
        val words = wordService.getWords(25)
        val roles = createRoles()
        val tiles = words.zip(roles) { word, role -> Tile(word, role) }.shuffled()
        val board = tiles.chunked(5)
        val newGame = Game(gameId, board, Team.RED)
        games[gameId] = newGame
        return newGame
    }

    fun getGame(id: String): Game? {
        return games[id]
    }

    fun resetGame(id: String): Game? {
        if (!games.containsKey(id)) {
            return null
        }
        val words = wordService.getWords(25)
        val roles = createRoles()
        val tiles = words.zip(roles) { word, role -> Tile(word, role) }.shuffled()
        val board = tiles.chunked(5)
        val newGame = Game(id, board, Team.RED)
        games[id] = newGame
        return newGame
    }

    fun revealTile(id: String, row: Int, col: Int): Game? {
        val game = games[id]
        game?.let {
            if (row >= 0 && row < it.board.size && col >= 0 && col < it.board[row].size) {
                it.board[row][col].revealed = true
            }
        }
        return game
    }

    private fun createRoles(): List<Role> {
        val roles = mutableListOf<Role>()
        repeat(9) { roles.add(Role.RED) }
        repeat(8) { roles.add(Role.BLUE) }
        repeat(7) { roles.add(Role.NEUTRAL) }
        roles.add(Role.ASSASSIN)
        return roles.shuffled()
    }
}
