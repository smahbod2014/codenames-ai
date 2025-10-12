package com.example.demo

import org.springframework.stereotype.Service

@Service
class GameService(private val wordService: WordService) {

    private var game: Game? = null

    fun createNewGame(): Game {
        val words = wordService.getWords(25)
        val roles = createRoles()
        val tiles = words.zip(roles) { word, role -> Tile(word, role) }.shuffled()
        val board = tiles.chunked(5)
        val newGame = Game(board, Team.RED)
        this.game = newGame
        return newGame
    }

    fun getGame(): Game? {
        return this.game
    }

    fun revealTile(row: Int, col: Int): Game? {
        this.game?.let {
            if (row >= 0 && row < it.board.size && col >= 0 && col < it.board[row].size) {
                it.board[row][col].revealed = true
            }
        }
        return this.game
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
