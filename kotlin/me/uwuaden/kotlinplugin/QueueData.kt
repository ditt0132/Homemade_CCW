package me.uwuaden.kotlinplugin

import java.util.*

class QueueData(val teamList: ArrayList<TeamClass> = ArrayList(), var queueEnabled: Boolean = true) {
    fun isInTeam(targetUUID: UUID): Boolean {
        this.teamList.forEach { tc ->
            tc.players.forEach { playerUUID ->
                if (playerUUID == targetUUID) return true
            }
        }
        return false
    }
    fun getTeamClass(playerUUID: UUID): TeamClass? {
        this.teamList.forEach {
            if (it.players.contains(playerUUID)) {
                return it
            }
        }
        return null
    }
}