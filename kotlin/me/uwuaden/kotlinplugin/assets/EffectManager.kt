package me.uwuaden.kotlinplugin.assets

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import me.uwuaden.kotlinplugin.Main
import me.uwuaden.kotlinplugin.Main.Companion.lastDamager
import me.uwuaden.kotlinplugin.Main.Companion.lastWeapon
import me.uwuaden.kotlinplugin.gameSystem.LastWeaponData
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.util.*
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

//EffectManager.playSurroundSound
object EffectManager {
    fun playSurroundSound(loc: Location, sound: Sound, volume: Float, pitch: Float) {
        loc.getNearbyPlayers(150.0).forEach { player ->
            val dist = loc.distance(player.location)
            if (dist <= 5) {
                player.playSound(loc, sound, volume, pitch)
            } else {
                //(log(dist + 30.0, 1.2) - 19.5 + volume).toFloat()
                val calculatedVol =  (dist/15).toFloat() + volume - 1.0f
                player.playSound(loc, sound, calculatedVol, pitch)
            }
        }
    }
    fun randomTip(): String {
        return listOf("마법부여가 된 방패는 근접피해를 추가로 줄여줍니다!", "필드에서 드롭되는 플레어건을 통해 더 다양한 아이템을 얻을 수 있습니다.",
            "활도 적의 방패를 해제시킬 수 있습니다.", "발리스타는 정밀한 원거리 사격이 가능합니다.", "엘리트 아이템으로 전투 스타일을 바꿔보세요!",
            "발리스타보다 활이 더 강력합니다.", "영역 수류탄은 건물을 붕괴시킵니다.", "때로는 킬을 안하는 것도 전략입니다!",
            "빠르게 킬을 하는 전략을 사용해보세요!", "싸우는 중인 적을 기습하세요!", "킬이 적을때 더 강력한 무기들이 있습니다.", "Shift + F (양손 들기 키)로 퀵슬롯을 열 수 있습니다!",
            "보급에 깔리면 아야해요.").random()
    }
    fun getSkull(url: String): ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD)
        if (url.isEmpty()) {
            return head
        }
        val headMeta = head.itemMeta as SkullMeta
        val profile = GameProfile(UUID.randomUUID(), null)
        profile.properties.put("textures", Property("textures", url))
        val profileField: Field
        try {
            profileField = headMeta.javaClass.getDeclaredField("profile")
            profileField.isAccessible = true
            profileField.set(headMeta, profile)
        } catch (ignored: NoSuchFieldException) {
        } catch (ignored: IllegalArgumentException) {
        } catch (ignored: IllegalAccessException) {
        }
        head.itemMeta = headMeta
        return head
    }
    fun breakBlock(blockLoc: Location) {
        if (blockLoc.world.isChunkLoaded(blockLoc.x.roundToInt() shr 4, blockLoc.z.roundToInt() shr 4) && blockLoc.y.roundToInt() > Main.groundY) {
            if (blockLoc.block.type != Material.AIR && blockLoc.block.type != Material.BARRIER) {
                blockLoc.world.spawnParticle(Particle.BLOCK_CRACK, blockLoc, 5, 0.5, 0.5, 0.5, 0.0, blockLoc.block.blockData)
                blockLoc.block.type = Material.AIR
            }
        }
    }
    fun drawParticleCircle(center: Location, radius: Double, Color: Color) {
        val world = center.world
        val centerX = center.x
        val centerY = center.y
        val centerZ = center.z

        // 원의 점 개수를 설정 (점 개수가 늘어날수록 원이 부드러워집니다)
        val points = 50

        for (i in 0 until points) {
            val angle = 2.0 * Math.PI * i / points
            val x = centerX + radius * cos(angle)
            val z = centerZ + radius * sin(angle)
            val particleLocation = Location(world, x, centerY, z)
            world.spawnParticle(Particle.REDSTONE, particleLocation, 3, 0.0, 0.0, 0.0, 0.0,
                Particle.DustOptions(Color, 1.0f)
            )
        }
    }
    fun getBlocksInCircle(center: Location, radius: Int): List<Block> {
        val blocksInCircle = mutableListOf<Block>()

        val centerX = center.blockX
        val centerY = center.blockY
        val centerZ = center.blockZ

        for (x in centerX - radius..centerX + radius) {
            for (z in centerZ - radius..centerZ + radius) {
                val distanceSquared = (centerX - x) * (centerX - x) + (centerZ - z) * (centerZ - z)
                if (distanceSquared <= radius * radius) {
                    val y = centerY // 원 형태이므로 y 좌표는 고정값으로 사용
                    blocksInCircle.add(Location(center.world, x.toDouble(), y.toDouble(), z.toDouble()).block)
                }
            }
        }

        return blocksInCircle
    }

    fun setLastDamager(attacker: LivingEntity, victim: LivingEntity, itemStack: ItemStack? = null, second: Int = 30) {
        if (attacker is Player && victim is Player) {
            if (itemStack == null) {
                if (lastDamager[victim] != attacker) {
                    lastWeapon.remove(victim)
                }
            } else {
                lastWeapon[victim] = LastWeaponData(itemStack, System.currentTimeMillis() + 1000 * second
                )
            }
            lastDamager[victim] = attacker
        }
    }
}