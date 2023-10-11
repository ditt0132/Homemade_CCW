package me.uwuaden.kotlinplugin.teamSystem

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class TeamEvent: Listener {
    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        if (e.damager is Player && e.entity is Player) {
            val damager = e.damager as Player
            val victim = e.entity as Player

            if (TeamManager.isSameTeam(damager.world, damager, victim)) {
                e.isCancelled = true
            }
        }
    }
}