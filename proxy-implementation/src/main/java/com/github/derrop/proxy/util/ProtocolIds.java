package com.github.derrop.proxy.util;

public interface ProtocolIds {

    interface ClientBound {
        
        interface Play {
            int KEEP_ALIVE = 0;

            int LOGIN = 1;

            int CHAT = 2;

            int UPDATE_TIME = 3;

            int ENTITY_EQUIPMENT = 4;

            int SPAWN_POSITION = 5;

            int UPDATE_HEALTH = 6;

            int RESPAWN = 7;

            int POSITION = 8;

            int HELD_ITEM_SLOT = 9;

            int BED = 10;

            int ANIMATION = 11;

            int NAMED_ENTITY_SPAWN = 12;

            int COLLECT = 13;

            int SPAWN_ENTITY = 14;

            int SPAWN_ENTITY_LIVING = 15;

            int SPAWN_ENTITY_PAINTING = 16;

            int SPAWN_ENTITY_EXPERIENCE_ORB = 17;

            int ENTITY_VELOCITY = 18;

            int ENTITY_DESTROY = 19;

            int ENTITY = 20;

            int REL_ENTITY_REMOVE = 21;

            int ENTITY_LOOK = 22;

            int REL_ENTITY_MOVE_LOOK = 23;

            int ENTITY_TELEPORT = 24;

            int ENTITY_HEAD_ROTATION = 25;

            int ENTITY_STATUS = 26;

            int ATTACH_ENTITY = 27;

            int ENTITY_METADATA = 28;

            int ENTITY_EFFECT = 29;

            int REMOVE_ENTITY_EFFECT = 30;

            int EXPERIENCE = 31;

            int UPDATE_ATTRIBUTES = 32;
        }
    }

    interface ServerBound {

        interface Handshaking {
            int HANDSHAKING_IN_SET_PROTOCOL = 0;
        }

    }
}
