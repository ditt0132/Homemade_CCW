package me.uwuaden.kotlinplugin.skillSystem

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import me.uwuaden.kotlinplugin.Main.Companion.boundingBoxExpand
import me.uwuaden.kotlinplugin.Main.Companion.econ
import me.uwuaden.kotlinplugin.Main.Companion.groundY
import me.uwuaden.kotlinplugin.Main.Companion.lastDamager
import me.uwuaden.kotlinplugin.Main.Companion.lastWeapon
import me.uwuaden.kotlinplugin.Main.Companion.plugin
import me.uwuaden.kotlinplugin.Main.Companion.scheduler
import me.uwuaden.kotlinplugin.assets.CustomItemData
import me.uwuaden.kotlinplugin.assets.EffectManager
import me.uwuaden.kotlinplugin.assets.ItemManipulator
import me.uwuaden.kotlinplugin.assets.ItemManipulator.getName
import me.uwuaden.kotlinplugin.gameSystem.LastWeaponData
import me.uwuaden.kotlinplugin.itemManager.ItemManager
import me.uwuaden.kotlinplugin.itemManager.customItem.CustomItemManager
import me.uwuaden.kotlinplugin.teamSystem.TeamManager
import net.kyori.adventure.text.Component
import org.apache.commons.lang3.Validate
import org.bukkit.*
import org.bukkit.Particle.*
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.util.*


private fun drawLine( /* Would be your orange wool */
                      point1: Location,  /* Your white wool */
                      point2: Location,  /*Space between each particle*/
                      space: Double,
                      r: Int,
                      g: Int,
                      b: Int
) {
    val world = point1.world

    /*Throw an error if the points are in different worlds*/Validate.isTrue(
        point2.world.equals(world),
        "Lines cannot be in different worlds!"
    )

    /*Distance between the two particles*/
    val distance = point1.distance(point2)

    /* The points as vectors */
    val p1 = point1.toVector()
    val p2 = point2.toVector()

    /* Subtract gives you a vector between the points, we multiply by the space*/
    val vector = p2.clone().subtract(p1).normalize().multiply(space)

    /*The distance covered*/
    var covered = 0.0

    /* We run this code while we haven't covered the distance, we increase the point by the space every time*/while (covered < distance) {

        /*Spawn the particle at the point*/point1.world.spawnParticle(Particle.REDSTONE, p1.x, p1.y, p1.z, 1,
            Particle.DustOptions(Color.fromRGB(r, g, b), 1.0F)
        )

        /* We add the space covered */covered += space
        p1.add(vector)
    }
}

private fun isHittable(player: Player, target: LivingEntity): Boolean {
    return !TeamManager.isSameTeam(player.world, player, target) && !(target is Player && target.gameMode == GameMode.SPECTATOR)
}

class SkillEvent: Listener {
    companion object {
        var playerEItem = HashMap<UUID, Int>() //현재 사용 중인거
        var playerEItemList = HashMap<UUID, PlayerSkillHolder>() //보유중인거
        var skillItem = HashMap<Int, ItemStack>() //아이디 -> 아이템
        var playerCapacityPoint = HashMap<UUID, Int>() //킬스택임 ㅇㅇ
        var playerMaxUse = HashMap<UUID, Int>() //아이템 그만뽑아라
        var score = Bukkit.getScoreboardManager().mainScoreboard
    }




