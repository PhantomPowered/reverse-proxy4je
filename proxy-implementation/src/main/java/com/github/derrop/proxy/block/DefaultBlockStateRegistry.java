package com.github.derrop.proxy.block;

import com.github.derrop.proxy.api.block.*;
import com.github.derrop.proxy.api.util.EnumFacing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// TODO BlockStates that are completely done have a "complete" behind, others still need to be fully implemented
public class DefaultBlockStateRegistry implements BlockStateRegistry {

    private static final Map<Integer, BlockState> BLOCK_STATE_IDS = new HashMap<>();

    private static final BlockState AIR_STATE = new DefaultBlockState(0, Material.AIR);

    static {
        BLOCK_STATE_IDS.put(0, AIR_STATE); // complete
        registerState(16, Material.STONE); // complete
        registerState(17, Material.STONE).subMaterial(SubMaterial.GRANITE); // complete
        registerState(18, Material.STONE).subMaterial(SubMaterial.POLISHED_GRANITE); // complete
        registerState(19, Material.STONE).subMaterial(SubMaterial.DIORITE); // complete
        registerState(20, Material.STONE).subMaterial(SubMaterial.POLISHED_DIORITE); // complete
        registerState(21, Material.STONE).subMaterial(SubMaterial.ANDESITE); // complete
        registerState(22, Material.STONE).subMaterial(SubMaterial.POLISHED_ANDESITE); // complete
        registerState(32, Material.GRASS); // complete
        registerState(48, Material.DIRT); // complete
        registerState(49, Material.DIRT).subMaterial(SubMaterial.COARSE_DIRT); // complete
        registerState(50, Material.DIRT).subMaterial(SubMaterial.PODZOL); // complete
        registerState(64, Material.BRICK); // complete
        registerState(80, Material.WOOD);
        registerState(81, Material.WOOD);
        registerState(82, Material.WOOD);
        registerState(83, Material.WOOD);
        registerState(84, Material.WOOD);
        registerState(85, Material.WOOD);
        registerState(96, Material.SAPLING);
        registerState(97, Material.SAPLING);
        registerState(98, Material.SAPLING);
        registerState(99, Material.SAPLING);
        registerState(100, Material.SAPLING);
        registerState(101, Material.SAPLING);
        registerState(104, Material.SAPLING);
        registerState(105, Material.SAPLING);
        registerState(106, Material.SAPLING);
        registerState(107, Material.SAPLING);
        registerState(108, Material.SAPLING);
        registerState(109, Material.SAPLING);
        registerState(112, Material.BEDROCK);
        registerState(128, Material.WATER);
        registerState(129, Material.WATER);
        registerState(130, Material.WATER);
        registerState(131, Material.WATER);
        registerState(132, Material.WATER);
        registerState(133, Material.WATER);
        registerState(134, Material.WATER);
        registerState(135, Material.WATER);
        registerState(136, Material.WATER);
        registerState(137, Material.WATER);
        registerState(138, Material.WATER);
        registerState(139, Material.WATER);
        registerState(140, Material.WATER);
        registerState(141, Material.WATER);
        registerState(142, Material.WATER);
        registerState(143, Material.WATER);
        registerState(144, Material.STATIONARY_WATER);
        registerState(145, Material.STATIONARY_WATER);
        registerState(146, Material.STATIONARY_WATER);
        registerState(147, Material.STATIONARY_WATER);
        registerState(148, Material.STATIONARY_WATER);
        registerState(149, Material.STATIONARY_WATER);
        registerState(150, Material.STATIONARY_WATER);
        registerState(151, Material.STATIONARY_WATER);
        registerState(152, Material.STATIONARY_WATER);
        registerState(153, Material.STATIONARY_WATER);
        registerState(154, Material.STATIONARY_WATER);
        registerState(155, Material.STATIONARY_WATER);
        registerState(156, Material.STATIONARY_WATER);
        registerState(157, Material.STATIONARY_WATER);
        registerState(158, Material.STATIONARY_WATER);
        registerState(159, Material.STATIONARY_WATER);
        registerState(160, Material.LAVA);
        registerState(161, Material.LAVA);
        registerState(162, Material.LAVA);
        registerState(163, Material.LAVA);
        registerState(164, Material.LAVA);
        registerState(165, Material.LAVA);
        registerState(166, Material.LAVA);
        registerState(167, Material.LAVA);
        registerState(168, Material.LAVA);
        registerState(169, Material.LAVA);
        registerState(170, Material.LAVA);
        registerState(171, Material.LAVA);
        registerState(172, Material.LAVA);
        registerState(173, Material.LAVA);
        registerState(174, Material.LAVA);
        registerState(175, Material.LAVA);
        registerState(176, Material.STATIONARY_LAVA);
        registerState(177, Material.STATIONARY_LAVA);
        registerState(178, Material.STATIONARY_LAVA);
        registerState(179, Material.STATIONARY_LAVA);
        registerState(180, Material.STATIONARY_LAVA);
        registerState(181, Material.STATIONARY_LAVA);
        registerState(182, Material.STATIONARY_LAVA);
        registerState(183, Material.STATIONARY_LAVA);
        registerState(184, Material.STATIONARY_LAVA);
        registerState(185, Material.STATIONARY_LAVA);
        registerState(186, Material.STATIONARY_LAVA);
        registerState(187, Material.STATIONARY_LAVA);
        registerState(188, Material.STATIONARY_LAVA);
        registerState(189, Material.STATIONARY_LAVA);
        registerState(190, Material.STATIONARY_LAVA);
        registerState(191, Material.STATIONARY_LAVA);
        registerState(192, Material.SAND);
        registerState(193, Material.SAND);
        registerState(208, Material.GRAVEL); // complete
        registerState(224, Material.GOLD_ORE); // complete
        registerState(240, Material.IRON_ORE); // complete
        registerState(256, Material.COAL_ORE); // complete
        registerState(272, Material.LOG);
        registerState(273, Material.LOG);
        registerState(274, Material.LOG);
        registerState(275, Material.LOG);
        registerState(276, Material.LOG);
        registerState(277, Material.LOG);
        registerState(278, Material.LOG);
        registerState(279, Material.LOG);
        registerState(280, Material.LOG);
        registerState(281, Material.LOG);
        registerState(282, Material.LOG);
        registerState(283, Material.LOG);
        registerState(284, Material.LOG);
        registerState(285, Material.LOG);
        registerState(286, Material.LOG);
        registerState(287, Material.LOG);
        registerState(288, Material.LEAVES);
        registerState(289, Material.LEAVES);
        registerState(290, Material.LEAVES);
        registerState(291, Material.LEAVES);
        registerState(292, Material.LEAVES);
        registerState(293, Material.LEAVES);
        registerState(294, Material.LEAVES);
        registerState(295, Material.LEAVES);
        registerState(296, Material.LEAVES);
        registerState(297, Material.LEAVES);
        registerState(298, Material.LEAVES);
        registerState(299, Material.LEAVES);
        registerState(300, Material.LEAVES);
        registerState(301, Material.LEAVES);
        registerState(302, Material.LEAVES);
        registerState(303, Material.LEAVES);
        registerState(304, Material.SPONGE);
        registerState(305, Material.SPONGE);
        registerState(320, Material.GLASS); // complete
        registerState(336, Material.LAPIS_ORE); // complete
        registerState(352, Material.LAPIS_BLOCK); // complete
        registerState(368, Material.DISPENSER);
        registerState(369, Material.DISPENSER);
        registerState(370, Material.DISPENSER);
        registerState(371, Material.DISPENSER);
        registerState(372, Material.DISPENSER);
        registerState(373, Material.DISPENSER);
        registerState(376, Material.DISPENSER);
        registerState(377, Material.DISPENSER);
        registerState(378, Material.DISPENSER);
        registerState(379, Material.DISPENSER);
        registerState(380, Material.DISPENSER);
        registerState(381, Material.DISPENSER);
        registerState(384, Material.SANDSTONE);
        registerState(385, Material.SANDSTONE);
        registerState(386, Material.SANDSTONE);
        registerState(400, Material.NOTE_BLOCK); // complete
        registerState(416, Material.BED_BLOCK);
        registerState(417, Material.BED_BLOCK);
        registerState(418, Material.BED_BLOCK);
        registerState(419, Material.BED_BLOCK);
        registerState(424, Material.BED_BLOCK);
        registerState(425, Material.BED_BLOCK);
        registerState(426, Material.BED_BLOCK);
        registerState(427, Material.BED_BLOCK);
        registerState(428, Material.BED_BLOCK);
        registerState(429, Material.BED_BLOCK);
        registerState(430, Material.BED_BLOCK);
        registerState(431, Material.BED_BLOCK);
        registerState(432, Material.POWERED_RAIL);
        registerState(433, Material.POWERED_RAIL);
        registerState(434, Material.POWERED_RAIL);
        registerState(435, Material.POWERED_RAIL);
        registerState(436, Material.POWERED_RAIL);
        registerState(437, Material.POWERED_RAIL);
        registerState(440, Material.POWERED_RAIL);
        registerState(441, Material.POWERED_RAIL);
        registerState(442, Material.POWERED_RAIL);
        registerState(443, Material.POWERED_RAIL);
        registerState(444, Material.POWERED_RAIL);
        registerState(445, Material.POWERED_RAIL);
        registerState(448, Material.DETECTOR_RAIL);
        registerState(449, Material.DETECTOR_RAIL);
        registerState(450, Material.DETECTOR_RAIL);
        registerState(451, Material.DETECTOR_RAIL);
        registerState(452, Material.DETECTOR_RAIL);
        registerState(453, Material.DETECTOR_RAIL);
        registerState(456, Material.DETECTOR_RAIL);
        registerState(457, Material.DETECTOR_RAIL);
        registerState(458, Material.DETECTOR_RAIL);
        registerState(459, Material.DETECTOR_RAIL);
        registerState(460, Material.DETECTOR_RAIL);
        registerState(461, Material.DETECTOR_RAIL);
        registerState(464, Material.PISTON_STICKY_BASE);
        registerState(465, Material.PISTON_STICKY_BASE);
        registerState(466, Material.PISTON_STICKY_BASE);
        registerState(467, Material.PISTON_STICKY_BASE);
        registerState(468, Material.PISTON_STICKY_BASE);
        registerState(469, Material.PISTON_STICKY_BASE);
        registerState(472, Material.PISTON_STICKY_BASE);
        registerState(473, Material.PISTON_STICKY_BASE);
        registerState(474, Material.PISTON_STICKY_BASE);
        registerState(475, Material.PISTON_STICKY_BASE);
        registerState(476, Material.PISTON_STICKY_BASE);
        registerState(477, Material.PISTON_STICKY_BASE);
        registerState(480, Material.WEB); // complete
        registerState(496, Material.LONG_GRASS);
        registerState(497, Material.LONG_GRASS);
        registerState(498, Material.LONG_GRASS);
        registerState(512, Material.DEAD_BUSH); // complete
        registerState(528, Material.PISTON_BASE);
        registerState(529, Material.PISTON_BASE);
        registerState(530, Material.PISTON_BASE);
        registerState(531, Material.PISTON_BASE);
        registerState(532, Material.PISTON_BASE);
        registerState(533, Material.PISTON_BASE);
        registerState(536, Material.PISTON_BASE);
        registerState(537, Material.PISTON_BASE);
        registerState(538, Material.PISTON_BASE);
        registerState(539, Material.PISTON_BASE);
        registerState(540, Material.PISTON_BASE);
        registerState(541, Material.PISTON_BASE);
        registerState(544, Material.PISTON_BASE);
        registerState(545, Material.PISTON_BASE);
        registerState(546, Material.PISTON_BASE);
        registerState(547, Material.PISTON_BASE);
        registerState(548, Material.PISTON_BASE);
        registerState(549, Material.PISTON_BASE);
        registerState(552, Material.PISTON_BASE);
        registerState(553, Material.PISTON_BASE);
        registerState(554, Material.PISTON_BASE);
        registerState(555, Material.PISTON_BASE);
        registerState(556, Material.PISTON_BASE);
        registerState(557, Material.PISTON_BASE);
        registerState(560, Material.WOOL);
        registerState(561, Material.WOOL);
        registerState(562, Material.WOOL);
        registerState(563, Material.WOOL);
        registerState(564, Material.WOOL);
        registerState(565, Material.WOOL);
        registerState(566, Material.WOOL);
        registerState(567, Material.WOOL);
        registerState(568, Material.WOOL);
        registerState(569, Material.WOOL);
        registerState(570, Material.WOOL);
        registerState(571, Material.WOOL);
        registerState(572, Material.WOOL);
        registerState(573, Material.WOOL);
        registerState(574, Material.WOOL);
        registerState(575, Material.WOOL);
        registerState(576, Material.PISTON_EXTENSION);
        registerState(577, Material.PISTON_EXTENSION);
        registerState(578, Material.PISTON_EXTENSION);
        registerState(579, Material.PISTON_EXTENSION);
        registerState(580, Material.PISTON_EXTENSION);
        registerState(581, Material.PISTON_EXTENSION);
        registerState(584, Material.PISTON_EXTENSION);
        registerState(585, Material.PISTON_EXTENSION);
        registerState(586, Material.PISTON_EXTENSION);
        registerState(587, Material.PISTON_EXTENSION);
        registerState(588, Material.PISTON_EXTENSION);
        registerState(589, Material.PISTON_EXTENSION);
        registerState(592, Material.YELLOW_FLOWER); // complete
        registerState(608, Material.RED_ROSE);
        registerState(609, Material.RED_ROSE);
        registerState(610, Material.RED_ROSE);
        registerState(611, Material.RED_ROSE);
        registerState(612, Material.RED_ROSE);
        registerState(613, Material.RED_ROSE);
        registerState(614, Material.RED_ROSE);
        registerState(615, Material.RED_ROSE);
        registerState(616, Material.RED_ROSE);
        registerState(624, Material.BROWN_MUSHROOM); // complete
        registerState(640, Material.RED_MUSHROOM); // complete
        registerState(656, Material.GOLD_BLOCK); // complete
        registerState(672, Material.IRON_BLOCK); // complete
        registerState(688, Material.DOUBLE_STONE_SLAB2);
        registerState(689, Material.DOUBLE_STONE_SLAB2);
        registerState(690, Material.DOUBLE_STONE_SLAB2);
        registerState(691, Material.DOUBLE_STONE_SLAB2);
        registerState(692, Material.DOUBLE_STONE_SLAB2);
        registerState(693, Material.DOUBLE_STONE_SLAB2);
        registerState(694, Material.DOUBLE_STONE_SLAB2);
        registerState(695, Material.DOUBLE_STONE_SLAB2);
        registerState(696, Material.DOUBLE_STONE_SLAB2);
        registerState(697, Material.DOUBLE_STONE_SLAB2);
        registerState(698, Material.DOUBLE_STONE_SLAB2);
        registerState(699, Material.DOUBLE_STONE_SLAB2);
        registerState(700, Material.DOUBLE_STONE_SLAB2);
        registerState(701, Material.DOUBLE_STONE_SLAB2);
        registerState(702, Material.DOUBLE_STONE_SLAB2);
        registerState(703, Material.DOUBLE_STONE_SLAB2);
        registerState(704, Material.STONE_SLAB2);
        registerState(705, Material.STONE_SLAB2);
        registerState(706, Material.STONE_SLAB2);
        registerState(707, Material.STONE_SLAB2);
        registerState(708, Material.STONE_SLAB2);
        registerState(709, Material.STONE_SLAB2);
        registerState(710, Material.STONE_SLAB2);
        registerState(711, Material.STONE_SLAB2);
        registerState(712, Material.STONE_SLAB2);
        registerState(713, Material.STONE_SLAB2);
        registerState(714, Material.STONE_SLAB2);
        registerState(715, Material.STONE_SLAB2);
        registerState(716, Material.STONE_SLAB2);
        registerState(717, Material.STONE_SLAB2);
        registerState(718, Material.STONE_SLAB2);
        registerState(719, Material.STONE_SLAB2);
        registerState(720, Material.BRICK); // complete
        registerState(736, Material.TNT);
        registerState(737, Material.TNT);
        registerState(752, Material.BOOKSHELF); // complete
        registerState(768, Material.MOSSY_COBBLESTONE); // complete
        registerState(784, Material.OBSIDIAN); // complete
        registerState(801, Material.TORCH);
        registerState(802, Material.TORCH);
        registerState(803, Material.TORCH);
        registerState(804, Material.TORCH);
        registerState(805, Material.TORCH);
        registerState(816, Material.FIRE);
        registerState(817, Material.FIRE);
        registerState(818, Material.FIRE);
        registerState(819, Material.FIRE);
        registerState(820, Material.FIRE);
        registerState(821, Material.FIRE);
        registerState(822, Material.FIRE);
        registerState(823, Material.FIRE);
        registerState(824, Material.FIRE);
        registerState(825, Material.FIRE);
        registerState(826, Material.FIRE);
        registerState(827, Material.FIRE);
        registerState(828, Material.FIRE);
        registerState(829, Material.FIRE);
        registerState(830, Material.FIRE);
        registerState(831, Material.FIRE);
        registerState(832, Material.MOB_SPAWNER);
        registerState(848, Material.WOOD_STAIRS);
        registerState(849, Material.WOOD_STAIRS);
        registerState(850, Material.WOOD_STAIRS);
        registerState(851, Material.WOOD_STAIRS);
        registerState(852, Material.WOOD_STAIRS);
        registerState(853, Material.WOOD_STAIRS);
        registerState(854, Material.WOOD_STAIRS);
        registerState(855, Material.WOOD_STAIRS);
        registerState(866, Material.CHEST);
        registerState(867, Material.CHEST);
        registerState(868, Material.CHEST);
        registerState(869, Material.CHEST);
        registerState(880, Material.REDSTONE_WIRE);
        registerState(881, Material.REDSTONE_WIRE);
        registerState(882, Material.REDSTONE_WIRE);
        registerState(883, Material.REDSTONE_WIRE);
        registerState(884, Material.REDSTONE_WIRE);
        registerState(885, Material.REDSTONE_WIRE);
        registerState(886, Material.REDSTONE_WIRE);
        registerState(887, Material.REDSTONE_WIRE);
        registerState(888, Material.REDSTONE_WIRE);
        registerState(889, Material.REDSTONE_WIRE);
        registerState(890, Material.REDSTONE_WIRE);
        registerState(891, Material.REDSTONE_WIRE);
        registerState(892, Material.REDSTONE_WIRE);
        registerState(893, Material.REDSTONE_WIRE);
        registerState(894, Material.REDSTONE_WIRE);
        registerState(895, Material.REDSTONE_WIRE);
        registerState(896, Material.DIAMOND_ORE); // complete
        registerState(912, Material.DIAMOND_BLOCK); // complete
        registerState(928, Material.WORKBENCH); // complete
        registerState(944, Material.CROPS);
        registerState(945, Material.CROPS);
        registerState(946, Material.CROPS);
        registerState(947, Material.CROPS);
        registerState(948, Material.CROPS);
        registerState(949, Material.CROPS);
        registerState(950, Material.CROPS);
        registerState(951, Material.CROPS);
        registerState(960, Material.SOIL);
        registerState(961, Material.SOIL);
        registerState(962, Material.SOIL);
        registerState(963, Material.SOIL);
        registerState(964, Material.SOIL);
        registerState(965, Material.SOIL);
        registerState(966, Material.SOIL);
        registerState(967, Material.SOIL);
        registerState(978, Material.FURNACE);
        registerState(979, Material.FURNACE);
        registerState(980, Material.FURNACE);
        registerState(981, Material.FURNACE);
        registerState(994, Material.FURNACE);
        registerState(995, Material.FURNACE);
        registerState(996, Material.FURNACE);
        registerState(997, Material.FURNACE);
        registerState(1008, Material.SIGN_POST);
        registerState(1009, Material.SIGN_POST);
        registerState(1010, Material.SIGN_POST);
        registerState(1011, Material.SIGN_POST);
        registerState(1012, Material.SIGN_POST);
        registerState(1013, Material.SIGN_POST);
        registerState(1014, Material.SIGN_POST);
        registerState(1015, Material.SIGN_POST);
        registerState(1016, Material.SIGN_POST);
        registerState(1017, Material.SIGN_POST);
        registerState(1018, Material.SIGN_POST);
        registerState(1019, Material.SIGN_POST);
        registerState(1020, Material.SIGN_POST);
        registerState(1021, Material.SIGN_POST);
        registerState(1022, Material.SIGN_POST);
        registerState(1023, Material.SIGN_POST);
        registerState(1024, Material.WOODEN_DOOR).facing(EnumFacing.EAST); // complete
        registerState(1025, Material.WOODEN_DOOR).facing(EnumFacing.SOUTH); // complete
        registerState(1026, Material.WOODEN_DOOR).facing(EnumFacing.WEST); // complete
        registerState(1027, Material.WOODEN_DOOR).facing(EnumFacing.NORTH); // complete
        registerState(1028, Material.WOODEN_DOOR).facing(EnumFacing.EAST).open(); // complete
        registerState(1029, Material.WOODEN_DOOR).facing(EnumFacing.SOUTH).open(); // complete
        registerState(1030, Material.WOODEN_DOOR).facing(EnumFacing.WEST).open(); // complete
        registerState(1031, Material.WOODEN_DOOR).facing(EnumFacing.NORTH).open(); // complete
        registerState(1032, Material.WOODEN_DOOR).hinge(HingePosition.LEFT); // complete
        registerState(1033, Material.WOODEN_DOOR).hinge(HingePosition.RIGHT); // complete
        registerState(1034, Material.WOODEN_DOOR).hinge(HingePosition.LEFT).powered(); // complete
        registerState(1035, Material.WOODEN_DOOR).hinge(HingePosition.RIGHT).powered(); // complete
        registerState(1042, Material.LADDER);
        registerState(1043, Material.LADDER);
        registerState(1044, Material.LADDER);
        registerState(1045, Material.LADDER);
        registerState(1056, Material.RAILS);
        registerState(1057, Material.RAILS);
        registerState(1058, Material.RAILS);
        registerState(1059, Material.RAILS);
        registerState(1060, Material.RAILS);
        registerState(1061, Material.RAILS);
        registerState(1062, Material.RAILS);
        registerState(1063, Material.RAILS);
        registerState(1064, Material.RAILS);
        registerState(1065, Material.RAILS);
        registerState(1072, Material.COBBLESTONE_STAIRS);
        registerState(1073, Material.COBBLESTONE_STAIRS);
        registerState(1074, Material.COBBLESTONE_STAIRS);
        registerState(1075, Material.COBBLESTONE_STAIRS);
        registerState(1076, Material.COBBLESTONE_STAIRS);
        registerState(1077, Material.COBBLESTONE_STAIRS);
        registerState(1078, Material.COBBLESTONE_STAIRS);
        registerState(1079, Material.COBBLESTONE_STAIRS);
        registerState(1090, Material.WALL_SIGN);
        registerState(1091, Material.WALL_SIGN);
        registerState(1092, Material.WALL_SIGN);
        registerState(1093, Material.WALL_SIGN);
        registerState(1104, Material.LEVER);
        registerState(1105, Material.LEVER);
        registerState(1106, Material.LEVER);
        registerState(1107, Material.LEVER);
        registerState(1108, Material.LEVER);
        registerState(1109, Material.LEVER);
        registerState(1110, Material.LEVER);
        registerState(1111, Material.LEVER);
        registerState(1112, Material.LEVER);
        registerState(1113, Material.LEVER);
        registerState(1114, Material.LEVER);
        registerState(1115, Material.LEVER);
        registerState(1116, Material.LEVER);
        registerState(1117, Material.LEVER);
        registerState(1118, Material.LEVER);
        registerState(1119, Material.LEVER);
        registerState(1120, Material.STONE_PLATE);
        registerState(1121, Material.STONE_PLATE);
        registerState(1136, Material.IRON_DOOR_BLOCK);
        registerState(1137, Material.IRON_DOOR_BLOCK);
        registerState(1138, Material.IRON_DOOR_BLOCK);
        registerState(1139, Material.IRON_DOOR_BLOCK);
        registerState(1140, Material.IRON_DOOR_BLOCK);
        registerState(1141, Material.IRON_DOOR_BLOCK);
        registerState(1142, Material.IRON_DOOR_BLOCK);
        registerState(1143, Material.IRON_DOOR_BLOCK);
        registerState(1144, Material.IRON_DOOR_BLOCK);
        registerState(1145, Material.IRON_DOOR_BLOCK);
        registerState(1146, Material.IRON_DOOR_BLOCK);
        registerState(1147, Material.IRON_DOOR_BLOCK);
        registerState(1152, Material.WOOD_PLATE);
        registerState(1153, Material.WOOD_PLATE);
        registerState(1168, Material.REDSTONE_ORE);
        registerState(1184, Material.REDSTONE_ORE);
        registerState(1201, Material.REDSTONE_TORCH_ON);
        registerState(1202, Material.REDSTONE_TORCH_ON);
        registerState(1203, Material.REDSTONE_TORCH_ON);
        registerState(1204, Material.REDSTONE_TORCH_ON);
        registerState(1205, Material.REDSTONE_TORCH_ON);
        registerState(1217, Material.REDSTONE_TORCH_OFF);
        registerState(1218, Material.REDSTONE_TORCH_OFF);
        registerState(1219, Material.REDSTONE_TORCH_OFF);
        registerState(1220, Material.REDSTONE_TORCH_OFF);
        registerState(1221, Material.REDSTONE_TORCH_OFF);
        registerState(1232, Material.WOOD_BUTTON);
        registerState(1233, Material.WOOD_BUTTON);
        registerState(1234, Material.WOOD_BUTTON);
        registerState(1235, Material.WOOD_BUTTON);
        registerState(1236, Material.WOOD_BUTTON);
        registerState(1237, Material.WOOD_BUTTON);
        registerState(1240, Material.WOOD_BUTTON);
        registerState(1241, Material.WOOD_BUTTON);
        registerState(1242, Material.WOOD_BUTTON);
        registerState(1243, Material.WOOD_BUTTON);
        registerState(1244, Material.WOOD_BUTTON);
        registerState(1245, Material.WOOD_BUTTON);
        registerState(1248, Material.SNOW).layers(1).height(0.125D); // complete
        registerState(1249, Material.SNOW).layers(2).height(0.25D); // complete
        registerState(1250, Material.SNOW).layers(3).height(0.325D); // complete
        registerState(1251, Material.SNOW).layers(4).height(0.5D); // complete
        registerState(1252, Material.SNOW).layers(5).height(0.625D); // complete
        registerState(1253, Material.SNOW).layers(6).height(0.75D); // complete
        registerState(1254, Material.SNOW).layers(7).height(0.875D); // complete
        registerState(1255, Material.SNOW).layers(8); // complete
        registerState(1264, Material.ICE); // complete
        registerState(1280, Material.SNOW_BLOCK); // complete
        registerState(1296, Material.CACTUS);
        registerState(1297, Material.CACTUS);
        registerState(1298, Material.CACTUS);
        registerState(1299, Material.CACTUS);
        registerState(1300, Material.CACTUS);
        registerState(1301, Material.CACTUS);
        registerState(1302, Material.CACTUS);
        registerState(1303, Material.CACTUS);
        registerState(1304, Material.CACTUS);
        registerState(1305, Material.CACTUS);
        registerState(1306, Material.CACTUS);
        registerState(1307, Material.CACTUS);
        registerState(1308, Material.CACTUS);
        registerState(1309, Material.CACTUS);
        registerState(1310, Material.CACTUS);
        registerState(1311, Material.CACTUS);
        registerState(1312, Material.CLAY); // complete
        registerState(1328, Material.SUGAR_CANE_BLOCK);
        registerState(1329, Material.SUGAR_CANE_BLOCK);
        registerState(1330, Material.SUGAR_CANE_BLOCK);
        registerState(1331, Material.SUGAR_CANE_BLOCK);
        registerState(1332, Material.SUGAR_CANE_BLOCK);
        registerState(1333, Material.SUGAR_CANE_BLOCK);
        registerState(1334, Material.SUGAR_CANE_BLOCK);
        registerState(1335, Material.SUGAR_CANE_BLOCK);
        registerState(1336, Material.SUGAR_CANE_BLOCK);
        registerState(1337, Material.SUGAR_CANE_BLOCK);
        registerState(1338, Material.SUGAR_CANE_BLOCK);
        registerState(1339, Material.SUGAR_CANE_BLOCK);
        registerState(1340, Material.SUGAR_CANE_BLOCK);
        registerState(1341, Material.SUGAR_CANE_BLOCK);
        registerState(1342, Material.SUGAR_CANE_BLOCK);
        registerState(1343, Material.SUGAR_CANE_BLOCK);
        registerState(1344, Material.JUKEBOX);
        registerState(1345, Material.JUKEBOX);
        registerState(1360, Material.FENCE); // complete
        registerState(1376, Material.PUMPKIN);
        registerState(1377, Material.PUMPKIN);
        registerState(1378, Material.PUMPKIN);
        registerState(1379, Material.PUMPKIN);
        registerState(1392, Material.NETHERRACK); // complete
        registerState(1408, Material.SOUL_SAND); // complete
        registerState(1424, Material.GLOWSTONE); // complete
        registerState(1441, Material.PORTAL);
        registerState(1442, Material.PORTAL);
        registerState(1456, Material.JACK_O_LANTERN);
        registerState(1457, Material.JACK_O_LANTERN);
        registerState(1458, Material.JACK_O_LANTERN);
        registerState(1459, Material.JACK_O_LANTERN);
        registerState(1472, Material.CAKE_BLOCK);
        registerState(1473, Material.CAKE_BLOCK);
        registerState(1474, Material.CAKE_BLOCK);
        registerState(1475, Material.CAKE_BLOCK);
        registerState(1476, Material.CAKE_BLOCK);
        registerState(1477, Material.CAKE_BLOCK);
        registerState(1478, Material.CAKE_BLOCK);
        registerState(1488, Material.DIODE_BLOCK_OFF);
        registerState(1489, Material.DIODE_BLOCK_OFF);
        registerState(1490, Material.DIODE_BLOCK_OFF);
        registerState(1491, Material.DIODE_BLOCK_OFF);
        registerState(1492, Material.DIODE_BLOCK_OFF);
        registerState(1493, Material.DIODE_BLOCK_OFF);
        registerState(1494, Material.DIODE_BLOCK_OFF);
        registerState(1495, Material.DIODE_BLOCK_OFF);
        registerState(1496, Material.DIODE_BLOCK_OFF);
        registerState(1497, Material.DIODE_BLOCK_OFF);
        registerState(1498, Material.DIODE_BLOCK_OFF);
        registerState(1499, Material.DIODE_BLOCK_OFF);
        registerState(1500, Material.DIODE_BLOCK_OFF);
        registerState(1501, Material.DIODE_BLOCK_OFF);
        registerState(1502, Material.DIODE_BLOCK_OFF);
        registerState(1503, Material.DIODE_BLOCK_OFF);
        registerState(1504, Material.DIODE_BLOCK_ON);
        registerState(1505, Material.DIODE_BLOCK_ON);
        registerState(1506, Material.DIODE_BLOCK_ON);
        registerState(1507, Material.DIODE_BLOCK_ON);
        registerState(1508, Material.DIODE_BLOCK_ON);
        registerState(1509, Material.DIODE_BLOCK_ON);
        registerState(1510, Material.DIODE_BLOCK_ON);
        registerState(1511, Material.DIODE_BLOCK_ON);
        registerState(1512, Material.DIODE_BLOCK_ON);
        registerState(1513, Material.DIODE_BLOCK_ON);
        registerState(1514, Material.DIODE_BLOCK_ON);
        registerState(1515, Material.DIODE_BLOCK_ON);
        registerState(1516, Material.DIODE_BLOCK_ON);
        registerState(1517, Material.DIODE_BLOCK_ON);
        registerState(1518, Material.DIODE_BLOCK_ON);
        registerState(1519, Material.DIODE_BLOCK_ON);
        registerState(1520, Material.STAINED_GLASS);
        registerState(1521, Material.STAINED_GLASS);
        registerState(1522, Material.STAINED_GLASS);
        registerState(1523, Material.STAINED_GLASS);
        registerState(1524, Material.STAINED_GLASS);
        registerState(1525, Material.STAINED_GLASS);
        registerState(1526, Material.STAINED_GLASS);
        registerState(1527, Material.STAINED_GLASS);
        registerState(1528, Material.STAINED_GLASS);
        registerState(1529, Material.STAINED_GLASS);
        registerState(1530, Material.STAINED_GLASS);
        registerState(1531, Material.STAINED_GLASS);
        registerState(1532, Material.STAINED_GLASS);
        registerState(1533, Material.STAINED_GLASS);
        registerState(1534, Material.STAINED_GLASS);
        registerState(1535, Material.STAINED_GLASS);
        registerState(1536, Material.TRAP_DOOR).facing(EnumFacing.SOUTH).half(TrapdoorPosition.BOTTOM).height(0.3D); // complete
        registerState(1537, Material.TRAP_DOOR).facing(EnumFacing.NORTH).half(TrapdoorPosition.BOTTOM).height(0.3D); // complete
        registerState(1538, Material.TRAP_DOOR).facing(EnumFacing.EAST).half(TrapdoorPosition.BOTTOM).height(0.3D); // complete
        registerState(1539, Material.TRAP_DOOR).facing(EnumFacing.WEST).half(TrapdoorPosition.BOTTOM).height(0.3D); // complete
        registerState(1540, Material.TRAP_DOOR).facing(EnumFacing.SOUTH).half(TrapdoorPosition.BOTTOM).open().thick(0.3D); // complete
        registerState(1541, Material.TRAP_DOOR).facing(EnumFacing.NORTH).half(TrapdoorPosition.BOTTOM).open().thick(0.3D); // complete
        registerState(1542, Material.TRAP_DOOR).facing(EnumFacing.EAST).half(TrapdoorPosition.BOTTOM).open().thick(0.3D); // complete
        registerState(1543, Material.TRAP_DOOR).facing(EnumFacing.WEST).half(TrapdoorPosition.BOTTOM).open().thick(0.3D); // complete
        registerState(1544, Material.TRAP_DOOR).facing(EnumFacing.SOUTH).half(TrapdoorPosition.TOP).height(0.3D); // complete
        registerState(1545, Material.TRAP_DOOR).facing(EnumFacing.NORTH).half(TrapdoorPosition.TOP).height(0.3D); // complete
        registerState(1546, Material.TRAP_DOOR).facing(EnumFacing.EAST).half(TrapdoorPosition.TOP).height(0.3D); // complete
        registerState(1547, Material.TRAP_DOOR).facing(EnumFacing.WEST).half(TrapdoorPosition.TOP).height(0.3D); // complete
        registerState(1548, Material.TRAP_DOOR).facing(EnumFacing.SOUTH).half(TrapdoorPosition.TOP).open().thick(0.3D); // complete
        registerState(1549, Material.TRAP_DOOR).facing(EnumFacing.NORTH).half(TrapdoorPosition.TOP).open().thick(0.3D); // complete
        registerState(1550, Material.TRAP_DOOR).facing(EnumFacing.EAST).half(TrapdoorPosition.TOP).open().thick(0.3D); // complete
        registerState(1551, Material.TRAP_DOOR).facing(EnumFacing.WEST).half(TrapdoorPosition.TOP).open().thick(0.3D); // complete
        registerState(1552, Material.MONSTER_EGGS);
        registerState(1553, Material.MONSTER_EGGS);
        registerState(1554, Material.MONSTER_EGGS);
        registerState(1555, Material.MONSTER_EGGS);
        registerState(1556, Material.MONSTER_EGGS);
        registerState(1557, Material.MONSTER_EGGS);
        registerState(1568, Material.SMOOTH_BRICK);
        registerState(1569, Material.SMOOTH_BRICK);
        registerState(1570, Material.SMOOTH_BRICK);
        registerState(1571, Material.SMOOTH_BRICK);
        registerState(1584, Material.HUGE_MUSHROOM_1);
        registerState(1585, Material.HUGE_MUSHROOM_1);
        registerState(1586, Material.HUGE_MUSHROOM_1);
        registerState(1587, Material.HUGE_MUSHROOM_1);
        registerState(1588, Material.HUGE_MUSHROOM_1);
        registerState(1589, Material.HUGE_MUSHROOM_1);
        registerState(1590, Material.HUGE_MUSHROOM_1);
        registerState(1591, Material.HUGE_MUSHROOM_1);
        registerState(1592, Material.HUGE_MUSHROOM_1);
        registerState(1593, Material.HUGE_MUSHROOM_1);
        registerState(1594, Material.HUGE_MUSHROOM_1);
        registerState(1598, Material.HUGE_MUSHROOM_1);
        registerState(1599, Material.HUGE_MUSHROOM_1);
        registerState(1600, Material.HUGE_MUSHROOM_2);
        registerState(1601, Material.HUGE_MUSHROOM_2);
        registerState(1602, Material.HUGE_MUSHROOM_2);
        registerState(1603, Material.HUGE_MUSHROOM_2);
        registerState(1604, Material.HUGE_MUSHROOM_2);
        registerState(1605, Material.HUGE_MUSHROOM_2);
        registerState(1606, Material.HUGE_MUSHROOM_2);
        registerState(1607, Material.HUGE_MUSHROOM_2);
        registerState(1608, Material.HUGE_MUSHROOM_2);
        registerState(1609, Material.HUGE_MUSHROOM_2);
        registerState(1610, Material.HUGE_MUSHROOM_2);
        registerState(1614, Material.HUGE_MUSHROOM_2);
        registerState(1615, Material.HUGE_MUSHROOM_2);
        registerState(1616, Material.IRON_FENCE); // complete
        registerState(1632, Material.THIN_GLASS); // complete
        registerState(1648, Material.MELON_BLOCK); // complete
        registerState(1664, Material.PUMPKIN_STEM);
        registerState(1665, Material.PUMPKIN_STEM);
        registerState(1666, Material.PUMPKIN_STEM);
        registerState(1667, Material.PUMPKIN_STEM);
        registerState(1668, Material.PUMPKIN_STEM);
        registerState(1669, Material.PUMPKIN_STEM);
        registerState(1670, Material.PUMPKIN_STEM);
        registerState(1671, Material.PUMPKIN_STEM);
        registerState(1680, Material.MELON_STEM);
        registerState(1681, Material.MELON_STEM);
        registerState(1682, Material.MELON_STEM);
        registerState(1683, Material.MELON_STEM);
        registerState(1684, Material.MELON_STEM);
        registerState(1685, Material.MELON_STEM);
        registerState(1686, Material.MELON_STEM);
        registerState(1687, Material.MELON_STEM);
        registerState(1696, Material.VINE);
        registerState(1697, Material.VINE);
        registerState(1698, Material.VINE);
        registerState(1699, Material.VINE);
        registerState(1700, Material.VINE);
        registerState(1701, Material.VINE);
        registerState(1702, Material.VINE);
        registerState(1703, Material.VINE);
        registerState(1704, Material.VINE);
        registerState(1705, Material.VINE);
        registerState(1706, Material.VINE);
        registerState(1707, Material.VINE);
        registerState(1708, Material.VINE);
        registerState(1709, Material.VINE);
        registerState(1710, Material.VINE);
        registerState(1711, Material.VINE);
        registerState(1712, Material.FENCE_GATE).facing(EnumFacing.SOUTH); // complete
        registerState(1713, Material.FENCE_GATE).facing(EnumFacing.WEST); // complete
        registerState(1714, Material.FENCE_GATE).facing(EnumFacing.NORTH); // complete
        registerState(1715, Material.FENCE_GATE).facing(EnumFacing.EAST); // complete
        registerState(1716, Material.FENCE_GATE).facing(EnumFacing.SOUTH).open(); // complete
        registerState(1717, Material.FENCE_GATE).facing(EnumFacing.WEST).open(); // complete
        registerState(1718, Material.FENCE_GATE).facing(EnumFacing.NORTH).open(); // complete
        registerState(1719, Material.FENCE_GATE).facing(EnumFacing.EAST).open(); // complete
        registerState(1720, Material.FENCE_GATE).facing(EnumFacing.SOUTH).powered(); // complete
        registerState(1721, Material.FENCE_GATE).facing(EnumFacing.WEST).powered(); // complete
        registerState(1722, Material.FENCE_GATE).facing(EnumFacing.NORTH).powered(); // complete
        registerState(1723, Material.FENCE_GATE).facing(EnumFacing.EAST).powered(); // complete
        registerState(1724, Material.FENCE_GATE).facing(EnumFacing.SOUTH).open().powered(); // complete
        registerState(1725, Material.FENCE_GATE).facing(EnumFacing.WEST).open().powered(); // complete
        registerState(1726, Material.FENCE_GATE).facing(EnumFacing.NORTH).open().powered(); // complete
        registerState(1727, Material.FENCE_GATE).facing(EnumFacing.EAST).open().powered(); // complete
        registerState(1728, Material.BRICK_STAIRS);
        registerState(1729, Material.BRICK_STAIRS);
        registerState(1730, Material.BRICK_STAIRS);
        registerState(1731, Material.BRICK_STAIRS);
        registerState(1732, Material.BRICK_STAIRS);
        registerState(1733, Material.BRICK_STAIRS);
        registerState(1734, Material.BRICK_STAIRS);
        registerState(1735, Material.BRICK_STAIRS);
        registerState(1744, Material.SMOOTH_STAIRS);
        registerState(1745, Material.SMOOTH_STAIRS);
        registerState(1746, Material.SMOOTH_STAIRS);
        registerState(1747, Material.SMOOTH_STAIRS);
        registerState(1748, Material.SMOOTH_STAIRS);
        registerState(1749, Material.SMOOTH_STAIRS);
        registerState(1750, Material.SMOOTH_STAIRS);
        registerState(1751, Material.SMOOTH_STAIRS);
        registerState(1760, Material.MYCEL); // complete
        registerState(1776, Material.WATER_LILY); // complete
        registerState(1792, Material.NETHER_BRICK); // complete
        registerState(1808, Material.NETHER_FENCE); // complete
        registerState(1824, Material.NETHER_BRICK_STAIRS);
        registerState(1825, Material.NETHER_BRICK_STAIRS);
        registerState(1826, Material.NETHER_BRICK_STAIRS);
        registerState(1827, Material.NETHER_BRICK_STAIRS);
        registerState(1828, Material.NETHER_BRICK_STAIRS);
        registerState(1829, Material.NETHER_BRICK_STAIRS);
        registerState(1830, Material.NETHER_BRICK_STAIRS);
        registerState(1831, Material.NETHER_BRICK_STAIRS);
        registerState(1840, Material.NETHER_WARTS);
        registerState(1841, Material.NETHER_WARTS);
        registerState(1842, Material.NETHER_WARTS);
        registerState(1843, Material.NETHER_WARTS);
        registerState(1856, Material.ENCHANTMENT_TABLE); // complete
        registerState(1872, Material.BREWING_STAND);
        registerState(1873, Material.BREWING_STAND);
        registerState(1874, Material.BREWING_STAND);
        registerState(1875, Material.BREWING_STAND);
        registerState(1876, Material.BREWING_STAND);
        registerState(1877, Material.BREWING_STAND);
        registerState(1878, Material.BREWING_STAND);
        registerState(1879, Material.BREWING_STAND);
        registerState(1888, Material.CAULDRON);
        registerState(1889, Material.CAULDRON);
        registerState(1890, Material.CAULDRON);
        registerState(1891, Material.CAULDRON);
        registerState(1904, Material.ENDER_PORTAL); // complete
        registerState(1920, Material.ENDER_PORTAL_FRAME);
        registerState(1921, Material.ENDER_PORTAL_FRAME);
        registerState(1922, Material.ENDER_PORTAL_FRAME);
        registerState(1923, Material.ENDER_PORTAL_FRAME);
        registerState(1924, Material.ENDER_PORTAL_FRAME);
        registerState(1925, Material.ENDER_PORTAL_FRAME);
        registerState(1926, Material.ENDER_PORTAL_FRAME);
        registerState(1927, Material.ENDER_PORTAL_FRAME);
        registerState(1936, Material.ENDER_STONE); // complete
        registerState(1952, Material.DRAGON_EGG); // complete
        registerState(1968, Material.REDSTONE_LAMP_OFF); // complete
        registerState(1984, Material.REDSTONE_LAMP_ON); // complete
        registerState(2000, Material.WOOD_DOUBLE_STEP);
        registerState(2001, Material.WOOD_DOUBLE_STEP);
        registerState(2002, Material.WOOD_DOUBLE_STEP);
        registerState(2003, Material.WOOD_DOUBLE_STEP);
        registerState(2004, Material.WOOD_DOUBLE_STEP);
        registerState(2005, Material.WOOD_DOUBLE_STEP);
        registerState(2016, Material.WOOD_DOUBLE_STEP);
        registerState(2017, Material.WOOD_DOUBLE_STEP);
        registerState(2018, Material.WOOD_DOUBLE_STEP);
        registerState(2019, Material.WOOD_STEP);
        registerState(2020, Material.WOOD_STEP);
        registerState(2021, Material.WOOD_STEP);
        registerState(2024, Material.WOOD_STEP);
        registerState(2025, Material.WOOD_STEP);
        registerState(2026, Material.WOOD_STEP);
        registerState(2027, Material.WOOD_STEP);
        registerState(2028, Material.WOOD_STEP);
        registerState(2029, Material.WOOD_STEP);
        registerState(2032, Material.COCOA);
        registerState(2033, Material.COCOA);
        registerState(2034, Material.COCOA);
        registerState(2035, Material.COCOA);
        registerState(2036, Material.COCOA);
        registerState(2037, Material.COCOA);
        registerState(2038, Material.COCOA);
        registerState(2039, Material.COCOA);
        registerState(2040, Material.COCOA);
        registerState(2041, Material.COCOA);
        registerState(2042, Material.COCOA);
        registerState(2043, Material.COCOA);
        registerState(2048, Material.SANDSTONE_STAIRS);
        registerState(2049, Material.SANDSTONE_STAIRS);
        registerState(2050, Material.SANDSTONE_STAIRS);
        registerState(2051, Material.SANDSTONE_STAIRS);
        registerState(2052, Material.SANDSTONE_STAIRS);
        registerState(2053, Material.SANDSTONE_STAIRS);
        registerState(2054, Material.SANDSTONE_STAIRS);
        registerState(2055, Material.SANDSTONE_STAIRS);
        registerState(2064, Material.EMERALD_ORE);
        registerState(2082, Material.ENDER_CHEST);
        registerState(2083, Material.ENDER_CHEST);
        registerState(2084, Material.ENDER_CHEST);
        registerState(2085, Material.ENDER_CHEST);
        registerState(2096, Material.TRIPWIRE_HOOK);
        registerState(2097, Material.TRIPWIRE_HOOK);
        registerState(2098, Material.TRIPWIRE_HOOK);
        registerState(2099, Material.TRIPWIRE_HOOK);
        registerState(2100, Material.TRIPWIRE_HOOK);
        registerState(2101, Material.TRIPWIRE_HOOK);
        registerState(2102, Material.TRIPWIRE_HOOK);
        registerState(2103, Material.TRIPWIRE_HOOK);
        registerState(2104, Material.TRIPWIRE_HOOK);
        registerState(2105, Material.TRIPWIRE_HOOK);
        registerState(2106, Material.TRIPWIRE_HOOK);
        registerState(2107, Material.TRIPWIRE_HOOK);
        registerState(2108, Material.TRIPWIRE_HOOK);
        registerState(2109, Material.TRIPWIRE_HOOK);
        registerState(2110, Material.TRIPWIRE_HOOK);
        registerState(2111, Material.TRIPWIRE_HOOK);
        registerState(2112, Material.TRIPWIRE);
        registerState(2113, Material.TRIPWIRE);
        registerState(2114, Material.TRIPWIRE);
        registerState(2115, Material.TRIPWIRE);
        registerState(2116, Material.TRIPWIRE);
        registerState(2117, Material.TRIPWIRE);
        registerState(2118, Material.TRIPWIRE);
        registerState(2119, Material.TRIPWIRE);
        registerState(2120, Material.TRIPWIRE);
        registerState(2121, Material.TRIPWIRE);
        registerState(2122, Material.TRIPWIRE);
        registerState(2123, Material.TRIPWIRE);
        registerState(2124, Material.TRIPWIRE);
        registerState(2125, Material.TRIPWIRE);
        registerState(2126, Material.TRIPWIRE);
        registerState(2127, Material.TRIPWIRE);
        registerState(2128, Material.EMERALD_BLOCK); // complete
        registerState(2144, Material.SPRUCE_WOOD_STAIRS);
        registerState(2145, Material.SPRUCE_WOOD_STAIRS);
        registerState(2146, Material.SPRUCE_WOOD_STAIRS);
        registerState(2147, Material.SPRUCE_WOOD_STAIRS);
        registerState(2148, Material.SPRUCE_WOOD_STAIRS);
        registerState(2149, Material.SPRUCE_WOOD_STAIRS);
        registerState(2150, Material.SPRUCE_WOOD_STAIRS);
        registerState(2151, Material.SPRUCE_WOOD_STAIRS);
        registerState(2160, Material.BIRCH_WOOD_STAIRS);
        registerState(2161, Material.BIRCH_WOOD_STAIRS);
        registerState(2162, Material.BIRCH_WOOD_STAIRS);
        registerState(2163, Material.BIRCH_WOOD_STAIRS);
        registerState(2164, Material.BIRCH_WOOD_STAIRS);
        registerState(2165, Material.BIRCH_WOOD_STAIRS);
        registerState(2166, Material.BIRCH_WOOD_STAIRS);
        registerState(2167, Material.BIRCH_WOOD_STAIRS);
        registerState(2176, Material.JUNGLE_WOOD_STAIRS);
        registerState(2177, Material.JUNGLE_WOOD_STAIRS);
        registerState(2178, Material.JUNGLE_WOOD_STAIRS);
        registerState(2179, Material.JUNGLE_WOOD_STAIRS);
        registerState(2180, Material.JUNGLE_WOOD_STAIRS);
        registerState(2181, Material.JUNGLE_WOOD_STAIRS);
        registerState(2182, Material.JUNGLE_WOOD_STAIRS);
        registerState(2183, Material.JUNGLE_WOOD_STAIRS);
        registerState(2192, Material.COMMAND);
        registerState(2193, Material.COMMAND);
        registerState(2208, Material.BEACON); // complete
        registerState(2224, Material.COBBLE_WALL);
        registerState(2225, Material.COBBLE_WALL);
        registerState(2240, Material.FLOWER_POT);
        registerState(2241, Material.FLOWER_POT);
        registerState(2242, Material.FLOWER_POT);
        registerState(2243, Material.FLOWER_POT);
        registerState(2244, Material.FLOWER_POT);
        registerState(2245, Material.FLOWER_POT);
        registerState(2246, Material.FLOWER_POT);
        registerState(2247, Material.FLOWER_POT);
        registerState(2248, Material.FLOWER_POT);
        registerState(2249, Material.FLOWER_POT);
        registerState(2250, Material.FLOWER_POT);
        registerState(2251, Material.FLOWER_POT);
        registerState(2252, Material.FLOWER_POT);
        registerState(2253, Material.FLOWER_POT);
        registerState(2254, Material.FLOWER_POT);
        registerState(2255, Material.FLOWER_POT);
        registerState(2256, Material.CARROT);
        registerState(2257, Material.CARROT);
        registerState(2258, Material.CARROT);
        registerState(2259, Material.CARROT);
        registerState(2260, Material.CARROT);
        registerState(2261, Material.CARROT);
        registerState(2262, Material.CARROT);
        registerState(2263, Material.CARROT);
        registerState(2272, Material.POTATO);
        registerState(2273, Material.POTATO);
        registerState(2274, Material.POTATO);
        registerState(2275, Material.POTATO);
        registerState(2276, Material.POTATO);
        registerState(2277, Material.POTATO);
        registerState(2278, Material.POTATO);
        registerState(2279, Material.POTATO);
        registerState(2288, Material.WOOD_BUTTON);
        registerState(2289, Material.WOOD_BUTTON);
        registerState(2290, Material.WOOD_BUTTON);
        registerState(2291, Material.WOOD_BUTTON);
        registerState(2292, Material.WOOD_BUTTON);
        registerState(2293, Material.WOOD_BUTTON);
        registerState(2296, Material.WOOD_BUTTON);
        registerState(2297, Material.WOOD_BUTTON);
        registerState(2298, Material.WOOD_BUTTON);
        registerState(2299, Material.WOOD_BUTTON);
        registerState(2300, Material.WOOD_BUTTON);
        registerState(2301, Material.WOOD_BUTTON);
        registerState(2304, Material.SKULL);
        registerState(2305, Material.SKULL);
        registerState(2306, Material.SKULL);
        registerState(2307, Material.SKULL);
        registerState(2308, Material.SKULL);
        registerState(2309, Material.SKULL);
        registerState(2312, Material.SKULL);
        registerState(2313, Material.SKULL);
        registerState(2314, Material.SKULL);
        registerState(2315, Material.SKULL);
        registerState(2316, Material.SKULL);
        registerState(2317, Material.SKULL);
        registerState(2320, Material.ANVIL);
        registerState(2321, Material.ANVIL);
        registerState(2322, Material.ANVIL);
        registerState(2323, Material.ANVIL);
        registerState(2324, Material.ANVIL);
        registerState(2325, Material.ANVIL);
        registerState(2326, Material.ANVIL);
        registerState(2327, Material.ANVIL);
        registerState(2328, Material.ANVIL);
        registerState(2329, Material.ANVIL);
        registerState(2330, Material.ANVIL);
        registerState(2331, Material.ANVIL);
        registerState(2338, Material.TRAPPED_CHEST);
        registerState(2339, Material.TRAPPED_CHEST);
        registerState(2340, Material.TRAPPED_CHEST);
        registerState(2341, Material.TRAPPED_CHEST);
        registerState(2352, Material.GOLD_PLATE);
        registerState(2353, Material.GOLD_PLATE);
        registerState(2354, Material.GOLD_PLATE);
        registerState(2355, Material.GOLD_PLATE);
        registerState(2356, Material.GOLD_PLATE);
        registerState(2357, Material.GOLD_PLATE);
        registerState(2358, Material.GOLD_PLATE);
        registerState(2359, Material.GOLD_PLATE);
        registerState(2360, Material.GOLD_PLATE);
        registerState(2361, Material.GOLD_PLATE);
        registerState(2362, Material.GOLD_PLATE);
        registerState(2363, Material.GOLD_PLATE);
        registerState(2364, Material.GOLD_PLATE);
        registerState(2365, Material.GOLD_PLATE);
        registerState(2366, Material.GOLD_PLATE);
        registerState(2367, Material.GOLD_PLATE);
        registerState(2368, Material.IRON_PLATE);
        registerState(2369, Material.IRON_PLATE);
        registerState(2370, Material.IRON_PLATE);
        registerState(2371, Material.IRON_PLATE);
        registerState(2372, Material.IRON_PLATE);
        registerState(2373, Material.IRON_PLATE);
        registerState(2374, Material.IRON_PLATE);
        registerState(2375, Material.IRON_PLATE);
        registerState(2376, Material.IRON_PLATE);
        registerState(2377, Material.IRON_PLATE);
        registerState(2378, Material.IRON_PLATE);
        registerState(2379, Material.IRON_PLATE);
        registerState(2380, Material.IRON_PLATE);
        registerState(2381, Material.IRON_PLATE);
        registerState(2382, Material.IRON_PLATE);
        registerState(2383, Material.IRON_PLATE);
        registerState(2384, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2385, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2386, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2387, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2388, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2389, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2390, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2391, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2392, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2393, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2394, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2395, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2396, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2397, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2398, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2399, Material.REDSTONE_COMPARATOR_OFF);
        registerState(2400, Material.REDSTONE_COMPARATOR_ON);
        registerState(2401, Material.REDSTONE_COMPARATOR_ON);
        registerState(2402, Material.REDSTONE_COMPARATOR_ON);
        registerState(2403, Material.REDSTONE_COMPARATOR_ON);
        registerState(2404, Material.REDSTONE_COMPARATOR_ON);
        registerState(2405, Material.REDSTONE_COMPARATOR_ON);
        registerState(2406, Material.REDSTONE_COMPARATOR_ON);
        registerState(2407, Material.REDSTONE_COMPARATOR_ON);
        registerState(2408, Material.REDSTONE_COMPARATOR_ON);
        registerState(2409, Material.REDSTONE_COMPARATOR_ON);
        registerState(2410, Material.REDSTONE_COMPARATOR_ON);
        registerState(2411, Material.REDSTONE_COMPARATOR_ON);
        registerState(2412, Material.REDSTONE_COMPARATOR_ON);
        registerState(2413, Material.REDSTONE_COMPARATOR_ON);
        registerState(2414, Material.REDSTONE_COMPARATOR_ON);
        registerState(2415, Material.REDSTONE_COMPARATOR_ON);
        registerState(2416, Material.DAYLIGHT_DETECTOR);
        registerState(2417, Material.DAYLIGHT_DETECTOR);
        registerState(2418, Material.DAYLIGHT_DETECTOR);
        registerState(2419, Material.DAYLIGHT_DETECTOR);
        registerState(2420, Material.DAYLIGHT_DETECTOR);
        registerState(2421, Material.DAYLIGHT_DETECTOR);
        registerState(2422, Material.DAYLIGHT_DETECTOR);
        registerState(2423, Material.DAYLIGHT_DETECTOR);
        registerState(2424, Material.DAYLIGHT_DETECTOR);
        registerState(2425, Material.DAYLIGHT_DETECTOR);
        registerState(2426, Material.DAYLIGHT_DETECTOR);
        registerState(2427, Material.DAYLIGHT_DETECTOR);
        registerState(2428, Material.DAYLIGHT_DETECTOR);
        registerState(2429, Material.DAYLIGHT_DETECTOR);
        registerState(2430, Material.DAYLIGHT_DETECTOR);
        registerState(2431, Material.DAYLIGHT_DETECTOR);
        registerState(2432, Material.REDSTONE_BLOCK);
        registerState(2448, Material.QUARTZ_ORE); // complete
        registerState(2464, Material.HOPPER);
        registerState(2466, Material.HOPPER);
        registerState(2467, Material.HOPPER);
        registerState(2468, Material.HOPPER);
        registerState(2469, Material.HOPPER);
        registerState(2472, Material.HOPPER);
        registerState(2474, Material.HOPPER);
        registerState(2475, Material.HOPPER);
        registerState(2476, Material.HOPPER);
        registerState(2477, Material.HOPPER);
        registerState(2480, Material.QUARTZ_BLOCK);
        registerState(2481, Material.QUARTZ_BLOCK);
        registerState(2482, Material.QUARTZ_BLOCK);
        registerState(2483, Material.QUARTZ_BLOCK);
        registerState(2484, Material.QUARTZ_BLOCK);
        registerState(2496, Material.QUARTZ_STAIRS);
        registerState(2497, Material.QUARTZ_STAIRS);
        registerState(2498, Material.QUARTZ_STAIRS);
        registerState(2499, Material.QUARTZ_STAIRS);
        registerState(2500, Material.QUARTZ_STAIRS);
        registerState(2501, Material.QUARTZ_STAIRS);
        registerState(2502, Material.QUARTZ_STAIRS);
        registerState(2503, Material.QUARTZ_STAIRS);
        registerState(2512, Material.ACTIVATOR_RAIL);
        registerState(2513, Material.ACTIVATOR_RAIL);
        registerState(2514, Material.ACTIVATOR_RAIL);
        registerState(2515, Material.ACTIVATOR_RAIL);
        registerState(2516, Material.ACTIVATOR_RAIL);
        registerState(2517, Material.ACTIVATOR_RAIL);
        registerState(2520, Material.ACTIVATOR_RAIL);
        registerState(2521, Material.ACTIVATOR_RAIL);
        registerState(2522, Material.ACTIVATOR_RAIL);
        registerState(2523, Material.ACTIVATOR_RAIL);
        registerState(2524, Material.ACTIVATOR_RAIL);
        registerState(2525, Material.ACTIVATOR_RAIL);
        registerState(2528, Material.DROPPER);
        registerState(2529, Material.DROPPER);
        registerState(2530, Material.DROPPER);
        registerState(2531, Material.DROPPER);
        registerState(2532, Material.DROPPER);
        registerState(2533, Material.DROPPER);
        registerState(2536, Material.DROPPER);
        registerState(2537, Material.DROPPER);
        registerState(2538, Material.DROPPER);
        registerState(2539, Material.DROPPER);
        registerState(2540, Material.DROPPER);
        registerState(2541, Material.DROPPER);
        registerState(2544, Material.STAINED_CLAY);
        registerState(2545, Material.STAINED_CLAY);
        registerState(2546, Material.STAINED_CLAY);
        registerState(2547, Material.STAINED_CLAY);
        registerState(2548, Material.STAINED_CLAY);
        registerState(2549, Material.STAINED_CLAY);
        registerState(2550, Material.STAINED_CLAY);
        registerState(2551, Material.STAINED_CLAY);
        registerState(2552, Material.STAINED_CLAY);
        registerState(2553, Material.STAINED_CLAY);
        registerState(2554, Material.STAINED_CLAY);
        registerState(2555, Material.STAINED_CLAY);
        registerState(2556, Material.STAINED_CLAY);
        registerState(2557, Material.STAINED_CLAY);
        registerState(2558, Material.STAINED_CLAY);
        registerState(2559, Material.STAINED_CLAY);
        registerState(2560, Material.STAINED_GLASS_PANE);
        registerState(2561, Material.STAINED_GLASS_PANE);
        registerState(2562, Material.STAINED_GLASS_PANE);
        registerState(2563, Material.STAINED_GLASS_PANE);
        registerState(2564, Material.STAINED_GLASS_PANE);
        registerState(2565, Material.STAINED_GLASS_PANE);
        registerState(2566, Material.STAINED_GLASS_PANE);
        registerState(2567, Material.STAINED_GLASS_PANE);
        registerState(2568, Material.STAINED_GLASS_PANE);
        registerState(2569, Material.STAINED_GLASS_PANE);
        registerState(2570, Material.STAINED_GLASS_PANE);
        registerState(2571, Material.STAINED_GLASS_PANE);
        registerState(2572, Material.STAINED_GLASS_PANE);
        registerState(2573, Material.STAINED_GLASS_PANE);
        registerState(2574, Material.STAINED_GLASS_PANE);
        registerState(2575, Material.STAINED_GLASS_PANE);
        registerState(2576, Material.LEAVES);
        registerState(2577, Material.LEAVES);
        registerState(2580, Material.LEAVES);
        registerState(2581, Material.LEAVES);
        registerState(2584, Material.LEAVES);
        registerState(2585, Material.LEAVES);
        registerState(2588, Material.LEAVES);
        registerState(2589, Material.LEAVES);
        registerState(2592, Material.LOG);
        registerState(2593, Material.LOG);
        registerState(2596, Material.LOG);
        registerState(2597, Material.LOG);
        registerState(2600, Material.LOG);
        registerState(2601, Material.LOG);
        registerState(2604, Material.LOG);
        registerState(2605, Material.LOG);
        registerState(2608, Material.ACACIA_STAIRS);
        registerState(2609, Material.ACACIA_STAIRS);
        registerState(2610, Material.ACACIA_STAIRS);
        registerState(2611, Material.ACACIA_STAIRS);
        registerState(2612, Material.ACACIA_STAIRS);
        registerState(2613, Material.ACACIA_STAIRS);
        registerState(2614, Material.ACACIA_STAIRS);
        registerState(2615, Material.ACACIA_STAIRS);
        registerState(2624, Material.DARK_OAK_STAIRS);
        registerState(2625, Material.DARK_OAK_STAIRS);
        registerState(2626, Material.DARK_OAK_STAIRS);
        registerState(2627, Material.DARK_OAK_STAIRS);
        registerState(2628, Material.DARK_OAK_STAIRS);
        registerState(2629, Material.DARK_OAK_STAIRS);
        registerState(2630, Material.DARK_OAK_STAIRS);
        registerState(2631, Material.DARK_OAK_STAIRS);
        registerState(2640, Material.SLIME_BLOCK); // complete
        registerState(2656, Material.BARRIER); // complete
        registerState(2672, Material.IRON_TRAPDOOR);
        registerState(2673, Material.IRON_TRAPDOOR);
        registerState(2674, Material.IRON_TRAPDOOR);
        registerState(2675, Material.IRON_TRAPDOOR);
        registerState(2676, Material.IRON_TRAPDOOR);
        registerState(2677, Material.IRON_TRAPDOOR);
        registerState(2678, Material.IRON_TRAPDOOR);
        registerState(2679, Material.IRON_TRAPDOOR);
        registerState(2680, Material.IRON_TRAPDOOR);
        registerState(2681, Material.IRON_TRAPDOOR);
        registerState(2682, Material.IRON_TRAPDOOR);
        registerState(2683, Material.IRON_TRAPDOOR);
        registerState(2684, Material.IRON_TRAPDOOR);
        registerState(2685, Material.IRON_TRAPDOOR);
        registerState(2686, Material.IRON_TRAPDOOR);
        registerState(2687, Material.IRON_TRAPDOOR);
        registerState(2688, Material.PRISMARINE);
        registerState(2689, Material.PRISMARINE);
        registerState(2690, Material.PRISMARINE);
        registerState(2704, Material.SEA_LANTERN); // complete
        registerState(2720, Material.HAY_BLOCK);
        registerState(2724, Material.HAY_BLOCK);
        registerState(2728, Material.HAY_BLOCK);
        registerState(2736, Material.CARPET);
        registerState(2737, Material.CARPET);
        registerState(2738, Material.CARPET);
        registerState(2739, Material.CARPET);
        registerState(2740, Material.CARPET);
        registerState(2741, Material.CARPET);
        registerState(2742, Material.CARPET);
        registerState(2743, Material.CARPET);
        registerState(2744, Material.CARPET);
        registerState(2745, Material.CARPET);
        registerState(2746, Material.CARPET);
        registerState(2747, Material.CARPET);
        registerState(2748, Material.CARPET);
        registerState(2749, Material.CARPET);
        registerState(2750, Material.CARPET);
        registerState(2751, Material.CARPET);
        registerState(2752, Material.HARD_CLAY); // complete
        registerState(2768, Material.COAL_BLOCK); // complete
        registerState(2784, Material.PACKED_ICE); // complete
        registerState(2800, Material.DOUBLE_PLANT);
        registerState(2801, Material.DOUBLE_PLANT);
        registerState(2802, Material.DOUBLE_PLANT);
        registerState(2803, Material.DOUBLE_PLANT);
        registerState(2804, Material.DOUBLE_PLANT);
        registerState(2805, Material.DOUBLE_PLANT);
        registerState(2808, Material.DOUBLE_PLANT);
        registerState(2809, Material.DOUBLE_PLANT);
        registerState(2810, Material.DOUBLE_PLANT);
        registerState(2811, Material.DOUBLE_PLANT);
        registerState(2816, Material.STANDING_BANNER);
        registerState(2817, Material.STANDING_BANNER);
        registerState(2818, Material.STANDING_BANNER);
        registerState(2819, Material.STANDING_BANNER);
        registerState(2820, Material.STANDING_BANNER);
        registerState(2821, Material.STANDING_BANNER);
        registerState(2822, Material.STANDING_BANNER);
        registerState(2823, Material.STANDING_BANNER);
        registerState(2824, Material.STANDING_BANNER);
        registerState(2825, Material.STANDING_BANNER);
        registerState(2826, Material.WALL_BANNER);
        registerState(2827, Material.WALL_BANNER);
        registerState(2828, Material.WALL_BANNER);
        registerState(2829, Material.WALL_BANNER);
        registerState(2830, Material.WALL_BANNER);
        registerState(2831, Material.WALL_BANNER);
        registerState(2834, Material.WALL_BANNER);
        registerState(2835, Material.WALL_BANNER);
        registerState(2836, Material.WALL_BANNER);
        registerState(2837, Material.WALL_BANNER);
        registerState(2848, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2849, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2850, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2851, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2852, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2853, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2854, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2855, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2856, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2857, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2858, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2859, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2860, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2861, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2862, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2863, Material.DAYLIGHT_DETECTOR_INVERTED);
        registerState(2864, Material.RED_SANDSTONE);
        registerState(2865, Material.RED_SANDSTONE);
        registerState(2866, Material.RED_SANDSTONE);
        registerState(2880, Material.RED_SANDSTONE_STAIRS);
        registerState(2881, Material.RED_SANDSTONE_STAIRS);
        registerState(2882, Material.RED_SANDSTONE_STAIRS);
        registerState(2883, Material.RED_SANDSTONE_STAIRS);
        registerState(2884, Material.RED_SANDSTONE_STAIRS);
        registerState(2885, Material.RED_SANDSTONE_STAIRS);
        registerState(2886, Material.RED_SANDSTONE_STAIRS);
        registerState(2887, Material.RED_SANDSTONE_STAIRS);
        registerState(2896, Material.DOUBLE_STONE_SLAB2);
        registerState(2904, Material.DOUBLE_STONE_SLAB2);
        registerState(2912, Material.STONE_SLAB2);
        registerState(2920, Material.STONE_SLAB2);
        registerState(2928, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.SOUTH); // complete
        registerState(2929, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.WEST); // complete
        registerState(2930, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.NORTH); // complete
        registerState(2931, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.EAST); // complete
        registerState(2932, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.SOUTH).open(); // complete
        registerState(2933, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.WEST).open(); // complete
        registerState(2934, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.NORTH).open(); // complete
        registerState(2935, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.EAST).open(); // complete
        registerState(2936, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.SOUTH).powered(); // complete
        registerState(2937, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.WEST).powered(); // complete
        registerState(2938, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.NORTH).powered(); // complete
        registerState(2939, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.EAST).powered(); // complete
        registerState(2940, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.SOUTH).open().powered(); // complete
        registerState(2941, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.WEST).open().powered(); // complete
        registerState(2942, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.NORTH).open().powered(); // complete
        registerState(2943, Material.SPRUCE_FENCE_GATE).facing(EnumFacing.EAST).open().powered(); // complete
        registerState(2944, Material.BIRCH_FENCE_GATE).facing(EnumFacing.SOUTH); // complete
        registerState(2945, Material.BIRCH_FENCE_GATE).facing(EnumFacing.WEST); // complete
        registerState(2946, Material.BIRCH_FENCE_GATE).facing(EnumFacing.NORTH); // complete
        registerState(2947, Material.BIRCH_FENCE_GATE).facing(EnumFacing.EAST); // complete
        registerState(2948, Material.BIRCH_FENCE_GATE).facing(EnumFacing.SOUTH).open(); // complete
        registerState(2949, Material.BIRCH_FENCE_GATE).facing(EnumFacing.WEST).open(); // complete
        registerState(2950, Material.BIRCH_FENCE_GATE).facing(EnumFacing.NORTH).open(); // complete
        registerState(2951, Material.BIRCH_FENCE_GATE).facing(EnumFacing.EAST).open(); // complete
        registerState(2952, Material.BIRCH_FENCE_GATE).facing(EnumFacing.SOUTH).powered(); // complete
        registerState(2953, Material.BIRCH_FENCE_GATE).facing(EnumFacing.WEST).powered(); // complete
        registerState(2954, Material.BIRCH_FENCE_GATE).facing(EnumFacing.NORTH).powered(); // complete
        registerState(2955, Material.BIRCH_FENCE_GATE).facing(EnumFacing.EAST).powered(); // complete
        registerState(2956, Material.BIRCH_FENCE_GATE).facing(EnumFacing.SOUTH).open().powered(); // complete
        registerState(2957, Material.BIRCH_FENCE_GATE).facing(EnumFacing.WEST).open().powered(); // complete
        registerState(2958, Material.BIRCH_FENCE_GATE).facing(EnumFacing.NORTH).open().powered(); // complete
        registerState(2959, Material.BIRCH_FENCE_GATE).facing(EnumFacing.EAST).open().powered(); // complete
        registerState(2960, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.SOUTH); // complete
        registerState(2961, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.WEST); // complete
        registerState(2962, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.NORTH); // complete
        registerState(2963, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.EAST); // complete
        registerState(2964, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.SOUTH).open(); // complete
        registerState(2965, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.WEST).open(); // complete
        registerState(2966, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.NORTH).open(); // complete
        registerState(2967, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.EAST).open(); // complete
        registerState(2968, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.SOUTH).powered(); // complete
        registerState(2969, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.WEST).powered(); // complete
        registerState(2970, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.NORTH).powered(); // complete
        registerState(2971, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.EAST).powered(); // complete
        registerState(2972, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.SOUTH).open().powered(); // complete
        registerState(2973, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.WEST).open().powered(); // complete
        registerState(2974, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.NORTH).open().powered(); // complete
        registerState(2975, Material.JUNGLE_FENCE_GATE).facing(EnumFacing.EAST).open().powered(); // complete
        registerState(2976, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.SOUTH); // complete
        registerState(2977, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.WEST); // complete
        registerState(2978, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.NORTH); // complete
        registerState(2979, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.EAST); // complete
        registerState(2980, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.SOUTH).open(); // complete
        registerState(2981, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.WEST).open(); // complete
        registerState(2982, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.NORTH).open(); // complete
        registerState(2983, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.EAST).open(); // complete
        registerState(2984, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.SOUTH).powered(); // complete
        registerState(2985, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.WEST).powered(); // complete
        registerState(2986, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.NORTH).powered(); // complete
        registerState(2987, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.EAST).powered(); // complete
        registerState(2988, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.SOUTH).open().powered(); // complete
        registerState(2989, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.WEST).open().powered(); // complete
        registerState(2990, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.NORTH).open().powered(); // complete
        registerState(2991, Material.DARK_OAK_FENCE_GATE).facing(EnumFacing.EAST).open().powered(); // complete
        registerState(2992, Material.ACACIA_FENCE_GATE).facing(EnumFacing.SOUTH); // complete
        registerState(2993, Material.ACACIA_FENCE_GATE).facing(EnumFacing.WEST); // complete
        registerState(2994, Material.ACACIA_FENCE_GATE).facing(EnumFacing.NORTH); // complete
        registerState(2995, Material.ACACIA_FENCE_GATE).facing(EnumFacing.EAST); // complete
        registerState(2996, Material.ACACIA_FENCE_GATE).facing(EnumFacing.SOUTH).open(); // complete
        registerState(2997, Material.ACACIA_FENCE_GATE).facing(EnumFacing.WEST).open(); // complete
        registerState(2998, Material.ACACIA_FENCE_GATE).facing(EnumFacing.NORTH).open(); // complete
        registerState(2999, Material.ACACIA_FENCE_GATE).facing(EnumFacing.EAST).open(); // complete
        registerState(3000, Material.ACACIA_FENCE_GATE).facing(EnumFacing.SOUTH).powered(); // complete
        registerState(3001, Material.ACACIA_FENCE_GATE).facing(EnumFacing.WEST).powered(); // complete
        registerState(3002, Material.ACACIA_FENCE_GATE).facing(EnumFacing.NORTH).powered(); // complete
        registerState(3003, Material.ACACIA_FENCE_GATE).facing(EnumFacing.EAST).powered(); // complete
        registerState(3004, Material.ACACIA_FENCE_GATE).facing(EnumFacing.SOUTH).open().powered(); // complete
        registerState(3005, Material.ACACIA_FENCE_GATE).facing(EnumFacing.WEST).open().powered(); // complete
        registerState(3006, Material.ACACIA_FENCE_GATE).facing(EnumFacing.NORTH).open().powered(); // complete
        registerState(3007, Material.ACACIA_FENCE_GATE).facing(EnumFacing.EAST).open().powered(); // complete
        registerState(3008, Material.SPRUCE_FENCE); // complete
        registerState(3024, Material.BIRCH_FENCE); // complete
        registerState(3040, Material.JUNGLE_FENCE); // complete
        registerState(3056, Material.DARK_OAK_FENCE); // complete
        registerState(3072, Material.ACACIA_FENCE); // complete
        registerState(3088, Material.SPRUCE_DOOR).facing(EnumFacing.EAST); // complete
        registerState(3089, Material.SPRUCE_DOOR).facing(EnumFacing.SOUTH); // complete
        registerState(3090, Material.SPRUCE_DOOR).facing(EnumFacing.WEST); // complete
        registerState(3091, Material.SPRUCE_DOOR).facing(EnumFacing.NORTH); // complete
        registerState(3092, Material.SPRUCE_DOOR).facing(EnumFacing.EAST).open(); // complete
        registerState(3093, Material.SPRUCE_DOOR).facing(EnumFacing.SOUTH).open(); // complete
        registerState(3094, Material.SPRUCE_DOOR).facing(EnumFacing.WEST).open(); // complete
        registerState(3095, Material.SPRUCE_DOOR).facing(EnumFacing.NORTH).open(); // complete
        registerState(3096, Material.SPRUCE_DOOR).hinge(HingePosition.LEFT); // complete
        registerState(3097, Material.SPRUCE_DOOR).hinge(HingePosition.RIGHT); // complete
        registerState(3098, Material.SPRUCE_DOOR).hinge(HingePosition.LEFT).powered(); // complete
        registerState(3099, Material.SPRUCE_DOOR).hinge(HingePosition.RIGHT).powered(); // complete
        registerState(3104, Material.BIRCH_DOOR).facing(EnumFacing.EAST); // complete
        registerState(3105, Material.BIRCH_DOOR).facing(EnumFacing.SOUTH); // complete
        registerState(3106, Material.BIRCH_DOOR).facing(EnumFacing.WEST); // complete
        registerState(3107, Material.BIRCH_DOOR).facing(EnumFacing.NORTH); // complete
        registerState(3108, Material.BIRCH_DOOR).facing(EnumFacing.EAST).open(); // complete
        registerState(3109, Material.BIRCH_DOOR).facing(EnumFacing.SOUTH).open(); // complete
        registerState(3110, Material.BIRCH_DOOR).facing(EnumFacing.WEST).open(); // complete
        registerState(3111, Material.BIRCH_DOOR).facing(EnumFacing.NORTH).open(); // complete
        registerState(3112, Material.BIRCH_DOOR).hinge(HingePosition.LEFT); // complete
        registerState(3113, Material.BIRCH_DOOR).hinge(HingePosition.RIGHT); // complete
        registerState(3114, Material.BIRCH_DOOR).hinge(HingePosition.LEFT).powered(); // complete
        registerState(3115, Material.BIRCH_DOOR).hinge(HingePosition.RIGHT).powered(); // complete
        registerState(3120, Material.JUNGLE_DOOR).facing(EnumFacing.EAST); // complete
        registerState(3121, Material.JUNGLE_DOOR).facing(EnumFacing.SOUTH); // complete
        registerState(3122, Material.JUNGLE_DOOR).facing(EnumFacing.WEST); // complete
        registerState(3123, Material.JUNGLE_DOOR).facing(EnumFacing.NORTH); // complete
        registerState(3124, Material.JUNGLE_DOOR).facing(EnumFacing.EAST).open(); // complete
        registerState(3125, Material.JUNGLE_DOOR).facing(EnumFacing.SOUTH).open(); // complete
        registerState(3126, Material.JUNGLE_DOOR).facing(EnumFacing.WEST).open(); // complete
        registerState(3127, Material.JUNGLE_DOOR).facing(EnumFacing.NORTH).open(); // complete
        registerState(3128, Material.JUNGLE_DOOR).hinge(HingePosition.LEFT); // complete
        registerState(3129, Material.JUNGLE_DOOR).hinge(HingePosition.RIGHT); // complete
        registerState(3130, Material.JUNGLE_DOOR).hinge(HingePosition.LEFT).powered(); // complete
        registerState(3131, Material.JUNGLE_DOOR).hinge(HingePosition.RIGHT).powered(); // complete
        registerState(3136, Material.ACACIA_DOOR).facing(EnumFacing.EAST); // complete
        registerState(3137, Material.ACACIA_DOOR).facing(EnumFacing.SOUTH); // complete
        registerState(3138, Material.ACACIA_DOOR).facing(EnumFacing.WEST); // complete
        registerState(3139, Material.ACACIA_DOOR).facing(EnumFacing.NORTH); // complete
        registerState(3140, Material.ACACIA_DOOR).facing(EnumFacing.EAST).open(); // complete
        registerState(3141, Material.ACACIA_DOOR).facing(EnumFacing.SOUTH).open(); // complete
        registerState(3142, Material.ACACIA_DOOR).facing(EnumFacing.WEST).open(); // complete
        registerState(3143, Material.ACACIA_DOOR).facing(EnumFacing.NORTH).open(); // complete
        registerState(3144, Material.ACACIA_DOOR).hinge(HingePosition.LEFT); // complete
        registerState(3145, Material.ACACIA_DOOR).hinge(HingePosition.RIGHT); // complete
        registerState(3146, Material.ACACIA_DOOR).hinge(HingePosition.LEFT).powered(); // complete
        registerState(3147, Material.ACACIA_DOOR).hinge(HingePosition.RIGHT).powered(); // complete
        registerState(3152, Material.DARK_OAK_DOOR).facing(EnumFacing.EAST); // complete
        registerState(3153, Material.DARK_OAK_DOOR).facing(EnumFacing.SOUTH); // complete
        registerState(3154, Material.DARK_OAK_DOOR).facing(EnumFacing.WEST); // complete
        registerState(3155, Material.DARK_OAK_DOOR).facing(EnumFacing.NORTH); // complete
        registerState(3156, Material.DARK_OAK_DOOR).facing(EnumFacing.EAST).open(); // complete
        registerState(3157, Material.DARK_OAK_DOOR).facing(EnumFacing.SOUTH).open(); // complete
        registerState(3158, Material.DARK_OAK_DOOR).facing(EnumFacing.WEST).open(); // complete
        registerState(3159, Material.DARK_OAK_DOOR).facing(EnumFacing.NORTH).open(); // complete
        registerState(3160, Material.DARK_OAK_DOOR).hinge(HingePosition.LEFT); // complete
        registerState(3161, Material.DARK_OAK_DOOR).hinge(HingePosition.RIGHT); // complete
        registerState(3162, Material.DARK_OAK_DOOR).hinge(HingePosition.LEFT).powered(); // complete
        registerState(3163, Material.DARK_OAK_DOOR).hinge(HingePosition.RIGHT).powered(); // complete
    }

