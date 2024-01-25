package me.uwuaden.kotlinplugin.cooldown

import me.uwuaden.kotlinplugin.Main.Companion.plugin
import me.uwuaden.kotlinplugin.Main.Companion.scheduler
import me.uwuaden.kotlinplugin.cooldown.Cooldown.Companion.cooldowns
import org.bukkit.entity.Entity

object CooldownManager {
    fun Entity.setCooldown(tag: String, cooldown: Int) {
        val entity = this
        cooldowns[Cooldown(entity.uniqueId, tag)] = cooldown
    }
    fun Entity.isOnCooldown(tag: String): Boolean {
        val entity = this
        return (cooldowns[Cooldown(entity.uniqueId, tag)] ?: 0) != 0
    }
    fun Entity.getCooldown(tag: String): Int {
        val entity = this
        return cooldowns[Cooldown(entity.uniqueId, tag)] ?: 0
    }

    fun Entity.resetCooldown() {
        val entity = this
        cooldowns.keys.forEach {
            if (it.uuid == entity.uniqueId) {
                cooldowns.remove(it)
            }
        }
    }

    fun sch() {
        scheduler.scheduleAsyncRepeatingTask(plugin, {
            cooldowns.forEach {
                if (it.value > 0) cooldowns[it.key] = it.value - 1
            }
            cooldowns.forEach {
                if (it.value <= 0) {
                    cooldowns.remove(it.key)
                }
            }
        }, 0, 1)
    }
}