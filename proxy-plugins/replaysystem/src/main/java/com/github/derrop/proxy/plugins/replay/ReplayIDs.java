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
package com.github.derrop.proxy.plugins.replay;

public class ReplayIDs {

    public static final int[] REPLAY_PACKETS = new int[]{
            //0, // keep alive
            //1, // join game
            2, // chat
            3, // time update
            4, // entity equipment
            5, // spawn position
            //6, // update health
            //7, // respawn
            //8, // player pos look
            //9, // held item change
            10, // use bed
            11, // animation
            12, // spawn player
            13, // collect item
            14, // spawn object
            15, // spawn mob
            16, // spawn painting
            17, // spawn exp orb
            18, // entity velocity
            19, // destroy entities
            //20, // entity (this isn't sent, it's the super class of the following 3 packets)
            21, // entity rel move
            22, // entity look
            23, // entity look move
            24, // entity teleport
            25, // entity head look
            26, // entity status
            27, // entity attach
            28, // entity metadata
            29, // entity effect
            30, // remove entity effect
            31, // set exp
            32, // entity properties
            33, // chunk data
            34, // multi block change
            35, // block change
            36, // block action
            37, // block break aim
            38, // map chunk bulk
            39, // explosion
            40, // effect
            41, // sound effect
            42, // particles
            //43, // change GameState
            44, // spawn global entity
            //45, // open window
            //46, // close window
            //47, // set slot
            //48, // window items
            //49, // window property
            //50, // confirm transaction
            51, // update sign
            52, // maps
            53, // update tile entity
            //54, // sign editor open
            //55, // statistics
            56, // player list item
            //57, // player abilities
            //58, // tab complete
            59, // scoreboard objective
            60, // update score
            61, // display scoreboard
            62, // teams
            63, // custom payload
            //64, // disconnect
            65, // server difficulty
            66, // combat event
            67, // camera
            68, // world border
            69, // title
            //70, // set compression level
            71, // player list header/footer
            //72, // resource pack send
            73 // update entity nbt
    };

    public static boolean isReplayPacket(int id) {
        for (int replayPacket : REPLAY_PACKETS) {
            if (replayPacket == id) {
                return true;
            }
        }
        return false;
    }

}
