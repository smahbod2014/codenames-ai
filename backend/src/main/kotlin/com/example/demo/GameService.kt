package com.example.demo

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GameService(private val wordService: WordService) {

    private val games = mutableMapOf<String, Game>()

    companion object {
        const val RED_TILES_COUNT = 9
        const val BLUE_TILES_COUNT = 8
        const val NEUTRAL_TILES_COUNT = 7
    }

    fun createNewGame(): Game {
        val gameId = UUID.randomUUID().toString().take(6)
        val words = wordService.getWords(25)
        val roles = createRoles()
        val tiles = words.zip(roles) { word, role -> Tile(word, role) }.shuffled()
        val board = tiles.chunked(5)
        val newGame = Game(id = gameId, board = board, turn = Team.RED, version = 1)
        games[gameId] = newGame
        return newGame
    }

    fun getGame(id: String): Game? {
        return games[id]
    }

    fun resetGame(id: String): Game? {
        val currentGame = games[id] ?: return null
        val words = wordService.getWords(25)
        val roles = createRoles()
        val tiles = words.zip(roles) { word, role -> Tile(word, role) }.shuffled()
        val board = tiles.chunked(5)
        val newGame = Game(id = id, board = board, turn = Team.RED, version = currentGame.version + 1)
        games[id] = newGame
        return newGame
    }

    fun revealTile(id: String, row: Int, col: Int): Game? {
        val game = games[id]
        game?.let {
            if (it.gameOver || it.board[row][col].revealed) {
                return it
            }

            val tile = it.board[row][col]
            tile.revealed = true

            // Decrement score
            if (tile.role == Role.RED) {
                it.redTilesRemaining--
            } else if (tile.role == Role.BLUE) {
                it.blueTilesRemaining--
            }

            if (tile.role == Role.ASSASSIN) {
                it.gameOver = true
                it.winner = if (it.turn == Team.RED) Team.BLUE else Team.RED
                revealAllTiles(it)
                return it
            }

            checkWinConditions(it)
            if (it.gameOver) {
                revealAllTiles(it)
                return it
            }

            val currentTurn = it.turn
            val tileRole = tile.role
            if (tileRole == Role.NEUTRAL || (currentTurn == Team.RED && tileRole == Role.BLUE) || (currentTurn == Team.BLUE && tileRole == Role.RED)) {
                it.turn = if (currentTurn == Team.RED) Team.BLUE else Team.RED
            }
        }
        return game
    }

    private fun checkWinConditions(game: Game) {
        if (game.redTilesRemaining == 0) {
            game.gameOver = true
            game.winner = Team.RED
        } else if (game.blueTilesRemaining == 0) {
            game.gameOver = true
            game.winner = Team.BLUE
        }
    }

    private fun revealAllTiles(game: Game) {
        game.board.flatten().forEach { it.revealed = true }
    }

    private fun createRoles(): List<Role> {
        val roles = mutableListOf<Role>()
        repeat(RED_TILES_COUNT) { roles.add(Role.RED) }
        repeat(BLUE_TILES_COUNT) { roles.add(Role.BLUE) }
        repeat(NEUTRAL_TILES_COUNT) { roles.add(Role.NEUTRAL) }
        roles.add(Role.ASSASSIN)
        return roles.shuffled()
    }
}