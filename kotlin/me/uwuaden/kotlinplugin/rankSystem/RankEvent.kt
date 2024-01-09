package me.uwuaden.kotlinplugin.rankSystem

import me.uwuaden.kotlinplugin.Main.Companion.plugin
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class RankEvent: Listener {
    @EventHandler
    fun onLeaderBoardInvClick(e: InventoryClickEvent) {
        val inv = e.view.topInventory
        val player = e.view.player as Player
        val slot = e.slot
        if (e.inventory != inv) return
        if (e.view.title != "§2§lRank LeaderBoard") return

        e.isCancelled = true
        try {
            val page = inv.getItem(0)!!.itemMeta.displayName.split(": ").last().toInt()
            if (inv.getItem(slot)?.type == Material.ARROW) {
                if (slot == 45) {
                    RankSystem.openLeaderBoardGui(player, page - 1)
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f ,1.0f)
                }
                if (slot == 53) {
                    RankSystem.openLeaderBoardGui(player, page + 1)
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f ,1.0f)
                }
            }
            if (listOf(4, 13, 22, 31, 40, 49).contains(slot) && inv.getItem(slot)?.itemMeta?.displayName?.contains(".") == true) {
                val name = ChatColor.stripColor(inv.getItem(slot)!!.itemMeta.displayName.split(". ").last())!!
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f ,1.0f)
                RankSystem.openGui(player, plugin.server.getOfflinePlayer(name).uniqueId)
            }
        } catch (e: Exception) {

        }
    }
    @EventHandler
    fun onRankBoardClick(e: InventoryClickEvent) {
        val inv = e.view.topInventory
        val player = e.view.player as Player
        val slot = e.slot
        val playerStat = RankSystem.initData(player.uniqueId)
        if (e.inventory != inv) return
        if (e.view.title != "§2§lRank Board") return

        e.isCancelled = true

        if (slot == 8) {
            if (inv.getItem(slot)!!.type == Material.GREEN_STAINED_GLASS) {
                playerStat.rank = false
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f ,1.0f)
                RankSystem.openGui(player)
            } else if (inv.getItem(slot)!!.type == Material.GRAY_STAINED_GLASS) {
                playerStat.rank = true
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f ,1.0f)
                RankSystem.openGui(player)
            }
        }
        if (slot == 53) {
            RankSystem.openLeaderBoardGui(player)
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f ,1.0f)
        }
    }
}