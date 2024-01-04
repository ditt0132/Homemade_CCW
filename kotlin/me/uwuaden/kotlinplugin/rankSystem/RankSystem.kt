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


private fun rgb(R: Int, G: Int, B: Int): net.md_5.bungee.api.ChatColor {
    return net.md_5.bungee.api.ChatColor.of(Color(R, G, B))
}

object RankSystem {
    fun changeRate(player: Player, rate: Int) {
        val ratePrev = rateToString(player)
        var change = rate
        val classData = initData(player.uniqueId)
        val rank1 = classData.playerRank

        if (change > 50) change = 50
        else if (change < -50) change = -50

        classData.playerRank += change

        if (classData.playerRank < 0) {
            classData.playerRank = 0
        }

        //강등 보정
        while (rank1/400 > classData.playerRank/400) {
            classData.playerRank += 1
            change += 1
        }

        player.sendMessage("${ChatColor.GOLD}========================================")
        player.sendMessage(" ")
        player.sendMessage("${ChatColor.GOLD}${ratePrev} ${ChatColor.GREEN}-> ${rateToString(player)}")
        player.sendMessage("  ")
        player.sendMessage("${ChatColor.GREEN}Rate: ${rateToScore(rank1)} -> ${rateToScore(classData.playerRank)} (${change})")
        player.sendMessage("   ")
        player.sendMessage("${ChatColor.GOLD}========================================")
    }

    fun rateToScore(rate: Int): Int {
        val masterRate = 3200
        if (rate < masterRate) { //마스터 아래
            return rate%100
        } else {
            return rate - masterRate
        }
    }
    fun rateToString(player: Player): String {

        val classData = initData(player.uniqueId)
        val rate = classData.playerRank
        if (classData.unRanked) return "${ChatColor.GRAY}Unranked"
        val index = rate/100

        return when (index) {
            0 -> "${rgb(192, 192, 192)}Iron I"
            1 -> "${rgb(192, 192, 192)}Iron II"
            2 -> "${rgb(192, 192, 192)}Iron III"
            3 -> "${rgb(192, 192, 192)}Iron IV"
            4 -> "${rgb(205, 128, 50)}Bronze I"
            5 -> "${rgb(205, 128, 50)}Bronze II"
            6 -> "${rgb(205, 128, 50)}Bronze III"
            7 -> "${rgb(205, 128, 50)}Bronze IV"
            8 -> "${rgb(176, 196, 222)}Silver I"
            9 -> "${rgb(176, 196, 222)}Silver II"
            10 -> "${rgb(176, 196, 222)}Silver III"
            11 -> "${rgb(176, 196, 222)}Silver IV"
            12 -> "${rgb(230, 179, 25)}Gold I"
            13 -> "${rgb(230, 179, 25)}Gold II"
            14 -> "${rgb(230, 179, 25)}Gold III"
            15 -> "${rgb(230, 179, 25)}Gold IV"
            16 -> "${rgb(131, 220, 183)}Platinum I"
            17 -> "${rgb(131, 220, 183)}Platinum II"
            18 -> "${rgb(131, 220, 183)}Platinum III"
            19 -> "${rgb(131, 220, 183)}Platinum IV"
            20 -> "${rgb(73, 159, 198)}Diamond I"
            21 -> "${rgb(73, 159, 198)}Diamond II"
            22 -> "${rgb(73, 159, 198)}Diamond III"
            23 -> "${rgb(73, 159, 198)}Diamond IV"
            24 -> "${rgb(215, 153, 255)}Master I"
            25 -> "${rgb(215, 153, 255)}Master II"
            26 -> "${rgb(215, 153, 255)}Master III"
            27 -> "${rgb(215, 153, 255)}Master IV"
            28 -> "${rgb(222, 0, 0)}Grand Master I"
            29 -> "${rgb(222, 0, 0)}Grand Master II"
            30 -> "${rgb(222, 0, 0)}Grand Master II"
            31 -> "${rgb(222, 0, 0)}Grand Master IV"
            32 -> "${rgb(197, 250, 250)}Eternel"
            else -> "Error"
        }

    }

    fun initData(playerUUID: UUID): PlayerStats {
        if (playerStat[playerUUID] == null) {
            playerStat[playerUUID] = PlayerStats(defaultMMR, 0)
        }
        return playerStat[playerUUID]!!
    }