    @EventHandler
    fun onClickSkillInv(e: InventoryClickEvent) {
        val clickedInventory: Inventory = e.clickedInventory ?: return
        val holder: InventoryHolder? = clickedInventory.holder
        if (holder !is SkillInventoryHolder) return
        e.isCancelled = true
        if (e.currentItem == null) return

        if (e.currentItem!!.itemMeta?.lore?.contains("${ChatColor.DARK_GRAY}Elite Item") == true) {
            val lores = e.currentItem!!.itemMeta.lore!!
            val player = e.view.player as Player
            val id = lores.filter { it.contains("ID: ") }[0].split(": ")[1].trim().toInt()
            val invSize = e.inventory.size
            val str = ChatColor.stripColor((e.inventory.getItem(invSize-5)?: return).itemMeta.displayName)?: return
            val page = str.split(":")[1].trim().toInt()


            if (!e.isShiftClick) {
                if (lores.contains("${ChatColor.YELLOW}Locked")) {
                    player.sendMessage("${ChatColor.RED}잠겨있습니다.")
                    player.playSound(player, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.8f, 1.0f)
                } else {
                    playerEItem[player.uniqueId] = id
                    player.sendMessage("${ChatColor.GREEN}선택되었습니다.")
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f)

                    player.openInventory(SkillManager.inv(holder, page, player))
                }
            } else {
                //shiftClick
                if (lores.contains("${ChatColor.YELLOW}Locked")) {
                    if (playerEItemList[player.uniqueId] == null) {
                        playerEItemList[player.uniqueId] = PlayerSkillHolder()
                    }
                    val itemHolder = playerEItemList[player.uniqueId]!!

                    if (itemHolder.eliteItems.contains(id)) return

                    if (econ.getBalance(player) >= 5000.0) {
                        econ.withdrawPlayer(player, 5000.0)




                        if (!itemHolder.eliteItems.contains(id)) itemHolder.eliteItems.add(id) //1레벨로 설정
                        playerEItem[player.uniqueId] = id
                        player.sendMessage("${ChatColor.GREEN}구입했습니다.")
                        player.openInventory(SkillManager.inv(holder, page, player))
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.4f)
                    } else {
                        player.sendMessage("${ChatColor.RED}돈이 부족합니다.")
                        player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f)
                    }
                }
            }
        }
    }


    @EventHandler
    fun onUseReusableCube(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND) return
        if (!e.action.isRightClick) return
        val player = e.player
        if (player.inventory.itemInMainHand.itemMeta?.displayName == "${ChatColor.AQUA}${ChatColor.BOLD}반중력 큐브 V2") {
            e.isCancelled = true

            if (player.getCooldown(Material.LIGHT_BLUE_DYE) > 0) return
            player.setCooldown(Material.LIGHT_BLUE_DYE, 20 * 20)
            val loc = player.location
            val exLoc = loc.clone()
            exLoc.add(loc.direction.multiply(2.0))
            EffectManager.playSurroundSound(loc, Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 2.0f)
            exLoc.getNearbyLivingEntities(2.5).forEach {
                val direction = it.location.toVector().subtract(exLoc.toVector()).normalize()
                it.velocity = direction.multiply(1.2)
            }
        }
    }


    @EventHandler
    fun onUseIllusionize(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND) return
        if (!e.action.isRightClick) return
        val player = e.player
        if (player.inventory.itemInMainHand.itemMeta?.displayName == "${ChatColor.RED}${ChatColor.BOLD}ILLUSIONIZE") {
            e.isCancelled = true

            if (player.getCooldown(Material.RED_DYE) > 0) return
            player.setCooldown(Material.RED_DYE, 20 * 30)
            var loc = player.getTargetBlockExact(100)?.location
            if (loc == null) {
                player.setCooldown(Material.RED_DYE, 20 * 3)
                player.sendMessage("${ChatColor.RED}너무 멉니다.")
                return
            }

            val players1 = loc.getNearbyPlayers(10.0).filter { isHittable(player, it) }.filter { it != player }

            var targetP: Player? = null
            if (players1.isNotEmpty()) {
                targetP = players1.random()
                loc = targetP.location
            }
            val random = Random()

            val reflectLoc = mutableListOf<Location>()
            val mirrors = mutableListOf<Entity>()


            for (i in 0 until 4) {
                val tempLoc = player.location.clone().add(random.nextInt(-20, 20).toDouble(), random.nextInt(10, 20).toDouble(), random.nextInt(-20, 20).toDouble())
                tempLoc.yaw = random.nextFloat(0.0F, 360.0F)
                tempLoc.pitch = random.nextFloat(-90.0F, 90.0F)
                reflectLoc.add(tempLoc)
            }

            reflectLoc.forEach {
                val itemDisplay = it.world.spawnEntity(it, EntityType.ITEM_DISPLAY) as ItemDisplay
                val display = itemDisplay.transformation
                itemDisplay.itemStack = ItemStack(Material.TINTED_GLASS)

                display.scale.set(4.7, 6.6, 0.5)
                itemDisplay.transformation = display
                mirrors.add(itemDisplay)
            }

            scheduler.scheduleSyncDelayedTask(plugin, {
                for (i in 0 until 4) {
                    scheduler.scheduleSyncDelayedTask(plugin, {
                        if (i == 3) {
                            drawLine(mirrors[i].location, loc, 0.2, 255, 0, 0)
                            loc.world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 2, 0.1, 0.1, 0.1, 0.0)
                            EffectManager.playSurroundSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 2.0F)
                            val players2 = loc.getNearbyPlayers(10.0).filter { isHittable(player, it) }.filter { it != player }
                            players2.forEach {
                                it.damage(4.0)
                            }
                        } else {
                            drawLine(mirrors[i].location, mirrors[i + 1].location, 0.2, 255, 0, 0)
                            for (s in 0 until 3) {
                                EffectManager.playSurroundSound(loc, Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0F, 2.0F)
                            }
                        }
                    }, 3 * i.toLong())
                }
                scheduler.scheduleSyncDelayedTask(plugin, {
                    if (targetP != null) {
                        val originLoc = player.location.clone()
                        originLoc.yaw = random.nextFloat(0.0F, 360.0F)
                        originLoc.pitch = random.nextFloat(-90.0F, 90.0F)

                        loc.yaw = player.yaw
                        loc.pitch = player.pitch

                        player.teleport(loc)
                        targetP.teleport(originLoc)
                        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 10, 0,false, false))
                        player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 2.0F)

                        targetP.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20*3, 0, false, false))
                        targetP.addPotionEffect(PotionEffect(PotionEffectType.DARKNESS, 20*3, 0, false, false))
                        targetP.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 20*3, 4, false, false))
                        targetP.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20*3, 4, false, false))
                        targetP.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 20*3, 4, false, false))
                    }
                }, 9)
            }, 40)

            scheduler.scheduleSyncDelayedTask(plugin, {
                mirrors.forEach {
                    loc.world.spawnParticle(Particle.EXPLOSION_HUGE, it.location, 1, 0.1, 0.1, 0.1, 0.0)
                    EffectManager.playSurroundSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 2.0F)
                    EffectManager.playSurroundSound(loc, Sound.BLOCK_GLASS_BREAK, 1.0F, 1.0F)
                    it.remove()
                }
            }, 20*5)
        }
    }
    @EventHandler
    fun onUsePrototypeEXI(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND) return
        if (!e.action.isRightClick) return
        val player = e.player
        if (player.inventory.itemInMainHand.itemMeta?.displayName == "${ChatColor.AQUA}${ChatColor.BOLD}Prototype E-XI") {
            if (player.getCooldown(Material.NETHERITE_HOE) > 0) return
            player.setCooldown(Material.NETHERITE_HOE, 4)
            if (player.getCooldown(Material.NETHERITE_HOE) <= 0) {
                player.setCooldown(Material.SHIELD, 20)
                return
            }
            player.setCooldown(Material.SHIELD, 20)
            val loc = player.eyeLocation
            val dir = loc.direction
            val entities = ArrayList<LivingEntity>()
            val item = player.inventory.itemInMainHand

            val volume: Float
            val charge1 = SkillManager.getChargeValue(item)
            if (charge1 in 0..50) volume = 0.5f
            else if (charge1 in 51..100) volume = 0.8f
            else if (charge1 in 101..150) volume = 1.1f
            else if (charge1 in 151..190) volume = 1.4f
            else if (charge1 in 191..200) volume = 2.0f
            else volume = 0.5f
            EffectManager.playSurroundSound(player.location, Sound.BLOCK_BEACON_AMBIENT, volume, 2.0f)
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 30, 3, false, false))

            player.sendActionBar("${ChatColor.DARK_AQUA}Charge: ${ChatColor.WHITE}[${ChatColor.DARK_AQUA}${SkillManager.createPercentageBar(SkillManager.getChargeValue(item).toDouble()/2.0, 10)}${ChatColor.WHITE}]${ChatColor.DARK_AQUA}")

            shooting@for (i in 0 until 100*15) {
                val pos = loc.clone().add(dir.clone().multiply(i / 100.0))
                if(!pos.isChunkLoaded) break@shooting

                if(pos.block.isSolid) {
                    if (charge1 in 191..210) {
                        if (pos.block.y > groundY) {
                            if (pos.block.type != Material.AIR) {
                                loc.world.spawnParticle(Particle.BLOCK_CRACK, loc, 5, 0.5, 0.5, 0.5, 0.0, loc.block.blockData)
                                pos.block.type = Material.AIR
                                EffectManager.playSurroundSound(pos, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.0F, 1.0F)
                            }
                        }
                    }
                    break@shooting
                }


                if (i%10 == 0 && i > 100*2) {
                    val dustOptions: DustOptions
                    val charge = SkillManager.getChargeValue(item)
                    if (charge in 0..50) dustOptions = DustOptions(Color.AQUA, 0.1f+SkillManager.getChargeValue(item).toFloat()/80.0f)
                    else if (charge in 51..100) dustOptions = DustOptions(Color.BLUE, 0.1f+SkillManager.getChargeValue(item).toFloat()/80.0f)
                    else if (charge in 101..150) dustOptions = DustOptions(Color.NAVY, 0.1f+SkillManager.getChargeValue(item).toFloat()/80.0f)
                    else if (charge in 151..210) dustOptions = DustOptions(Color.PURPLE, 0.1f+SkillManager.getChargeValue(item).toFloat()/80.0f)
                    else dustOptions = DustOptions(Color.AQUA, 0.1f+SkillManager.getChargeValue(item).toFloat()/80.0f)
                    pos.world.spawnParticle(Particle.REDSTONE, pos, 1, 0.0, 0.0, 0.0, 10.0, dustOptions)

                }
                pos.getNearbyLivingEntities(10.0, 10.0, 10.0).forEach {
                    if(it != player && it !is ArmorStand && it is LivingEntity && it.boundingBox.clone().expand(boundingBoxExpand).contains(pos.x, pos.y, pos.z) && isHittable(player, it)) {
                        entities.add(it)
                    }
                }

            }

            if (entities.isNotEmpty()) {
                val before2 = SkillManager.getChargeValue(item)
                if (before2 + 5 < 200) {
                    SkillManager.changeChargeValue(item, before2 + 5)
                } else {
                    SkillManager.changeChargeValue(item, 200)
                }
            }

            entities.forEach {
                if (it is Player && isHittable(player, it)) {
                    lastDamager[it] = player
                    lastWeapon[it] = LastWeaponData(ItemManager.createNamedItem(Material.NETHERITE_HOE, 1, "${ChatColor.AQUA}${ChatColor.BOLD}Prototype E-XI", null), System.currentTimeMillis()+1000*10)
                }
                it.damage(SkillManager.getChargeValue(item).toDouble()/88.888 + 0.25)
            }

        }
    }

