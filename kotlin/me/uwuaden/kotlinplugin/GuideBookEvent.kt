package me.uwuaden.kotlinplugin

import me.uwuaden.kotlinplugin.itemManager.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Suppress("UNREACHABLE_CODE")
class GuideBookEvent: Listener {
    @EventHandler
    fun onGuideBookInvClick(e: InventoryClickEvent) {
        val inventory = Bukkit.createInventory(null, 54, "§e§lItem Guide Book")
        val clickedInv = e.clickedInventory ?: return
        val slot = e.slot
        if (e.view.title == "§e§lItem Guide Book") {
            e.isCancelled = true
            //if로 slot 찾아서 이벤트 알아서 추가.
        }

        //2-33번 POWER INIZER 수정
        //해당된 종류칸 다시 클릭시 RETURN

        if (e.isRightClick or e.isLeftClick) {
            if (e.slot == 1) {
                for (i in 4 .. 8 ){
                    inventory.setItem(i, ItemManager.createNamedItem(Material.WHITE_STAINED_GLASS_PANE, 1, "", null))
                }
                inventory.setItem(10, ItemManager.createNamedItem(Material.GREEN_STAINED_GLASS_PANE, 1, "", null))
                inventory.setItem(11, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, "", null))
                inventory.setItem(12, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, "", null))

                //기본드랍 - 확정(설명)
                inventory.setItem(
                    19,
                    ItemManager.createNamedItem(
                        Material.REDSTONE_TORCH,
                        1,
                        "",
                        listOf("${ChatColor.GRAY}다음 아이템들이 필드에 확정적으로 뜹니다(여러개)")
                    )
                )

                inventory.setItem(20, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, "", null))

                inventory.setItem(
                    21,
                    ItemManager.createNamedItem(
                        Material.ARROW,
                        1,
                        "화살",
                        null
                    )
                )

                inventory.setItem(
                    22,
                    ItemManager.createNamedItem(
                        Material.COOKED_BEEF,
                        1,
                        "스테이크",
                        null
                    )
                )

                for (i in 23..27) {
                    inventory.setItem(i, ItemManager.createNamedItem(Material.AIR, 1, "", null))
                }

