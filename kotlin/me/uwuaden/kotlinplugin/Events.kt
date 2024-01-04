package me.uwuaden.kotlinplugin

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import me.uwuaden.kotlinplugin.Main.Companion.econ
import me.uwuaden.kotlinplugin.Main.Companion.lastDamager
import me.uwuaden.kotlinplugin.Main.Companion.lastWeapon
import me.uwuaden.kotlinplugin.Main.Companion.lobbyLoc
import me.uwuaden.kotlinplugin.Main.Companion.luckpermAPI
import me.uwuaden.kotlinplugin.Main.Companion.plugin
import me.uwuaden.kotlinplugin.Main.Companion.scheduler
import me.uwuaden.kotlinplugin.assets.CustomItemData
import me.uwuaden.kotlinplugin.assets.EffectManager
import me.uwuaden.kotlinplugin.assets.ItemManipulator.setCount
import me.uwuaden.kotlinplugin.gameSystem.LastWeaponData
import me.uwuaden.kotlinplugin.gameSystem.WorldManager
import me.uwuaden.kotlinplugin.itemManager.ItemManager
import me.uwuaden.kotlinplugin.itemManager.customItem.CustomItemManager
import me.uwuaden.kotlinplugin.rankSystem.RankSystem
import me.uwuaden.kotlinplugin.skillSystem.SkillEvent
import me.uwuaden.kotlinplugin.skillSystem.SkillManager
import me.uwuaden.kotlinplugin.teamSystem.TeamManager
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.block.Chest
import org.bukkit.entity.Firework
import org.bukkit.entity.IronGolem
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Team
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

//e.player.world.name.contains("Field-")
//isBattleWorld(e.player.world)
private fun isBattleWorld(world: World): Boolean {
    return world.name.contains("Field-") || world.name.contains("death_match")
}

private fun deathPlayer(p: Player) {
    if (p.world.name.contains("Field-")) {
        p.gameMode = GameMode.SPECTATOR

        var killer = p.killer
        if (killer == null) {
            killer = lastDamager[p]
        }
        val dataClass = WorldManager.initData(p.world)
        if (dataClass.deadPlayer.contains(p)) return
        if (lastDamager[p] != null) {
            if (TeamManager.isSameTeam(p.world, p, lastDamager[p]!!)) {
                Main.lastDamager.remove(p)
            }
        }

        if (killer != null) {
            dataClass.playerKill[killer.uniqueId] = (dataClass.playerKill[killer.uniqueId] ?: 0) + 1
            if (dataClass.worldMode == "Heist") {
                SkillManager.addCapacityPoint(killer, 100)
                econ.depositPlayer(killer, 100.0)
                killer.sendMessage("§e플레이어 킬! (+100코인)")
            }
            else {
                SkillManager.addCapacityPoint(killer, 100)
                econ.depositPlayer(killer, 500.0)
                killer.sendMessage("§e플레이어 킬! (+500코인)")
            }
        }
        if (lastDamager[p] == null) {
            WorldManager.broadcastWorld(
                p.world,
                "${ChatColor.RED}☠   ${ChatColor.BOLD}➔ ${ChatColor.RED}${p.name}"
            )
        } else {
            var weaponName: String? = null
            var weapon: ItemStack? = null

            if (lastWeapon[p] != null) {
                val data = lastWeapon[p]!!
                if (data.effTimeMilli > System.currentTimeMillis() && data.item.itemMeta?.displayName != "") {
                    weaponName = data.item.itemMeta?.displayName
                    weapon = data.item.clone()

                }
            }

            var msg = Component.text("${ChatColor.RED}☠ ${lastDamager[p]!!.name} ${ChatColor.BOLD}➔ ${ChatColor.RED}${p.name}")
            if (weaponName != null) {
                msg = msg.append(Component.text("${ChatColor.RED} with ").append(Component.text("${ChatColor.RED}${ChatColor.BOLD}[${weaponName}${ChatColor.RED}${ChatColor.BOLD}]").hoverEvent(
                    weapon)))
            }

            WorldManager.broadcastWorld(
                p.world,
                msg
            )
        }

        var coreExist = false
        val teamId = TeamManager.getTeam(p.world, p)?.id
        if (teamId == 0) {
            if (dataClass.dataInt1 == 1) {
                coreExist = true
            }
        }
        if (teamId == 1) {
            if (dataClass.dataInt2 == 1) {
                coreExist = true
            }
        }

        if (dataClass.worldMode == "SoloSurvival") return
        if (dataClass.worldMode == "Heist" && coreExist) {
            p.sendMessage("§a15초 뒤에 부활합니다..")
            p.sendMessage("§7(코어로 인한 부활)")
            scheduler.scheduleSyncDelayedTask(plugin, {
                if (teamId == 0) {
                    p.teleport(dataClass.dataLoc1)
                }
                if (teamId == 1) {
                    p.teleport(dataClass.dataLoc2)
                }
                p.gameMode = GameMode.SURVIVAL
                p.health = p.maxHealth
                p.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*10, 4, false, false))
            }, 20*15)
            return
        }

        val drop = ItemManager.createDroppedItem(p.location, true, 6)

        val items = ArrayList<ItemStack>()

        p.inventory.forEach {
            if (it != null) {
                items.add(it)
            }
        }
        if (p.inventory.itemInOffHand.type != Material.AIR) {
            items.add(p.inventory.itemInOffHand)
        }

        items.removeIf { it.itemMeta.lore?.contains("${ChatColor.GRAY}Gadget") == true }

        items.shuffle()
        var i = 0
        items.forEach {
            drop.items[i] = it
            i++
        }
        ItemManager.createDisplay(drop)

        p.inventory.clear()

        lastDamager.remove(p)

        if (!dataClass.deadPlayer.contains(p) && (listOf("Solo", "Quick").contains(dataClass.worldMode)) && !dataClass.gameEndedWorld) {
            dataClass.deadPlayer.add(p)

            RankSystem.updateMMR(p, dataClass.playerKill[p.uniqueId]?: 0, dataClass.totalPlayer, p.world.players.filter { it.gameMode == GameMode.SURVIVAL }.size + 1, dataClass.avgMMR)
            RankSystem.updateRank(p, dataClass.playerKill[p.uniqueId]?: 0, dataClass.totalPlayer, p.world.players.filter { it.gameMode == GameMode.SURVIVAL }.size + 1, dataClass.avgMMR)

        }
    }
}

