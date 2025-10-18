package com.example.demo

enum class Role {
    RED, BLUE, NEUTRAL, ASSASSIN
}

enum class Team {
    RED, BLUE
}

data class Tile(val word: String, val role: Role, var revealed: Boolean = false)

data class Game(val id: String, val board: List<List<Tile>>, var turn: Team)
