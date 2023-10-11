package me.uwuaden.kotlinplugin.gameSystem

import org.bukkit.Location
import org.bukkit.World

object BorderManager {
    fun initBoarder(center: Location, r: Double) {
        center.world.worldBorder.center = center
        center.world.worldBorder.damageAmount = 1.0
        center.world.worldBorder.damageBuffer = 0.0
        center.world.worldBorder.warningDistance = 0
        center.world.worldBorder.warningTime = 0
        center.world.worldBorder.setSize(r, 0)
    }
    fun changeInSec(world: World, r: Double, sec: Int) {
        world.worldBorder.setSize(r, sec.toLong())
    }
}