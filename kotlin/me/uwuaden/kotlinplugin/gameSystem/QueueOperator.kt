package me.uwuaden.kotlinplugin.gameSystem

import me.uwuaden.kotlinplugin.Main.Companion.debugStart
import me.uwuaden.kotlinplugin.Main.Companion.plugin
import me.uwuaden.kotlinplugin.Main.Companion.queueClosed
import me.uwuaden.kotlinplugin.Main.Companion.queueMode
import me.uwuaden.kotlinplugin.Main.Companion.queueStartIn
import me.uwuaden.kotlinplugin.Main.Companion.scheduler
import me.uwuaden.kotlinplugin.Main.Companion.worldLoaded
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World

private fun playerHolder(world: World) {
    val gameWorld = plugin.server.getWorld("Field-"+world.name.replace("Queue-", ""))?: return

    if (worldLoaded.contains(gameWorld.name)) {
        worldLoaded.remove(gameWorld.name)
        WorldManager.broadcastWorld(world, "${ChatColor.YELLOW}월드 로드 완료!")
    }

    if (debugStart) {
        if (world.players.size >= 1 && !queueStartIn.containsKey(world.name)) {
            val startSec = 60

            queueStartIn[world.name] = System.currentTimeMillis() + startSec * 1000
            WorldManager.broadcastWorld(world, "${ChatColor.GREEN}게임이 ${startSec}초 뒤에 시작합니다")
        }
    }
    if (world.players.size >= 10 && !queueStartIn.containsKey(world.name)) {
        val startSec = 120

        queueStartIn[world.name] = System.currentTimeMillis() + startSec*1000
        WorldManager.broadcastWorld(world, "${ChatColor.GREEN}게임이 ${startSec}초 뒤에 시작합니다")
    }

    if (world.players.size >= 30 && ((queueStartIn[world.name] ?: 0)- System.currentTimeMillis())/1000 >= 60) {
        val startSec = 60

        queueStartIn[world.name] = System.currentTimeMillis() + startSec*1000
        WorldManager.broadcastWorld(world, "${ChatColor.GREEN}게임이 ${startSec}초 뒤에 시작합니다")
    }
    if (world.players.size >= 50 && ((queueStartIn[world.name] ?: 0)- System.currentTimeMillis())/1000 >= 30) {
        val startSec = 30

        queueStartIn[world.name] = System.currentTimeMillis() + startSec*1000
        WorldManager.broadcastWorld(world, "${ChatColor.GREEN}게임이 ${startSec}초 뒤에 시작합니다!")
    }
    if (world.players.size >= 70 && ((queueStartIn[world.name] ?: 0)- System.currentTimeMillis())/1000 >= 10) {
        val startSec = 10

        queueStartIn[world.name] = System.currentTimeMillis() + startSec*1000
        WorldManager.broadcastWorld(world, "${ChatColor.GREEN}게임이 ${startSec}초 뒤에 시작합니다!")
    }


    val sec = queueStartIn[world.name] ?: return
    if ((sec - System.currentTimeMillis()) < 0) {
        queueStartIn.remove(world.name)
        queueClosed.add(world.name)
        if (queueMode[world] == null) {
            GameManager.joinPlayers(world, gameWorld, "Solo")
        } else {
            GameManager.joinPlayers(world, gameWorld, queueMode[world]!!)
        }
    }
}

object QueueOperator {
    fun sch() {
        scheduler.scheduleSyncRepeatingTask(plugin, {
            plugin.server.worlds.forEach { world ->
                if (world.name.startsWith("Queue-")) {
                    playerHolder(world)
                }
            }
        }, 0, 20)
        scheduler.scheduleSyncRepeatingTask(plugin, {
            if (queueList().size < 2 && WorldManager.getInGameWorldCount() == 0) {
                createQueue("Solo")
            }
        }, 0, 20*60)
    }

    fun createQueue(mode: String) {
        require( mode == "Solo" || mode == "Duo" )

        val uuid = WorldManager.createQueueWorld()
        scheduler.scheduleSyncDelayedTask(plugin, {
            val worldStr = WorldManager.createFieldWorld("test", uuid)
            scheduler.scheduleSyncDelayedTask(plugin, {
                try {
                    val world = plugin.server.getWorld(worldStr)!!
                    val dataClass = WorldManager.initData(world)

                    dataClass.worldMode[world] = mode
                    WorldManager.loadWorldChunk(world, Location(world, -1000.0, 0.0, -1000.0), Location(world, 1000.0, 0.0, 1000.0), true)
                } catch (e: Exception) {
                    println("world load error")
                }
            }, 20*20)
        }, 20*20)
    }

    fun queueList(): List<World> {
        return plugin.server.worlds.filter { it.name.contains("Queue-") }
    }
}