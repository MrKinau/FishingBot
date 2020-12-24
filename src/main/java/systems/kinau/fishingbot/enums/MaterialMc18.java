/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.enums;

/**
 * An enum of all material IDs accepted by the official server and client
 */
public enum MaterialMc18 {
    AIR(0, 0),
    STONE(1),
    GRASS(2),
    DIRT(3),
    COBBLESTONE(4),
    WOOD(5),
    SAPLING(6),
    BEDROCK(7),
    WATER(8),
    STATIONARY_WATER(9),
    LAVA(10),
    STATIONARY_LAVA(11),
    SAND(12),
    GRAVEL(13),
    GOLD_ORE(14),
    IRON_ORE(15),
    COAL_ORE(16),
    LOG(17),
    LEAVES(18),
    SPONGE(19),
    GLASS(20),
    LAPIS_ORE(21),
    LAPIS_BLOCK(22),
    DISPENSER(23),
    SANDSTONE(24),
    NOTE_BLOCK(25),
    BED_BLOCK(26),
    POWERED_RAIL(27),
    DETECTOR_RAIL(28),
    PISTON_STICKY_BASE(29),
    WEB(30),
    LONG_GRASS(31),
    DEAD_BUSH(32),
    PISTON_BASE(33),
    PISTON_EXTENSION(34),
    WOOL(35),
    PISTON_MOVING_PIECE(36),
    YELLOW_FLOWER(37),
    RED_ROSE(38),
    BROWN_MUSHROOM(39),
    RED_MUSHROOM(40),
    GOLD_BLOCK(41),
    IRON_BLOCK(42),
    DOUBLE_STEP(43),
    STEP(44),
    BRICK(45),
    TNT(46),
    BOOKSHELF(47),
    MOSSY_COBBLESTONE(48),
    OBSIDIAN(49),
    TORCH(50),
    FIRE(51),
    MOB_SPAWNER(52),
    WOOD_STAIRS(53),
    CHEST(54),
    REDSTONE_WIRE(55),
    DIAMOND_ORE(56),
    DIAMOND_BLOCK(57),
    WORKBENCH(58),
    CROPS(59),
    SOIL(60),
    FURNACE(61),
    BURNING_FURNACE(62),
    SIGN_POST(63, 64),
    WOODEN_DOOR(64),
    LADDER(65),
    RAILS(66),
    COBBLESTONE_STAIRS(67),
    WALL_SIGN(68, 64),
    LEVER(69),
    STONE_PLATE(70),
    IRON_DOOR_BLOCK(71),
    WOOD_PLATE(72),
    REDSTONE_ORE(73),
    GLOWING_REDSTONE_ORE(74),
    REDSTONE_TORCH_OFF(75),
    REDSTONE_TORCH_ON(76),
    STONE_BUTTON(77),
    SNOW(78),
    ICE(79),
    SNOW_BLOCK(80),
    CACTUS(81),
    CLAY(82),
    SUGAR_CANE_BLOCK(83),
    JUKEBOX(84),
    FENCE(85),
    PUMPKIN(86),
    NETHERRACK(87),
    SOUL_SAND(88),
    GLOWSTONE(89),
    PORTAL(90),
    JACK_O_LANTERN(91),
    CAKE_BLOCK(92, 64),
    DIODE_BLOCK_OFF(93),
    DIODE_BLOCK_ON(94),
    @Deprecated
    LOCKED_CHEST(95),
    STAINED_GLASS(95),
    TRAP_DOOR(96),
    MONSTER_EGGS(97),
    SMOOTH_BRICK(98),
    HUGE_MUSHROOM_1(99),
    HUGE_MUSHROOM_2(100),
    IRON_FENCE(101),
    THIN_GLASS(102),
    MELON_BLOCK(103),
    PUMPKIN_STEM(104),
    MELON_STEM(105),
    VINE(106),
    FENCE_GATE(107),
    BRICK_STAIRS(108),
    SMOOTH_STAIRS(109),
    MYCEL(110),
    WATER_LILY(111),
    NETHER_BRICK(112),
    NETHER_FENCE(113),
    NETHER_BRICK_STAIRS(114),
    NETHER_WARTS(115),
    ENCHANTMENT_TABLE(116),
    BREWING_STAND(117),
    CAULDRON(118),
    ENDER_PORTAL(119),
    ENDER_PORTAL_FRAME(120),
    ENDER_STONE(121),
    DRAGON_EGG(122),
    REDSTONE_LAMP_OFF(123),
    REDSTONE_LAMP_ON(124),
    WOOD_DOUBLE_STEP(125),
    WOOD_STEP(126),
    COCOA(127),
    SANDSTONE_STAIRS(128),
    EMERALD_ORE(129),
    ENDER_CHEST(130),
    TRIPWIRE_HOOK(131),
    TRIPWIRE(132),
    EMERALD_BLOCK(133),
    SPRUCE_WOOD_STAIRS(134),
    BIRCH_WOOD_STAIRS(135),
    JUNGLE_WOOD_STAIRS(136),
    COMMAND(137),
    BEACON(138),
    COBBLE_WALL(139),
    FLOWER_POT(140),
    CARROT(141),
    POTATO(142),
    WOOD_BUTTON(143),
    SKULL(144),
    ANVIL(145),
    TRAPPED_CHEST(146),
    GOLD_PLATE(147),
    IRON_PLATE(148),
    REDSTONE_COMPARATOR_OFF(149),
    REDSTONE_COMPARATOR_ON(150),
    DAYLIGHT_DETECTOR(151),
    REDSTONE_BLOCK(152),
    QUARTZ_ORE(153),
    HOPPER(154),
    QUARTZ_BLOCK(155),
    QUARTZ_STAIRS(156),
    ACTIVATOR_RAIL(157),
    DROPPER(158),
    STAINED_CLAY(159),
    STAINED_GLASS_PANE(160),
    LEAVES_2(161),
    LOG_2(162),
    ACACIA_STAIRS(163),
    DARK_OAK_STAIRS(164),
    HAY_BLOCK(170),
    CARPET(171),
    HARD_CLAY(172),
    COAL_BLOCK(173),
    PACKED_ICE(174),
    DOUBLE_PLANT(175),
    // ----- Item Separator -----
    IRON_SPADE(256, 1, 250),
    IRON_PICKAXE(257, 1, 250),
    IRON_AXE(258, 1, 250),
    FLINT_AND_STEEL(259, 1, 64),
    APPLE(260),
    BOW(261, 1, 384),
    ARROW(262),
    COAL(263),
    DIAMOND(264),
    IRON_INGOT(265),
    GOLD_INGOT(266),
    IRON_SWORD(267, 1, 250),
    WOOD_SWORD(268, 1, 59),
    WOOD_SPADE(269, 1, 59),
    WOOD_PICKAXE(270, 1, 59),
    WOOD_AXE(271, 1, 59),
    STONE_SWORD(272, 1, 131),
    STONE_SPADE(273, 1, 131),
    STONE_PICKAXE(274, 1, 131),
    STONE_AXE(275, 1, 131),
    DIAMOND_SWORD(276, 1, 1561),
    DIAMOND_SPADE(277, 1, 1561),
    DIAMOND_PICKAXE(278, 1, 1561),
    DIAMOND_AXE(279, 1, 1561),
    STICK(280),
    BOWL(281),
    MUSHROOM_SOUP(282, 1),
    GOLD_SWORD(283, 1, 32),
    GOLD_SPADE(284, 1, 32),
    GOLD_PICKAXE(285, 1, 32),
    GOLD_AXE(286, 1, 32),
    STRING(287),
    FEATHER(288),
    SULPHUR(289),
    WOOD_HOE(290, 1, 59),
    STONE_HOE(291, 1, 131),
    IRON_HOE(292, 1, 250),
    DIAMOND_HOE(293, 1, 1561),
    GOLD_HOE(294, 1, 32),
    SEEDS(295),
    WHEAT(296),
    BREAD(297),
    LEATHER_HELMET(298, 1, 55),
    LEATHER_CHESTPLATE(299, 1, 80),
    LEATHER_LEGGINGS(300, 1, 75),
    LEATHER_BOOTS(301, 1, 65),
    CHAINMAIL_HELMET(302, 1, 165),
    CHAINMAIL_CHESTPLATE(303, 1, 240),
    CHAINMAIL_LEGGINGS(304, 1, 225),
    CHAINMAIL_BOOTS(305, 1, 195),
    IRON_HELMET(306, 1, 165),
    IRON_CHESTPLATE(307, 1, 240),
    IRON_LEGGINGS(308, 1, 225),
    IRON_BOOTS(309, 1, 195),
    DIAMOND_HELMET(310, 1, 363),
    DIAMOND_CHESTPLATE(311, 1, 528),
    DIAMOND_LEGGINGS(312, 1, 495),
    DIAMOND_BOOTS(313, 1, 429),
    GOLD_HELMET(314, 1, 77),
    GOLD_CHESTPLATE(315, 1, 112),
    GOLD_LEGGINGS(316, 1, 105),
    GOLD_BOOTS(317, 1, 91),
    FLINT(318),
    PORK(319),
    GRILLED_PORK(320),
    PAINTING(321),
    GOLDEN_APPLE(322),
    SIGN(323, 16),
    WOOD_DOOR(324, 1),
    BUCKET(325, 16),
    WATER_BUCKET(326, 1),
    LAVA_BUCKET(327, 1),
    MINECART(328, 1),
    SADDLE(329, 1),
    IRON_DOOR(330, 1),
    REDSTONE(331),
    SNOW_BALL(332, 16),
    BOAT(333, 1),
    LEATHER(334),
    MILK_BUCKET(335, 1),
    CLAY_BRICK(336),
    CLAY_BALL(337),
    SUGAR_CANE(338),
    PAPER(339),
    BOOK(340),
    SLIME_BALL(341),
    STORAGE_MINECART(342, 1),
    POWERED_MINECART(343, 1),
    EGG(344, 16),
    COMPASS(345),
    FISHING_ROD(346, 1, 64),
    WATCH(347),
    GLOWSTONE_DUST(348),
    RAW_FISH(349),
    COOKED_FISH(350),
    INK_SACK(351),
    BONE(352),
    SUGAR(353),
    CAKE(354, 1),
    BED(355, 1),
    DIODE(356),
    COOKIE(357),
    MAP(358),
    SHEARS(359, 1, 238),
    MELON(360),
    PUMPKIN_SEEDS(361),
    MELON_SEEDS(362),
    RAW_BEEF(363),
    COOKED_BEEF(364),
    RAW_CHICKEN(365),
    COOKED_CHICKEN(366),
    ROTTEN_FLESH(367),
    ENDER_PEARL(368, 16),
    BLAZE_ROD(369),
    GHAST_TEAR(370),
    GOLD_NUGGET(371),
    NETHER_STALK(372),
    POTION(373, 1),
    GLASS_BOTTLE(374),
    SPIDER_EYE(375),
    FERMENTED_SPIDER_EYE(376),
    BLAZE_POWDER(377),
    MAGMA_CREAM(378),
    BREWING_STAND_ITEM(379),
    CAULDRON_ITEM(380),
    EYE_OF_ENDER(381),
    SPECKLED_MELON(382),
    MONSTER_EGG(383, 64),
    EXP_BOTTLE(384, 64),
    FIREBALL(385, 64),
    BOOK_AND_QUILL(386, 1),
    WRITTEN_BOOK(387, 16),
    EMERALD(388, 64),
    ITEM_FRAME(389),
    FLOWER_POT_ITEM(390),
    CARROT_ITEM(391),
    POTATO_ITEM(392),
    BAKED_POTATO(393),
    POISONOUS_POTATO(394),
    EMPTY_MAP(395),
    GOLDEN_CARROT(396),
    SKULL_ITEM(397),
    CARROT_STICK(398, 1, 25),
    NETHER_STAR(399),
    PUMPKIN_PIE(400),
    FIREWORK(401),
    FIREWORK_CHARGE(402),
    ENCHANTED_BOOK(403, 1),
    REDSTONE_COMPARATOR(404),
    NETHER_BRICK_ITEM(405),
    QUARTZ(406),
    EXPLOSIVE_MINECART(407, 1),
    HOPPER_MINECART(408, 1),
    IRON_BARDING(417, 1),
    GOLD_BARDING(418, 1),
    DIAMOND_BARDING(419, 1),
    LEASH(420),
    NAME_TAG(421),
    COMMAND_MINECART(422, 1),
    GOLD_RECORD(2256, 1),
    GREEN_RECORD(2257, 1),
    RECORD_3(2258, 1),
    RECORD_4(2259, 1),
    RECORD_5(2260, 1),
    RECORD_6(2261, 1),
    RECORD_7(2262, 1),
    RECORD_8(2263, 1),
    RECORD_9(2264, 1),
    RECORD_10(2265, 1),
    RECORD_11(2266, 1),
    RECORD_12(2267, 1),
    ;

