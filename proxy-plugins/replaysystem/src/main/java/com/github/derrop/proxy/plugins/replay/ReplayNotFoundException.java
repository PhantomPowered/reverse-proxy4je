package com.github.derrop.proxy.plugins.replay;

import java.util.UUID;

public class ReplayNotFoundException extends RuntimeException {
    public ReplayNotFoundException(UUID replayId) {
        super("Replay " + replayId + " not found");
    }
}
