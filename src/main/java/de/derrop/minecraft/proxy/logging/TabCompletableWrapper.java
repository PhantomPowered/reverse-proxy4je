package de.derrop.minecraft.proxy.logging;
/*
 * Created by Mc_Ruben on 17.03.2019
 */

import lombok.*;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class TabCompletableWrapper {

    private boolean enabled;
    private ITabCompletable tabCompletable;

    public Collection<String> tabComplete(String commandLine, String[] args) {
        return this.enabled ? this.tabCompletable.tabComplete(commandLine, args) : null;
    }

}
