package com.github.derrop.proxy.logging;
/*
 * Created by Mc_Ruben on 13.03.2019
 */

import java.util.Collection;

public interface ITabCompletable {

    Collection<String> tabComplete(String commandLine, String[] args);

}
