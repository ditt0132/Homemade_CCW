package me.uwuaden.kotlinplugin.teamSystem

import me.uwuaden.kotlinplugin.Main.Companion.worldDatas
import org.bukkit.Color
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*
import kotlin.random.Random


class TeamClass(val world: World, var players: MutableSet<Player> = mutableSetOf(), var color: Color = Color.BLACK, var uuid: UUID = UUID.randomUUID(), var id: Int = -1) {
    init {
        this.uuid = UUID.randomUUID()
        val random = Random
        this.color = Color.fromRGB(random.nextInt(0, 256), random.nextInt(0, 256), random.nextInt(0, 256))
    }
    fun addPlayer(world: World, player: Player) {
        val data = worldDatas[world]
        if (data != null) {
            this.players.add(player)
        }
    }
}