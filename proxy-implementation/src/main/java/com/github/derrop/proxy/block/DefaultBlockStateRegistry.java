/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.block;

import com.github.derrop.proxy.api.block.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        registerState(80, Material.PLANKS).subMaterial(SubMaterial.WOOD_OAK); // complete
        registerState(81, Material.PLANKS).subMaterial(SubMaterial.WOOD_SPRUCE); // complete
        registerState(82, Material.PLANKS).subMaterial(SubMaterial.WOOD_BIRCH); // complete
        registerState(83, Material.PLANKS).subMaterial(SubMaterial.WOOD_JUNGLE); // complete
        registerState(84, Material.PLANKS).subMaterial(SubMaterial.WOOD_ACACIA); // complete
        registerState(85, Material.PLANKS).subMaterial(SubMaterial.WOOD_DARK_OAK); // complete
        registerState(96, Material.SAPLING).age(0).subMaterial(SubMaterial.WOOD_OAK); // complete
        registerState(97, Material.SAPLING).age(0).subMaterial(SubMaterial.WOOD_SPRUCE); // complete
        registerState(98, Material.SAPLING).age(0).subMaterial(SubMaterial.WOOD_BIRCH); // complete
        registerState(99, Material.SAPLING).age(0).subMaterial(SubMaterial.WOOD_JUNGLE); // complete
        registerState(100, Material.SAPLING).age(0).subMaterial(SubMaterial.WOOD_ACACIA); // complete
        registerState(101, Material.SAPLING).age(0).subMaterial(SubMaterial.WOOD_DARK_OAK); // complete
        registerState(104, Material.SAPLING).age(1).subMaterial(SubMaterial.WOOD_OAK); // complete
        registerState(105, Material.SAPLING).age(1).subMaterial(SubMaterial.WOOD_SPRUCE); // complete
        registerState(106, Material.SAPLING).age(1).subMaterial(SubMaterial.WOOD_BIRCH); // complete
        registerState(107, Material.SAPLING).age(1).subMaterial(SubMaterial.WOOD_JUNGLE); // complete
        registerState(108, Material.SAPLING).age(1).subMaterial(SubMaterial.WOOD_ACACIA); // complete
        registerState(109, Material.SAPLING).age(1).subMaterial(SubMaterial.WOOD_DARK_OAK); // complete
        registerState(112, Material.BEDROCK); // complete
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
        registerState(192, Material.SAND).subMaterial(SubMaterial.SAND_NORMAL); // complete
        registerState(193, Material.SAND).subMaterial(SubMaterial.SAND_RED); // complete
        registerState(208, Material.GRAVEL); // complete
        registerState(224, Material.GOLD_ORE); // complete
        registerState(240, Material.IRON_ORE); // complete
        registerState(256, Material.COAL_ORE); // complete
        registerState(272, Material.LOG).subMaterial(SubMaterial.WOOD_OAK).axis(Facing.Axis.Y); // complete
        registerState(273, Material.LOG).subMaterial(SubMaterial.WOOD_SPRUCE).axis(Facing.Axis.Y); // complete
        registerState(274, Material.LOG).subMaterial(SubMaterial.WOOD_BIRCH).axis(Facing.Axis.Y); // complete
        registerState(275, Material.LOG).subMaterial(SubMaterial.WOOD_JUNGLE).axis(Facing.Axis.Y); // complete
        registerState(276, Material.LOG).subMaterial(SubMaterial.WOOD_OAK).axis(Facing.Axis.X); // complete
        registerState(277, Material.LOG).subMaterial(SubMaterial.WOOD_SPRUCE).axis(Facing.Axis.X); // complete
        registerState(278, Material.LOG).subMaterial(SubMaterial.WOOD_BIRCH).axis(Facing.Axis.X); // complete
        registerState(279, Material.LOG).subMaterial(SubMaterial.WOOD_JUNGLE).axis(Facing.Axis.X); // complete
        registerState(280, Material.LOG).subMaterial(SubMaterial.WOOD_OAK).axis(Facing.Axis.Z); // complete
        registerState(281, Material.LOG).subMaterial(SubMaterial.WOOD_SPRUCE).axis(Facing.Axis.Z); // complete
        registerState(282, Material.LOG).subMaterial(SubMaterial.WOOD_BIRCH).axis(Facing.Axis.Z); // complete
        registerState(283, Material.LOG).subMaterial(SubMaterial.WOOD_JUNGLE).axis(Facing.Axis.Z); // complete
        registerState(284, Material.LOG).subMaterial(SubMaterial.WOOD_OAK).axis(Facing.Axis.NONE); // complete
        registerState(285, Material.LOG).subMaterial(SubMaterial.WOOD_SPRUCE).axis(Facing.Axis.NONE); // complete
        registerState(286, Material.LOG).subMaterial(SubMaterial.WOOD_BIRCH).axis(Facing.Axis.NONE); // complete
        registerState(287, Material.LOG).subMaterial(SubMaterial.WOOD_JUNGLE).axis(Facing.Axis.NONE); // complete
        registerState(288, Material.LEAVES).subMaterial(SubMaterial.WOOD_OAK).decayable(); // complete
        registerState(289, Material.LEAVES).subMaterial(SubMaterial.WOOD_SPRUCE).decayable(); // complete
        registerState(290, Material.LEAVES).subMaterial(SubMaterial.WOOD_BIRCH).decayable(); // complete
        registerState(291, Material.LEAVES).subMaterial(SubMaterial.WOOD_JUNGLE).decayable(); // complete
        registerState(292, Material.LEAVES).subMaterial(SubMaterial.WOOD_OAK); // complete
        registerState(293, Material.LEAVES).subMaterial(SubMaterial.WOOD_SPRUCE); // complete
        registerState(294, Material.LEAVES).subMaterial(SubMaterial.WOOD_BIRCH); // complete
        registerState(295, Material.LEAVES).subMaterial(SubMaterial.WOOD_JUNGLE); // complete
        registerState(296, Material.LEAVES);
        registerState(297, Material.LEAVES);
        registerState(298, Material.LEAVES);
        registerState(299, Material.LEAVES);
        registerState(300, Material.LEAVES);
        registerState(301, Material.LEAVES);
        registerState(302, Material.LEAVES);
        registerState(303, Material.LEAVES);
        registerState(304, Material.SPONGE).subMaterial(SubMaterial.SPONGE_NORMAL); // complete
        registerState(305, Material.SPONGE).subMaterial(SubMaterial.SPONGE_WET); // complete
        registerState(320, Material.GLASS); // complete
        registerState(336, Material.LAPIS_ORE); // complete
        registerState(352, Material.LAPIS_BLOCK); // complete
        registerState(368, Material.DISPENSER).facing(Facing.DOWN); // complete
        registerState(369, Material.DISPENSER).facing(Facing.UP); // complete
        registerState(370, Material.DISPENSER).facing(Facing.NORTH); // complete
        registerState(371, Material.DISPENSER).facing(Facing.SOUTH); // complete
        registerState(372, Material.DISPENSER).facing(Facing.WEST); // complete
        registerState(373, Material.DISPENSER).facing(Facing.EAST); // complete
        registerState(376, Material.DISPENSER).facing(Facing.DOWN).triggered(); // complete
        registerState(377, Material.DISPENSER).facing(Facing.UP).triggered(); // complete
        registerState(378, Material.DISPENSER).facing(Facing.NORTH).triggered(); // complete
        registerState(379, Material.DISPENSER).facing(Facing.SOUTH).triggered(); // complete
        registerState(380, Material.DISPENSER).facing(Facing.WEST).triggered(); // complete
        registerState(381, Material.DISPENSER).facing(Facing.EAST).triggered(); // complete
        registerState(384, Material.SANDSTONE).subMaterial(SubMaterial.SANDSTONE_NORMAL); // complete
        registerState(385, Material.SANDSTONE).subMaterial(SubMaterial.SANDSTONE_CHISELED); // complete
        registerState(386, Material.SANDSTONE).subMaterial(SubMaterial.SANDSTONE_SMOOTH); // complete
        registerState(400, Material.NOTE_BLOCK); // complete
        registerState(416, Material.BED_BLOCK).half(Half.BOTTOM).facing(Facing.SOUTH); // complete
        registerState(417, Material.BED_BLOCK).half(Half.BOTTOM).facing(Facing.WEST); // complete
        registerState(418, Material.BED_BLOCK).half(Half.BOTTOM).facing(Facing.NORTH); // complete
        registerState(419, Material.BED_BLOCK).half(Half.BOTTOM).facing(Facing.EAST); // complete
        registerState(424, Material.BED_BLOCK).half(Half.TOP).facing(Facing.SOUTH); // complete
        registerState(425, Material.BED_BLOCK).half(Half.TOP).facing(Facing.WEST); // complete
        registerState(426, Material.BED_BLOCK).half(Half.TOP).facing(Facing.NORTH); // complete
        registerState(427, Material.BED_BLOCK).half(Half.TOP).facing(Facing.EAST); // complete
        registerState(428, Material.BED_BLOCK).occupied().half(Half.TOP).facing(Facing.SOUTH); // complete
        registerState(429, Material.BED_BLOCK).occupied().half(Half.TOP).facing(Facing.WEST); // complete
        registerState(430, Material.BED_BLOCK).occupied().half(Half.TOP).facing(Facing.NORTH); // complete
        registerState(431, Material.BED_BLOCK).occupied().half(Half.TOP).facing(Facing.EAST); // complete
        registerState(432, Material.POWERED_RAIL).shape(BlockShape.NORTH_SOUTH); // complete
        registerState(433, Material.POWERED_RAIL).shape(BlockShape.EAST_WEST); // complete
        registerState(434, Material.POWERED_RAIL).shape(BlockShape.ASCENDING_EAST); // complete
        registerState(435, Material.POWERED_RAIL).shape(BlockShape.ASCENDING_WEST); // complete
        registerState(436, Material.POWERED_RAIL).shape(BlockShape.ASCENDING_NORTH); // complete
        registerState(437, Material.POWERED_RAIL).shape(BlockShape.ASCENDING_SOUTH); // complete
        registerState(440, Material.POWERED_RAIL).shape(BlockShape.NORTH_SOUTH).powered(); // complete
        registerState(441, Material.POWERED_RAIL).shape(BlockShape.EAST_WEST).powered(); // complete
        registerState(442, Material.POWERED_RAIL).shape(BlockShape.ASCENDING_EAST).powered(); // complete
        registerState(443, Material.POWERED_RAIL).shape(BlockShape.ASCENDING_WEST).powered(); // complete
        registerState(444, Material.POWERED_RAIL).shape(BlockShape.ASCENDING_NORTH).powered(); // complete
        registerState(445, Material.POWERED_RAIL).shape(BlockShape.ASCENDING_SOUTH).powered(); // complete
        registerState(448, Material.DETECTOR_RAIL).shape(BlockShape.NORTH_SOUTH); // complete
        registerState(449, Material.DETECTOR_RAIL).shape(BlockShape.EAST_WEST); // complete
        registerState(450, Material.DETECTOR_RAIL).shape(BlockShape.ASCENDING_EAST); // complete
        registerState(451, Material.DETECTOR_RAIL).shape(BlockShape.ASCENDING_WEST); // complete
        registerState(452, Material.DETECTOR_RAIL).shape(BlockShape.ASCENDING_NORTH); // complete
        registerState(453, Material.DETECTOR_RAIL).shape(BlockShape.ASCENDING_SOUTH); // complete
        registerState(456, Material.DETECTOR_RAIL).shape(BlockShape.NORTH_SOUTH).powered(); // complete
        registerState(457, Material.DETECTOR_RAIL).shape(BlockShape.EAST_WEST).powered(); // complete
        registerState(458, Material.DETECTOR_RAIL).shape(BlockShape.ASCENDING_EAST).powered(); // complete
        registerState(459, Material.DETECTOR_RAIL).shape(BlockShape.ASCENDING_WEST).powered(); // complete
        registerState(460, Material.DETECTOR_RAIL).shape(BlockShape.ASCENDING_NORTH).powered(); // complete
        registerState(461, Material.DETECTOR_RAIL).shape(BlockShape.ASCENDING_SOUTH).powered(); // complete
        registerState(464, Material.PISTON_STICKY_BASE).facing(Facing.DOWN); // complete
        registerState(465, Material.PISTON_STICKY_BASE).facing(Facing.UP); // complete
        registerState(466, Material.PISTON_STICKY_BASE).facing(Facing.NORTH); // complete
        registerState(467, Material.PISTON_STICKY_BASE).facing(Facing.SOUTH); // complete
        registerState(468, Material.PISTON_STICKY_BASE).facing(Facing.WEST); // complete
        registerState(469, Material.PISTON_STICKY_BASE).facing(Facing.EAST); // complete
        registerState(472, Material.PISTON_STICKY_BASE).facing(Facing.DOWN).powered(); // complete
        registerState(473, Material.PISTON_STICKY_BASE).facing(Facing.UP).powered(); // complete
        registerState(474, Material.PISTON_STICKY_BASE).facing(Facing.NORTH).powered(); // complete
        registerState(475, Material.PISTON_STICKY_BASE).facing(Facing.SOUTH).powered(); // complete
        registerState(476, Material.PISTON_STICKY_BASE).facing(Facing.WEST).powered(); // complete
        registerState(477, Material.PISTON_STICKY_BASE).facing(Facing.EAST).powered(); // complete
        registerState(480, Material.WEB); // complete
        /* type = ?: */ registerState(496, Material.LONG_GRASS);
        /* type = tall grass: */ registerState(497, Material.LONG_GRASS);
        /* type = fern: */ registerState(498, Material.LONG_GRASS);
        registerState(512, Material.DEAD_BUSH); // complete
        registerState(528, Material.PISTON_BASE).half(Half.BOTTOM).facing(Facing.DOWN); // complete
        registerState(529, Material.PISTON_BASE).half(Half.BOTTOM).facing(Facing.UP); // complete
        registerState(530, Material.PISTON_BASE).half(Half.BOTTOM).facing(Facing.NORTH); // complete
        registerState(531, Material.PISTON_BASE).half(Half.BOTTOM).facing(Facing.SOUTH); // complete
        registerState(532, Material.PISTON_BASE).half(Half.BOTTOM).facing(Facing.WEST); // complete
        registerState(533, Material.PISTON_BASE).half(Half.BOTTOM).facing(Facing.EAST); // complete
        registerState(536, Material.PISTON_BASE).half(Half.BOTTOM).facing(Facing.DOWN).powered(); // complete
        registerState(537, Material.PISTON_BASE).half(Half.BOTTOM).facing(Facing.UP).powered(); // complete
        registerState(538, Material.PISTON_BASE).half(Half.BOTTOM).facing(Facing.NORTH).powered(); // complete
        registerState(539, Material.PISTON_BASE).half(Half.BOTTOM).facing(Facing.SOUTH).powered(); // complete
        registerState(540, Material.PISTON_BASE).half(Half.BOTTOM).facing(Facing.WEST).powered(); // complete
        registerState(541, Material.PISTON_BASE).half(Half.BOTTOM).facing(Facing.EAST).powered(); // complete
        registerState(544, Material.PISTON_BASE).pistonType(PistonType.NORMAL).half(Half.TOP).setShort().facing(Facing.DOWN); // complete
        registerState(545, Material.PISTON_BASE).pistonType(PistonType.NORMAL).half(Half.TOP).setShort().facing(Facing.UP); // complete
        registerState(546, Material.PISTON_BASE).pistonType(PistonType.NORMAL).half(Half.TOP).setShort().facing(Facing.NORTH); // complete
        registerState(547, Material.PISTON_BASE).pistonType(PistonType.NORMAL).half(Half.TOP).setShort().facing(Facing.SOUTH); // complete
        registerState(548, Material.PISTON_BASE).pistonType(PistonType.NORMAL).half(Half.TOP).setShort().facing(Facing.WEST); // complete
        registerState(549, Material.PISTON_BASE).pistonType(PistonType.NORMAL).half(Half.TOP).setShort().facing(Facing.EAST); // complete
        registerState(552, Material.PISTON_BASE).pistonType(PistonType.STICKY).half(Half.TOP).setShort().facing(Facing.DOWN); // complete
        registerState(553, Material.PISTON_BASE).pistonType(PistonType.STICKY).half(Half.TOP).setShort().facing(Facing.UP); // complete
        registerState(554, Material.PISTON_BASE).pistonType(PistonType.STICKY).half(Half.TOP).setShort().facing(Facing.NORTH); // complete
        registerState(555, Material.PISTON_BASE).pistonType(PistonType.STICKY).half(Half.TOP).setShort().facing(Facing.SOUTH); // complete
        registerState(556, Material.PISTON_BASE).pistonType(PistonType.STICKY).half(Half.TOP).setShort().facing(Facing.WEST); // complete
        registerState(557, Material.PISTON_BASE).pistonType(PistonType.STICKY).half(Half.TOP).setShort().facing(Facing.EAST); // complete
        registerState(560, Material.WOOL).subMaterial(SubMaterial.COLOR_WHITE); // complete;
        registerState(561, Material.WOOL).subMaterial(SubMaterial.COLOR_ORANGE); // complete
        registerState(562, Material.WOOL).subMaterial(SubMaterial.COLOR_MAGENTA); // complete
        registerState(563, Material.WOOL).subMaterial(SubMaterial.COLOR_LIGHT_BLUE); // complete
        registerState(564, Material.WOOL).subMaterial(SubMaterial.COLOR_YELLOW); // complete
        registerState(565, Material.WOOL).subMaterial(SubMaterial.COLOR_GREEN); // complete
        registerState(566, Material.WOOL).subMaterial(SubMaterial.COLOR_PINK); // complete
        registerState(567, Material.WOOL).subMaterial(SubMaterial.COLOR_GRAY); // complete
        registerState(568, Material.WOOL).subMaterial(SubMaterial.COLOR_LIGHT_GRAY); // complete
        registerState(569, Material.WOOL).subMaterial(SubMaterial.COLOR_CYAN); // complete
        registerState(570, Material.WOOL).subMaterial(SubMaterial.COLOR_PURPLE); // complete
        registerState(571, Material.WOOL).subMaterial(SubMaterial.COLOR_BLUE); // complete
        registerState(572, Material.WOOL).subMaterial(SubMaterial.COLOR_BROWN); // complete
        registerState(573, Material.WOOL).subMaterial(SubMaterial.COLOR_GREEN); // complete
        registerState(574, Material.WOOL).subMaterial(SubMaterial.COLOR_RED); // complete
        registerState(575, Material.WOOL).subMaterial(SubMaterial.COLOR_BLACK); // complete
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
        registerState(608, Material.FLOWER).subMaterial(SubMaterial.FLOWER_POPPY); // complete
        registerState(609, Material.FLOWER).subMaterial(SubMaterial.FLOWER_BLUE_ORCHID); // complete
        registerState(610, Material.FLOWER).subMaterial(SubMaterial.FLOWER_ALLIUM); // complete
        registerState(611, Material.FLOWER).subMaterial(SubMaterial.FLOWER_AZURE_BLUET); // complete
        registerState(612, Material.FLOWER).subMaterial(SubMaterial.FLOWER_RED_TULIP); // complete
        registerState(613, Material.FLOWER).subMaterial(SubMaterial.FLOWER_ORANGE_TULIP); // complete
        registerState(614, Material.FLOWER).subMaterial(SubMaterial.FLOWER_WHITE_TULIP); // complete
        registerState(615, Material.FLOWER).subMaterial(SubMaterial.FLOWER_PINK_TULIP); // complete
        registerState(616, Material.FLOWER).subMaterial(SubMaterial.FLOWER_OXEYE_DAISY); // complete
        registerState(624, Material.BROWN_MUSHROOM); // complete
        registerState(640, Material.RED_MUSHROOM); // complete
        registerState(656, Material.GOLD_BLOCK); // complete
        registerState(672, Material.IRON_BLOCK); // complete
        registerState(688, Material.DOUBLE_STONE_SLAB).subMaterial(SubMaterial.STONE_SLAB_STONE); // complete
        registerState(689, Material.DOUBLE_STONE_SLAB).subMaterial(SubMaterial.STONE_SLAB_SANDSTONE); // complete
        registerState(690, Material.DOUBLE_STONE_SLAB).subMaterial(SubMaterial.STONE_SLAB_WOOD_OLD); // complete
        registerState(691, Material.DOUBLE_STONE_SLAB).subMaterial(SubMaterial.STONE_SLAB_COBBLESTONE); // complete
        registerState(692, Material.DOUBLE_STONE_SLAB).subMaterial(SubMaterial.STONE_SLAB_BRICKS); // complete
        registerState(693, Material.DOUBLE_STONE_SLAB).subMaterial(SubMaterial.STONE_SLAB_STONE_BRICKS); // complete
        registerState(694, Material.DOUBLE_STONE_SLAB).subMaterial(SubMaterial.STONE_SLAB_NETHER_BRICKS); // complete
        registerState(695, Material.DOUBLE_STONE_SLAB).subMaterial(SubMaterial.STONE_SLAB_QUARTZ); // complete
        registerState(696, Material.DOUBLE_STONE_SLAB).seamless().subMaterial(SubMaterial.STONE_SLAB_STONE); // complete
        registerState(697, Material.DOUBLE_STONE_SLAB).seamless().subMaterial(SubMaterial.STONE_SLAB_SANDSTONE); // complete
        registerState(698, Material.DOUBLE_STONE_SLAB).seamless().subMaterial(SubMaterial.STONE_SLAB_WOOD_OLD); // complete
        registerState(699, Material.DOUBLE_STONE_SLAB).seamless().subMaterial(SubMaterial.STONE_SLAB_COBBLESTONE); // complete
        registerState(700, Material.DOUBLE_STONE_SLAB).seamless().subMaterial(SubMaterial.STONE_SLAB_BRICKS); // complete
        registerState(701, Material.DOUBLE_STONE_SLAB).seamless().subMaterial(SubMaterial.STONE_SLAB_STONE_BRICKS); // complete
        registerState(702, Material.DOUBLE_STONE_SLAB).seamless().subMaterial(SubMaterial.STONE_SLAB_NETHER_BRICKS); // complete
        registerState(703, Material.DOUBLE_STONE_SLAB).seamless().subMaterial(SubMaterial.STONE_SLAB_QUARTZ); // complete
        registerState(704, Material.STONE_SLAB).half(Half.BOTTOM).subMaterial(SubMaterial.STONE_SLAB_STONE); // complete
        registerState(705, Material.STONE_SLAB).half(Half.BOTTOM).subMaterial(SubMaterial.STONE_SLAB_SANDSTONE); // complete
        registerState(706, Material.STONE_SLAB).half(Half.BOTTOM).subMaterial(SubMaterial.STONE_SLAB_WOOD_OLD); // complete
        registerState(707, Material.STONE_SLAB).half(Half.BOTTOM).subMaterial(SubMaterial.STONE_SLAB_COBBLESTONE); // complete
        registerState(708, Material.STONE_SLAB).half(Half.BOTTOM).subMaterial(SubMaterial.STONE_SLAB_BRICKS); // complete
        registerState(709, Material.STONE_SLAB).half(Half.BOTTOM).subMaterial(SubMaterial.STONE_SLAB_STONE_BRICKS); // complete
        registerState(710, Material.STONE_SLAB).half(Half.BOTTOM).subMaterial(SubMaterial.STONE_SLAB_NETHER_BRICKS); // complete
        registerState(711, Material.STONE_SLAB).half(Half.BOTTOM).subMaterial(SubMaterial.STONE_SLAB_QUARTZ); // complete
        registerState(712, Material.STONE_SLAB).half(Half.TOP).subMaterial(SubMaterial.STONE_SLAB_STONE); // complete
        registerState(713, Material.STONE_SLAB).half(Half.TOP).subMaterial(SubMaterial.STONE_SLAB_SANDSTONE); // complete
        registerState(714, Material.STONE_SLAB).half(Half.TOP).subMaterial(SubMaterial.STONE_SLAB_WOOD_OLD); // complete
        registerState(715, Material.STONE_SLAB).half(Half.TOP).subMaterial(SubMaterial.STONE_SLAB_COBBLESTONE); // complete
        registerState(716, Material.STONE_SLAB).half(Half.TOP).subMaterial(SubMaterial.STONE_SLAB_BRICKS); // complete
        registerState(717, Material.STONE_SLAB).half(Half.TOP).subMaterial(SubMaterial.STONE_SLAB_STONE_BRICKS); // complete
        registerState(718, Material.STONE_SLAB).half(Half.TOP).subMaterial(SubMaterial.STONE_SLAB_NETHER_BRICKS); // complete
        registerState(719, Material.STONE_SLAB).half(Half.TOP).subMaterial(SubMaterial.STONE_SLAB_QUARTZ); // complete
        registerState(720, Material.BRICK); // complete
        registerState(736, Material.TNT); // complete
        registerState(737, Material.TNT);
        registerState(752, Material.BOOKSHELF); // complete
        registerState(768, Material.MOSSY_COBBLESTONE); // complete
        registerState(784, Material.OBSIDIAN); // complete
        registerState(801, Material.TORCH).facing(Facing.EAST); // complete
        registerState(802, Material.TORCH).facing(Facing.WEST); // complete
        registerState(803, Material.TORCH).facing(Facing.SOUTH); // complete
        registerState(804, Material.TORCH).facing(Facing.NORTH); // complete
        registerState(805, Material.TORCH); // complete
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
        registerState(832, Material.MOB_SPAWNER); // complete
        registerState(848, Material.WOOD_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(849, Material.WOOD_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(850, Material.WOOD_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(851, Material.WOOD_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(852, Material.WOOD_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(853, Material.WOOD_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(854, Material.WOOD_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(855, Material.WOOD_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(866, Material.CHEST).facing(Facing.NORTH); // complete
        registerState(867, Material.CHEST).facing(Facing.SOUTH); // complete
        registerState(868, Material.CHEST).facing(Facing.WEST); // complete
        registerState(869, Material.CHEST).facing(Facing.EAST); // complete
        registerState(880, Material.REDSTONE_WIRE).power(0); // complete
        registerState(881, Material.REDSTONE_WIRE).power(1); // complete
        registerState(882, Material.REDSTONE_WIRE).power(2); // complete
        registerState(883, Material.REDSTONE_WIRE).power(3); // complete
        registerState(884, Material.REDSTONE_WIRE).power(4); // complete
        registerState(885, Material.REDSTONE_WIRE).power(5); // complete
        registerState(886, Material.REDSTONE_WIRE).power(6); // complete
        registerState(887, Material.REDSTONE_WIRE).power(7); // complete
        registerState(888, Material.REDSTONE_WIRE).power(8); // complete
        registerState(889, Material.REDSTONE_WIRE).power(9); // complete
        registerState(890, Material.REDSTONE_WIRE).power(10); // complete
        registerState(891, Material.REDSTONE_WIRE).power(11); // complete
        registerState(892, Material.REDSTONE_WIRE).power(12); // complete
        registerState(893, Material.REDSTONE_WIRE).power(13); // complete
        registerState(894, Material.REDSTONE_WIRE).power(14); // complete
        registerState(895, Material.REDSTONE_WIRE).power(15); // complete
        registerState(896, Material.DIAMOND_ORE); // complete
        registerState(912, Material.DIAMOND_BLOCK); // complete
        registerState(928, Material.WORKBENCH); // complete
        registerState(944, Material.CROPS).age(0); // complete
        registerState(945, Material.CROPS).age(1); // complete
        registerState(946, Material.CROPS).age(2); // complete
        registerState(947, Material.CROPS).age(3); // complete
        registerState(948, Material.CROPS).age(4); // complete
        registerState(949, Material.CROPS).age(5); // complete
        registerState(950, Material.CROPS).age(6); // complete
        registerState(951, Material.CROPS).age(7); // complete
        registerState(960, Material.SOIL).moisture(0); // complete
        registerState(961, Material.SOIL).moisture(1); // complete
        registerState(962, Material.SOIL).moisture(2); // complete
        registerState(963, Material.SOIL).moisture(3); // complete
        registerState(964, Material.SOIL).moisture(4); // complete
        registerState(965, Material.SOIL).moisture(5); // complete
        registerState(966, Material.SOIL).moisture(6); // complete
        registerState(967, Material.SOIL).moisture(7); // complete
        registerState(978, Material.FURNACE).facing(Facing.NORTH); // complete
        registerState(979, Material.FURNACE).facing(Facing.SOUTH); // complete
        registerState(980, Material.FURNACE).facing(Facing.WEST); // complete
        registerState(981, Material.FURNACE).facing(Facing.EAST); // complete
        registerState(994, Material.BURNING_FURNACE).facing(Facing.NORTH); // complete
        registerState(995, Material.BURNING_FURNACE).facing(Facing.SOUTH); // complete
        registerState(996, Material.BURNING_FURNACE).facing(Facing.WEST); // complete
        registerState(997, Material.BURNING_FURNACE).facing(Facing.EAST); // complete
        registerState(1008, Material.SIGN_POST).rotation(0); // complete
        registerState(1009, Material.SIGN_POST).rotation(1); // complete
        registerState(1010, Material.SIGN_POST).rotation(2); // complete
        registerState(1011, Material.SIGN_POST).rotation(3); // complete
        registerState(1012, Material.SIGN_POST).rotation(4); // complete
        registerState(1013, Material.SIGN_POST).rotation(5); // complete
        registerState(1014, Material.SIGN_POST).rotation(6); // complete
        registerState(1015, Material.SIGN_POST).rotation(7); // complete
        registerState(1016, Material.SIGN_POST).rotation(8); // complete
        registerState(1017, Material.SIGN_POST).rotation(9); // complete
        registerState(1018, Material.SIGN_POST).rotation(10); // complete
        registerState(1019, Material.SIGN_POST).rotation(11); // complete
        registerState(1020, Material.SIGN_POST).rotation(12); // complete
        registerState(1021, Material.SIGN_POST).rotation(13); // complete
        registerState(1022, Material.SIGN_POST).rotation(14); // complete
        registerState(1023, Material.SIGN_POST).rotation(15); // complete
        registerState(1024, Material.WOODEN_DOOR).facing(Facing.EAST); // complete
        registerState(1025, Material.WOODEN_DOOR).facing(Facing.SOUTH); // complete
        registerState(1026, Material.WOODEN_DOOR).facing(Facing.WEST); // complete
        registerState(1027, Material.WOODEN_DOOR).facing(Facing.NORTH); // complete
        registerState(1028, Material.WOODEN_DOOR).facing(Facing.EAST).open(); // complete
        registerState(1029, Material.WOODEN_DOOR).facing(Facing.SOUTH).open(); // complete
        registerState(1030, Material.WOODEN_DOOR).facing(Facing.WEST).open(); // complete
        registerState(1031, Material.WOODEN_DOOR).facing(Facing.NORTH).open(); // complete
        registerState(1032, Material.WOODEN_DOOR).hinge(HingePosition.LEFT); // complete
        registerState(1033, Material.WOODEN_DOOR).hinge(HingePosition.RIGHT); // complete
        registerState(1034, Material.WOODEN_DOOR).hinge(HingePosition.LEFT).powered(); // complete
        registerState(1035, Material.WOODEN_DOOR).hinge(HingePosition.RIGHT).powered(); // complete
        registerState(1042, Material.LADDER).facing(Facing.NORTH); // complete
        registerState(1043, Material.LADDER).facing(Facing.SOUTH); // complete
        registerState(1044, Material.LADDER).facing(Facing.WEST); // complete
        registerState(1045, Material.LADDER).facing(Facing.EAST); // complete
        registerState(1056, Material.RAILS).shape(BlockShape.NORTH_SOUTH); // complete
        registerState(1057, Material.RAILS).shape(BlockShape.EAST_WEST); // complete
        registerState(1058, Material.RAILS).shape(BlockShape.ASCENDING_EAST); // complete
        registerState(1059, Material.RAILS).shape(BlockShape.ASCENDING_WEST); // complete
        registerState(1060, Material.RAILS).shape(BlockShape.ASCENDING_NORTH); // complete
        registerState(1061, Material.RAILS).shape(BlockShape.ASCENDING_SOUTH); // complete
        registerState(1062, Material.RAILS).shape(BlockShape.SOUTH_EAST); // complete
        registerState(1063, Material.RAILS).shape(BlockShape.SOUTH_WEST); // complete
        registerState(1064, Material.RAILS).shape(BlockShape.NORTH_WEST); // complete
        registerState(1065, Material.RAILS).shape(BlockShape.NORTH_EAST); // complete
        registerState(1072, Material.COBBLESTONE_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(1073, Material.COBBLESTONE_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(1074, Material.COBBLESTONE_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(1075, Material.COBBLESTONE_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(1076, Material.COBBLESTONE_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(1077, Material.COBBLESTONE_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(1078, Material.COBBLESTONE_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(1079, Material.COBBLESTONE_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(1090, Material.WALL_SIGN).facing(Facing.NORTH); // complete
        registerState(1091, Material.WALL_SIGN).facing(Facing.SOUTH); // complete
        registerState(1092, Material.WALL_SIGN).facing(Facing.WEST); // complete
        registerState(1093, Material.WALL_SIGN).facing(Facing.EAST); // complete
        registerState(1104, Material.LEVER).facing(Facing.DOWN).axis(Facing.Axis.X); // complete
        registerState(1105, Material.LEVER).facing(Facing.EAST); // complete
        registerState(1106, Material.LEVER).facing(Facing.WEST); // complete
        registerState(1107, Material.LEVER).facing(Facing.SOUTH); // complete
        registerState(1108, Material.LEVER).facing(Facing.NORTH); // complete
        registerState(1109, Material.LEVER).facing(Facing.UP).axis(Facing.Axis.Z); // complete
        registerState(1110, Material.LEVER).facing(Facing.UP).axis(Facing.Axis.X); // complete
        registerState(1111, Material.LEVER).facing(Facing.DOWN).axis(Facing.Axis.Z); // complete
        registerState(1112, Material.LEVER).powered().facing(Facing.DOWN).axis(Facing.Axis.X); // complete
        registerState(1113, Material.LEVER).powered().facing(Facing.EAST); // complete
        registerState(1114, Material.LEVER).powered().facing(Facing.WEST); // complete
        registerState(1115, Material.LEVER).powered().facing(Facing.SOUTH); // complete
        registerState(1116, Material.LEVER).powered().facing(Facing.NORTH); // complete
        registerState(1117, Material.LEVER).powered().facing(Facing.UP).axis(Facing.Axis.Z); // complete
        registerState(1118, Material.LEVER).powered().facing(Facing.UP).axis(Facing.Axis.X); // complete
        registerState(1119, Material.LEVER).powered().facing(Facing.DOWN).axis(Facing.Axis.Z); // complete
        registerState(1120, Material.STONE_PLATE); // complete
        registerState(1121, Material.STONE_PLATE).powered(); // complete
        registerState(1136, Material.IRON_DOOR_BLOCK).half(Half.BOTTOM).facing(Facing.EAST); // complete
        registerState(1137, Material.IRON_DOOR_BLOCK).half(Half.BOTTOM).facing(Facing.SOUTH); // complete
        registerState(1138, Material.IRON_DOOR_BLOCK).half(Half.BOTTOM).facing(Facing.WEST); // complete
        registerState(1139, Material.IRON_DOOR_BLOCK).half(Half.BOTTOM).facing(Facing.NORTH); // complete
        registerState(1140, Material.IRON_DOOR_BLOCK).half(Half.BOTTOM).open().facing(Facing.EAST); // complete
        registerState(1141, Material.IRON_DOOR_BLOCK).half(Half.BOTTOM).open().facing(Facing.SOUTH); // complete
        registerState(1142, Material.IRON_DOOR_BLOCK).half(Half.BOTTOM).open().facing(Facing.WEST); // complete
        registerState(1143, Material.IRON_DOOR_BLOCK).half(Half.BOTTOM).open().facing(Facing.NORTH); // complete
        registerState(1144, Material.IRON_DOOR_BLOCK).half(Half.TOP).hinge(HingePosition.LEFT); // complete
        registerState(1145, Material.IRON_DOOR_BLOCK).half(Half.TOP).hinge(HingePosition.RIGHT); // complete
        registerState(1146, Material.IRON_DOOR_BLOCK).half(Half.TOP).powered().hinge(HingePosition.LEFT); // complete;
        registerState(1147, Material.IRON_DOOR_BLOCK).half(Half.TOP).powered().hinge(HingePosition.RIGHT); // complete;
        registerState(1152, Material.WOOD_PLATE); // complete
        registerState(1153, Material.WOOD_PLATE).powered(); // complete
        registerState(1168, Material.REDSTONE_ORE); // complete
        registerState(1184, Material.REDSTONE_ORE).powered(); // complete
        registerState(1201, Material.REDSTONE_TORCH_OFF).facing(Facing.EAST); // complete
        registerState(1202, Material.REDSTONE_TORCH_OFF).facing(Facing.WEST); // complete
        registerState(1203, Material.REDSTONE_TORCH_OFF).facing(Facing.SOUTH); // complete
        registerState(1204, Material.REDSTONE_TORCH_OFF).facing(Facing.NORTH); // complete
        registerState(1205, Material.REDSTONE_TORCH_OFF).facing(Facing.UP); // complete
        registerState(1217, Material.REDSTONE_TORCH_ON).facing(Facing.EAST); // complete
        registerState(1218, Material.REDSTONE_TORCH_ON).facing(Facing.WEST); // complete
        registerState(1219, Material.REDSTONE_TORCH_ON).facing(Facing.SOUTH); // complete
        registerState(1220, Material.REDSTONE_TORCH_ON).facing(Facing.NORTH); // complete
        registerState(1221, Material.REDSTONE_TORCH_ON).facing(Facing.UP); // complete
        registerState(1232, Material.STONE_BUTTON).facing(Facing.DOWN); // complete
        registerState(1233, Material.STONE_BUTTON).facing(Facing.EAST); // complete
        registerState(1234, Material.STONE_BUTTON).facing(Facing.WEST); // complete
        registerState(1235, Material.STONE_BUTTON).facing(Facing.SOUTH); // complete
        registerState(1236, Material.STONE_BUTTON).facing(Facing.NORTH); // complete
        registerState(1237, Material.STONE_BUTTON).facing(Facing.UP); // complete
        registerState(1240, Material.STONE_BUTTON).powered().facing(Facing.DOWN); // complete
        registerState(1241, Material.STONE_BUTTON).powered().facing(Facing.EAST); // complete
        registerState(1242, Material.STONE_BUTTON).powered().facing(Facing.WEST); // complete
        registerState(1243, Material.STONE_BUTTON).powered().facing(Facing.SOUTH); // complete
        registerState(1244, Material.STONE_BUTTON).powered().facing(Facing.NORTH); // complete
        registerState(1245, Material.STONE_BUTTON).powered().facing(Facing.UP); // complete
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
        registerState(1296, Material.CACTUS).age(0); // complete
        registerState(1297, Material.CACTUS).age(1); // complete
        registerState(1298, Material.CACTUS).age(2); // complete
        registerState(1299, Material.CACTUS).age(3); // complete
        registerState(1300, Material.CACTUS).age(4); // complete
        registerState(1301, Material.CACTUS).age(5); // complete
        registerState(1302, Material.CACTUS).age(6); // complete
        registerState(1303, Material.CACTUS).age(7); // complete
        registerState(1304, Material.CACTUS).age(8); // complete
        registerState(1305, Material.CACTUS).age(9); // complete
        registerState(1306, Material.CACTUS).age(10); // complete
        registerState(1307, Material.CACTUS).age(11); // complete
        registerState(1308, Material.CACTUS).age(12); // complete
        registerState(1309, Material.CACTUS).age(13); // complete
        registerState(1310, Material.CACTUS).age(14); // complete
        registerState(1311, Material.CACTUS).age(15); // complete
        registerState(1312, Material.CLAY); // complete
        registerState(1328, Material.SUGAR_CANE_BLOCK).age(0); // complete
        registerState(1329, Material.SUGAR_CANE_BLOCK).age(1); // complete
        registerState(1330, Material.SUGAR_CANE_BLOCK).age(2); // complete
        registerState(1331, Material.SUGAR_CANE_BLOCK).age(3); // complete
        registerState(1332, Material.SUGAR_CANE_BLOCK).age(4); // complete
        registerState(1333, Material.SUGAR_CANE_BLOCK).age(5); // complete
        registerState(1334, Material.SUGAR_CANE_BLOCK).age(6); // complete
        registerState(1335, Material.SUGAR_CANE_BLOCK).age(7); // complete
        registerState(1336, Material.SUGAR_CANE_BLOCK).age(8); // complete
        registerState(1337, Material.SUGAR_CANE_BLOCK).age(9); // complete
        registerState(1338, Material.SUGAR_CANE_BLOCK).age(10); // complete
        registerState(1339, Material.SUGAR_CANE_BLOCK).age(11); // complete
        registerState(1340, Material.SUGAR_CANE_BLOCK).age(12); // complete
        registerState(1341, Material.SUGAR_CANE_BLOCK).age(13); // complete
        registerState(1342, Material.SUGAR_CANE_BLOCK).age(14); // complete
        registerState(1343, Material.SUGAR_CANE_BLOCK).age(15); // complete
        registerState(1344, Material.JUKEBOX); // complete
        registerState(1345, Material.JUKEBOX).withRecord(); // complete
        registerState(1360, Material.FENCE); // complete
        registerState(1376, Material.PUMPKIN).facing(Facing.SOUTH); // complete
        registerState(1377, Material.PUMPKIN).facing(Facing.WEST); // complete
        registerState(1378, Material.PUMPKIN).facing(Facing.NORTH); // complete
        registerState(1379, Material.PUMPKIN).facing(Facing.EAST); // complete
        registerState(1392, Material.NETHERRACK); // complete
        registerState(1408, Material.SOUL_SAND); // complete
        registerState(1424, Material.GLOWSTONE); // complete
        registerState(1441, Material.PORTAL).axis(Facing.Axis.X); // complete
        registerState(1442, Material.PORTAL).axis(Facing.Axis.Z); // complete
        registerState(1456, Material.JACK_O_LANTERN).facing(Facing.SOUTH); // complete
        registerState(1457, Material.JACK_O_LANTERN).facing(Facing.WEST); // complete
        registerState(1458, Material.JACK_O_LANTERN).facing(Facing.NORTH); // complete
        registerState(1459, Material.JACK_O_LANTERN).facing(Facing.NORTH); // complete
        registerState(1472, Material.CAKE_BLOCK).bites(0); // complete
        registerState(1473, Material.CAKE_BLOCK).bites(1); // complete
        registerState(1474, Material.CAKE_BLOCK).bites(2); // complete
        registerState(1475, Material.CAKE_BLOCK).bites(3); // complete
        registerState(1476, Material.CAKE_BLOCK).bites(4); // complete
        registerState(1477, Material.CAKE_BLOCK).bites(5); // complete
        registerState(1478, Material.CAKE_BLOCK).bites(6); // complete
        registerState(1488, Material.DIODE_BLOCK_OFF).delay(1).facing(Facing.SOUTH); // complete
        registerState(1489, Material.DIODE_BLOCK_OFF).delay(1).facing(Facing.WEST); // complete
        registerState(1490, Material.DIODE_BLOCK_OFF).delay(1).facing(Facing.NORTH); // complete
        registerState(1491, Material.DIODE_BLOCK_OFF).delay(1).facing(Facing.EAST); // complete
        registerState(1492, Material.DIODE_BLOCK_OFF).delay(2).facing(Facing.SOUTH); // complete
        registerState(1493, Material.DIODE_BLOCK_OFF).delay(2).facing(Facing.WEST); // complete
        registerState(1494, Material.DIODE_BLOCK_OFF).delay(2).facing(Facing.NORTH); // complete
        registerState(1495, Material.DIODE_BLOCK_OFF).delay(2).facing(Facing.EAST); // complete
        registerState(1496, Material.DIODE_BLOCK_OFF).delay(3).facing(Facing.SOUTH); // complete
        registerState(1497, Material.DIODE_BLOCK_OFF).delay(3).facing(Facing.WEST); // complete
        registerState(1498, Material.DIODE_BLOCK_OFF).delay(3).facing(Facing.NORTH); // complete
        registerState(1499, Material.DIODE_BLOCK_OFF).delay(3).facing(Facing.EAST); // complete
        registerState(1500, Material.DIODE_BLOCK_OFF).delay(4).facing(Facing.SOUTH); // complete
        registerState(1501, Material.DIODE_BLOCK_OFF).delay(4).facing(Facing.WEST); // complete
        registerState(1502, Material.DIODE_BLOCK_OFF).delay(4).facing(Facing.NORTH); // complete
        registerState(1503, Material.DIODE_BLOCK_OFF).delay(4).facing(Facing.EAST); // complete
        registerState(1504, Material.DIODE_BLOCK_ON).delay(1).facing(Facing.SOUTH); // complete
        registerState(1505, Material.DIODE_BLOCK_ON).delay(1).facing(Facing.WEST); // complete
        registerState(1506, Material.DIODE_BLOCK_ON).delay(1).facing(Facing.NORTH); // complete
        registerState(1507, Material.DIODE_BLOCK_ON).delay(1).facing(Facing.EAST); // complete
        registerState(1508, Material.DIODE_BLOCK_ON).delay(2).facing(Facing.SOUTH); // complete
        registerState(1509, Material.DIODE_BLOCK_ON).delay(2).facing(Facing.WEST); // complete
        registerState(1510, Material.DIODE_BLOCK_ON).delay(2).facing(Facing.NORTH); // complete
        registerState(1511, Material.DIODE_BLOCK_ON).delay(2).facing(Facing.EAST); // complete
        registerState(1512, Material.DIODE_BLOCK_ON).delay(3).facing(Facing.SOUTH); // complete
        registerState(1513, Material.DIODE_BLOCK_ON).delay(3).facing(Facing.WEST); // complete
        registerState(1514, Material.DIODE_BLOCK_ON).delay(3).facing(Facing.NORTH); // complete
        registerState(1515, Material.DIODE_BLOCK_ON).delay(3).facing(Facing.EAST); // complete
        registerState(1516, Material.DIODE_BLOCK_ON).delay(4).facing(Facing.SOUTH); // complete
        registerState(1517, Material.DIODE_BLOCK_ON).delay(4).facing(Facing.WEST); // complete
        registerState(1518, Material.DIODE_BLOCK_ON).delay(4).facing(Facing.NORTH); // complete
        registerState(1519, Material.DIODE_BLOCK_ON).delay(4).facing(Facing.EAST); // complete
        registerState(1520, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_WHITE); // complete
        registerState(1521, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_ORANGE); // complete
        registerState(1522, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_MAGENTA); // complete
        registerState(1523, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_LIGHT_BLUE); // complete
        registerState(1524, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_YELLOW); // complete
        registerState(1525, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_GREEN); // complete
        registerState(1526, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_PINK); // complete
        registerState(1527, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_GRAY); // complete
        registerState(1528, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_LIGHT_GRAY); // complete
        registerState(1529, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_CYAN); // complete
        registerState(1530, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_PURPLE); // complete
        registerState(1531, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_BLUE); // complete
        registerState(1532, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_BROWN); // complete
        registerState(1533, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_GREEN); // complete
        registerState(1534, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_RED); // complete
        registerState(1535, Material.STAINED_GLASS).subMaterial(SubMaterial.COLOR_BLACK); // complete
        registerState(1536, Material.TRAP_DOOR).facing(Facing.SOUTH).half(Half.BOTTOM).height(0.3D); // complete
        registerState(1537, Material.TRAP_DOOR).facing(Facing.NORTH).half(Half.BOTTOM).height(0.3D); // complete
        registerState(1538, Material.TRAP_DOOR).facing(Facing.EAST).half(Half.BOTTOM).height(0.3D); // complete
        registerState(1539, Material.TRAP_DOOR).facing(Facing.WEST).half(Half.BOTTOM).height(0.3D); // complete
        registerState(1540, Material.TRAP_DOOR).facing(Facing.SOUTH).half(Half.BOTTOM).open().thick(0.3D); // complete
        registerState(1541, Material.TRAP_DOOR).facing(Facing.NORTH).half(Half.BOTTOM).open().thick(0.3D); // complete
        registerState(1542, Material.TRAP_DOOR).facing(Facing.EAST).half(Half.BOTTOM).open().thick(0.3D); // complete
        registerState(1543, Material.TRAP_DOOR).facing(Facing.WEST).half(Half.BOTTOM).open().thick(0.3D); // complete
        registerState(1544, Material.TRAP_DOOR).facing(Facing.SOUTH).half(Half.TOP).height(0.3D); // complete
        registerState(1545, Material.TRAP_DOOR).facing(Facing.NORTH).half(Half.TOP).height(0.3D); // complete
        registerState(1546, Material.TRAP_DOOR).facing(Facing.EAST).half(Half.TOP).height(0.3D); // complete
        registerState(1547, Material.TRAP_DOOR).facing(Facing.WEST).half(Half.TOP).height(0.3D); // complete
        registerState(1548, Material.TRAP_DOOR).facing(Facing.SOUTH).half(Half.TOP).open().thick(0.3D); // complete
        registerState(1549, Material.TRAP_DOOR).facing(Facing.NORTH).half(Half.TOP).open().thick(0.3D); // complete
        registerState(1550, Material.TRAP_DOOR).facing(Facing.EAST).half(Half.TOP).open().thick(0.3D); // complete
        registerState(1551, Material.TRAP_DOOR).facing(Facing.WEST).half(Half.TOP).open().thick(0.3D); // complete
        registerState(1552, Material.MONSTER_EGGS).subMaterial(SubMaterial.STONE); // complete
        registerState(1553, Material.MONSTER_EGGS).subMaterial(SubMaterial.COBBLESTONE); // complete
        registerState(1554, Material.MONSTER_EGGS).subMaterial(SubMaterial.STONE_BRICK_EGG); // complete
        registerState(1555, Material.MONSTER_EGGS).subMaterial(SubMaterial.MOSSY_STONE_BRICK_EGG); // complete
        registerState(1556, Material.MONSTER_EGGS).subMaterial(SubMaterial.CRACKED_STONE_BRICK_EGG); // complete
        registerState(1557, Material.MONSTER_EGGS).subMaterial(SubMaterial.CHISELED_STONE_BRICK_EGG); // complete
        registerState(1568, Material.SMOOTH_BRICK).subMaterial(SubMaterial.STONE_BRICK); // complete
        registerState(1569, Material.SMOOTH_BRICK).subMaterial(SubMaterial.MOSSY_STONE_BRICK); // complete
        registerState(1570, Material.SMOOTH_BRICK).subMaterial(SubMaterial.CRACKED_STONE_BRICK); // complete
        registerState(1571, Material.SMOOTH_BRICK).subMaterial(SubMaterial.CRACKED_STONE_BRICK); // complete
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
        registerState(1664, Material.PUMPKIN_STEM).age(0); // complete
        registerState(1665, Material.PUMPKIN_STEM).age(1); // complete
        registerState(1666, Material.PUMPKIN_STEM).age(2); // complete
        registerState(1667, Material.PUMPKIN_STEM).age(3); // complete
        registerState(1668, Material.PUMPKIN_STEM).age(4); // complete
        registerState(1669, Material.PUMPKIN_STEM).age(5); // complete
        registerState(1670, Material.PUMPKIN_STEM).age(6); // complete
        registerState(1671, Material.PUMPKIN_STEM).age(7); // complete
        registerState(1680, Material.MELON_STEM).age(0); // complete
        registerState(1681, Material.MELON_STEM).age(1); // complete
        registerState(1682, Material.MELON_STEM).age(2); // complete
        registerState(1683, Material.MELON_STEM).age(3); // complete
        registerState(1684, Material.MELON_STEM).age(4); // complete
        registerState(1685, Material.MELON_STEM).age(5); // complete
        registerState(1686, Material.MELON_STEM).age(6); // complete
        registerState(1687, Material.MELON_STEM).age(7); // complete
        registerState(1696, Material.VINE).facings(Facing.UP); // complete
        registerState(1697, Material.VINE).facings(Facing.SOUTH); // complete
        registerState(1698, Material.VINE).facings(Facing.WEST); // complete
        registerState(1699, Material.VINE).facings(Facing.SOUTH, Facing.WEST); // complete
        registerState(1700, Material.VINE).facings(Facing.NORTH); // complete
        registerState(1701, Material.VINE).facings(Facing.NORTH, Facing.SOUTH); // complete
        registerState(1702, Material.VINE).facings(Facing.NORTH, Facing.WEST); // complete
        registerState(1703, Material.VINE).facings(Facing.NORTH, Facing.SOUTH, Facing.WEST); // complete
        registerState(1704, Material.VINE).facing(Facing.EAST); // complete
        registerState(1705, Material.VINE).facings(Facing.EAST, Facing.SOUTH); // complete
        registerState(1706, Material.VINE).facings(Facing.EAST, Facing.WEST); // complete
        registerState(1707, Material.VINE).facings(Facing.EAST, Facing.SOUTH, Facing.WEST); // complete
        registerState(1708, Material.VINE).facings(Facing.EAST, Facing.NORTH); // complete
        registerState(1709, Material.VINE).facings(Facing.EAST, Facing.NORTH, Facing.SOUTH); // complete
        registerState(1710, Material.VINE).facings(Facing.EAST, Facing.NORTH, Facing.WEST); // complete
        registerState(1711, Material.VINE).facings(Facing.EAST, Facing.NORTH, Facing.SOUTH, Facing.WEST); // complete
        registerState(1712, Material.FENCE_GATE).facing(Facing.SOUTH); // complete
        registerState(1713, Material.FENCE_GATE).facing(Facing.WEST); // complete
        registerState(1714, Material.FENCE_GATE).facing(Facing.NORTH); // complete
        registerState(1715, Material.FENCE_GATE).facing(Facing.EAST); // complete
        registerState(1716, Material.FENCE_GATE).facing(Facing.SOUTH).open(); // complete
        registerState(1717, Material.FENCE_GATE).facing(Facing.WEST).open(); // complete
        registerState(1718, Material.FENCE_GATE).facing(Facing.NORTH).open(); // complete
        registerState(1719, Material.FENCE_GATE).facing(Facing.EAST).open(); // complete
        registerState(1720, Material.FENCE_GATE).facing(Facing.SOUTH).powered(); // complete
        registerState(1721, Material.FENCE_GATE).facing(Facing.WEST).powered(); // complete
        registerState(1722, Material.FENCE_GATE).facing(Facing.NORTH).powered(); // complete
        registerState(1723, Material.FENCE_GATE).facing(Facing.EAST).powered(); // complete
        registerState(1724, Material.FENCE_GATE).facing(Facing.SOUTH).open().powered(); // complete
        registerState(1725, Material.FENCE_GATE).facing(Facing.WEST).open().powered(); // complete
        registerState(1726, Material.FENCE_GATE).facing(Facing.NORTH).open().powered(); // complete
        registerState(1727, Material.FENCE_GATE).facing(Facing.EAST).open().powered(); // complete
        registerState(1728, Material.BRICK_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(1729, Material.BRICK_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(1730, Material.BRICK_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(1731, Material.BRICK_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(1732, Material.BRICK_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(1733, Material.BRICK_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(1734, Material.BRICK_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(1735, Material.BRICK_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(1744, Material.SMOOTH_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(1745, Material.SMOOTH_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(1746, Material.SMOOTH_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(1747, Material.SMOOTH_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(1748, Material.SMOOTH_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(1749, Material.SMOOTH_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(1750, Material.SMOOTH_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(1751, Material.SMOOTH_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(1760, Material.MYCEL); // complete
        registerState(1776, Material.WATER_LILY); // complete
        registerState(1792, Material.NETHER_BRICK); // complete
        registerState(1808, Material.NETHER_FENCE); // complete
        registerState(1824, Material.NETHER_BRICK_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(1825, Material.NETHER_BRICK_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(1826, Material.NETHER_BRICK_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(1827, Material.NETHER_BRICK_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(1828, Material.NETHER_BRICK_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(1829, Material.NETHER_BRICK_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(1830, Material.NETHER_BRICK_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(1831, Material.NETHER_BRICK_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(1840, Material.NETHER_WARTS).age(0); // complete
        registerState(1841, Material.NETHER_WARTS).age(1); // complete
        registerState(1842, Material.NETHER_WARTS).age(2); // complete
        registerState(1843, Material.NETHER_WARTS).age(3); // complete
        registerState(1856, Material.ENCHANTMENT_TABLE); // complete
        registerState(1872, Material.BREWING_STAND).filledPotions(); // complete
        registerState(1873, Material.BREWING_STAND).filledPotions(BrewingStandPotion.POTION_0); // complete
        registerState(1874, Material.BREWING_STAND).filledPotions(BrewingStandPotion.POTION_1); // complete
        registerState(1875, Material.BREWING_STAND).filledPotions(BrewingStandPotion.POTION_0, BrewingStandPotion.POTION_1); // complete
        registerState(1876, Material.BREWING_STAND).filledPotions(BrewingStandPotion.POTION_2); // complete
        registerState(1877, Material.BREWING_STAND).filledPotions(BrewingStandPotion.POTION_0, BrewingStandPotion.POTION_2); // complete
        registerState(1878, Material.BREWING_STAND).filledPotions(BrewingStandPotion.POTION_1, BrewingStandPotion.POTION_2); // complete
        registerState(1879, Material.BREWING_STAND).filledPotions(BrewingStandPotion.POTION_0, BrewingStandPotion.POTION_1, BrewingStandPotion.POTION_2); // complete
        registerState(1888, Material.CAULDRON).layers(0); // complete
        registerState(1889, Material.CAULDRON).layers(1); // complete
        registerState(1890, Material.CAULDRON).layers(2); // complete
        registerState(1891, Material.CAULDRON).layers(3); // complete
        registerState(1904, Material.ENDER_PORTAL); // complete
        registerState(1920, Material.ENDER_PORTAL_FRAME).facing(Facing.SOUTH); // complete
        registerState(1921, Material.ENDER_PORTAL_FRAME).facing(Facing.WEST); // complete
        registerState(1922, Material.ENDER_PORTAL_FRAME).facing(Facing.NORTH); // complete
        registerState(1923, Material.ENDER_PORTAL_FRAME).facing(Facing.EAST); // complete
        registerState(1924, Material.ENDER_PORTAL_FRAME).withEye().facing(Facing.SOUTH); // complete
        registerState(1925, Material.ENDER_PORTAL_FRAME).withEye().facing(Facing.WEST); // complete
        registerState(1926, Material.ENDER_PORTAL_FRAME).withEye().facing(Facing.NORTH); // complete
        registerState(1927, Material.ENDER_PORTAL_FRAME).withEye().facing(Facing.EAST); // complete
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
        registerState(2032, Material.COCOA).age(0).facing(Facing.SOUTH); // complete
        registerState(2033, Material.COCOA).age(0).facing(Facing.WEST); // complete
        registerState(2034, Material.COCOA).age(0).facing(Facing.NORTH); // complete
        registerState(2035, Material.COCOA).age(0).facing(Facing.EAST); // complete
        registerState(2036, Material.COCOA).age(1).facing(Facing.SOUTH); // complete
        registerState(2037, Material.COCOA).age(1).facing(Facing.WEST); // complete
        registerState(2038, Material.COCOA).age(1).facing(Facing.NORTH); // complete
        registerState(2039, Material.COCOA).age(1).facing(Facing.EAST); // complete
        registerState(2040, Material.COCOA).age(2).facing(Facing.SOUTH); // complete
        registerState(2041, Material.COCOA).age(2).facing(Facing.WEST); // complete
        registerState(2042, Material.COCOA).age(2).facing(Facing.NORTH); // complete
        registerState(2043, Material.COCOA).age(2).facing(Facing.EAST); // complete

        registerState(2048, Material.SANDSTONE_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2049, Material.SANDSTONE_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2050, Material.SANDSTONE_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2051, Material.SANDSTONE_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2052, Material.SANDSTONE_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2053, Material.SANDSTONE_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2054, Material.SANDSTONE_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2055, Material.SANDSTONE_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2064, Material.EMERALD_ORE); // complete
        registerState(2082, Material.ENDER_CHEST).facing(Facing.NORTH); // complete
        registerState(2083, Material.ENDER_CHEST).facing(Facing.SOUTH); // complete
        registerState(2084, Material.ENDER_CHEST).facing(Facing.WEST); // complete
        registerState(2085, Material.ENDER_CHEST).facing(Facing.EAST); // complete
        registerState(2096, Material.TRIPWIRE_HOOK).facing(Facing.SOUTH); // complete
        registerState(2097, Material.TRIPWIRE_HOOK).facing(Facing.WEST); // complete
        registerState(2098, Material.TRIPWIRE_HOOK).facing(Facing.NORTH); // complete
        registerState(2099, Material.TRIPWIRE_HOOK).facing(Facing.EAST); // complete
        registerState(2100, Material.TRIPWIRE_HOOK).attached().facing(Facing.SOUTH); // complete
        registerState(2101, Material.TRIPWIRE_HOOK).attached().facing(Facing.WEST); // complete
        registerState(2102, Material.TRIPWIRE_HOOK).attached().facing(Facing.NORTH); // complete
        registerState(2103, Material.TRIPWIRE_HOOK).attached().facing(Facing.EAST); // complete
        registerState(2104, Material.TRIPWIRE_HOOK).powered().facing(Facing.SOUTH); // complete
        registerState(2105, Material.TRIPWIRE_HOOK).powered().facing(Facing.WEST); // complete
        registerState(2106, Material.TRIPWIRE_HOOK).powered().facing(Facing.NORTH); // complete
        registerState(2107, Material.TRIPWIRE_HOOK).powered().facing(Facing.EAST); // complete
        registerState(2108, Material.TRIPWIRE_HOOK).attached().powered().facing(Facing.SOUTH); // complete
        registerState(2109, Material.TRIPWIRE_HOOK).attached().powered().facing(Facing.WEST); // complete
        registerState(2110, Material.TRIPWIRE_HOOK).attached().powered().facing(Facing.NORTH); // complete
        registerState(2111, Material.TRIPWIRE_HOOK).attached().powered().facing(Facing.EAST); // complete
        registerState(2112, Material.TRIPWIRE); // complete
        registerState(2113, Material.TRIPWIRE).powered(); // complete
        registerState(2114, Material.TRIPWIRE).suspended(); // complete
        registerState(2115, Material.TRIPWIRE).suspended().powered(); // complete
        registerState(2116, Material.TRIPWIRE).attached(); // complete
        registerState(2117, Material.TRIPWIRE).attached().powered(); // complete
        registerState(2118, Material.TRIPWIRE).attached().suspended(); // complete
        registerState(2119, Material.TRIPWIRE).attached().suspended().powered(); // complete
        registerState(2120, Material.TRIPWIRE).disarmed(); // complete
        registerState(2121, Material.TRIPWIRE).disarmed().powered(); // complete
        registerState(2122, Material.TRIPWIRE).disarmed().suspended(); // complete
        registerState(2123, Material.TRIPWIRE).disarmed().suspended().powered(); // complete
        registerState(2124, Material.TRIPWIRE).disarmed().attached(); // complete
        registerState(2125, Material.TRIPWIRE).disarmed().attached().powered(); // complete
        registerState(2126, Material.TRIPWIRE).disarmed().attached().suspended(); // complete
        registerState(2127, Material.TRIPWIRE).disarmed().attached().suspended().powered(); // complete
        registerState(2128, Material.EMERALD_BLOCK); // complete
        registerState(2144, Material.SPRUCE_WOOD_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2145, Material.SPRUCE_WOOD_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2146, Material.SPRUCE_WOOD_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2147, Material.SPRUCE_WOOD_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2148, Material.SPRUCE_WOOD_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2149, Material.SPRUCE_WOOD_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2150, Material.SPRUCE_WOOD_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2151, Material.SPRUCE_WOOD_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete

        registerState(2160, Material.BIRCH_WOOD_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2161, Material.BIRCH_WOOD_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2162, Material.BIRCH_WOOD_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2163, Material.BIRCH_WOOD_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2164, Material.BIRCH_WOOD_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2165, Material.BIRCH_WOOD_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2166, Material.BIRCH_WOOD_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2167, Material.BIRCH_WOOD_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2176, Material.JUNGLE_WOOD_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2177, Material.JUNGLE_WOOD_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2178, Material.JUNGLE_WOOD_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2179, Material.JUNGLE_WOOD_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2180, Material.JUNGLE_WOOD_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2181, Material.JUNGLE_WOOD_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2182, Material.JUNGLE_WOOD_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2183, Material.JUNGLE_WOOD_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2192, Material.COMMAND); // complete
        registerState(2193, Material.COMMAND).triggered(); // complete
        registerState(2208, Material.BEACON); // complete
        registerState(2224, Material.COBBLE_WALL).subMaterial(SubMaterial.COBBLESTONE_WALL); // complete
        registerState(2225, Material.COBBLE_WALL).subMaterial(SubMaterial.MOSSY_COBBLESTONE_WALL); // complete
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
        registerState(2256, Material.CARROT).age(0); // complete
        registerState(2257, Material.CARROT).age(1); // complete
        registerState(2258, Material.CARROT).age(2); // complete
        registerState(2259, Material.CARROT).age(3); // complete
        registerState(2260, Material.CARROT).age(4); // complete
        registerState(2261, Material.CARROT).age(5); // complete
        registerState(2262, Material.CARROT).age(6); // complete
        registerState(2263, Material.CARROT).age(7); // complete
        registerState(2272, Material.POTATO).age(0); // complete
        registerState(2273, Material.POTATO).age(1); // complete
        registerState(2274, Material.POTATO).age(2); // complete
        registerState(2275, Material.POTATO).age(3); // complete
        registerState(2276, Material.POTATO).age(4); // complete
        registerState(2277, Material.POTATO).age(5); // complete
        registerState(2278, Material.POTATO).age(6); // complete
        registerState(2279, Material.POTATO).age(7); // complete
        registerState(2288, Material.WOOD_BUTTON).facing(Facing.DOWN); // complete
        registerState(2289, Material.WOOD_BUTTON).facing(Facing.EAST); // complete
        registerState(2290, Material.WOOD_BUTTON).facing(Facing.WEST); // complete
        registerState(2291, Material.WOOD_BUTTON).facing(Facing.SOUTH); // complete
        registerState(2292, Material.WOOD_BUTTON).facing(Facing.NORTH); // complete
        registerState(2293, Material.WOOD_BUTTON).facing(Facing.UP); // complete
        registerState(2296, Material.WOOD_BUTTON).powered().facing(Facing.DOWN); // complete
        registerState(2297, Material.WOOD_BUTTON).powered().facing(Facing.EAST); // complete
        registerState(2298, Material.WOOD_BUTTON).powered().facing(Facing.WEST); // complete
        registerState(2299, Material.WOOD_BUTTON).powered().facing(Facing.SOUTH); // complete
        registerState(2300, Material.WOOD_BUTTON).powered().facing(Facing.NORTH); // complete
        registerState(2301, Material.WOOD_BUTTON).powered().facing(Facing.UP); // complete
        registerState(2304, Material.SKULL).facing(Facing.DOWN); // complete
        registerState(2305, Material.SKULL).facing(Facing.UP); // complete
        registerState(2306, Material.SKULL).facing(Facing.NORTH); // complete
        registerState(2307, Material.SKULL).facing(Facing.SOUTH); // complete
        registerState(2308, Material.SKULL).facing(Facing.WEST); // complete
        registerState(2309, Material.SKULL).facing(Facing.EAST); // complete
        registerState(2312, Material.SKULL).noDrop().facing(Facing.DOWN); // complete
        registerState(2313, Material.SKULL).noDrop().facing(Facing.UP); // complete
        registerState(2314, Material.SKULL).noDrop().facing(Facing.NORTH); // complete
        registerState(2315, Material.SKULL).noDrop().facing(Facing.SOUTH); // complete
        registerState(2316, Material.SKULL).noDrop().facing(Facing.WEST); // complete
        registerState(2317, Material.SKULL).noDrop().facing(Facing.EAST); // complete
        registerState(2320, Material.ANVIL).damage(0).facing(Facing.SOUTH); // complete
        registerState(2321, Material.ANVIL).damage(0).facing(Facing.WEST); // complete
        registerState(2322, Material.ANVIL).damage(0).facing(Facing.NORTH); // complete
        registerState(2323, Material.ANVIL).damage(0).facing(Facing.EAST); // complete
        registerState(2324, Material.ANVIL).damage(1).facing(Facing.SOUTH); // complete
        registerState(2325, Material.ANVIL).damage(1).facing(Facing.WEST); // complete
        registerState(2326, Material.ANVIL).damage(1).facing(Facing.NORTH); // complete
        registerState(2327, Material.ANVIL).damage(1).facing(Facing.EAST); // complete
        registerState(2328, Material.ANVIL).damage(2).facing(Facing.SOUTH); // complete
        registerState(2329, Material.ANVIL).damage(2).facing(Facing.WEST); // complete
        registerState(2330, Material.ANVIL).damage(2).facing(Facing.NORTH); // complete
        registerState(2331, Material.ANVIL).damage(2).facing(Facing.EAST); // complete
        registerState(2338, Material.TRAPPED_CHEST).facing(Facing.NORTH); // complete
        registerState(2339, Material.TRAPPED_CHEST).facing(Facing.SOUTH); // complete
        registerState(2340, Material.TRAPPED_CHEST).facing(Facing.WEST); // complete
        registerState(2341, Material.TRAPPED_CHEST).facing(Facing.EAST); // complete
        registerState(2352, Material.GOLD_PLATE).power(0); // complete
        registerState(2353, Material.GOLD_PLATE).power(1); // complete
        registerState(2354, Material.GOLD_PLATE).power(2); // complete
        registerState(2355, Material.GOLD_PLATE).power(3); // complete
        registerState(2356, Material.GOLD_PLATE).power(4); // complete
        registerState(2357, Material.GOLD_PLATE).power(5); // complete
        registerState(2358, Material.GOLD_PLATE).power(6); // complete
        registerState(2359, Material.GOLD_PLATE).power(7); // complete
        registerState(2360, Material.GOLD_PLATE).power(8); // complete
        registerState(2361, Material.GOLD_PLATE).power(9); // complete
        registerState(2362, Material.GOLD_PLATE).power(10); // complete
        registerState(2363, Material.GOLD_PLATE).power(11); // complete
        registerState(2364, Material.GOLD_PLATE).power(12); // complete
        registerState(2365, Material.GOLD_PLATE).power(13); // complete
        registerState(2366, Material.GOLD_PLATE).power(14); // complete
        registerState(2367, Material.GOLD_PLATE).power(15); // complete
        registerState(2368, Material.IRON_PLATE).power(0); // complete
        registerState(2369, Material.IRON_PLATE).power(1); // complete
        registerState(2370, Material.IRON_PLATE).power(2); // complete
        registerState(2371, Material.IRON_PLATE).power(3); // complete
        registerState(2372, Material.IRON_PLATE).power(4); // complete
        registerState(2373, Material.IRON_PLATE).power(5); // complete
        registerState(2374, Material.IRON_PLATE).power(6); // complete
        registerState(2375, Material.IRON_PLATE).power(7); // complete
        registerState(2376, Material.IRON_PLATE).power(8); // complete
        registerState(2377, Material.IRON_PLATE).power(9); // complete
        registerState(2378, Material.IRON_PLATE).power(10); // complete
        registerState(2379, Material.IRON_PLATE).power(11); // complete
        registerState(2380, Material.IRON_PLATE).power(12); // complete
        registerState(2381, Material.IRON_PLATE).power(13); // complete
        registerState(2382, Material.IRON_PLATE).power(14); // complete
        registerState(2383, Material.IRON_PLATE).power(15); // complete
        registerState(2384, Material.REDSTONE_COMPARATOR_OFF).comparatorMode(ComparatorMode.COMPARE).facing(Facing.SOUTH); // complete
        registerState(2385, Material.REDSTONE_COMPARATOR_OFF).comparatorMode(ComparatorMode.COMPARE).facing(Facing.WEST); // complete
        registerState(2386, Material.REDSTONE_COMPARATOR_OFF).comparatorMode(ComparatorMode.COMPARE).facing(Facing.NORTH); // complete
        registerState(2387, Material.REDSTONE_COMPARATOR_OFF).comparatorMode(ComparatorMode.COMPARE).facing(Facing.EAST); // complete
        registerState(2388, Material.REDSTONE_COMPARATOR_OFF).comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.SOUTH); // complete
        registerState(2389, Material.REDSTONE_COMPARATOR_OFF).comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.WEST); // complete
        registerState(2390, Material.REDSTONE_COMPARATOR_OFF).comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.NORTH); // complete
        registerState(2391, Material.REDSTONE_COMPARATOR_OFF).comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.EAST); // complete
        registerState(2392, Material.REDSTONE_COMPARATOR_OFF).powered().comparatorMode(ComparatorMode.COMPARE).facing(Facing.SOUTH); // complete
        registerState(2393, Material.REDSTONE_COMPARATOR_OFF).powered().comparatorMode(ComparatorMode.COMPARE).facing(Facing.WEST); // complete
        registerState(2394, Material.REDSTONE_COMPARATOR_OFF).powered().comparatorMode(ComparatorMode.COMPARE).facing(Facing.NORTH); // complete
        registerState(2395, Material.REDSTONE_COMPARATOR_OFF).powered().comparatorMode(ComparatorMode.COMPARE).facing(Facing.EAST); // complete
        registerState(2396, Material.REDSTONE_COMPARATOR_OFF).powered().comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.SOUTH); // complete
        registerState(2397, Material.REDSTONE_COMPARATOR_OFF).powered().comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.WEST); // complete
        registerState(2398, Material.REDSTONE_COMPARATOR_OFF).powered().comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.NORTH); // complete
        registerState(2399, Material.REDSTONE_COMPARATOR_OFF).powered().comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.EAST); // complete
        registerState(2400, Material.REDSTONE_COMPARATOR_ON).comparatorMode(ComparatorMode.COMPARE).facing(Facing.SOUTH); // complete
        registerState(2401, Material.REDSTONE_COMPARATOR_ON).comparatorMode(ComparatorMode.COMPARE).facing(Facing.WEST); // complete
        registerState(2402, Material.REDSTONE_COMPARATOR_ON).comparatorMode(ComparatorMode.COMPARE).facing(Facing.NORTH); // complete
        registerState(2403, Material.REDSTONE_COMPARATOR_ON).comparatorMode(ComparatorMode.COMPARE).facing(Facing.EAST); // complete
        registerState(2404, Material.REDSTONE_COMPARATOR_ON).comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.SOUTH); // complete
        registerState(2405, Material.REDSTONE_COMPARATOR_ON).comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.WEST); // complete
        registerState(2406, Material.REDSTONE_COMPARATOR_ON).comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.NORTH); // complete
        registerState(2407, Material.REDSTONE_COMPARATOR_ON).comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.EAST); // complete
        registerState(2408, Material.REDSTONE_COMPARATOR_ON).powered().comparatorMode(ComparatorMode.COMPARE).facing(Facing.SOUTH); // complete
        registerState(2409, Material.REDSTONE_COMPARATOR_ON).powered().comparatorMode(ComparatorMode.COMPARE).facing(Facing.WEST); // complete
        registerState(2410, Material.REDSTONE_COMPARATOR_ON).powered().comparatorMode(ComparatorMode.COMPARE).facing(Facing.NORTH); // complete
        registerState(2411, Material.REDSTONE_COMPARATOR_ON).powered().comparatorMode(ComparatorMode.COMPARE).facing(Facing.EAST); // complete
        registerState(2412, Material.REDSTONE_COMPARATOR_ON).powered().comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.SOUTH); // complete
        registerState(2413, Material.REDSTONE_COMPARATOR_ON).powered().comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.WEST); // complete
        registerState(2414, Material.REDSTONE_COMPARATOR_ON).powered().comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.NORTH); // complete
        registerState(2415, Material.REDSTONE_COMPARATOR_ON).powered().comparatorMode(ComparatorMode.SUBTRACT).facing(Facing.EAST); // complete
        registerState(2416, Material.DAYLIGHT_DETECTOR).power(0); // complete
        registerState(2417, Material.DAYLIGHT_DETECTOR).power(1); // complete
        registerState(2418, Material.DAYLIGHT_DETECTOR).power(2); // complete
        registerState(2419, Material.DAYLIGHT_DETECTOR).power(3); // complete
        registerState(2420, Material.DAYLIGHT_DETECTOR).power(4); // complete
        registerState(2421, Material.DAYLIGHT_DETECTOR).power(5); // complete
        registerState(2422, Material.DAYLIGHT_DETECTOR).power(6); // complete
        registerState(2423, Material.DAYLIGHT_DETECTOR).power(7); // complete
        registerState(2424, Material.DAYLIGHT_DETECTOR).power(8); // complete
        registerState(2425, Material.DAYLIGHT_DETECTOR).power(9); // complete
        registerState(2426, Material.DAYLIGHT_DETECTOR).power(10); // complete
        registerState(2427, Material.DAYLIGHT_DETECTOR).power(11); // complete
        registerState(2428, Material.DAYLIGHT_DETECTOR).power(12); // complete
        registerState(2429, Material.DAYLIGHT_DETECTOR).power(13); // complete
        registerState(2430, Material.DAYLIGHT_DETECTOR).power(14); // complete
        registerState(2431, Material.DAYLIGHT_DETECTOR).power(15); // complete
        registerState(2432, Material.REDSTONE_BLOCK); // complete
        registerState(2448, Material.QUARTZ_ORE); // complete
        registerState(2464, Material.HOPPER).facing(Facing.DOWN); // complete
        registerState(2466, Material.HOPPER).facing(Facing.NORTH); // complete
        registerState(2467, Material.HOPPER).facing(Facing.SOUTH); // complete
        registerState(2468, Material.HOPPER).facing(Facing.WEST); // complete
        registerState(2469, Material.HOPPER).facing(Facing.EAST); // complete
        registerState(2472, Material.HOPPER).facing(Facing.DOWN).powered(); // complete
        registerState(2474, Material.HOPPER).facing(Facing.NORTH).powered(); // complete
        registerState(2475, Material.HOPPER).facing(Facing.SOUTH).powered(); // complete
        registerState(2476, Material.HOPPER).facing(Facing.WEST).powered(); // complete
        registerState(2477, Material.HOPPER).facing(Facing.EAST).powered(); // complete
        registerState(2480, Material.QUARTZ_BLOCK).subMaterial(SubMaterial.QUARTZ_NORMAL); // complete
        registerState(2481, Material.QUARTZ_BLOCK).subMaterial(SubMaterial.QUARTZ_CHISELED); // complete
        registerState(2482, Material.QUARTZ_BLOCK).subMaterial(SubMaterial.QUARTZ_PILLAR).axis(Facing.Axis.Y); // complete
        registerState(2483, Material.QUARTZ_BLOCK).subMaterial(SubMaterial.QUARTZ_PILLAR).axis(Facing.Axis.X); // complete
        registerState(2484, Material.QUARTZ_BLOCK).subMaterial(SubMaterial.QUARTZ_PILLAR).axis(Facing.Axis.Z); // complete
        registerState(2496, Material.QUARTZ_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2497, Material.QUARTZ_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2498, Material.QUARTZ_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2499, Material.QUARTZ_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2500, Material.QUARTZ_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2501, Material.QUARTZ_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2502, Material.QUARTZ_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2503, Material.QUARTZ_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2512, Material.ACTIVATOR_RAIL).shape(BlockShape.NORTH_SOUTH); // complete
        registerState(2513, Material.ACTIVATOR_RAIL).shape(BlockShape.EAST_WEST); // complete
        registerState(2514, Material.ACTIVATOR_RAIL).shape(BlockShape.ASCENDING_EAST); // complete
        registerState(2515, Material.ACTIVATOR_RAIL).shape(BlockShape.ASCENDING_WEST); // complete
        registerState(2516, Material.ACTIVATOR_RAIL).shape(BlockShape.ASCENDING_NORTH); // complete
        registerState(2517, Material.ACTIVATOR_RAIL).shape(BlockShape.ASCENDING_SOUTH); // complete
        registerState(2520, Material.ACTIVATOR_RAIL).shape(BlockShape.NORTH_SOUTH).powered(); // complete
        registerState(2521, Material.ACTIVATOR_RAIL).shape(BlockShape.EAST_WEST).powered(); // complete
        registerState(2522, Material.ACTIVATOR_RAIL).shape(BlockShape.ASCENDING_EAST).powered(); // complete
        registerState(2523, Material.ACTIVATOR_RAIL).shape(BlockShape.ASCENDING_WEST).powered(); // complete
        registerState(2524, Material.ACTIVATOR_RAIL).shape(BlockShape.ASCENDING_NORTH).powered(); // complete
        registerState(2525, Material.ACTIVATOR_RAIL).shape(BlockShape.ASCENDING_SOUTH).powered(); // complete
        registerState(2528, Material.DROPPER).facing(Facing.DOWN); // complete
        registerState(2529, Material.DROPPER).facing(Facing.UP); // complete
        registerState(2530, Material.DROPPER).facing(Facing.NORTH); // complete
        registerState(2531, Material.DROPPER).facing(Facing.SOUTH); // complete
        registerState(2532, Material.DROPPER).facing(Facing.WEST); // complete
        registerState(2533, Material.DROPPER).facing(Facing.EAST); // complete
        registerState(2536, Material.DROPPER).facing(Facing.DOWN).triggered(); // complete
        registerState(2537, Material.DROPPER).facing(Facing.UP).triggered(); // complete
        registerState(2538, Material.DROPPER).facing(Facing.NORTH).triggered(); // complete
        registerState(2539, Material.DROPPER).facing(Facing.SOUTH).triggered(); // complete
        registerState(2540, Material.DROPPER).facing(Facing.WEST).triggered(); // complete
        registerState(2541, Material.DROPPER).facing(Facing.EAST).triggered(); // complete
        registerState(2544, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_WHITE); // complete
        registerState(2545, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_ORANGE); // complete
        registerState(2546, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_MAGENTA); // complete
        registerState(2547, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_LIGHT_BLUE); // complete
        registerState(2548, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_YELLOW); // complete
        registerState(2549, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_GREEN); // complete
        registerState(2550, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_PINK); // complete
        registerState(2551, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_GRAY); // complete
        registerState(2552, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_LIGHT_GRAY); // complete
        registerState(2553, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_CYAN); // complete
        registerState(2554, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_PURPLE); // complete
        registerState(2555, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_BLUE); // complete
        registerState(2556, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_BROWN); // complete
        registerState(2557, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_GREEN); // complete
        registerState(2558, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_RED); // complete
        registerState(2559, Material.STAINED_CLAY).subMaterial(SubMaterial.COLOR_BLACK); // complete
        registerState(2560, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_WHITE); // complete
        registerState(2561, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_ORANGE); // complete
        registerState(2562, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_MAGENTA); // complete
        registerState(2563, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_LIGHT_BLUE); // complete
        registerState(2564, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_YELLOW); // complete
        registerState(2565, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_GREEN); // complete
        registerState(2566, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_PINK); // complete
        registerState(2567, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_GRAY); // complete
        registerState(2568, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_LIGHT_GRAY); // complete
        registerState(2569, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_CYAN); // complete
        registerState(2570, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_PURPLE); // complete
        registerState(2571, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_BLUE); // complete
        registerState(2572, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_BROWN); // complete
        registerState(2573, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_GREEN); // complete
        registerState(2574, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_RED); // complete
        registerState(2575, Material.STAINED_GLASS_PANE).subMaterial(SubMaterial.COLOR_BLACK); // complete
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
        registerState(2608, Material.ACACIA_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2609, Material.ACACIA_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2610, Material.ACACIA_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2611, Material.ACACIA_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2612, Material.ACACIA_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2613, Material.ACACIA_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2614, Material.ACACIA_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2615, Material.ACACIA_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2624, Material.DARK_OAK_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2625, Material.DARK_OAK_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2626, Material.DARK_OAK_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2627, Material.DARK_OAK_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2628, Material.DARK_OAK_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2629, Material.DARK_OAK_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2630, Material.DARK_OAK_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2631, Material.DARK_OAK_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
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
        registerState(2688, Material.PRISMARINE).subMaterial(SubMaterial.PRISMARINE_NORMAL); // complete
        registerState(2689, Material.PRISMARINE).subMaterial(SubMaterial.PRISMARINE_BRICKS); // complete
        registerState(2690, Material.PRISMARINE).subMaterial(SubMaterial.PRISMARINE_DARK); // complete
        registerState(2704, Material.SEA_LANTERN); // complete
        registerState(2720, Material.HAY_BLOCK).axis(Facing.Axis.Y); // complete
        registerState(2724, Material.HAY_BLOCK).axis(Facing.Axis.X); // complete
        registerState(2728, Material.HAY_BLOCK).axis(Facing.Axis.Z); // complete
        registerState(2736, Material.CARPET).subMaterial(SubMaterial.COLOR_WHITE); // complete;
        registerState(2737, Material.CARPET).subMaterial(SubMaterial.COLOR_ORANGE); // complete
        registerState(2738, Material.CARPET).subMaterial(SubMaterial.COLOR_MAGENTA); // complete
        registerState(2739, Material.CARPET).subMaterial(SubMaterial.COLOR_LIGHT_BLUE); // complete
        registerState(2740, Material.CARPET).subMaterial(SubMaterial.COLOR_YELLOW); // complete
        registerState(2741, Material.CARPET).subMaterial(SubMaterial.COLOR_GREEN); // complete
        registerState(2742, Material.CARPET).subMaterial(SubMaterial.COLOR_PINK); // complete
        registerState(2743, Material.CARPET).subMaterial(SubMaterial.COLOR_GRAY); // complete
        registerState(2744, Material.CARPET).subMaterial(SubMaterial.COLOR_LIGHT_GRAY); // complete
        registerState(2745, Material.CARPET).subMaterial(SubMaterial.COLOR_CYAN); // complete
        registerState(2746, Material.CARPET).subMaterial(SubMaterial.COLOR_PURPLE); // complete
        registerState(2747, Material.CARPET).subMaterial(SubMaterial.COLOR_BLUE); // complete
        registerState(2748, Material.CARPET).subMaterial(SubMaterial.COLOR_BROWN); // complete
        registerState(2749, Material.CARPET).subMaterial(SubMaterial.COLOR_GREEN); // complete
        registerState(2750, Material.CARPET).subMaterial(SubMaterial.COLOR_RED); // complete
        registerState(2751, Material.CARPET).subMaterial(SubMaterial.COLOR_BLACK); // complete
        registerState(2752, Material.HARD_CLAY); // complete
        registerState(2768, Material.COAL_BLOCK); // complete
        registerState(2784, Material.PACKED_ICE); // complete
        registerState(2800, Material.DOUBLE_PLANT).subMaterial(SubMaterial.PLANT_SUN_FLOWER).half(Half.BOTTOM).facing(Facing.EAST); // complete
        registerState(2801, Material.DOUBLE_PLANT).subMaterial(SubMaterial.PLANT_LILAC).half(Half.BOTTOM).facing(Facing.EAST); // complete
        registerState(2802, Material.DOUBLE_PLANT).subMaterial(SubMaterial.PLANT_TALL_GRASS).half(Half.BOTTOM).facing(Facing.EAST); // complete
        registerState(2803, Material.DOUBLE_PLANT).subMaterial(SubMaterial.PLANT_LARGE_FERN).half(Half.BOTTOM).facing(Facing.EAST); // complete
        registerState(2804, Material.DOUBLE_PLANT).subMaterial(SubMaterial.PLANT_RED_ROSE).half(Half.BOTTOM).facing(Facing.EAST); // complete
        registerState(2805, Material.DOUBLE_PLANT).subMaterial(SubMaterial.PLANT_PEONY).half(Half.BOTTOM).facing(Facing.EAST); // complete
        registerState(2808, Material.DOUBLE_PLANT);
        registerState(2809, Material.DOUBLE_PLANT);
        registerState(2810, Material.DOUBLE_PLANT).half(Half.TOP).facing(Facing.NORTH); // complete
        registerState(2811, Material.DOUBLE_PLANT);
        registerState(2816, Material.STANDING_BANNER).rotation(0); // complete
        registerState(2817, Material.STANDING_BANNER).rotation(1); // complete
        registerState(2818, Material.STANDING_BANNER).rotation(2); // complete
        registerState(2819, Material.STANDING_BANNER).rotation(3); // complete
        registerState(2820, Material.STANDING_BANNER).rotation(4); // complete
        registerState(2821, Material.STANDING_BANNER).rotation(5); // complete
        registerState(2822, Material.STANDING_BANNER).rotation(6); // complete
        registerState(2823, Material.STANDING_BANNER).rotation(7); // complete
        registerState(2824, Material.STANDING_BANNER).rotation(8); // complete
        registerState(2825, Material.STANDING_BANNER).rotation(9); // complete
        registerState(2826, Material.STANDING_BANNER).rotation(10); // complete
        registerState(2827, Material.STANDING_BANNER).rotation(11); // complete
        registerState(2828, Material.STANDING_BANNER).rotation(12); // complete
        registerState(2829, Material.STANDING_BANNER).rotation(13); // complete
        registerState(2830, Material.STANDING_BANNER).rotation(14); // complete
        registerState(2831, Material.STANDING_BANNER).rotation(15); // complete
        registerState(2834, Material.WALL_BANNER).facing(Facing.NORTH); // complete
        registerState(2835, Material.WALL_BANNER).facing(Facing.SOUTH); // complete
        registerState(2836, Material.WALL_BANNER).facing(Facing.WEST); // complete
        registerState(2837, Material.WALL_BANNER).facing(Facing.EAST); // complete
        registerState(2848, Material.DAYLIGHT_DETECTOR_INVERTED).power(0); // complete
        registerState(2849, Material.DAYLIGHT_DETECTOR_INVERTED).power(1); // complete
        registerState(2850, Material.DAYLIGHT_DETECTOR_INVERTED).power(2); // complete
        registerState(2851, Material.DAYLIGHT_DETECTOR_INVERTED).power(3); // complete
        registerState(2852, Material.DAYLIGHT_DETECTOR_INVERTED).power(4); // complete
        registerState(2853, Material.DAYLIGHT_DETECTOR_INVERTED).power(5); // complete
        registerState(2854, Material.DAYLIGHT_DETECTOR_INVERTED).power(6); // complete
        registerState(2855, Material.DAYLIGHT_DETECTOR_INVERTED).power(7); // complete
        registerState(2856, Material.DAYLIGHT_DETECTOR_INVERTED).power(8); // complete
        registerState(2857, Material.DAYLIGHT_DETECTOR_INVERTED).power(9); // complete
        registerState(2858, Material.DAYLIGHT_DETECTOR_INVERTED).power(10); // complete
        registerState(2859, Material.DAYLIGHT_DETECTOR_INVERTED).power(11); // complete
        registerState(2860, Material.DAYLIGHT_DETECTOR_INVERTED).power(12); // complete
        registerState(2861, Material.DAYLIGHT_DETECTOR_INVERTED).power(13); // complete
        registerState(2862, Material.DAYLIGHT_DETECTOR_INVERTED).power(14); // complete
        registerState(2863, Material.DAYLIGHT_DETECTOR_INVERTED).power(15); // complete
        registerState(2864, Material.RED_SANDSTONE).subMaterial(SubMaterial.SANDSTONE_NORMAL); // complete
        registerState(2865, Material.RED_SANDSTONE).subMaterial(SubMaterial.SANDSTONE_CHISELED); // complete
        registerState(2866, Material.RED_SANDSTONE).subMaterial(SubMaterial.SANDSTONE_SMOOTH); // complete
        registerState(2880, Material.RED_SANDSTONE_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2881, Material.RED_SANDSTONE_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2882, Material.RED_SANDSTONE_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2883, Material.RED_SANDSTONE_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2884, Material.RED_SANDSTONE_STAIRS).facing(Facing.EAST).half(Half.BOTTOM); // complete
        registerState(2885, Material.RED_SANDSTONE_STAIRS).facing(Facing.WEST).half(Half.BOTTOM); // complete
        registerState(2886, Material.RED_SANDSTONE_STAIRS).facing(Facing.SOUTH).half(Half.BOTTOM); // complete
        registerState(2887, Material.RED_SANDSTONE_STAIRS).facing(Facing.NORTH).half(Half.BOTTOM); // complete
        registerState(2896, Material.DOUBLE_STONE_SLAB2);
        registerState(2904, Material.DOUBLE_STONE_SLAB2);
        registerState(2912, Material.STONE_SLAB2);
        registerState(2920, Material.STONE_SLAB2);
        registerState(2928, Material.SPRUCE_FENCE_GATE).facing(Facing.SOUTH); // complete
        registerState(2929, Material.SPRUCE_FENCE_GATE).facing(Facing.WEST); // complete
        registerState(2930, Material.SPRUCE_FENCE_GATE).facing(Facing.NORTH); // complete
        registerState(2931, Material.SPRUCE_FENCE_GATE).facing(Facing.EAST); // complete
        registerState(2932, Material.SPRUCE_FENCE_GATE).facing(Facing.SOUTH).open(); // complete
        registerState(2933, Material.SPRUCE_FENCE_GATE).facing(Facing.WEST).open(); // complete
        registerState(2934, Material.SPRUCE_FENCE_GATE).facing(Facing.NORTH).open(); // complete
        registerState(2935, Material.SPRUCE_FENCE_GATE).facing(Facing.EAST).open(); // complete
        registerState(2936, Material.SPRUCE_FENCE_GATE).facing(Facing.SOUTH).powered(); // complete
        registerState(2937, Material.SPRUCE_FENCE_GATE).facing(Facing.WEST).powered(); // complete
        registerState(2938, Material.SPRUCE_FENCE_GATE).facing(Facing.NORTH).powered(); // complete
        registerState(2939, Material.SPRUCE_FENCE_GATE).facing(Facing.EAST).powered(); // complete
        registerState(2940, Material.SPRUCE_FENCE_GATE).facing(Facing.SOUTH).open().powered(); // complete
        registerState(2941, Material.SPRUCE_FENCE_GATE).facing(Facing.WEST).open().powered(); // complete
        registerState(2942, Material.SPRUCE_FENCE_GATE).facing(Facing.NORTH).open().powered(); // complete
        registerState(2943, Material.SPRUCE_FENCE_GATE).facing(Facing.EAST).open().powered(); // complete
        registerState(2944, Material.BIRCH_FENCE_GATE).facing(Facing.SOUTH); // complete
        registerState(2945, Material.BIRCH_FENCE_GATE).facing(Facing.WEST); // complete
        registerState(2946, Material.BIRCH_FENCE_GATE).facing(Facing.NORTH); // complete
        registerState(2947, Material.BIRCH_FENCE_GATE).facing(Facing.EAST); // complete
        registerState(2948, Material.BIRCH_FENCE_GATE).facing(Facing.SOUTH).open(); // complete
        registerState(2949, Material.BIRCH_FENCE_GATE).facing(Facing.WEST).open(); // complete
        registerState(2950, Material.BIRCH_FENCE_GATE).facing(Facing.NORTH).open(); // complete
        registerState(2951, Material.BIRCH_FENCE_GATE).facing(Facing.EAST).open(); // complete
        registerState(2952, Material.BIRCH_FENCE_GATE).facing(Facing.SOUTH).powered(); // complete
        registerState(2953, Material.BIRCH_FENCE_GATE).facing(Facing.WEST).powered(); // complete
        registerState(2954, Material.BIRCH_FENCE_GATE).facing(Facing.NORTH).powered(); // complete
        registerState(2955, Material.BIRCH_FENCE_GATE).facing(Facing.EAST).powered(); // complete
        registerState(2956, Material.BIRCH_FENCE_GATE).facing(Facing.SOUTH).open().powered(); // complete
        registerState(2957, Material.BIRCH_FENCE_GATE).facing(Facing.WEST).open().powered(); // complete
        registerState(2958, Material.BIRCH_FENCE_GATE).facing(Facing.NORTH).open().powered(); // complete
        registerState(2959, Material.BIRCH_FENCE_GATE).facing(Facing.EAST).open().powered(); // complete
        registerState(2960, Material.JUNGLE_FENCE_GATE).facing(Facing.SOUTH); // complete
        registerState(2961, Material.JUNGLE_FENCE_GATE).facing(Facing.WEST); // complete
        registerState(2962, Material.JUNGLE_FENCE_GATE).facing(Facing.NORTH); // complete
        registerState(2963, Material.JUNGLE_FENCE_GATE).facing(Facing.EAST); // complete
        registerState(2964, Material.JUNGLE_FENCE_GATE).facing(Facing.SOUTH).open(); // complete
        registerState(2965, Material.JUNGLE_FENCE_GATE).facing(Facing.WEST).open(); // complete
        registerState(2966, Material.JUNGLE_FENCE_GATE).facing(Facing.NORTH).open(); // complete
        registerState(2967, Material.JUNGLE_FENCE_GATE).facing(Facing.EAST).open(); // complete
        registerState(2968, Material.JUNGLE_FENCE_GATE).facing(Facing.SOUTH).powered(); // complete
        registerState(2969, Material.JUNGLE_FENCE_GATE).facing(Facing.WEST).powered(); // complete
        registerState(2970, Material.JUNGLE_FENCE_GATE).facing(Facing.NORTH).powered(); // complete
        registerState(2971, Material.JUNGLE_FENCE_GATE).facing(Facing.EAST).powered(); // complete
        registerState(2972, Material.JUNGLE_FENCE_GATE).facing(Facing.SOUTH).open().powered(); // complete
        registerState(2973, Material.JUNGLE_FENCE_GATE).facing(Facing.WEST).open().powered(); // complete
        registerState(2974, Material.JUNGLE_FENCE_GATE).facing(Facing.NORTH).open().powered(); // complete
        registerState(2975, Material.JUNGLE_FENCE_GATE).facing(Facing.EAST).open().powered(); // complete
        registerState(2976, Material.DARK_OAK_FENCE_GATE).facing(Facing.SOUTH); // complete
        registerState(2977, Material.DARK_OAK_FENCE_GATE).facing(Facing.WEST); // complete
        registerState(2978, Material.DARK_OAK_FENCE_GATE).facing(Facing.NORTH); // complete
        registerState(2979, Material.DARK_OAK_FENCE_GATE).facing(Facing.EAST); // complete
        registerState(2980, Material.DARK_OAK_FENCE_GATE).facing(Facing.SOUTH).open(); // complete
        registerState(2981, Material.DARK_OAK_FENCE_GATE).facing(Facing.WEST).open(); // complete
        registerState(2982, Material.DARK_OAK_FENCE_GATE).facing(Facing.NORTH).open(); // complete
        registerState(2983, Material.DARK_OAK_FENCE_GATE).facing(Facing.EAST).open(); // complete
        registerState(2984, Material.DARK_OAK_FENCE_GATE).facing(Facing.SOUTH).powered(); // complete
        registerState(2985, Material.DARK_OAK_FENCE_GATE).facing(Facing.WEST).powered(); // complete
        registerState(2986, Material.DARK_OAK_FENCE_GATE).facing(Facing.NORTH).powered(); // complete
        registerState(2987, Material.DARK_OAK_FENCE_GATE).facing(Facing.EAST).powered(); // complete
        registerState(2988, Material.DARK_OAK_FENCE_GATE).facing(Facing.SOUTH).open().powered(); // complete
        registerState(2989, Material.DARK_OAK_FENCE_GATE).facing(Facing.WEST).open().powered(); // complete
        registerState(2990, Material.DARK_OAK_FENCE_GATE).facing(Facing.NORTH).open().powered(); // complete
        registerState(2991, Material.DARK_OAK_FENCE_GATE).facing(Facing.EAST).open().powered(); // complete
        registerState(2992, Material.ACACIA_FENCE_GATE).facing(Facing.SOUTH); // complete
        registerState(2993, Material.ACACIA_FENCE_GATE).facing(Facing.WEST); // complete
        registerState(2994, Material.ACACIA_FENCE_GATE).facing(Facing.NORTH); // complete
        registerState(2995, Material.ACACIA_FENCE_GATE).facing(Facing.EAST); // complete
        registerState(2996, Material.ACACIA_FENCE_GATE).facing(Facing.SOUTH).open(); // complete
        registerState(2997, Material.ACACIA_FENCE_GATE).facing(Facing.WEST).open(); // complete
        registerState(2998, Material.ACACIA_FENCE_GATE).facing(Facing.NORTH).open(); // complete
        registerState(2999, Material.ACACIA_FENCE_GATE).facing(Facing.EAST).open(); // complete
        registerState(3000, Material.ACACIA_FENCE_GATE).facing(Facing.SOUTH).powered(); // complete
        registerState(3001, Material.ACACIA_FENCE_GATE).facing(Facing.WEST).powered(); // complete
        registerState(3002, Material.ACACIA_FENCE_GATE).facing(Facing.NORTH).powered(); // complete
        registerState(3003, Material.ACACIA_FENCE_GATE).facing(Facing.EAST).powered(); // complete
        registerState(3004, Material.ACACIA_FENCE_GATE).facing(Facing.SOUTH).open().powered(); // complete
        registerState(3005, Material.ACACIA_FENCE_GATE).facing(Facing.WEST).open().powered(); // complete
        registerState(3006, Material.ACACIA_FENCE_GATE).facing(Facing.NORTH).open().powered(); // complete
        registerState(3007, Material.ACACIA_FENCE_GATE).facing(Facing.EAST).open().powered(); // complete
        registerState(3008, Material.SPRUCE_FENCE); // complete
        registerState(3024, Material.BIRCH_FENCE); // complete
        registerState(3040, Material.JUNGLE_FENCE); // complete
        registerState(3056, Material.DARK_OAK_FENCE); // complete
        registerState(3072, Material.ACACIA_FENCE); // complete
        registerState(3088, Material.SPRUCE_DOOR).facing(Facing.EAST); // complete
        registerState(3089, Material.SPRUCE_DOOR).facing(Facing.SOUTH); // complete
        registerState(3090, Material.SPRUCE_DOOR).facing(Facing.WEST); // complete
        registerState(3091, Material.SPRUCE_DOOR).facing(Facing.NORTH); // complete
        registerState(3092, Material.SPRUCE_DOOR).facing(Facing.EAST).open(); // complete
        registerState(3093, Material.SPRUCE_DOOR).facing(Facing.SOUTH).open(); // complete
        registerState(3094, Material.SPRUCE_DOOR).facing(Facing.WEST).open(); // complete
        registerState(3095, Material.SPRUCE_DOOR).facing(Facing.NORTH).open(); // complete
        registerState(3096, Material.SPRUCE_DOOR).hinge(HingePosition.LEFT); // complete
        registerState(3097, Material.SPRUCE_DOOR).hinge(HingePosition.RIGHT); // complete
        registerState(3098, Material.SPRUCE_DOOR).hinge(HingePosition.LEFT).powered(); // complete
        registerState(3099, Material.SPRUCE_DOOR).hinge(HingePosition.RIGHT).powered(); // complete
        registerState(3104, Material.BIRCH_DOOR).facing(Facing.EAST); // complete
        registerState(3105, Material.BIRCH_DOOR).facing(Facing.SOUTH); // complete
        registerState(3106, Material.BIRCH_DOOR).facing(Facing.WEST); // complete
        registerState(3107, Material.BIRCH_DOOR).facing(Facing.NORTH); // complete
        registerState(3108, Material.BIRCH_DOOR).facing(Facing.EAST).open(); // complete
        registerState(3109, Material.BIRCH_DOOR).facing(Facing.SOUTH).open(); // complete
        registerState(3110, Material.BIRCH_DOOR).facing(Facing.WEST).open(); // complete
        registerState(3111, Material.BIRCH_DOOR).facing(Facing.NORTH).open(); // complete
        registerState(3112, Material.BIRCH_DOOR).hinge(HingePosition.LEFT); // complete
        registerState(3113, Material.BIRCH_DOOR).hinge(HingePosition.RIGHT); // complete
        registerState(3114, Material.BIRCH_DOOR).hinge(HingePosition.LEFT).powered(); // complete
        registerState(3115, Material.BIRCH_DOOR).hinge(HingePosition.RIGHT).powered(); // complete
        registerState(3120, Material.JUNGLE_DOOR).facing(Facing.EAST); // complete
        registerState(3121, Material.JUNGLE_DOOR).facing(Facing.SOUTH); // complete
        registerState(3122, Material.JUNGLE_DOOR).facing(Facing.WEST); // complete
        registerState(3123, Material.JUNGLE_DOOR).facing(Facing.NORTH); // complete
        registerState(3124, Material.JUNGLE_DOOR).facing(Facing.EAST).open(); // complete
        registerState(3125, Material.JUNGLE_DOOR).facing(Facing.SOUTH).open(); // complete
        registerState(3126, Material.JUNGLE_DOOR).facing(Facing.WEST).open(); // complete
        registerState(3127, Material.JUNGLE_DOOR).facing(Facing.NORTH).open(); // complete
        registerState(3128, Material.JUNGLE_DOOR).hinge(HingePosition.LEFT); // complete
        registerState(3129, Material.JUNGLE_DOOR).hinge(HingePosition.RIGHT); // complete
        registerState(3130, Material.JUNGLE_DOOR).hinge(HingePosition.LEFT).powered(); // complete
        registerState(3131, Material.JUNGLE_DOOR).hinge(HingePosition.RIGHT).powered(); // complete
        registerState(3136, Material.ACACIA_DOOR).facing(Facing.EAST); // complete
        registerState(3137, Material.ACACIA_DOOR).facing(Facing.SOUTH); // complete
        registerState(3138, Material.ACACIA_DOOR).facing(Facing.WEST); // complete
        registerState(3139, Material.ACACIA_DOOR).facing(Facing.NORTH); // complete
        registerState(3140, Material.ACACIA_DOOR).facing(Facing.EAST).open(); // complete
        registerState(3141, Material.ACACIA_DOOR).facing(Facing.SOUTH).open(); // complete
        registerState(3142, Material.ACACIA_DOOR).facing(Facing.WEST).open(); // complete
        registerState(3143, Material.ACACIA_DOOR).facing(Facing.NORTH).open(); // complete
        registerState(3144, Material.ACACIA_DOOR).hinge(HingePosition.LEFT); // complete
        registerState(3145, Material.ACACIA_DOOR).hinge(HingePosition.RIGHT); // complete
        registerState(3146, Material.ACACIA_DOOR).hinge(HingePosition.LEFT).powered(); // complete
        registerState(3147, Material.ACACIA_DOOR).hinge(HingePosition.RIGHT).powered(); // complete
        registerState(3152, Material.DARK_OAK_DOOR).facing(Facing.EAST); // complete
        registerState(3153, Material.DARK_OAK_DOOR).facing(Facing.SOUTH); // complete
        registerState(3154, Material.DARK_OAK_DOOR).facing(Facing.WEST); // complete
        registerState(3155, Material.DARK_OAK_DOOR).facing(Facing.NORTH); // complete
        registerState(3156, Material.DARK_OAK_DOOR).facing(Facing.EAST).open(); // complete
        registerState(3157, Material.DARK_OAK_DOOR).facing(Facing.SOUTH).open(); // complete
        registerState(3158, Material.DARK_OAK_DOOR).facing(Facing.WEST).open(); // complete
        registerState(3159, Material.DARK_OAK_DOOR).facing(Facing.NORTH).open(); // complete
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