    private static DefaultBlockState registerState(int id, Material material) {
        DefaultBlockState state = new DefaultBlockState(id, material);
        BLOCK_STATE_IDS.put(id, state);
        return state;
    }

    @NotNull
    @Override
    public int[] getValidBlockStateIDs(@Nullable Material material) {
        if (material == null || material == Material.AIR) {
            return new int[]{0};
        }
        return BLOCK_STATE_IDS.entrySet().stream()
                .filter(entry -> entry.getValue().getMaterial().equals(material))
                .mapToInt(Map.Entry::getKey)
                .toArray();
    }

    @Override
    public @NotNull BlockState[] getValidStates(@Nullable Material material) {
        if (material == null || material == Material.AIR) {
            return new BlockState[]{AIR_STATE};
        }
        return BLOCK_STATE_IDS.entrySet().stream()
                .filter(entry -> entry.getValue().getMaterial().equals(material))
                .map(Map.Entry::getValue)
                .toArray(BlockState[]::new);
    }

    @Override
    public int getDefaultBlockState(@Nullable Material material) {
        int[] ids = this.getValidBlockStateIDs(material);
        return ids.length != 0 ? ids[0] : 0;
    }

    @Override
    public @NotNull BlockState getExactDefaultBlockState(@Nullable Material material) {
        BlockState[] states = this.getValidStates(material);
        return states.length != 0 ? states[0] : AIR_STATE;
    }

    @NotNull
    @Override
    public BlockState getExactBlockState(int blockStateId) {
        return BLOCK_STATE_IDS.getOrDefault(blockStateId, AIR_STATE);
    }

    @NotNull
    @Override
    public Material getMaterial(int blockStateId) {
        return BLOCK_STATE_IDS.getOrDefault(blockStateId, AIR_STATE).getMaterial();
    }

    @Override
    public boolean isMaterial(int blockStateId, @NotNull Material material) {
        return this.getMaterial(blockStateId) == material;
    }
}