                //기본드랍 - 확률
                inventory.setItem(
                    28,
                    ItemManager.createNamedItem(
                        Material.REDSTONE_TORCH,
                        1,
                        "",
                        listOf("${ChatColor.GRAY}다음 아이템들이 필드에 확률적으로 뜹니다(중복 가능)")
                    )
                )
                val GuidbookStoneAxe = ItemManager.createNamedItem(
                    Material.STONE_AXE,
                    1,
                    "돌 도끼",
                    listOf("도끼는 유리나 문을 좌클릭으로 부술 수 있습니다")
                )
                GuidbookStoneAxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3)
                inventory.setItem(
                    31, GuidbookStoneAxe
                )

                val GuidbookStonePickAxe = ItemManager.createNamedItem(
                    Material.STONE_PICKAXE,
                    1,
                    "돌 곡괭이",
                    null
                )
                GuidbookStonePickAxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3)
                inventory.setItem(
                    32, GuidbookStonePickAxe
                )

                val GuidbookIronPickaxe = ItemManager.createNamedItem(
                    Material.IRON_AXE,
                    1,
                    "철 도끼",
                    listOf("도끼는 유리나 문을 좌클릭으로 부술 수 있습니다")
                )
                GuidbookIronPickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3)
                inventory.setItem(
                    33, GuidbookIronPickaxe
                )

                val GuidbookIronPickAxe = ItemManager.createNamedItem(
                    Material.IRON_PICKAXE,
                    1,
                    "철 곡괭이",
                    null
                )
                GuidbookIronPickAxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3)
                inventory.setItem(
                    34, GuidbookIronPickAxe
                )

                inventory.setItem(
                    35, ItemManager.createNamedItem(
                        Material.IRON_CHESTPLATE,
                        1,
                        "철 갑옷",
                        listOf(
                            "${ChatColor.GRAY}다음 중 하나 획득 가능 :",
                            "${ChatColor.WHITE}철 모자",
                            "${ChatColor.WHITE}철 흉갑",
                            "${ChatColor.WHITE}철 바지",
                            "${ChatColor.WHITE}철 신발"
                        )
                    )
                )

                inventory.setItem(
                    36,
                    ItemManager.createNamedItem(
                        Material.DIAMOND_CHESTPLATE,
                        1,
                        "다이아몬드 갑옷",
                        listOf(
                            "${ChatColor.GRAY}다음 중 하나 획득 가능 :",
                            "${ChatColor.WHITE}다이아몬드 모자",
                            "${ChatColor.WHITE}다이아몬드 흉갑",
                            "${ChatColor.WHITE}다이아몬드 바지",
                            "${ChatColor.WHITE}다이아몬드 신발"
                        )
                    )
                )

                inventory.setItem(37, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, "", null))
                inventory.setItem(38, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, "", null))

                inventory.setItem(
                    39,
                    ItemManager.createNamedItem(
                        Material.IRON_SWORD,
                        1,
                        "철 검",
                        null
                    )
                )

                inventory.setItem(
                    40,
                    ItemManager.createNamedItem(
                        Material.DIAMOND_SWORD,
                        1,
                        "다이아몬드 검",
                        null
                    )
                )

                val GuidbookBow = ItemManager.createNamedItem(
                    Material.BOW,
                    1,
                    "활",
                    listOf("${ChatColor.GRAY}활은 방패를 해제시킬 수 있습니다!")
                )
                inventory.setItem(
                    41, GuidbookBow
                )

                val GuidbookPowerBow = ItemManager.createNamedItem(
                    Material.IRON_PICKAXE,
                    1,
                    "활",
                    listOf("${ChatColor.GRAY}활은 방패를 해제시킬 수 있습니다!")
                )
                GuidbookPowerBow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
                inventory.setItem(
                    42, GuidbookBow
                )

                inventory.setItem(
                    43,
                    ItemManager.createNamedItem(
                        Material.GOLDEN_APPLE,
                        1,
                        "황금사과",
                        null
                    )
                )

                for (i in 44..45) {
                    inventory.setItem(i, ItemManager.createNamedItem(Material.AIR, 1, "", null))
                }
                for (i in 46 .. 54 ){
                    inventory.setItem(i, ItemManager.createNamedItem(Material.WHITE_STAINED_GLASS_PANE, 1, "", null))
                }
            }
        }

        if (e.isRightClick or e.isLeftClick) {
            if (e.slot == 2) {
                for (i in 4 .. 8 ){
                    inventory.setItem(i, ItemManager.createNamedItem(Material.YELLOW_STAINED_GLASS_PANE, 1, "", null))
                }
                for (i in 10..13) {
                    inventory.setItem(
                        i, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, "", null)
                    )
                    inventory.setItem(
                        11,
                        ItemManager.createNamedItem(Material.GREEN_STAINED_GLASS_PANE, 1, "", null)
                    )
                    inventory.setItem(
                        19, ItemManager.createNamedItem(
                            Material.REDSTONE_TORCH,
                            1,
                            "",
                            listOf("${ChatColor.WHITE}다음 아이템들은 서로 3초의 쿨타임을 공유합니다)")
                        )
                    )

                    inventory.setItem(
                        21,
                        ItemManager.createNamedItem
                            (
                            Material.DARK_OAK_BUTTON,
                            1,
                            "영역 수류탄",
                            listOf(
                                "${ChatColor.YELLOW}중력 수류탄",
                                "§71회용*", "§7우클릭으로 투척할 수 있습니다.",
                                "${ChatColor.GRAY}폭발시 미니 블랙홀을 생성합니다.",
                                "${ChatColor.GRAY}미니 블랙홀은 주변 플레이어를 천천히 당깁니다.",
                                "§7블랙홀이 사라질때 약한 대미지를 줍니다.",
                                "§2대미지: 2.0",
                            )
                        )
                    )

                    inventory.setItem(
                        22,
                        ItemManager.createNamedItem(
                            Material.STONE_BUTTON,
                            1,
                            "${ChatColor.YELLOW}연막탄",
                            listOf(
                                "§71회용*",
                                "${ChatColor.GRAY}우클릭으로 투척시 주변에 연막을 생성합니다.",
                                "${ChatColor.GRAY}연막은 원형 모양으로, 적의 시야를 차단할 수 있습니다.",
                                "",
                                "${ChatColor.GRAY}지속시간 : ?초.",
                                "${ChatColor.GRAY}${ChatColor.BOLD}연막 안에서는 주변 시아가 차단됩니다"
                            )

                        )
                    )
                    inventory.setItem(
                        23,
                        ItemManager.createNamedItem(
                            Material.BIRCH_BUTTON,
                            1,
                            "${ChatColor.YELLOW}화염병",
                            listOf(
                                "§71회용*",
                                "${ChatColor.GRAY}우클릭으로 투척 후 화염병이 깨지면, 주변에 지속적인 화염 대미지를 줍니다.",
                                "§7불에 탈 때 회복불가 상태가 됩니다.",
                                "",
                                "${ChatColor.GRAY}불 장판 지속시간 : 8초.",
                                "${ChatColor.GRAY}화염대미지 지속시간 : 1.0 x 4틱 (팀에게는 2틱)."
                            )
                        )
                    )
                    inventory.setItem(
                        24,
                        ItemManager.createNamedItem(
                            Material.CRIMSON_BUTTON,
                            1,
                            "${ChatColor.YELLOW}중력 수류탄",
                            listOf(
                                "§71회용*",
                                "§7우클릭으로 투척할 수 있습니다.",
                                "${ChatColor.GRAY}폭발시 미니 블랙홀을 생성합니다.",
                                "${ChatColor.GRAY}미니 블랙홀은 주변 플레이어를 천천히 당깁니다.",
                                "§7블랙홀이 사라질때 약한 대미지를 줍니다.",
                                "",
                                "§2대미지: 2.0"
                            )
                        )
                    )
                    inventory.setItem(
                        25,
                        ItemManager.createNamedItem(
                            Material.WARPED_BUTTON,
                            1,
                            "${ChatColor.YELLOW}섬광탄",
                            listOf(
                                "§71회용*",
                                "§7우클릭으로 투척할 수 있습니다.",
                                "${ChatColor.GRAY}터질 경우 시아 내에 섬광탄이 있으면, 시아가 차단됩니다.",
                                "",
                                "§2시아 차단시간: 5초"
                            )
                        )
                    )
                    inventory.setItem(
                        28,
                        ItemManager.createNamedItem(
                            Material.REDSTONE_TORCH,
                            1,
                            "다음 아이템들이 확률적으로 필드에 드랍됩니다!(중복 가능)",
                            null
                        )
                    )
                    val GuidbookValistar = ItemManager.createNamedItem(
                        Material.CROSSBOW,
                        1,
                        "${ChatColor.YELLOW}${ChatColor.BOLD}Vallista",
                        listOf(
                            "${ChatColor.GRAY}관통하는 히트스캔 방식의 화살을 발사합니다!",
                            "${ChatColor.GRAY}관통대미지를 넣습니다.",
                            "",
                            "${ChatColor.GRAY}대미지 : 4.0",
                            "${ChatColor.GRAY}재장전 속도 : 2초 + 빠른장전",
                            "${ChatColor.GRAY}최대로 붙일 수 있는 빠른장전 계수는 '2' 입니다"
                        )
                    )
                    GuidbookValistar.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 1)
                    inventory.setItem(
                        30, GuidbookValistar
                    )
                    inventory.setItem(
                        31,
                        ItemManager.createNamedItem(
                            Material.GOLDEN_SWORD,
                            1,
                            "${ChatColor.YELLOW}${ChatColor.BOLD}Revelation",
                            listOf(
                                "${ChatColor.GRAY}적을 공격시 5초의 쿨타임으로 적에게 관통 대미지를 넣습니다!",
                                "${ChatColor.GRAY}킬이 높을수록 대미지가 감소하며, 킬이 3을 넘어가면 능력을 잃습니다.",
                                "",
                                "${ChatColor.GRAY}킬 비례 데미지 : 0/1/2/3 킬 -> 4.0/3.0/2.0/0",
                            )
                        )
                    )
                    inventory.setItem(
                        32,
                        ItemManager.createNamedItem(
                            Material.GLOW_INK_SAC,
                            1,
                            "${ChatColor.YELLOW}반중력 샷건",
                            listOf(
                                "${ChatColor.GRAY}1회용*",
                                "${ChatColor.GRAY}우클릭시 탄환을 발사하며, 강한 반동과 함께 적과 자신을 밀쳐냅니다.",
                                "${ChatColor.GRAY}탄환에 맞은 적은 스턴이 적용됩니다.",
                                "",
                                "§2대미지: 0.25 x 12",
                                "§2스턴은은 3초동안 점프를 할 수 없으며 구속 3에 걸립니다"
                            )
                        )
                    )
                    inventory.setItem(
                        33,
                        ItemManager.createNamedItem(
                            Material.WOODEN_SHOVEL,
                            1,
                            "§e§lRocket Launcher",
                            listOf(
                            "§7우클릭시 높은 대미지의 로켓을 발사합니다.",
                            "§7로켓은 건물을 파괴시킬 수 있습니다.",
                            " ",
                            "§2대미지: 6.0  §2쿨타임: 30초"
                            )
                        )
                    )
                    //INDEX 34 POTION
                    val item = ItemManager.createNamedItem(
                        Material.POTION,
                        1,
                        "§b§lPOWER INIZER",
                        listOf(
                        "§7마실 수 있는 에너지 드링크입니다!",
                        "§7마시면 30초간 신속, 대미지 증가, 성급함 등의 효과를 얻습니다!",
                        "§8THE NEW ENERGY DRINK",
                        "§8 ",
                        "§8  *NO SUGAR",
                        "§8  *NO ADDICTION",
                        "§8  *NO BALANCE"
                        )
                    )
                    val meta = item.itemMeta as PotionMeta
                    meta.addCustomEffect(PotionEffect(PotionEffectType.SPEED, 20 * 30, 0, false, true), true)
                    meta.addCustomEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 30, 0, false, true), true)
                    meta.addCustomEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 30, 0, false, true), true)
                    meta.color = Color.fromRGB(149, 255, 240)
                    item.itemMeta = meta

                    inventory.setItem(
                        35,
                        ItemManager.createNamedItem(
                            Material.GOLDEN_CARROT,
                            1,
                            "${ChatColor.GOLD}Golden Carrot",
                            listOf(
                            "${ChatColor.GRAY}작아서 휴대하기 편합니다!",
                            " ",
                            "${ChatColor.GRAY}우클릭하면 즉시 hp를 회복합니다.",
                            " ",
                            "${ChatColor.GRAY}회복 효과 : 재생 1 10초 & 흡수 1 5분"
                            )
                        )
                    )
                    inventory.setItem(
                        36,
                        ItemManager.createNamedItem(
                            Material.REDSTONE_TORCH,
                            1,
                            "${ChatColor.RED}Flare Gun",
                            listOf(
                            "${ChatColor.GRAY}하늘에 발사시",
                            "${ChatColor.GRAY}보급품이 떨어집니다!",
                            " ",
                            "${ChatColor.GRAY}보급품에 깔리지 않게 조심하세요!",
                            " ",
                            "${ChatColor.GRAY}보급품안에는 강력한 아이템들이 들어있습니다",
                            "${ChatColor.GRAY}자세한건 보급 드랍 참조",
                            " ",
                            "${ChatColor.GRAY}보급상자 드랍 시간 : 15초 / 보급상자 데미지",
                            "${ChatColor.GRAY}보급탄 데미지 : 6.0 x 화염데미지 1.0 6틱"
                            )
                        )
                    )
                    inventory.setItem(
                        37,
                        ItemManager.createNamedItem(
                            Material.GRAY_STAINED_GLASS_PANE,
                            1,
                            "",
                            null
                        )
                    )
                    inventory.setItem(
                        38,
                        ItemManager.createNamedItem(
                            Material.BLACK_DYE,
                            1,
                            "§b컨버터",
                            listOf(
                            "§7다이아몬드 장비를 대미지를 감소시키는 아이템으로 변환시킵니다.",
                            "§7우클릭하여 컨버터 메뉴를 열 수 있습니다",
                            ""
                            )
                        )
                    )
                    inventory.setItem(
                        39,
                        ItemManager.createNamedItem(
                            Material.ECHO_SHARD,
                            1,
                            "${ChatColor.BLUE}${ChatColor.BOLD}보호의 조각",
                            listOf(
                                "${ChatColor.YELLOW}이 아이템은 컨버터 혹은 플레이어 시체에서만 획득할 수 있습니다",
                                "",
                                "${ChatColor.GRAY}물리 피해 대미지를 조각 하나당 5% 감소시킵니다.",
                                "${ChatColor.GRAY}(최대 5개까지 적용)"
                            )
                        )
                    )
                    inventory.setItem(
                        40,
                        ItemManager.createNamedItem(
                        Material.SHIELD,
                        1,
                        "Shield",
                        listOf("§7왼손에 들고 있을 시, 받는 근접 대미지를 10% 감소 시킵니다.")
                        )
                    )
                    val GuidbookShield = ItemManager.createNamedItem(
                        Material.SHIELD,
                        1,
                        "§b§lEnchanted Shield",
                        listOf(
                            "§7왼손에 들고 있을 시, 받는 근접 대미지를 20% 감소 시킵니다."
                        )
                    )
                    GuidbookValistar.addUnsafeEnchantment(Enchantment.DURABILITY, 3)
                    inventory.setItem(
                        41, GuidbookShield
                    )
                    for (i in 46 .. 54 ){
                        inventory.setItem(i, ItemManager.createNamedItem(Material.YELLOW_STAINED_GLASS_PANE, 1, "", null))
                    }

                }
            }
        }
        if (e.isRightClick or e.isLeftClick) {
            if (e.slot == 3) {
                for (i in 4 .. 8 ){
                    inventory.setItem(i, ItemManager.createNamedItem(Material.RED_STAINED_GLASS_PANE, 1, "", null))
                }
                for (i in 9..10) {
                    inventory.setItem(
                        i,
                        ItemManager.createNamedItem(
                            Material.GRAY_STAINED_GLASS_PANE,
                            1,
                            "",
                            null
                        )
                    )

                }
                inventory.setItem(
                    11,
                    ItemManager.createNamedItem(
                        Material.GREEN_STAINED_GLASS_PANE,
                        1,
                        "",
                        null
                    )

                )
                inventory.setItem(
                    19,
                    ItemManager.createNamedItem(
                        Material.REDSTONE_TORCH,
                        1,
                        "${ChatColor.GRAY}다음 아이템들이 확정적으로 뜹니다",
                        null,
                    )
                )
                inventory.setItem(
                    21,
                    ItemManager.createNamedItem(
                        Material.REDSTONE_TORCH,
                        1,
                        "${ChatColor.RED}Player Tracker",
                        listOf(
                        "${ChatColor.GRAY}160블럭 내에 있는 가장 가까운 플레이어를 추적합니다!",
                        "",
                        "160칸 안에 플레이어가 없을시 작동하지 않고 10?초의 재사용 대기시간이 걸립니다")
                    )
                )
                inventory.setItem(
                    22,
                    ItemManager.createNamedItem(
                        Material.COOKED_BEEF,
                        1,
                        "스테이크",
                        listOf(
                            "여러개가 상자에 포함되어있습니다"
                        )
                    )
                )
                inventory.setItem(
                    23,
                    ItemManager.createNamedItem(
                        Material.GOLDEN_APPLE,
                        1,
                        "황금사과",
                        listOf(
                            "여러개가 상자에 포함되어있습니다"
                        )
                    )
                )
                inventory.setItem(
                    24,
                    ItemManager.createNamedItem(
                        Material.GOLDEN_CARROT,
                        1,
                        "${ChatColor.GOLD}Golden Carrot",
                        listOf(
                            "${ChatColor.GRAY}작아서 휴대하기 편합니다!",
                            " ",
                            "${ChatColor.GRAY}우클릭하면 즉시 hp를 회복합니다.",
                            " ",
                            "${ChatColor.GRAY}회복 효과 : 재생 1 10초 & 흡수 1 5분",
                            "${ChatColor.GRAY}재사용 대기시간 : 1초"
                        )
                    )
                )
                for (i in 25..27) {
                    inventory.setItem(
                        i,
                        ItemManager.createNamedItem(
                            Material.AIR,
                            1,
                            "",
                            null
                        )
                    )
                    inventory.setItem(
                        28,
                        ItemManager.createNamedItem(
                            Material.REDSTONE_TORCH,
                            1,
                            "${ChatColor.GRAY}다음 강력한 아이템 중 두 개가 보급품에 포함되어있습니다!",
                            null
                        )
                    )

                    inventory.setItem(
                        30,
                        ItemManager.createNamedItem(
                            Material.NETHERITE_SHOVEL,
                            1,
                            "${ChatColor.AQUA}${ChatColor.BOLD}Prototype V3",
                            listOf(
                            "${ChatColor.GRAY}매우 강력한 스나이퍼 라이플입니다.",
                            "${ChatColor.GRAY}거리가 멀수록 대미지가 증가합니다!",
                            "",
                            "${ChatColor.GRAY}거리비례 대미지 : 3.0 ~ 10.0",
                            "${ChatColor.GRAY}재사용 대기시간 : 8초",
                            "${ChatColor.GRAY}해당 아이템을 들 때 구속 3 및 점프불가에 걸립니다"

                            )
                        )
                    )
                    inventory.setItem(
                        31,
                        ItemManager.createNamedItem(
                            Material.BOW,
                            1,
                            "${ChatColor.YELLOW}Explosive Bow",
                            listOf(
                            "${ChatColor.GRAY}폭발하는 화살을 발사합니다!",
                            "",
                            "폭발 반경 : ?칸 / 폭발 데미지 : 1.0"
                            )
                        )
                    )
                    inventory.setItem(
                        32,
                        ItemManager.createNamedItem(
                            Material.BOW,
                            1,
                            "${ChatColor.YELLOW}${ChatColor.BOLD}Purify",
                            listOf(
                                "${ChatColor.GRAY}킬이 0이고 공중에 있을 때 능력이 발동됩니다.",
                                "${ChatColor.GRAY}폭발하는 히트스캔 화살을 발사합니다.",
                                "${ChatColor.GRAY}풀차징일때 넉백과 기절을 부여합니다.",
                                "",
                                "${ChatColor.GRAY}폭발 대미지 (최대): 8.0  충격파 대미지 (최대): 4.0",
                                "${ChatColor.GRAY}폭발 반경 : 5칸 / 충격파 반경 : 20칸 (중앙 기준 지름)",
                                "${ChatColor.GRAY}발사 후에 반동으로 느린낙하를 2초간 받습니다",
                                "${ChatColor.GRAY}또한 기절은 다음 효과들을 4초간 받습니다",
                                "${ChatColor.GRAY}실명 1 / 어둠 1 / 나약함 5"
                            )
                        )
                    )
                    for (i in 33..36){
                        inventory.setItem(
                            i,
                            ItemManager.createNamedItem(
                                Material.AIR,
                                1,
                                "",
                                null
                            )
                        )
                        inventory.setItem(
                            37,
                            ItemManager.createNamedItem(
                                Material.ANVIL,
                                1,
                                "다음 아이템들은 모루 1개와 함께 나옵니다",
                                listOf("또한 보급 아이템 두 개 중 하나로 나옵니다")
                            )
                        )
                        val GuidbookProtectionBook = ItemManager.createNamedItem(
                            Material.ENCHANTED_BOOK,
                            1,
                            "보호 1",
                            listOf("최대로 붙일 수 있는 날카로움 계수는 4입니다")
                        )
                        GuidbookProtectionBook.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        inventory.setItem(
                            39, GuidbookProtectionBook
                        )
                        val GuidbookSharpnessBook = ItemManager.createNamedItem(
                            Material.ENCHANTED_BOOK,
                            1,
                            "날카로움 1",
                            listOf("최대로 붙일 수 있는 날카로움 계수는 5입니다")
                        )

                        GuidbookSharpnessBook.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1)
                        inventory.setItem(
                            40, GuidbookSharpnessBook
                        )
                        val GuidbookPowerBook = ItemManager.createNamedItem(
                            Material.ENCHANTED_BOOK,
                            1,
                            "힘 2",
                            listOf("최대로 붙일 수 있는 힘 계수는 3입니다")
                        )
                        GuidbookPowerBook.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
                        inventory.setItem(
                            41, GuidbookPowerBook
                        )

                        val GuidbookPunchBook = ItemManager.createNamedItem(
                            Material.ENCHANTED_BOOK,
                            1,
                            "밀어내기 1",
                            listOf("최대로 붙일 수 있는 밀어내기 계수는 2입니다")
                        )
                        GuidbookPunchBook.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1)
                        inventory.setItem(
                            41, GuidbookPunchBook
                        )
                        for (i in 42..45){
                            inventory.setItem(
                                i,
                                ItemManager.createNamedItem(
                                    Material.AIR,
                                    1,
                                    "",
                                    null
                                )
                            )
                        }
                        for (i in 46 .. 54 ){
                            inventory.setItem(i, ItemManager.createNamedItem(Material.RED_STAINED_GLASS_PANE, 1, "", null))
                        }
                    }
                }
            }
        }
    }
}








