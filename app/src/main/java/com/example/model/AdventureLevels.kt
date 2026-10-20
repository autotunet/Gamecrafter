package com.example.model

import java.util.Calendar

object AdventureLevels {
    val levels: List<AdventureLevel> = listOf(
        AdventureLevel(
            levelNumber = 1,
            title = "Primer Paso",
            targetScore = 400,
            targetLines = 4,
            targetCombo = 1,
            rewardCoins = 25
        ),
        AdventureLevel(
            levelNumber = 2,
            title = "Calentamiento",
            targetScore = 650,
            targetLines = 6,
            targetCombo = 2,
            rewardCoins = 30
        ),
        AdventureLevel(
            levelNumber = 3,
            title = "Esquinas de Hielo",
            targetScore = 850,
            targetLines = 7,
            targetCombo = 1,
            initialObstacles = listOf(0 to 0, 0 to 7, 7 to 0, 7 to 7),
            rewardCoins = 35
        ),
        AdventureLevel(
            levelNumber = 4,
            title = "Cruz Contrarreloj",
            targetScore = 1100,
            targetLines = 9,
            targetCombo = 2,
            timeLimitSeconds = 90,
            initialObstacles = listOf(3 to 3, 3 to 4, 4 to 3, 4 to 4),
            rewardCoins = 40
        ),
        AdventureLevel(
            levelNumber = 5,
            title = "Fiebre de Combo",
            targetScore = 1400,
            targetLines = 11,
            targetCombo = 3,
            movesLimit = 36,
            rewardCoins = 45
        ),
        AdventureLevel(
            levelNumber = 6,
            title = "Laberinto Neón",
            targetScore = 1750,
            targetLines = 13,
            initialObstacles = listOf(1 to 1, 1 to 6, 6 to 1, 6 to 6, 3 to 3, 4 to 4),
            movesLimit = 32,
            rewardCoins = 50
        ),
        AdventureLevel(
            levelNumber = 7,
            title = "Velocidad Extrema",
            targetScore = 2000,
            targetLines = 15,
            targetCombo = 3,
            timeLimitSeconds = 75,
            rewardCoins = 55
        ),
        AdventureLevel(
            levelNumber = 8,
            title = "Fortaleza Cuadrada",
            targetScore = 2400,
            targetLines = 16,
            initialObstacles = listOf(2 to 2, 2 to 5, 5 to 2, 5 to 5, 3 to 3, 4 to 4),
            movesLimit = 34,
            rewardCoins = 60
        ),
        AdventureLevel(
            levelNumber = 9,
            title = "Super Racha",
            targetScore = 2700,
            targetLines = 18,
            targetCombo = 4,
            movesLimit = 36,
            rewardCoins = 65
        ),
        AdventureLevel(
            levelNumber = 10,
            title = "Tormenta Relámpago",
            targetScore = 3100,
            targetLines = 20,
            initialObstacles = listOf(0 to 3, 0 to 4, 7 to 3, 7 to 4, 3 to 0, 4 to 0, 3 to 7, 4 to 7),
            timeLimitSeconds = 65,
            rewardCoins = 75
        ),
        AdventureLevel(
            levelNumber = 11,
            title = "Diamante Estelar",
            targetScore = 3500,
            targetLines = 22,
            initialObstacles = listOf(1 to 3, 1 to 4, 3 to 1, 4 to 1, 3 to 6, 4 to 6, 6 to 3, 6 to 4),
            movesLimit = 36,
            rewardCoins = 80
        ),
        AdventureLevel(
            levelNumber = 12,
            title = "Gran Maestro Extremo",
            targetScore = 4200,
            targetLines = 26,
            targetCombo = 4,
            movesLimit = 38,
            timeLimitSeconds = 85,
            initialObstacles = listOf(2 to 2, 2 to 3, 2 to 4, 5 to 3, 5 to 4, 5 to 5),
            rewardCoins = 100
        )
    )

    fun getDailyLevel(): AdventureLevel {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        val obstacles = when (day % 4) {
            0 -> listOf(2 to 2, 5 to 5, 2 to 5, 5 to 2)
            1 -> listOf(3 to 3, 3 to 4, 4 to 3, 4 to 4)
            2 -> listOf(0 to 0, 7 to 7, 0 to 7, 7 to 0)
            else -> listOf(1 to 3, 3 to 1, 6 to 4, 4 to 6)
        }
        return AdventureLevel(
            levelNumber = day,
            title = "Reto Diario #${day}",
            targetScore = 1500 + (day % 10) * 150,
            targetLines = 12 + (day % 6),
            targetCombo = 2 + (day % 3),
            movesLimit = 30 + (day % 15),
            initialObstacles = obstacles,
            rewardCoins = 70
        )
    }
}
