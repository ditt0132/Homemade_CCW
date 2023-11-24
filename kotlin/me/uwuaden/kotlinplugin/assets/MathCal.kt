package me.uwuaden.kotlinplugin.assets

import kotlin.random.Random

object MathCal {
    fun probabilityTrue(n: Double): Boolean {
        require(n in 0.0..100.0) { "확률은 0에서 100 사이의 값이어야 합니다." }

        val randomValue = Random.nextDouble(0.0, 100.0)
        return randomValue < n
    }
}