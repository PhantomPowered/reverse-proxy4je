package com.github.derrop.proxy.replay;

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
