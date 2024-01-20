package me.uwuaden.kotlinplugin.gameSystem

import me.uwuaden.kotlinplugin.Main
import me.uwuaden.kotlinplugin.Main.Companion.debugStart
import me.uwuaden.kotlinplugin.Main.Companion.map
import me.uwuaden.kotlinplugin.Main.Companion.plugin
import me.uwuaden.kotlinplugin.Main.Companion.queueClosed
import me.uwuaden.kotlinplugin.Main.Companion.queueMode
import me.uwuaden.kotlinplugin.Main.Companion.queueStartIn
import me.uwuaden.kotlinplugin.Main.Companion.scheduler
import me.uwuaden.kotlinplugin.Main.Companion.worldLoaded
import me.uwuaden.kotlinplugin.QueueData
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.World

private fun playerHolder(world: World) {

    val gameWorld = plugin.server.getWorld("Field-"+ world.name.replace("Queue-", ""))?: return
    val data = QueueOperator.initData(world)

    if (worldLoaded.contains(gameWorld.name)) {
        worldLoaded.remove(gameWorld.name)
        WorldManager.broadcastWorld(world, "§e월드 로드 완료!")
    }
    if (world.players.isEmpty()) {
        queueStartIn.remove(world.name)
    }

    if (debugStart) {
        if (world.players.size >= 1 && !queueStartIn.containsKey(world.name)) {
            val startSec = 60

            queueStartIn[world.name] = System.currentTimeMillis() + startSec * 1000
            WorldManager.broadcastWorld(world, "§a게임이 ${startSec}초 뒤에 시작합니다")
        }
    }
    if (world.players.size >= 20 && !queueStartIn.containsKey(world.name)) {
        val startSec = 300

        queueStartIn[world.name] = System.currentTimeMillis() + startSec*1000
        WorldManager.broadcastWorld(world, "§a게임이 ${startSec}초 뒤에 시작합니다")
    }

    if (world.players.size >= 40 && ((queueStartIn[world.name] ?: 0)- System.currentTimeMillis())/1000 >= 180) {
        val startSec = 180

        queueStartIn[world.name] = System.currentTimeMillis() + startSec*1000
        WorldManager.broadcastWorld(world, "§a게임이 ${startSec}초 뒤에 시작합니다")
    }
    if (world.players.size >= 60 && ((queueStartIn[world.name] ?: 0)- System.currentTimeMillis())/1000 >= 120) {
        val startSec = 120

        queueStartIn[world.name] = System.currentTimeMillis() + startSec*1000
        WorldManager.broadcastWorld(world, "§a게임이 ${startSec}초 뒤에 시작합니다!")
    }
    if (world.players.size >= 80 && ((queueStartIn[world.name] ?: 0)- System.currentTimeMillis())/1000 >= 60) {
        val startSec = 60

        queueStartIn[world.name] = System.currentTimeMillis() + startSec*1000
        WorldManager.broadcastWorld(world, "§a게임이 ${startSec}초 뒤에 시작합니다!")
    }

    val sec = queueStartIn[world.name] ?: return
    val timeSec = ((sec - System.currentTimeMillis())/1000).toInt()
    if (timeSec > 0) {
        if (timeSec in 1..10) {
            WorldManager.broadcastWorld(world, "§a게임이 ${timeSec}초 뒤에 시작합니다!")
            world.players.forEach {
                it.playSound(it, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f)
            }
        }
    }
    if (timeSec < 0) {
        queueStartIn.remove(world.name)
        queueClosed.add(world.name)
        data.queueEnabled = false
        if (queueMode[world] == null) {
            GameManager.joinPlayers(world, gameWorld, "Solo")
        } else {
            GameManager.joinPlayers(world, gameWorld, queueMode[world]!!)
        }
    }
}

object QueueOperator {
    fun initData(world: World): QueueData {
        if (Main.queueDatas[world] == null) Main.queueDatas[world] = QueueData()
        return Main.queueDatas[world]!!
    }
    fun sch() {
        scheduler.scheduleSyncRepeatingTask(plugin, {
            plugin.server.worlds.forEach { world ->
                if (world.name.startsWith("Queue-")) {
                    playerHolder(world)
                }
            }
        }, 0, 20)
        scheduler.scheduleSyncRepeatingTask(plugin, {
            if (queueList().size < 1 && WorldManager.getInGameWorldCount() == 0) { //큐 개수 수정
                createQueue("Solo", map) //Sinchon
            }
        }, 0, 20*60)
    }

    fun createQueue(mode: String, worldFolderName: String) { //수정 완료.
        val uuid = WorldManager.createQueueWorld()
        var worldStr = ""
        scheduler.runTaskAsynchronously(plugin, Runnable {
            scheduler.scheduleSyncDelayedTask(plugin, {
                worldStr = WorldManager.createFieldWorld(worldFolderName, uuid)
            }, 0)
            while (plugin.server.getWorld(worldStr) == null) {
                Thread.sleep(100)
            }
            scheduler.scheduleSyncDelayedTask(plugin, {
                if (worldStr != "") {
                    try {
                        val world = plugin.server.getWorld(worldStr)!!
                        val dataClass = WorldManager.initData(world)
                        dataClass.worldMode = mode
                        dataClass.worldFolderName = worldFolderName
                        WorldManager.loadWorldChunk(
                            world,
                            Location(world, -1000.0, 0.0, -1000.0),
                            Location(world, 1000.0, 0.0, 1000.0),
                            true
                        )
                    } catch (e: Exception) {
                        println("world load error")
                    }
                }
            }, 100)
        })
    }

    fun queueList(): List<World> {
        return plugin.server.worlds.filter { it.name.contains("Queue-") }
    }
}