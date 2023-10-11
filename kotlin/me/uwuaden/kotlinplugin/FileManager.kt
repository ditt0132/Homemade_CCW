package me.uwuaden.kotlinplugin

import me.uwuaden.kotlinplugin.Main.Companion.plugin
import me.uwuaden.kotlinplugin.Main.Companion.scheduler
import me.uwuaden.kotlinplugin.quickSlot.PlayerQuickSlotData
import me.uwuaden.kotlinplugin.quickSlot.QuickSlotEvent.Companion.playerQuickSlot
import me.uwuaden.kotlinplugin.rankSystem.RankSystem
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*

private fun removeLastChars(str: String, n: Int): String {
    return str.substring(0, str.length - n)
}

object FileManager {
    fun copyDir(src: Path, destination: Path) {
        scheduler.runTaskAsynchronously(plugin, Runnable {
            try {
                Files.walk(src).forEach {
                    Files.copy(
                        it, destination.resolve(src.relativize(it)),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                }
            } catch (e: NumberFormatException) {
                println(e)
            }
        })
    }
    fun saveVar() {
        var f = File(plugin.dataFolder, "PlayerMMR.yml")
        var t = ""
        Main.playerStat.forEach {
            t += "${it.key}: ${it.value.playerMMR}, ${it.value.playerRank}, ${it.value.rank}, ${it.value.gamePlayed}, ${it.value.unRanked}\n"
        }
        f.writeText(t)

        f = File(plugin.dataFolder, "PlayerQuickSlot.yml")
        t = ""
        playerQuickSlot.forEach { (uuid, data) ->
            t += "${uuid}/-:-/ "
            data.slotData.forEach { (idx, item) ->
                var displayName = item.itemMeta.displayName //Todo: 모르겠음

                t += "${idx}/-&-/${displayName}/-&-/${item.type}/-,-/ "
            }
            t = removeLastChars(t, 6)
            t += "\n"
        }
        f.writeText(t)
    }

    fun loadVar() {
        var f = File(plugin.dataFolder.path)
        if (!f.exists()) {
            f.mkdirs()
        }
        f = File(plugin.dataFolder, "PlayerMMR.yml")
        if (f.exists()) {
            f.readText(Charsets.UTF_8).split("\n").forEach {
                if (it.trim() != "") {
                    try {
                        val key = (UUID.fromString(it.split(": ")[0].trim()))
                        if (key != null) {

                            val classData = RankSystem.initData(key)
                            val text = it.split(": ")[1]
                            val t = StringTokenizer(text, ",")
                            if (t.hasMoreTokens()) {
                                val mmr = t.nextToken().trim().toInt()
                                classData.playerMMR = mmr
                                println(classData.playerMMR)
                            }
                            if (t.hasMoreTokens()) {
                                val rank = t.nextToken().trim().toInt()
                                classData.playerRank = rank
                                println(classData.playerRank)
                            }
                            if (t.hasMoreTokens()) {
                                val bool = t.nextToken().trim().toBoolean()
                                classData.rank = bool

                            }
                            if (t.hasMoreTokens()) {
                                val gamePlayed = t.nextToken().trim().toInt()
                                classData.gamePlayed = gamePlayed
                            }
                            if (t.hasMoreTokens()) {
                                val unranked = t.nextToken().trim().toBoolean()
                                classData.unRanked = unranked
                            }
                        }
                    } catch (e: Exception) {
                        println(e)
                    }
                }
            }
        } else {
            f.createNewFile()
        }
        f = File(plugin.dataFolder, "PlayerQuickSlot.yml")
        if (f.exists()) {
            f.readText(Charsets.UTF_8).split("\n").forEach {
                if (it.trim() != "") {
                    try {
                        val key = (UUID.fromString(it.split("/-:-/ ")[0].trim()))
                        if (key != null) {
                            val text = it.split("/-:-/ ")[1]

                            playerQuickSlot[key] = PlayerQuickSlotData()
                            text.split("/-,-/").forEach { str ->
                                println(str)
                                val data = str.split("/-&-/")

                                val item = ItemStack(Material.valueOf(data[2].trim()))
                                val meta = item.itemMeta
                                if (data[1] != "") {
                                    meta.displayName(Component.text(data[1]))
                                }
                                item.itemMeta = meta
                                playerQuickSlot[key]!!.slotData[data[0].trim().toInt()] = item
                            }
                        }
                    } catch (e: Exception) {
                        println(e)
                    }
                }
            }
        } else {
            f.createNewFile()
        }
    }
}