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
package com.github.derrop.proxy.connection;

public interface PacketConstants {

    int UPDATE_HEALTH = 6;
    int CHUNK_DATA = 33;
    int CHUNK_BULK = 38;
    int BLOCK_UPDATE = 35;
    int MULTI_BLOCK_UPDATE = 34;
    int SPAWN_PLAYER = 12;
    int GLOBAL_ENTITY_SPAWN = 44;
    int SPAWN_OBJECT = 14;
    int SPAWN_MOB = 15;
    int ENTITY_TELEPORT = 24;
    int DESTROY_ENTITIES = 19;
    int ENTITY_EQUIPMENT = 9;
    int ENTITY_METADATA = 28;
    int SET_SLOT = 47;
    int WINDOW_ITEMS = 48;
    int PLAYER_ABILITIES = 57;
    int DISCONNECT = 64;
    int WORLD_BORDER = 68;
    int ENTITY_EFFECT = 29;
    int REMOVE_ENTITY_EFFECT = 30;
    int UPDATE_SIGN = 51;
    int CAMERA = 67;
    int MAPS = 52;
    int TIME_UPDATE = 3;
    int SCOREBOARD_OBJECTIVE = 59;
    int SCOREBOARD_SCORE = 60;
    int SCOREBOARD_DISPLAY = 61;
    int SCOREBOARD_TEAM = 62;
    int GAME_STATE_CHANGE = 43;
    int RESOURCE_PACK_SEND = 72;

}
