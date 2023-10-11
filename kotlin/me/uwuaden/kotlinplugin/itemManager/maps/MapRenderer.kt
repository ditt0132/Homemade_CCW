package me.uwuaden.kotlinplugin.itemManager.maps


import me.uwuaden.kotlinplugin.gameSystem.WorldManager
import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import org.bukkit.map.MinecraftFont
import java.awt.Color

private fun colorWords(): List<String> {
    return listOf("white", "light_gray", "gray", "black", "brown", "red", "orange", "yellow", "lime", "green", "cyan", "light_blue", "blue", "purple", "magenta", "pink")
}

private fun isColored(material: Material): Color? {
    val materialStr = material.toString().lowercase()
    if (colorWords().any { materialStr.contains(it.lowercase()) }) {
        val colorStr = colorWords().filter { materialStr.contains(it.lowercase()) }[0]
        return when(colorStr) {
            "white" -> Color.WHITE
            "light_gray" -> Color.LIGHT_GRAY
            "gray" -> Color.GRAY
            "black" -> Color.BLACK
            "brown" -> Color(100, 65, 23)
            "red" -> Color.RED
            "orange" -> Color.ORANGE
            "yellow" -> Color.YELLOW
            "lime" -> Color(92, 255, 103)
            "green" -> Color.GREEN
            "cyan" -> Color.CYAN
            "light_blue" -> Color(0, 191, 255)
            "blue" -> Color.BLUE
            "purple" -> Color(139, 0, 255)
            "magenta" -> Color.MAGENTA
            "pink" -> Color.PINK
            else -> Color.BLACK
        }
    }
    return null
}
private fun getBlockColor(material: Material): Color {
    if (isColored(material) != null) return isColored(material)!!
    val materialStr = material.toString().lowercase()



    val m = when (material) {
        Material.AIR -> Color.WHITE // 투명 (흰색)
        Material.GRASS_BLOCK -> Color.GREEN // 초록색
        Material.STONE -> Color.GRAY // 회색
        Material.WATER -> Color.BLUE // 파란색
        Material.LAVA -> Color.RED // 빨간색
        Material.SAND -> Color(208, 151, 100)
        Material.WAXED_CUT_COPPER -> Color(164, 67, 34)
        Material.SMOOTH_STONE -> Color.GRAY
        Material.CHERRY_LEAVES -> Color(242, 115, 242)
        Material.CAMPFIRE -> Color(100, 65, 23)
        Material.DIRT_PATH -> Color(143, 120, 75)
        Material.OXIDIZED_CUT_COPPER -> Color(36, 164, 164)
        Material.GRAVEL -> Color.LIGHT_GRAY
        else -> null
    }
    if (m != null) return m
    if(materialStr.contains("granite")) return Color(175, 89, 62)
    if(materialStr.contains("andesite")) return Color.LIGHT_GRAY
    if(materialStr.contains("nether_brick")) return Color(75, 20, 20)
    if(materialStr.contains("leaves")) return Color(123, 179, 93)
    if(materialStr.contains("stone")) return Color.LIGHT_GRAY
    if(materialStr.contains("quartz")) return Color(233, 223, 224)
    if(materialStr.contains("spruce")) return Color(115, 80, 38)
    if(materialStr.contains("birch")) return Color(248, 223, 161)
    if(materialStr.contains("dark_oak")) return Color(85, 52, 43)
    if(materialStr.contains("brick")) return Color(172, 113, 84)

    return Color.BLACK
}
private fun averageColor(colors: ArrayList<Color>): Color {

    val red = colors.sumOf { it.red }/colors.size
    val green = colors.sumOf { it.green }/colors.size
    val blue = colors.sumOf { it.blue }/colors.size
    return Color(red, green, blue)
}
private fun mixWithBlueAndOpacity(baseColor: Color, opacity: Double): Color {
    val blue = Color.BLUE
    val red = baseColor.red
    val green = baseColor.green
    val blueValue = blue.blue

    val newRed = (red * (1 - opacity) + blue.red * opacity).toInt().coerceIn(0, 255)
    val newGreen = (green * (1 - opacity) + blue.green * opacity).toInt().coerceIn(0, 255)
    val newBlue = (blueValue * (1 - opacity) + blueValue * opacity).toInt().coerceIn(0, 255)

    return Color(newRed, newGreen, newBlue)
}
class MapRenderer : MapRenderer() {
    companion object {
        val blockTypeCach = HashMap<Location, Material>()
    }
    override fun render(mapView: MapView, canvas: MapCanvas, player: Player) {
        if (player.inventory.itemInMainHand.type == Material.FILLED_MAP || player.inventory.itemInOffHand.type == Material.FILLED_MAP) {
            val dataClass = WorldManager.initData(player.world)
            player.sendActionBar(Component.text("${ChatColor.GREEN}킬: ${dataClass.playerKill[player.uniqueId]?: 0}"))
            val centerX = canvas.mapView.centerX
            val centerY = canvas.mapView.centerZ

            // 지도에 그릴 내용을 여기에 추가하면 됩니다.
            // 예시로서 가운데에 빨간색 점을 찍도록 하겠습니다.
            for (x in 0 until 128) {
                for (y in 0 until 128) {
                    canvas.setPixelColor(x, y, Color.white)
                }
            }
            val square = 2
            for (x in 0 until 128) {
                for (z in 0 until 128) {
                    val plX = (player.location.blockX - (x - 64) * square)
                    val plZ = (player.location.blockZ - (z - 64) * square)

                    if (player.world.isChunkLoaded(plX shr 4, plZ shr 4)) {
                        val colors = ArrayList<Color>()
                        val avrX = plX + ((square - plX % square) % square)
                        val avrZ = plZ + ((square - plZ % square) % square)
                        for (x2 in avrX until avrX + square) {
                            for (z2 in avrZ until avrZ + square) {

                                val type = blockTypeCach[Location(player.world, x2.toDouble(), 0.0, z2.toDouble())]

                                if (type != null) {
                                    colors.add(getBlockColor(type))
                                } else {
                                    blockTypeCach[Location(player.world, x2.toDouble(), 0.0, z2.toDouble())] = player.world.getHighestBlockAt(x2, z2).type
                                    colors.add(Color(0, 0, 0))
                                }

                            }
                        }
                        if (WorldManager.isOutsideBorder(
                                Location(
                                    player.world,
                                    plX.toDouble(),
                                    player.location.y,
                                    plZ.toDouble()
                                )
                            )
                        ) {
                            canvas.setPixelColor(x, z, Color(136, 172, 224))
                        } else {
                            canvas.setPixelColor(x, z, averageColor(colors))
                        }
                    } else {
                        canvas.setPixelColor(x, z, Color.BLACK)
                    }
                }
            }
            canvas.drawText(5, 5, MinecraftFont.Font, player.facing.toString()[0].uppercase())

            canvas.setPixelColor(64, 64, Color.WHITE)
            canvas.setPixelColor(64, 65, Color.WHITE)
            canvas.setPixelColor(64, 63, Color.WHITE)
            canvas.setPixelColor(63, 64, Color.WHITE)
            canvas.setPixelColor(65, 64, Color.WHITE)

            canvas.setPixelColor(65, 65, Color.LIGHT_GRAY)
            canvas.setPixelColor(63, 63, Color.LIGHT_GRAY)
            canvas.setPixelColor(65, 63, Color.LIGHT_GRAY)
            canvas.setPixelColor(63, 65, Color.LIGHT_GRAY)

            canvas.setPixelColor(66, 63, Color.BLACK)
            canvas.setPixelColor(66, 64, Color.BLACK)
            canvas.setPixelColor(66, 65, Color.BLACK)

            canvas.setPixelColor(63, 66, Color.BLACK)
            canvas.setPixelColor(64, 66, Color.BLACK)
            canvas.setPixelColor(65, 66, Color.BLACK)

            canvas.setPixelColor(63, 62, Color.BLACK)
            canvas.setPixelColor(64, 62, Color.BLACK)
            canvas.setPixelColor(65, 62, Color.BLACK)

            canvas.setPixelColor(62, 63, Color.BLACK)
            canvas.setPixelColor(62, 64, Color.BLACK)
            canvas.setPixelColor(62, 65, Color.BLACK)
        }
    }
}