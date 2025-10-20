package com.example.demo

import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.random.Random

@Service
class GameService(private val wordService: WordService) {

    private val games = mutableMapOf<String, Game>()

    companion object {
        const val NEUTRAL_TILES_COUNT = 7
        const val FIRST_TEAM_TILES = 9
        const val SECOND_TEAM_TILES = 8
        const val BOARD_SIZE = 25
    }

    fun createNewGame(customWords: List<String>): Game {
        val gameId = UUID.randomUUID().toString().take(6)
        return createGameInstance(gameId, 1, customWords)
    }

    fun getGame(id: String): Game? {
        return games[id]
    }

    fun resetGame(id: String): Game? {
        val currentGame = games[id] ?: return null
        return createGameInstance(id, currentGame.version + 1, currentGame.customWords, currentGame.redWins, currentGame.blueWins)
    }

    private fun createGameInstance(id: String, version: Int, customWords: List<String>, redWins: Int = 0, blueWins: Int = 0): Game {
        val startingTeam = if (Random.nextBoolean()) Team.RED else Team.BLUE
        val redCount = if (startingTeam == Team.RED) FIRST_TEAM_TILES else SECOND_TEAM_TILES
        val blueCount = if (startingTeam == Team.RED) SECOND_TEAM_TILES else FIRST_TEAM_TILES

        // Combine word sources and ensure uniqueness
        val combinedWords = (wordService.getBaseWords() + customWords).distinct().toMutableList()

        // If not enough words, supplement with the fallback list
        if (combinedWords.size < BOARD_SIZE) {
            combinedWords.addAll(wordService.getFallbackWords())
        }

        // Ensure final list is unique and shuffled, then take the required number of words
        val gameWords = combinedWords.distinct().shuffled().take(BOARD_SIZE)

        val roles = createRoles(redCount, blueCount)
        val tiles = gameWords.zip(roles) { word, role -> Tile(word, role) }.shuffled()
        val board = tiles.chunked(5)

        val newGame = Game(
            id = id,
            board = board,
            turn = startingTeam,
            version = version,
            redTilesRemaining = redCount,
            blueTilesRemaining = blueCount,
            customWords = customWords,
            redWins = redWins,
            blueWins = blueWins
        )
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

            if (tile.role == Role.RED) {
                it.redTilesRemaining--
            } else if (tile.role == Role.BLUE) {
                it.blueTilesRemaining--
            }

            if (tile.role == Role.ASSASSIN) {
                it.gameOver = true
                it.winner = if (it.turn == Team.RED) Team.BLUE else Team.RED
                if (it.winner == Team.RED) it.redWins++ else it.blueWins++
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
            game.redWins++
        } else if (game.blueTilesRemaining == 0) {
            game.gameOver = true
            game.winner = Team.BLUE
            game.blueWins++
        }
    }

    private fun revealAllTiles(game: Game) {
        game.board.flatten().forEach { it.revealed = true }
    }

    private fun createRoles(redCount: Int, blueCount: Int): List<Role> {
        val roles = mutableListOf<Role>()
        repeat(redCount) { roles.add(Role.RED) }
        repeat(blueCount) { roles.add(Role.BLUE) }
        repeat(NEUTRAL_TILES_COUNT) { roles.add(Role.NEUTRAL) }
        roles.add(Role.ASSASSIN)
        return roles.shuffled()
    }
}