    /**
     *
     * 점수, MMR은 +- 최대 50점까지 오름
     * 점수는 순위 기반 (킬도 영향 받긴 받음)
     * MMR은 개인 성적 (순위보단 킬의 영향을 많이 받음)
     *
     */
    fun updateMMR(player: Player, kill: Int, playerTotal: Int, Ranking: Int, AvgMMR: Int) {
        val classData = initData(player.uniqueId)


//        val playerMMR = classData.playerMMR
//        val gap = AvgMMR - playerMMR
//        var mergingScore = abs(gap/100)
//        if (mergingScore > 2) mergingScore = 2
        var killFinal = (kill.toDouble()/playerTotal.toDouble())*((10.0/100.0).pow(-1)) //100명 당 10킬 기준으로 갈림 = 100명일때 10킬시 최대 킬 스코어 (1.0)

        if (killFinal > 1.0) killFinal = 1.0

        val rate = 1.0 - (Ranking.toDouble()-1.0)/playerTotal.toDouble() //1등시 1.0 꼴지하면 0.0

        val killFactor = 80
        val rankFactor = 20

        var change = (-30 + rate*rankFactor + killFinal*killFactor).roundToInt() //최대한 오르게 -50 -> -30으로 개선



//        if (gap > 0) { //HighMMR = 추가 점수
//            change += mergingScore*10
//        } else { //LowMMR
//            change -= mergingScore*10
//        }

        if (change > 50) change = 50
        else if (change < -50) change = -50

        if (classData.gamePlayed < 3 && change > 0) {
            change*=5
        }
        classData.gamePlayed += 1

        classData.playerMMR += change

        if (classData.playerMMR < classData.playerRank - 400) {
            classData.playerMMR = classData.playerRank - 400
        }

        if (classData.playerMMR < 0) {
            classData.playerMMR = 0
        } //0이하로 떨어지면 보정 들어갑니다.
    }
    fun updateRank(player: Player, kill: Int, playerTotal: Int, Ranking: Int, AvgMMR: Int) {
        val classData = initData(player.uniqueId)


        val playerRate = classData.playerRank
        val playerMMR = classData.playerMMR
        val playCountNeeded = 3

        if (classData.unRanked && classData.rank) {
            if (classData.gamePlayed >= playCountNeeded) { 

                classData.playerRank = (playerMMR/100)*100 + 50 //-50으로 보정 했는데 높은곳으로 올려주기 위해 +50으로 바꿈

                if (classData.playerRank < 0) classData.playerRank = 0

                classData.unRanked = false

                player.sendMessage("${ChatColor.GOLD}========================================")
                player.sendMessage(" ")
                player.sendMessage("${ChatColor.GREEN}승급했습니다!")
                player.sendMessage("  ")
                player.sendMessage("${ChatColor.GREEN}티어: ${rateToString(player)} (${rateToScore(classData.playerRank)})")
                player.sendMessage("   ")
                player.sendMessage("${ChatColor.GOLD}========================================")
            } else {
                player.sendMessage("${ChatColor.GREEN}게임을 ${playCountNeeded}회 플레이 후 랭크를 확인하세요.")
                player.sendMessage("${ChatColor.GREEN}현재: ${classData.gamePlayed}")
            }

        } else {
            if (classData.unRanked) return

            val selfGap = playerMMR - playerRate
            var mergingRate = abs(selfGap / 50)
            if (mergingRate > 2) mergingRate = 2

            var killFinal = (kill.toDouble() / playerTotal.toDouble()) * ((10.0 / 100.0).pow(-1)) //100명 당 10킬 기준으로 갈림

            if (killFinal > 1.0) killFinal = 1.0

            val rate = 1.0 - (Ranking.toDouble() - 1.0) / playerTotal.toDouble()

            val killFactor = 30
            val rankFactor = 70

            var change = (-50 + rate * rankFactor + killFinal * killFactor).roundToInt()

            //MMR BOOST 마이너스 뺌.
            if (selfGap > 0) {
                change += mergingRate * 10
            }

            changeRate(player, change)
        }
    }

    fun saveSch() {
        scheduler.scheduleAsyncRepeatingTask(plugin, {
            FileManager.saveVar()
        }, 0, 20*60*20)
    }

}
