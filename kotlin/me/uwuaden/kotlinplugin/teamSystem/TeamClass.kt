package me.uwuaden.kotlinplugin.teamSystem

import me.uwuaden.kotlinplugin.Main.Companion.worldDatas
import org.bukkit.World
import org.bukkit.entity.Player

class TeamClass(val world: World, var players: MutableSet<Player> = mutableSetOf()) {

    fun addPlayer(world: World, player: Player) {
        val data = worldDatas[world]
        if (data != null) {
            this.players.add(player)
        }
    }
}