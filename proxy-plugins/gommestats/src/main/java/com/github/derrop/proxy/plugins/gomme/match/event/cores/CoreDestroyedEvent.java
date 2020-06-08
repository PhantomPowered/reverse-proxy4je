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
package com.github.derrop.proxy.plugins.gomme.match.event.cores;

import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;

public class CoreDestroyedEvent extends MatchEvent {

    private final String destroyer;
    private final String team;
    private final String core;

    public CoreDestroyedEvent(String destroyer, String team, String core) {
        this.destroyer = destroyer;
        this.team = team;
        this.core = core;
    }

    @Override
    public String toPlainText() {
        return "The core " + this.core + " of the team " + this.team + " was destroyed by " + this.destroyer;
    }

    public String getDestroyer() {
        return this.destroyer;
    }

    public String getTeam() {
        return this.team;
    }

    public String getCore() {
        return this.core;
    }
}