//    @EventHandler
//    fun onSavePrototypeEXI(e: PlayerSwapHandItemsEvent) {
//        val player = e.player
//        val item = player.inventory.itemInMainHand
//        if (player.inventory.itemInMainHand.itemMeta?.displayName == "${ChatColor.AQUA}${ChatColor.BOLD}Prototype E-XI") {
//            e.isCancelled = true
//            val charge = SkillManager.getChargeValue(item)
//            val saved = SkillManager.getSaveValue(item)
//            if (charge*0.7 > saved) {
//                SkillManager.changeSaveValue(item, (charge*0.7).roundToInt())
//                player.setCooldown(Material.NETHERITE_HOE, 20*10)//소리 추가
//                EffectManager.playSurroundSound(player.location, Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 2.0f)
//            }
//        }
//    }

    @EventHandler
    fun onSlotChange(e: PlayerItemHeldEvent) {
        val player = e.player
        val item = player.inventory.getItem(e.newSlot)?: return
        if (item.itemMeta.displayName == "${ChatColor.AQUA}${ChatColor.BOLD}Prototype E-XI") {
            SkillManager.changeChargeValue(item, SkillManager.getSaveValue(item))
        }
    }
    @EventHandler
    fun onJump(e: PlayerJumpEvent) {
        val player = e.player
        val item = player.inventory.itemInMainHand
        if (player.inventory.itemInMainHand.itemMeta?.displayName == "${ChatColor.AQUA}${ChatColor.BOLD}Prototype E-XI") {
            player.setCooldown(Material.NETHERITE_HOE, 10)
        }
    }

    @EventHandler
    fun onUseTeleportPack(e: PlayerToggleSneakEvent) {
        val player = e.player
        if (player.inventory.leggings?.itemMeta?.displayName == "${ChatColor.AQUA}${ChatColor.BOLD}Teleport Leggings") {
            if (player.gameMode == GameMode.SPECTATOR) return
            if (!e.isSneaking) return
            if (player.getCooldown(Material.LEATHER_LEGGINGS) > 0) return
            player.setCooldown(Material.LEATHER_LEGGINGS, 5)

            val playerOriginLoc = player.location.clone()
            val playerLoc = player.location.clone()
            val random = Random()
            playerLoc.pitch = 0.0f
            val playerTargetLoc = playerLoc.clone().add(playerLoc.clone().direction.multiply(random.nextInt(7, 11).toDouble())).add(0.0, 0.5, 0.0)

            for (i in 0 until 100) {
                player.world.spawnParticle(REDSTONE, player.eyeLocation, 1, 0.5, 1.0, 0.5, DustOptions(Color.fromRGB(random.nextInt(0, 256), random.nextInt(0, 256), random.nextInt(0, 256)), 0.8f))
            }
            EffectManager.playSurroundSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
            EffectManager.playSurroundSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 2.0f)
            EffectManager.playSurroundSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.5f, 2.0f)

            playerTargetLoc.yaw = playerOriginLoc.yaw
            playerTargetLoc.pitch = playerOriginLoc.pitch
            player.teleport(playerTargetLoc)
            player.fallDistance = 0.0f

            EffectManager.playSurroundSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
            EffectManager.playSurroundSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 2.0f)
            EffectManager.playSurroundSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.5f, 2.0f)

            for (i in 0 until 100) {
                player.world.spawnParticle(REDSTONE, player.eyeLocation, 1, 0.5, 1.0, 0.5, DustOptions(Color.fromRGB(random.nextInt(0, 256), random.nextInt(0, 256), random.nextInt(0, 256)), 0.8f))
            }


            val block1 = playerTargetLoc.block
            val block2 = playerTargetLoc.clone().add(0.0, 1.0, 0.0).block
            if (block1.isSolid || block2.isSolid) {
                lastDamager[player] = player
                lastWeapon[player] = LastWeaponData(ItemManager.createNamedItem(Material.LEATHER_LEGGINGS, 1, "${ChatColor.AQUA}${ChatColor.BOLD}Teleport Leggings", null), System.currentTimeMillis()+1000*10)
                player.damage(40.0)

            }
            var dmg = false
            playerTargetLoc.getNearbyLivingEntities(5.0).forEach {
                if (it != player) {
                    if (isHittable(player, it)) {
                        val loc = playerTargetLoc.clone().add(0.0, 1.0, 0.0)
                        if (it.boundingBox.expand(1.5).contains(loc.x, loc.y, loc.z)) {
                            if (it is Player) {
                                lastDamager[it] = player
                                lastWeapon[it] = LastWeaponData(ItemManager.createNamedItem(Material.LEATHER_LEGGINGS, 1, "${ChatColor.AQUA}${ChatColor.BOLD}Teleport Leggings", null), System.currentTimeMillis()+1000*10)
                            }
                            it.damage(5.0)
                            dmg = true
                        }
                    }
                }
            }
            if (dmg) {
                player.damage(5.0)
            }
        }
    }
    @EventHandler
    fun swordOfHealingDamage(e: EntityDamageByEntityEvent) {
        val attacker = e.damager
        val victim = e.entity

        if (attacker is Player && victim is LivingEntity && CustomItemManager.isHittable(attacker, victim)) {
            if (attacker.inventory.itemInMainHand.itemMeta?.displayName == CustomItemData.getSwordOfHealing().getName()) {
                if (attacker.getCooldown(Material.IRON_SWORD) > 0) {
                    attacker.sendMessage(Component.text("${ChatColor.RED} 쿨타임 중 입니다. (${attacker.getCooldown(Material.IRON_SWORD).toDouble() / 20.0}초)"))
                    return
                }
                attacker.setCooldown(Material.IRON_SWORD, 20 * 8)

                val players = attacker.location.getNearbyPlayers(8.0).filter { TeamManager.isSameTeam(attacker.world, attacker, it) }.toMutableSet()
                players.add(attacker)

                players.forEach {
                    it.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 33, 2,false, false))
                }
                EffectManager.drawParticleCircle(attacker.location, 8.0, Color.fromRGB(255, 241, 54))

                EffectManager.playSurroundSound(
                    victim.location,
                    Sound.ENTITY_PLAYER_LEVELUP,
                    1.0F,
                    2.0f
                )
            }
        }
    }

    @EventHandler
    fun onUseShotgun(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND) return
        if (!e.action.isRightClick) return
        val player = e.player
        if (player.inventory.itemInMainHand.itemMeta?.displayName == CustomItemData.getShotGun().getName()) {
            e.isCancelled = true
            if (player.getCooldown(Material.IRON_HOE) > 0) return
            player.setCooldown(Material.IRON_HOE, 30)
            val random = java.util.Random()

            val loc = player.location
            val entities = ArrayList<LivingEntity>()

            val exLoc = player.location.clone()
            exLoc.add(loc.direction.multiply(2.0))
//            EffectManager.playSurroundSound(exLoc, Sound.ENTITY_GHAST_SHOOT, 1.0f, 0.5f)
//            EffectManager.playSurroundSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f)
//            바꿀 사운드
            EffectManager.playSurroundSound(player.location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.5f, 1.1f)
            EffectManager.playSurroundSound(player.location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.5f, 2.0f)

//            추가로 찰칵 사운드 두 개
            scheduler.scheduleSyncDelayedTask(plugin, {
                EffectManager.playSurroundSound(player.location, Sound.BLOCK_PISTON_CONTRACT, 0.5f, 0.7f)
            }, 8)
//            지연 0.2초 (20 * 2) - 레드스톤 중계기 두번
            scheduler.scheduleSyncDelayedTask(plugin, {
                EffectManager.playSurroundSound(player.location, Sound.BLOCK_PISTON_EXTEND, 0.5f, 0.8f)
            }, 12)
            val pDirection = player.location.toVector().subtract(exLoc.toVector()).normalize()
            player.velocity = pDirection.multiply(0.3)


            for (n in 0 until 12) {
                val loc2 = player.eyeLocation.clone()
                val ent = mutableSetOf<LivingEntity>()
                loc2.yaw = loc.yaw + random.nextFloat(-15.0F, 15.0F) //15
                loc2.pitch = loc.pitch + random.nextFloat(-7.5F, 7.5F)
                sh@ for (i in 0 until 10 * 320) {
                    loc2.add(loc2.direction.multiply(0.1))
                    if (i > 10 && i%10 == 0) loc2.world.spawnParticle(
                        SMOKE_NORMAL,
                        loc2,
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                    )
                    if (loc2.block.isSolid) {
                        break@sh
                    }
                    for (it in loc2.getNearbyLivingEntities(10.0, 10.0, 10.0).filterNot { it == player }) {
                        if (it.boundingBox.clone().expand(boundingBoxExpand).contains(loc2.x, loc2.y, loc2.z) && isHittable(
                                player,
                                it
                            )
                        ) {
                            ent.add(it)
                            break@sh
                        }
                    }
                }
                ent.forEach {
                    entities.add(it)
                }
            }
            if (entities.isNotEmpty()) player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 1.0f)

            val dmgEntities = mutableSetOf<LivingEntity>()

            entities.forEach {
                dmgEntities.add(it)
            }
            dmgEntities.forEach { entity ->
                entity.damage(entities.filter { it == entity }.size * 0.3)
                if (entity is Player) {
                    lastDamager[entity] = player
                    lastWeapon[entity] = LastWeaponData(
                        CustomItemData.getShotGun(), System.currentTimeMillis() + 1000 * 10
                    )
                }
                entity.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 10, 0, false, false))
            }
        }
    }
    @EventHandler
    fun onUseQuickRocketLauncher(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND) return
        if (!e.action.isRightClick) return
        val player = e.player
        if (player.inventory.itemInMainHand.itemMeta?.displayName == ItemManipulator.itemName(CustomItemData.getQuickRocketLauncher())) {
            e.isCancelled = true

            if (player.getCooldown(Material.STONE_SHOVEL) > 0) return
            player.setCooldown(Material.STONE_SHOVEL, 20 * 4)

            val loc = player.eyeLocation
            var explode = false
            val exLoc = player.location.clone()
            exLoc.add(loc.direction.multiply(2.0))
            EffectManager.playSurroundSound(exLoc, Sound.ENTITY_GHAST_SHOOT, 1.0f, 0.5f)
            EffectManager.playSurroundSound(exLoc, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0F, 1.2F)

            val kb = 0.4

            val direction = player.location.toVector().subtract(exLoc.toVector()).normalize()
            player.velocity = direction.multiply(kb)


            scheduler.runTaskAsynchronously(plugin, Runnable {
                sh@for (i in 0 until 10*320) {
                    scheduler.scheduleSyncDelayedTask(plugin, {
                        if (i > 10) loc.world.spawnParticle(REDSTONE, loc, 1, 0.1, 0.1, 0.1, 0.0, DustOptions(Color.GRAY, 1.5F))
                        if (loc.block.isSolid) {
                            explode = true
                        }
                        for (it in loc.getNearbyLivingEntities(10.0, 10.0, 10.0).filterNot { it == player }.filter {
                            CustomItemManager.isHittable(
                                player,
                                it
                            )
                        }) {
                            if (it.boundingBox.clone().expand(boundingBoxExpand).contains(loc.x, loc.y, loc.z)) {
                                explode = true
                            }
                        }
                        loc.add(loc.direction.multiply(0.1))
                    }, 0)
                    if (explode) {
                        scheduler.scheduleSyncDelayedTask(plugin, {
                            EffectManager.playSurroundSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 2.0F)
                            loc.world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 1, 0.0, 0.0, 0.0, 0.0)
                            loc.getNearbyLivingEntities(3.0).filter {
                                CustomItemManager.isHittable(
                                    player,
                                    it
                                )
                            }.forEach {
                                EffectManager.setLastDamager(player, it, CustomItemData.getQuickRocketLauncher())
                                it.damage(4.0)

                                val directionVel = it.location.toVector().subtract(loc.toVector()).normalize().setY(0.5)
                                it.velocity = directionVel.multiply(kb)

                            }
                            val blocks = mutableSetOf<Block>()

                            val r = 3
                            loc.y += 1.0
                            while (loc.y < 320) {

                                EffectManager.getBlocksInCircle(loc, r).forEach {
                                    blocks.add(it)
                                }
                                loc.y += 1.0
                            }
                            blocks.filter { it.type != Material.AIR }

                            blocks.forEach {
                                EffectManager.breakBlock(it.location)
                            }
                        }, 0)
                        break@sh
                    }
                    Thread.sleep(2)
                }
            })
        }
    }
    @EventHandler
    fun onUseBookOfSalvation(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND) return
        if (!e.action.isRightClick) return
        val player = e.player
        if (player.inventory.itemInMainHand.itemMeta?.displayName == CustomItemData.getBookOfSalvation().getName()) {
            e.isCancelled = true
            if (player.getCooldown(Material.BOOK) > 0) return

            val loc = player.eyeLocation.clone()
            val locDir = player.location.clone()

            sh@ for (i in 0 until 10 * 100) {
                loc.add(loc.direction.multiply(0.1))
                val players = loc.getNearbyPlayers(10.0, 10.0, 10.0).filterNot { it == player }.filter {
                    it.boundingBox.clone().expand(1.5)
                        .contains(loc.x, loc.y, loc.z) && TeamManager.isSameTeam(e.player.world, player, it) && it.gameMode == GameMode.SURVIVAL
                }
                if (players.isNotEmpty()) {
                    val target = players.sortedBy { it.location.distance(player.location) }[0]
                    EffectManager.playSurroundSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
                    val targetLoc = target.location.clone()
                    targetLoc.yaw = locDir.yaw
                    targetLoc.pitch = locDir.pitch
                    player.teleport(targetLoc)
                    EffectManager.playSurroundSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
                    EffectManager.playSurroundSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.1f)
                    for (y in 0 until 320) {
                        val pLoc = target.location.clone()
                        pLoc.y += y.toDouble()/2.0
                        player.world.spawnParticle(REDSTONE, pLoc, 3, 0.0, 0.0, 0.0, DustOptions(Color.BLUE, 1.0f))
                    }
                    player.addPotionEffect(PotionEffect(PotionEffectType.HEAL, 1, 1, false, false))
                    player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 4, false, false))
                    player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20*3, 1, false, false))
                    player.setCooldown(Material.BOOK, 20 * 60)
                    break@sh
                }
            }
            player.sendMessage("§c타겟이 없습니다.")
        }
    }
    @EventHandler
    fun onUseGravitization(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND) return
        if (!e.action.isRightClick) return
        val player = e.player
        if (player.inventory.itemInMainHand.itemMeta?.displayName == CustomItemData.getGravitization().getName()) {
            e.isCancelled = true

            if (player.getCooldown(Material.RED_DYE) > 0) return
            player.setCooldown(Material.RED_DYE, 20 * 30)
            var loc = player.getTargetBlockExact(100)?.location
            if (loc == null) {
                player.setCooldown(Material.RED_DYE, 20 * 3)
                player.sendMessage("${ChatColor.RED}너무 멉니다.")
                return
            }
            val random = Random()
            scheduler.runTaskAsynchronously(plugin, Runnable {
                for (i in 0 until 10*11) {
                    if (i >= 10) {
                        val entities = mutableSetOf<Entity>()
                        scheduler.scheduleSyncDelayedTask(plugin, {
                            EffectManager.playSurroundSound(loc, Sound.BLOCK_BEACON_AMBIENT, 1.0f, 2.0f)
                            for (y in 0 until 50) {
                                val loc2 = loc.clone().add(0.0, y.toDouble(), 0.0)
                                val r = 4.0
                                loc2.getNearbyEntities(r, r, r).filter { it.location.distance(loc2) <= r }.filter { it is LivingEntity || it is Projectile }.filter { !entities.contains(it) }.forEach {
                                    if (!(it is LivingEntity && !CustomItemManager.isHittable(player, it))) entities.add(it)
                                }
                            }

                            entities.forEach {
                                if (it is LivingEntity) {
                                    if (i % 10 == 0) {
                                        it.damage(1.0)
                                    }
                                    it.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20, 2, false, false))
                                    if (it is ArmorStand) {
                                        it.velocity = Vector(0.0, -0.6, 0.0)
                                    }
                                } else {
                                    it.velocity = Vector(0.0, -0.6, 0.0)
                                }
                            }


                            val particleLoc = loc.clone().add(random.nextDouble(-4.0, 4.0), random.nextDouble(2.0, 15.0), random.nextDouble(-4.0, 4.0))
                            scheduler.runTaskAsynchronously(plugin, Runnable {
                                for (y in 0..20) {
                                    scheduler.scheduleSyncDelayedTask(plugin, {
                                        particleLoc.add(0.0, -0.15, 0.0)
                                        particleLoc.world.spawnParticle(REDSTONE, particleLoc, 2, DustOptions(Color.RED, 0.5f))
                                    }, 0)
                                    Thread.sleep(1000/10)
                                }
                            })

                        }, 0)
                    }

                    if (i%4 == 0) {
                        val particleLoc = loc.clone().add(0.0, 1.1, 0.0)
                        EffectManager.drawImageXZ(particleLoc.clone().add(0.0, 0.0, 0.7), "https://i.ibb.co/DgRzRf0/illu.png", 80, 80, 10.0)
                        EffectManager.drawParticleCircle(particleLoc, 4.0, Color.RED)
                    }
                    Thread.sleep(1000/10)
                }
            })
        }
    }
}