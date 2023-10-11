package me.uwuaden.kotlinplugin.teamSystem

import me.uwuaden.kotlinplugin.Main.Companion.plugin
import me.uwuaden.kotlinplugin.Main.Companion.scheduler
import me.uwuaden.kotlinplugin.Main.Companion.worldDatas
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ArmorMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.trim.ArmorTrim
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern

object TeamManager {
    fun isSameTeam(world: World, player1: Player, player2: Player): Boolean {
        val teams = worldDatas[world]?.teams ?: return false
        if (teams.filter { it.players.contains(player1) }.isEmpty()) return false
        if (teams.filter { it.players.contains(player2) }.isEmpty()) return false
        return teams.filter { it.players.contains(player1) } == teams.filter { it.players.contains(player2) }
    }

    fun isSameTeam(world: World, attacker: Player, victim: LivingEntity): Boolean {
        if (victim !is Player) return false
        val teams = worldDatas[world]?.teams ?: return false
        if (teams.filter { it.players.contains(attacker) }.isEmpty()) return false
        if (teams.filter { it.players.contains(victim) }.isEmpty()) return false
        return teams.filter { it.players.contains(attacker) } == teams.filter { it.players.contains(victim) }
    }

    fun getTeam(world: World, player: Player): TeamClass? {
        val teams = worldDatas[world]?.teams ?: return null
        val t = teams.filter { it.players.contains(player) }
        if (t.isNotEmpty()) {
            return t[0]
        }
        return null
    }

    fun sch() {
        scheduler.scheduleSyncRepeatingTask(plugin, {
            plugin.server.worlds.forEach { world ->
                if (world.name.contains("Field-")) {
                    world.players.forEach { p ->
                        p.location.getNearbyPlayers(100.0).forEach {
                            if (isSameTeam(it.world, p, it)) {
                                if (it != p) {
                                    if (it.equipment.helmet != null && it.equipment.helmet.type.toString().contains("HELMET")) {
                                        val item = it.equipment.helmet.clone()
                                        val meta = item.itemMeta as ArmorMeta
                                        meta.trim = ArmorTrim(TrimMaterial.EMERALD, TrimPattern.WARD)
                                        item.itemMeta = meta
                                        if (item.type != Material.AIR) {
                                            p.sendEquipmentChange(it, EquipmentSlot.HEAD, item)
                                        }
                                    } else {
                                        val leatherHelm = ItemStack(Material.LEATHER_HELMET)
                                        val leatherMeta = leatherHelm.itemMeta as LeatherArmorMeta
                                        leatherMeta.setColor(Color.LIME)
                                        leatherHelm.itemMeta = leatherMeta
                                        p.sendEquipmentChange(it, EquipmentSlot.HEAD, leatherHelm)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, 0, 5)
    }
}