package com.example.demo

enum class Role {
    RED, BLUE, NEUTRAL, ASSASSIN
}

enum class Team {
    RED, BLUE
}

data class Tile(val word: String, val role: Role, var revealed: Boolean = false)

data class Game(
    val id: String,
    val board: List<List<Tile>>,
    var turn: Team,
    var gameOver: Boolean = false,
    var winner: Team? = null,
    val version: Int = 1,
    var redTilesRemaining: Int,
    var blueTilesRemaining: Int,
    val customWords: List<String> = emptyList(),
    var redWins: Int = 0,
    var blueWins: Int = 0
)