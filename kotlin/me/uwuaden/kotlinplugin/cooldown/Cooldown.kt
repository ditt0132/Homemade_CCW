package me.uwuaden.kotlinplugin.cooldown

import java.util.*

class Cooldown(var uuid: UUID, var tag: String) {
    companion object {
        val cooldowns = HashMap<Cooldown, Int>()
    }
}