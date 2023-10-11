package me.uwuaden.kotlinplugin.itemManager.maps

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapView

object MapManager {

    fun createMapView(player: Player): ItemStack {
        val map = Bukkit.createMap(player.world)
        val mapItem = ItemStack(Material.FILLED_MAP)
        val meta = mapItem.itemMeta as MapMeta

        map.addRenderer(MapRenderer())
        map.scale = MapView.Scale.FARTHEST

        meta.mapView = map
        meta.displayName(Component.text("${ChatColor.YELLOW}${ChatColor.BOLD}MAP"))
        mapItem.itemMeta = meta


        return mapItem
    }




}