    int id;
    short durability;
    int maxStack;
    private static MaterialMc18[] byId = new MaterialMc18[2267];

    private MaterialMc18(final int id) {
        this(id, 64);
    }

    private MaterialMc18(final int id, final int stack) {
        this(id, stack, 1);
    }

    private MaterialMc18(final int id, final int stack, final int durability) {
        this.id = id;
        this.durability = (short) durability;
        this.maxStack = stack;
    }

    /**
     * Gets the item ID or block ID of this Material
     *
     * @return ID of this material
     * @deprecated Magic value
     */
    @Deprecated
    public int getId() {
        return id;
    }

    /**
     * Gets the maximum amount of this material that can be held in a stack
     *
     * @return Maximum stack size for this material
     */
    public int getMaxStackSize() {
        return maxStack;
    }

    /**
     * Gets the maximum durability of this material
     *
     * @return Maximum durability for this material
     */
    public short getMaxDurability() {
        return durability;
    }

    /**
     * Checks if this Material is a placable block
     *
     * @return true if this material is a block
     */
    public boolean isBlock() {
        return id < 256;
    }

    /**
     * Checks if this Material is edible.
     *
     * @return true if this Material is edible.
     */
    public boolean isEdible() {
        switch (this) {
            case BREAD:
            case CARROT_ITEM:
            case BAKED_POTATO:
            case POTATO_ITEM:
            case POISONOUS_POTATO:
            case GOLDEN_CARROT:
            case PUMPKIN_PIE:
            case COOKIE:
            case MELON:
            case MUSHROOM_SOUP:
            case RAW_CHICKEN:
            case COOKED_CHICKEN:
            case RAW_BEEF:
            case COOKED_BEEF:
            case RAW_FISH:
            case COOKED_FISH:
            case PORK:
            case GRILLED_PORK:
            case APPLE:
            case GOLDEN_APPLE:
            case ROTTEN_FLESH:
            case SPIDER_EYE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Attempts to get the Material with the given ID
     *
     * @param id ID of the material to get
     * @return Material if found, or null
     */
    public static MaterialMc18 getMaterial(final int id) {
        if (byId.length > id && id >= 0) {
            return byId[id];
        } else {
            return null;
        }
    }

    public static String getMaterialName(final int id, final short durability) {
        if (byId.length > id && id >= 0) {
            if (byId[id].equals(RAW_FISH) && durability == 0) {
                return "cod";
            } else if (byId[id].equals(RAW_FISH) && durability == 1) {
                return "salmon";
            } else if (byId[id].equals(RAW_FISH) && durability == 2) {
                return "tropical_fish";
            } else if (byId[id].equals(RAW_FISH) && durability == 3) {
                return "pufferfish";
            } else if (byId[id].equals(WATER_LILY)) {
                return "lily_pad";
            }else if (byId[id].equals(INK_SACK)) {
                return "ink_sac";
            }
            return byId[id].name();
        } else {
            return "";
        }
    }


    static {
        for (MaterialMc18 material : values()) {
            if (byId.length > material.id) {
                byId[material.id] = material;
            }
        }
    }

    /**
     * @return True if this material represents a playable music disk.
     */
    public boolean isRecord() {
        return id >= GOLD_RECORD.id && id <= RECORD_12.id;
    }

    /**
     * Check if the material is a block and solid (cannot be passed through by
     * a player)
     *
     * @return True if this material is a block and solid
     */
    public boolean isSolid() {
        if (!isBlock() || id == 0) {
            return false;
        }
        switch (this) {
            case STONE:
            case GRASS:
            case DIRT:
            case COBBLESTONE:
            case WOOD:
            case BEDROCK:
            case SAND:
            case GRAVEL:
            case GOLD_ORE:
            case IRON_ORE:
            case COAL_ORE:
            case LOG:
            case LEAVES:
            case SPONGE:
            case GLASS:
            case LAPIS_ORE:
            case LAPIS_BLOCK:
            case DISPENSER:
            case SANDSTONE:
            case NOTE_BLOCK:
            case BED_BLOCK:
            case PISTON_STICKY_BASE:
            case PISTON_BASE:
            case PISTON_EXTENSION:
            case WOOL:
            case PISTON_MOVING_PIECE:
            case GOLD_BLOCK:
            case IRON_BLOCK:
            case DOUBLE_STEP:
            case STEP:
            case BRICK:
            case TNT:
            case BOOKSHELF:
            case MOSSY_COBBLESTONE:
            case OBSIDIAN:
            case MOB_SPAWNER:
            case WOOD_STAIRS:
            case CHEST:
            case DIAMOND_ORE:
            case DIAMOND_BLOCK:
            case WORKBENCH:
            case SOIL:
            case FURNACE:
            case BURNING_FURNACE:
            case SIGN_POST:
            case WOODEN_DOOR:
            case COBBLESTONE_STAIRS:
            case WALL_SIGN:
            case STONE_PLATE:
            case IRON_DOOR_BLOCK:
            case WOOD_PLATE:
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
            case ICE:
            case SNOW_BLOCK:
            case CACTUS:
            case CLAY:
            case JUKEBOX:
            case FENCE:
            case PUMPKIN:
            case NETHERRACK:
            case SOUL_SAND:
            case GLOWSTONE:
            case JACK_O_LANTERN:
            case CAKE_BLOCK:
            case LOCKED_CHEST:
            case STAINED_GLASS:
            case TRAP_DOOR:
            case MONSTER_EGGS:
            case SMOOTH_BRICK:
            case HUGE_MUSHROOM_1:
            case HUGE_MUSHROOM_2:
            case IRON_FENCE:
            case THIN_GLASS:
            case MELON_BLOCK:
            case FENCE_GATE:
            case BRICK_STAIRS:
            case SMOOTH_STAIRS:
            case MYCEL:
            case NETHER_BRICK:
            case NETHER_FENCE:
            case NETHER_BRICK_STAIRS:
            case ENCHANTMENT_TABLE:
            case BREWING_STAND:
            case CAULDRON:
            case ENDER_PORTAL_FRAME:
            case ENDER_STONE:
            case DRAGON_EGG:
            case REDSTONE_LAMP_OFF:
            case REDSTONE_LAMP_ON:
            case WOOD_DOUBLE_STEP:
            case WOOD_STEP:
            case SANDSTONE_STAIRS:
            case EMERALD_ORE:
            case ENDER_CHEST:
            case EMERALD_BLOCK:
            case SPRUCE_WOOD_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case COMMAND:
            case BEACON:
            case COBBLE_WALL:
            case ANVIL:
            case TRAPPED_CHEST:
            case GOLD_PLATE:
            case IRON_PLATE:
            case DAYLIGHT_DETECTOR:
            case REDSTONE_BLOCK:
            case QUARTZ_ORE:
            case HOPPER:
            case QUARTZ_BLOCK:
            case QUARTZ_STAIRS:
            case DROPPER:
            case STAINED_CLAY:
            case HAY_BLOCK:
            case HARD_CLAY:
            case COAL_BLOCK:
            case STAINED_GLASS_PANE:
            case LEAVES_2:
            case LOG_2:
            case ACACIA_STAIRS:
            case DARK_OAK_STAIRS:
            case PACKED_ICE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if the material is a block and does not block any light
     *
     * @return True if this material is a block and does not block any light
     */
    public boolean isTransparent() {
        if (!isBlock()) {
            return false;
        }
        switch (this) {
            case AIR:
            case SAPLING:
            case POWERED_RAIL:
            case DETECTOR_RAIL:
            case LONG_GRASS:
            case DEAD_BUSH:
            case YELLOW_FLOWER:
            case RED_ROSE:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case TORCH:
            case FIRE:
            case REDSTONE_WIRE:
            case CROPS:
            case LADDER:
            case RAILS:
            case LEVER:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
            case STONE_BUTTON:
            case SNOW:
            case SUGAR_CANE_BLOCK:
            case PORTAL:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case PUMPKIN_STEM:
            case MELON_STEM:
            case VINE:
            case WATER_LILY:
            case NETHER_WARTS:
            case ENDER_PORTAL:
            case COCOA:
            case TRIPWIRE_HOOK:
            case TRIPWIRE:
            case FLOWER_POT:
            case CARROT:
            case POTATO:
            case WOOD_BUTTON:
            case SKULL:
            case REDSTONE_COMPARATOR_OFF:
            case REDSTONE_COMPARATOR_ON:
            case ACTIVATOR_RAIL:
            case CARPET:
            case DOUBLE_PLANT:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if the material is a block and can catch fire
     *
     * @return True if this material is a block and can catch fire
     */
    public boolean isFlammable() {
        if (!isBlock()) {
            return false;
        }
        switch (this) {
            case WOOD:
            case LOG:
            case LEAVES:
            case NOTE_BLOCK:
            case BED_BLOCK:
            case LONG_GRASS:
            case DEAD_BUSH:
            case WOOL:
            case TNT:
            case BOOKSHELF:
            case WOOD_STAIRS:
            case CHEST:
            case WORKBENCH:
            case SIGN_POST:
            case WOODEN_DOOR:
            case WALL_SIGN:
            case WOOD_PLATE:
            case JUKEBOX:
            case FENCE:
            case TRAP_DOOR:
            case HUGE_MUSHROOM_1:
            case HUGE_MUSHROOM_2:
            case VINE:
            case FENCE_GATE:
            case WOOD_DOUBLE_STEP:
            case WOOD_STEP:
            case SPRUCE_WOOD_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case TRAPPED_CHEST:
            case DAYLIGHT_DETECTOR:
            case CARPET:
            case LEAVES_2:
            case LOG_2:
            case ACACIA_STAIRS:
            case DARK_OAK_STAIRS:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if the material is a block and can burn away
     *
     * @return True if this material is a block and can burn away
     */
    public boolean isBurnable() {
        if (!isBlock()) {
            return false;
        }
        switch (this) {
            case WOOD:
            case LOG:
            case LEAVES:
            case LONG_GRASS:
            case WOOL:
            case YELLOW_FLOWER:
            case RED_ROSE:
            case TNT:
            case BOOKSHELF:
            case WOOD_STAIRS:
            case FENCE:
            case VINE:
            case WOOD_DOUBLE_STEP:
            case WOOD_STEP:
            case SPRUCE_WOOD_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case HAY_BLOCK:
            case COAL_BLOCK:
            case LEAVES_2:
            case LOG_2:
            case CARPET:
            case DOUBLE_PLANT:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if the material is a block and completely blocks vision
     *
     * @return True if this material is a block and completely blocks vision
     */
    public boolean isOccluding() {
        if (!isBlock()) {
            return false;
        }
        switch (this) {
            case STONE:
            case GRASS:
            case DIRT:
            case COBBLESTONE:
            case WOOD:
            case BEDROCK:
            case SAND:
            case GRAVEL:
            case GOLD_ORE:
            case IRON_ORE:
            case COAL_ORE:
            case LOG:
            case SPONGE:
            case LAPIS_ORE:
            case LAPIS_BLOCK:
            case DISPENSER:
            case SANDSTONE:
            case NOTE_BLOCK:
            case WOOL:
            case GOLD_BLOCK:
            case IRON_BLOCK:
            case DOUBLE_STEP:
            case BRICK:
            case BOOKSHELF:
            case MOSSY_COBBLESTONE:
            case OBSIDIAN:
            case MOB_SPAWNER:
            case DIAMOND_ORE:
            case DIAMOND_BLOCK:
            case WORKBENCH:
            case FURNACE:
            case BURNING_FURNACE:
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
            case SNOW_BLOCK:
            case CLAY:
            case JUKEBOX:
            case PUMPKIN:
            case NETHERRACK:
            case SOUL_SAND:
            case JACK_O_LANTERN:
            case MONSTER_EGGS:
            case SMOOTH_BRICK:
            case HUGE_MUSHROOM_1:
            case HUGE_MUSHROOM_2:
            case MELON_BLOCK:
            case MYCEL:
            case NETHER_BRICK:
            case ENDER_PORTAL_FRAME:
            case ENDER_STONE:
            case REDSTONE_LAMP_OFF:
            case REDSTONE_LAMP_ON:
            case WOOD_DOUBLE_STEP:
            case EMERALD_ORE:
            case EMERALD_BLOCK:
            case COMMAND:
            case QUARTZ_ORE:
            case QUARTZ_BLOCK:
            case DROPPER:
            case STAINED_CLAY:
            case HAY_BLOCK:
            case HARD_CLAY:
            case COAL_BLOCK:
            case LOG_2:
            case PACKED_ICE:
                return true;
            default:
                return false;
        }
    }

    /**
     * @return True if this material is affected by gravity.
     */
    public boolean hasGravity() {
        if (!isBlock()) {
            return false;
        }
        switch (this) {
            case SAND:
            case GRAVEL:
            case ANVIL:
                return true;
            default:
                return false;
        }
    }
}
