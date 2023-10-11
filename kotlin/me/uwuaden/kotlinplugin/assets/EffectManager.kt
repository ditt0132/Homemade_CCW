package me.uwuaden.kotlinplugin.assets

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.util.*
import kotlin.math.log
//EffectManager.playSurroundSound
object EffectManager {
    fun playSurroundSound(loc: Location, sound: Sound, volume: Float, pitch: Float) {
        loc.getNearbyPlayers(150.0).forEach { player ->
            val dist = loc.distance(player.location)
            if (dist <= 5) {
                player.playSound(loc, sound, volume, pitch)
            } else {
                val calculatedVol = volume * (log(dist + 30.0, 1.2) - 18.5).toFloat()
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
}