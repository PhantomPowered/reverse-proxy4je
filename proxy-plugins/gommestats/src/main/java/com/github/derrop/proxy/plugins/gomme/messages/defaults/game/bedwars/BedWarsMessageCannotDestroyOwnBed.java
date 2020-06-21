package com.github.derrop.proxy.plugins.gomme.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.bedwars.BedWarsTryDestroyOwnBedEvent;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.github.derrop.proxy.plugins.gomme.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SpecificGameMessageRegistrar;

public class BedWarsMessageCannotDestroyOwnBed extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerMessage(Language.GERMANY, MessageType.CANNOT_DESTROY_OWN_BED, "[BedWars] Du kannst dein eigenes Bett nicht zerstören!", BedWarsTryDestroyOwnBedEvent::new);
        registrar.registerMessage(Language.AUSTRIA, MessageType.CANNOT_DESTROY_OWN_BED, "[BedWars] Du konnst dei eiganes Bett net zastöan!", BedWarsTryDestroyOwnBedEvent::new);
    }
}