class Events: Listener {
//    @EventHandler
//    fun onChunkLoad(e: ChunkLoadEvent) {
//        if (e.world.name.contains("Field-")) {
//            WorldItemManager.createItems(e.chunk) //데이터 생성
//            GameManager.initDroppedItemLoc(e.chunk) //위치 설정
//            //chunkItemDisplayGen.add(e.chunk)
//        }
//    }
    @EventHandler
    fun slotChange(e: PlayerItemHeldEvent) {
        val item = e.player.inventory.getItem(e.newSlot) ?: return
        if (ItemManager.isRangedWeapon(item.type)) {
            ItemManager.rangedWeaponList().forEach {
                if (e.player.getCooldown(it) < 20) {
                    e.player.setCooldown(it, 20)
                }
            }
        }
    }
    @EventHandler
    fun onNaturalHealing(e: EntityRegainHealthEvent) {
        if (e.entity is Player) {
            val player = e.entity as Player
            if (player.fireTicks > 0) {
                e.isCancelled = true
                player.sendActionBar("${ChatColor.RED}불에 타는 중에는 회복이 되지 않습니다.")
            }
        }
    }
    @EventHandler
    fun onDamageByEntity(e: EntityDamageByEntityEvent) {
        if (e.damager is Player && e.entity is Player) {
            val damager = e.damager as Player
            val victim = e.entity as Player

            if (!TeamManager.isSameTeam(damager.world, damager, victim)) {
                lastDamager[e.entity as Player] = e.damager as Player
                LastWeaponData(damager.inventory.itemInMainHand, System.currentTimeMillis()+1000*10).set(e.entity as Player)
            }
            if ((victim.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)?: return).amplifier >= 4) {
                damager.sendActionBar("${ChatColor.GRAY}${victim.name}님은 면역상태입니다.")
                e.isCancelled = true
            }
        }
    }
    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        if (e.entity is Player) {
            val player = e.entity as Player
            if (listOf(EntityDamageEvent.DamageCause.ENTITY_ATTACK, EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK).contains(e.cause)) {
                e.damage*=0.9
            } else if (listOf(EntityDamageEvent.DamageCause.PROJECTILE).contains(e.cause)) {
                e.damage*=0.7
            }
            if ((player.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)?: return).amplifier >= 4) {
                e.isCancelled = true
            }
        }
    }
    @EventHandler
    fun onPlayerJump(e: PlayerJumpEvent) {
        val player = e.player
        if ((player.getPotionEffect(PotionEffectType.SLOW)?: return).amplifier >= 2) { //3보다 크거나 같다.
            player.sendActionBar("${ChatColor.GRAY}구속 효과로 인해 점프가 불가능합니다.")
            scheduler.runTaskAsynchronously(plugin, Runnable {
                for (i in 0 until 20) {
                    scheduler.scheduleSyncDelayedTask(plugin, {
                        if (!e.player.isOnGround) {
                            val vector = Vector(0.0, player.velocity.y, 0.0)
                            if (player.velocity.y > 0) {
                                vector.setY(-0.1)
                            }
                            player.velocity = vector
                        }
                    }, 0)
                    Thread.sleep(1000/20)
                }
            })
        }
    }
    @EventHandler
    fun onFirework(e: EntityDamageByEntityEvent) {
        if (e.damager is Firework && e.damager.scoreboardTags.contains("display_firework")) {
            e.isCancelled = true
        }
    }
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!e.player.world.name.contains("Field-")) return
        e.isCancelled = true
        e.keepInventory = true
        e.drops.clear()
        deathPlayer(e.player)
    }
    @EventHandler
    fun onPlayerDMDeath(e: PlayerDeathEvent) {
        if (!e.player.world.name.contains("death_match")) return
        e.drops.clear()
        e.player.inventory.clear()
        e.isCancelled = true

        e.player.world.dropItem(e.player.location, ItemStack(Material.ARROW, 32))
        e.player.world.dropItem(e.player.location, ItemStack(Material.COOKED_BEEF, 16))
        val dropItem: ItemStack
        val random = Random
        val randomNumber = random.nextInt(0, 5)
        dropItem = when (randomNumber) {
            0 -> CustomItemData.getFlashBang().setCount(3)
            1 -> CustomItemData.getMolt().setCount(3)
            2 -> CustomItemData.getEarthGr().setCount(3)
            3 -> CustomItemData.getGravityG().setCount(3)
            4 -> ItemStack(Material.GOLDEN_APPLE, 3)
            else -> ItemStack(Material.ARROW)
        }
        e.player.world.dropItem(e.player.location, dropItem)

        e.player.health = e.player.maxHealth
        e.player.teleport(lobbyLoc)
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player

        e.joinMessage = ""
        e.player.teleport(lobbyLoc)
        e.player.gameMode = GameMode.SURVIVAL
        e.player.inventory.clear()
        e.player.level = 0
        e.player.exp = 0.0F
        var t = SkillEvent.score.getTeam("nameTagHide")
        if (t == null) {
            t = SkillEvent.score.registerNewTeam("nameTagHide")
            t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
        }
        if(!t.hasPlayer(player)) {
            t.addPlayer(player)
        }
    }
    @EventHandler
    fun onSpecTeleportEvent(e: PlayerTeleportEvent){
        val player = e.player
        if (player.isOp) {
            return
        }
        if (player.gameMode != GameMode.SPECTATOR) {
            return
        }
        e.isCancelled = true
    }
    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        e.quitMessage = ""
        deathPlayer(e.player)
    }

    @EventHandler
    fun onChat(e: PlayerChatEvent) {
        val player = e.player
        val user = luckpermAPI.getPlayerAdapter(Player::class.java).getUser(player)
        val prefix = user.cachedData.metaData.prefix ?: ""
        e.format = "§f${prefix}${player.name}§f: ${e.message}"
        val world = player.world
        val msg = e.message
        val team = TeamManager.getTeam(world, player)
        if (team == null) {
            if (!player.hasPermission("CCW.Chat")) {
                if (player.hasCooldown(Material.PAPER)) {
                    e.isCancelled = true
                    player.sendMessage("§c채팅이 쿨타임 중 입니다. (${(player.getCooldown(Material.PAPER) / 20.0).roundToInt()}초)")
                } else {
                    player.setCooldown(Material.PAPER, 20 * 3)
                }
                return
            }
        } else {
            e.isCancelled = true
            if (msg == "집결") {
                player.performCommand("teamcmd assemble")
            } else if (msg == "아이템") {
                team.players.filter {it.world == player.world }.filter { it.location.distance(player.location) <= 50 }.forEach {
                    it.sendMessage(Component.text("§a${player.name}님이 주변에 여유 아이템이 있다고 합니다!"))
                }
            } else {
                team.players.forEach {
                    it.sendMessage(Component.text("§a[Team] §e${player.name}: $msg §7(${player.location.distance(it.location).roundToInt()}m)"))
                }
            }
        }
    }

    @EventHandler
    fun useAxe(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND) return
        if (!e.action.isLeftClick) return
        if (e.clickedBlock == null) return
        if (!e.player.inventory.itemInMainHand.type.name.contains("_AXE")) return
        val clickedBlock = e.clickedBlock!!

        val blockLocations = mutableSetOf<Location>()
        blockLocations.add(clickedBlock.location)


        for (i in 0 until 3) {
            val locations = blockLocations.toList()
            locations.forEach {
                for (x in -1..1) {
                    for (y in -1..1) {
                        for (z in -1..1) {
                            if (it.block.type.name.contains("GLASS") || it.block.type.name.contains("IRON_DOOR") || it.block.type.name.contains("_DOOR")) {
                                val newLoc = it.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                                blockLocations.add(newLoc)
                            }
                        }
                    }
                }
            }
        }

        blockLocations.forEach {
            if (it.block.type.name.contains("GLASS")) {
                it.world.spawnParticle(Particle.BLOCK_CRACK, it, 5, 0.5, 0.5, 0.5, 0.0, it.block.blockData)
                it.block.type = Material.AIR
                EffectManager.playSurroundSound(it, Sound.BLOCK_GLASS_BREAK, 1.0F, 1.5F)
            }
            if (it.block.type.name.contains("IRON_DOOR")) {
                it.world.spawnParticle(Particle.BLOCK_CRACK, it, 5, 0.5, 0.5, 0.5, 0.0, it.block.blockData)
                EffectManager.playSurroundSound(it, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.5F, 1.0F)
            } else if (it.block.type.name.contains("_DOOR")) {
                it.world.spawnParticle(Particle.BLOCK_CRACK, it, 5, 0.5, 0.5, 0.5, 0.0, it.block.blockData)
                it.block.type = Material.AIR
                EffectManager.playSurroundSound(it, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.0F, 1.5F)
            }
        }
    }

    @EventHandler
    fun onProjectileHit(e: ProjectileHitEvent) {
        val projectile = e.entity
        val shooter = e.entity.shooter ?: return
        val entity = e.hitEntity ?: return
        if (shooter is Player && entity is Player) {
            if (!CustomItemManager.isHittable(shooter, entity)) {
                e.isCancelled = true
                projectile.remove()
                return
            }

            if (entity.isBlocking) {
                CustomItemManager.disablePlayerShield(entity)
                entity.setCooldown(Material.SHIELD, 20 * 8)
                projectile.remove()

            }
            if (entity != shooter) lastDamager[entity] = shooter
        }
    }
    @EventHandler
    fun onWorldChange(e: PlayerChangedWorldEvent) {
        if (e.player.world.name == "world") {
            e.player.inventory.clear()
        }
    }

    @EventHandler
    fun interactWorldItems(e: PlayerInteractEvent) {
        val clickedBlock = e.clickedBlock ?: return
        if (e.action.isLeftClick) return
        if (!isBattleWorld(e.player.world)) return
        if (clickedBlock.type == Material.CHEST) {
            val chest = clickedBlock.state as Chest
            if (chest.customName != "${ChatColor.YELLOW}Supplies") {
                e.isCancelled = true
            }
        }

        if (e.player.gameMode == GameMode.CREATIVE) {
            return
        }
        if (clickedBlock.type == Material.TRAPPED_CHEST) {
            e.isCancelled = true
        } else if (clickedBlock.type == Material.ENDER_CHEST) {
            e.isCancelled = true
        } else if (clickedBlock.type.toString().contains("_SHULKER_BOX")) {
            e.isCancelled = true
        } else if (clickedBlock.type == Material.BARREL) {
            e.isCancelled = true
        }
    }
    @EventHandler
    fun onCoreTakeDamage(e: EntityDamageByEntityEvent) {
        val attacker = e.damager
        val victim = e.entity
        if (victim !is IronGolem) return
        if (!victim.scoreboardTags.contains("core")) return
        if (attacker !is Player) {
            e.isCancelled = true
            return
        }
        if (victim.scoreboardTags.filter { it.contains("team:") }.isEmpty()) return
        val uuid = UUID.fromString(victim.scoreboardTags.filter { it.contains("team:") }.first().split(":")[1])

        if (victim.scoreboardTags.filter { it.contains("teamid:") }.isEmpty()) return
        val id = victim.scoreboardTags.filter { it.contains("teamid:") }.first().split(":")[1].toInt()

        if ((TeamManager.getTeam(attacker.world, attacker)?: return).uuid == uuid) {
            e.isCancelled = true
            return
        }
        val victimTeam = TeamManager.getTeam(victim.world, uuid)?: return
        attacker.sendActionBar(Component.text("§aCore Health: (${victim.health.roundToInt()}/${victim.maxHealth.roundToInt()})"))
        victimTeam.players.forEach {
            it.sendActionBar(Component.text("§a코어가 공격 받고 있습니다! (${victim.health.roundToInt()}/${victim.maxHealth.roundToInt()})"))
        }
        EffectManager.playSurroundSound(victim.location, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.5f, 0.9f)

    }
    @EventHandler
    fun onCoreDeath(e: EntityDeathEvent) {
        val victim = e.entity
        if (victim !is IronGolem) return
        if (!victim.scoreboardTags.contains("core")) return
        if (victim.scoreboardTags.filter { it.contains("team:") }.isEmpty()) return
        val uuid = UUID.fromString(victim.scoreboardTags.filter { it.contains("team:") }.first().split(":")[1])

        if (victim.scoreboardTags.filter { it.contains("teamid:") }.isEmpty()) return
        val id = victim.scoreboardTags.filter { it.contains("teamid:") }.first().split(":")[1].toInt()
        victim.remove()
        val dataClass = WorldManager.initData(victim.world)
        if (id == 0) {
            dataClass.dataInt1 = 0
            dataClass.teams.filter { it.id == 1 }.first().players.forEach {
                it.sendMessage("§e적팀의 코어가 파괴되었습니다.")
                it.sendMessage("§e적팀은 이제 부활할 수 없습니다!")
                it.sendTitle("§e§l적 코어 파괴됨!", " ", 5, 20*5, 5)
                it.playSound(it, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f)
            }
        }
        if (id == 1) {
            dataClass.dataInt2 = 0
            dataClass.teams.filter { it.id == 0 }.first().players.forEach {
                it.sendMessage("§e적팀의 코어가 파괴되었습니다.")
                it.sendMessage("§e적팀은 이제 부활할 수 없습니다!")
                it.sendTitle("§e§l적 코어 파괴됨!", " ", 5, 20*5, 5)
                it.playSound(it, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f)
            }
        }

        val victimTeam = TeamManager.getTeam(victim.world, uuid)?: return
        victimTeam.players.forEach {
            it.sendTitle("§c§l코어 파괴됨!", "§e더 이상 부활할 수 없습니다.", 5, 20*5, 5)
            it.sendMessage("§c팀의 코어가 파괴되었습니다. 더 이상 부활할 수 없습니다.")
            it.playSound(it, Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f)
        }

        victim.world.spawnParticle(Particle.EXPLOSION_HUGE, victim.location, 1, 0.0, 0.0, 0.0)
        EffectManager.playSurroundSound(victim.location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
        EffectManager.playSurroundSound(victim.location, Sound.BLOCK_BEACON_DEACTIVATE, 2.0f, 1.0f)
        victim.location.getNearbyEntities(5.0, 5.0, 5.0).filterIsInstance<ItemDisplay>().forEach {
            if (it.scoreboardTags.contains("core_display")) it.remove()
        }

    }
}