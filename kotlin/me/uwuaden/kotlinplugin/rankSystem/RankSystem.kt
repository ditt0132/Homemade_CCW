package me.uwuaden.kotlinplugin.rankSystem

import me.uwuaden.kotlinplugin.FileManager
import me.uwuaden.kotlinplugin.Main.Companion.defaultMMR
import me.uwuaden.kotlinplugin.Main.Companion.playerStat
import me.uwuaden.kotlinplugin.Main.Companion.plugin
import me.uwuaden.kotlinplugin.Main.Companion.scheduler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.awt.Color
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 *
 * 점수, MMR은 +- 최대 50점까지 오름
 * 점수는 순위 기반 (킬도 영향 받긴 받음)
 * MMR은 개인 성적 (순위보단 킬의 영향을 많이 받음)
 *
 */

private fun rgb(R: Int, G: Int, B: Int): net.md_5.bungee.api.ChatColor {
    return net.md_5.bungee.api.ChatColor.of(Color(R, G, B))
}

object RankSystem {
    fun rateToString(player: Player): String {

        val classData = initData(player.uniqueId)
        val rate = classData.playerRank
        if (classData.unRanked) return "${ChatColor.GRAY}Unranked"
        val index = rate/100

        val list = listOf(
            "${rgb(192, 192, 192)}Iron I",
            "${rgb(192, 192, 192)}Iron II",
            "${rgb(192, 192, 192)}Iron III",
            "${rgb(192, 192, 192)}Iron IV",
            "${rgb(205, 128, 50)}Bronze I",
            "${rgb(205, 128, 50)}Bronze II",
            "${rgb(205, 128, 50)}Bronze III",
            "${rgb(205, 128, 50)}Bronze IV",
            "${rgb(176, 196, 222)}Silver I",
            "${rgb(176, 196, 222)}Silver II",
            "${rgb(176, 196, 222)}Silver III",
            "${rgb(176, 196, 222)}Silver IV",
            "${rgb(230, 179, 25)}Gold I",
            "${rgb(230, 179, 25)}Gold II",
            "${rgb(230, 179, 25)}Gold III",
            "${rgb(230, 179, 25)}Gold IV",
            "${rgb(131, 220, 183)}Platinum I",
            "${rgb(131, 220, 183)}Platinum II",
            "${rgb(131, 220, 183)}Platinum III",
            "${rgb(131, 220, 183)}Platinum IV",
            "${rgb(73, 159, 198)}Diamond I",
            "${rgb(73, 159, 198)}Diamond II",
            "${rgb(73, 159, 198)}Diamond III",
            "${rgb(73, 159, 198)}Diamond IV"
            )
        if (list.size > index) {
            return list[index]
        } else {
            return "${rgb(222, 0, 0)}Master"
        }

    }

    fun initData(playerUUID: UUID): PlayerStats {
        if (playerStat[playerUUID] == null) {
            playerStat[playerUUID] = PlayerStats(defaultMMR, 0)
        }
        return playerStat[playerUUID]!!
    }

    fun updateMMR(player: Player, kill: Int, playerTotal: Int, Ranking: Int, AvgMMR: Int) {
        val classData = initData(player.uniqueId)


        val playerMMR = classData.playerMMR
        val gap = AvgMMR - playerMMR
        var mergingScore = abs(gap/100)
        if (mergingScore > 2) mergingScore = 2
        var killFinal = (kill.toDouble()/playerTotal.toDouble())*((10.0/100.0).pow(-1)) //100명 당 10킬 기준으로 갈림

        if (killFinal > 1.0) killFinal = 1.0

        val rate = 1.0 - (Ranking.toDouble()-1.0)/playerTotal.toDouble()

        val killFactor = 80
        val rankFactor = 20

        var change = (-50 + rate*rankFactor + killFinal*killFactor).roundToInt()



        if (gap > 0) { //HighMMR = 추가 점수
            change += mergingScore*10
        } else { //LowMMR
            change -= mergingScore*10
        }

        if (change > 50) change = 50
        else if (change < -50) change = -50

        if (classData.gamePlayed < 3) {
            change*=3
        }

        classData.playerMMR = (classData.playerMMR) + change

        if (classData.playerMMR < 0) {
            classData.playerMMR = 0
        }
    }
    fun updateRank(player: Player, kill: Int, playerTotal: Int, Ranking: Int, AvgMMR: Int) {
        val classData = initData(player.uniqueId)

        val ratePrev = rateToString(player)

        val playerRate = classData.playerRank
        val playerMMR = classData.playerMMR
        val playCountNeeded = 3
        
        classData.gamePlayed += 1
        if (classData.unRanked && classData.rank) {
            if (classData.gamePlayed >= playCountNeeded) { 

                classData.playerRank = (playerMMR/100)*100 - 50

                if (classData.playerRank < 0) classData.playerRank = 0

                classData.unRanked = false

                player.sendMessage("${ChatColor.GOLD}========================================")
                player.sendMessage(" ")
                player.sendMessage("${ChatColor.GREEN}승급했습니다!")
                player.sendMessage("  ")
                player.sendMessage("${ChatColor.GREEN}티어: ${rateToString(player)}") 
                player.sendMessage("   ")
                player.sendMessage("${ChatColor.GOLD}========================================")
            } else {
                player.sendMessage("${ChatColor.GREEN}게임을 ${playCountNeeded}회 플레이 후 랭크를 확인하세요.")
                player.sendMessage("${ChatColor.GREEN}현재: ${classData.gamePlayed}")
            }

        } else {
            if (classData.unRanked) return

            val selfGap = playerMMR - playerRate
            var mergingRate = abs(selfGap / 100)
            if (mergingRate > 2) mergingRate = 2

            val gap = AvgMMR - playerMMR
            var mergingScore = abs(gap / 50)
            if (mergingScore > 3) mergingScore = 3
            var killFinal = (kill.toDouble() / playerTotal.toDouble()) * ((10.0 / 100.0).pow(-1)) //100명 당 10킬 기준으로 갈림

            if (killFinal > 1.0) killFinal = 1.0

            val rate = 1.0 - (Ranking.toDouble() - 1.0) / playerTotal.toDouble()

            val killFactor = 30
            val rankFactor = 70

            var change = (-50 + rate * rankFactor + killFinal * killFactor).roundToInt()



            if (gap > 0) { //HighMMR = 추가 점수
                change += mergingScore * 10
            } else { //LowMMR
                change -= mergingScore * 10
            }


            //MMR BOOST
            if (selfGap > 0) {
                change += mergingRate * 10
            } else {
                change -= mergingRate * 10
            }

            if (change > 50) change = 50
            else if (change < -50) change = -50

            val rank1 = classData.playerRank

            classData.playerRank = (classData.playerRank) + change

            if (classData.playerRank < 0) {
                classData.playerRank = 0
            }

            player.sendMessage("${ChatColor.GOLD}========================================")
            player.sendMessage(" ")
            player.sendMessage("${ChatColor.GOLD}${ratePrev} ${ChatColor.GREEN}-> ${rateToString(player)}")
            player.sendMessage("  ")
            player.sendMessage("${ChatColor.GREEN}Rate: ${rank1%100} -> ${(classData.playerRank)%100} (${change})")
            player.sendMessage("   ")
            player.sendMessage("${ChatColor.GOLD}========================================")


        }
    }

    fun saveSch() {
        scheduler.scheduleAsyncRepeatingTask(plugin, {
            FileManager.saveVar()
        }, 0, 20*60*20)
    }

}
