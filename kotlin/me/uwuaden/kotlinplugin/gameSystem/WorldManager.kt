package me.uwuaden.kotlinplugin.gameSystem

import me.uwuaden.kotlinplugin.FileManager
import me.uwuaden.kotlinplugin.Main
import me.uwuaden.kotlinplugin.Main.Companion.plugin
import me.uwuaden.kotlinplugin.Main.Companion.scheduler
import me.uwuaden.kotlinplugin.Main.Companion.worldDatas
import me.uwuaden.kotlinplugin.Main.Companion.worldLoaded
import me.uwuaden.kotlinplugin.WorldDataManager
import me.uwuaden.kotlinplugin.itemManager.maps.MapRenderer.Companion.blockTypeCach
import net.kyori.adventure.text.Component
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.logging.Level


object  WorldManager {
    fun initData(world: World): WorldDataManager {
        if (worldDatas[world] == null) worldDatas[world] = WorldDataManager()
        return worldDatas[world]!!
    }

    fun deleteWorldData(world: World) {
        worldDatas.keys.removeIf { world == it }
        blockTypeCach.keys.removeIf { world == it.world }
    }
    fun isOutsideBorder(location: Location): Boolean {
        val world = location.world
        val borderSize = world.worldBorder.size
        val centerX = world.worldBorder.center.x
        val centerZ = world.worldBorder.center.z
        val x = location.x
        val z = location.z
        val minX = centerX - borderSize / 2.0
        val maxX = centerX + borderSize / 2.0
        val minZ = centerZ - borderSize / 2.0
        val maxZ = centerZ + borderSize / 2.0

        return !((minX < x && x < maxX) && (minZ < z && z < maxZ))
    }
    fun createQueueWorld(): UUID {
        /*
        create World named "Queue-(UUID)"
        returns world Name
        */
        val worldUUID = UUID.randomUUID()
        val worldName = "Queue-$worldUUID"

        val copyDir = File(File(plugin.dataFolder, "maps"), "queue")
        val pasteDir = File(plugin.server.worldContainer, worldName)
        scheduler.runTaskAsynchronously(plugin, Runnable {
            FileManager.copyDir(copyDir.toPath(), pasteDir.toPath())
            scheduler.scheduleSyncDelayedTask(plugin, {
                loadWorld(worldName)
            }, 0)
        })

        return worldUUID
    }
    fun createFieldWorld(worldFolderName: String, name: UUID): String {
        /*
        create World named "Field-(name)"
        returns world Name
        */
        val worldName = "Field-$name"

        val copyDir = File(File(plugin.dataFolder, "maps"), worldFolderName)
        val pasteDir = File(plugin.server.worldContainer, worldName)
        scheduler.runTaskAsynchronously(plugin, Runnable {
            FileManager.copyDir(copyDir.toPath(), pasteDir.toPath())
            scheduler.scheduleSyncDelayedTask(plugin, {
                loadWorld(worldName)
            }, 0)
        })


        return worldName
    }
    fun loadWorld(WorldName: String) {
        scheduler.scheduleSyncDelayedTask(plugin, {
            Main.plugin.server.createWorld(WorldCreator(WorldName))
        }, 20*5)
    }

    fun getInGameWorldCount(): Int {
        return plugin.server.worlds.filter { it.name.contains("Field-") && it.playerCount != 0}.size
    }

    fun loadWorldChunk(world: World, from: Location, to: Location, loadAnnounce: Boolean) {
        val minX = from.blockX.coerceAtMost(to.blockX)
        val minZ = from.blockZ.coerceAtMost(to.blockZ)
        val maxX = from.blockX.coerceAtLeast(to.blockX)
        val maxZ = from.blockZ.coerceAtLeast(to.blockZ)

        val chunkXMin = minX shr 4
        val chunkXMax = maxX shr 4
        val chunkZMin = minZ shr 4
        val chunkZMax = maxZ shr 4
        plugin.logger.log(Level.WARNING, "World \"${world.name}\" Chunk Loading Started")
        if (getInGameWorldCount() == 0) {
            plugin.logger.log(Level.WARNING, "Waiting for Ingame Progress..")
        }

        val milli = System.currentTimeMillis()

        Main.scheduler.runTaskAsynchronously(Main.plugin, Runnable {

            while (getInGameWorldCount() != 0) {
                Thread.sleep(1000)
            }
            plugin.logger.log(Level.WARNING, "Chunk Load Started Again..")
            val chunkFutures = mutableListOf<CompletableFuture<Chunk>>()
            for (x in chunkXMin..chunkXMax) {
                for (z in chunkZMin..chunkZMax) {
                    val chunkFuture = world.getChunkAtAsync(x, z)
                    chunkFutures.add(chunkFuture)
                }
            }

            CompletableFuture.allOf(*chunkFutures.toTypedArray()).thenRun {
                plugin.logger.log(Level.WARNING, "World \"${world.name}\" Chunk Loading Done (${(System.currentTimeMillis()-milli)/1000.0} Seconds)")
                if (loadAnnounce) worldLoaded.add(world.name)
            }
        })
    }
    fun broadcastWorld(world: World, message: String) {
        world.players.forEach { player ->
            player.sendMessage(message)
        }
    }
    fun broadcastWorld(world: World, component: Component) {
        world.players.forEach { player ->
            player.sendMessage(component)
        }
    }

    fun deleteWorld(world: World) {
        deleteWorldData(world)
        scheduler.runTaskAsynchronously(plugin, Runnable {
            scheduler.scheduleSyncDelayedTask(plugin, {
                plugin.server.unloadWorld(world, false)
                plugin.server.worlds.remove(world)
            }, 0)
            scheduler.scheduleSyncDelayedTask(plugin, {
                world.worldFolder.deleteRecursively()
            }, 20 * 5)
        })
    }
    fun deleteWorldInstantly(world: World) {
        plugin.server.unloadWorld(world, false)

        world.worldFolder.deleteRecursively()

    }

}