package com.github.derrop.proxy.plugins.betterlogin;

import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.player.PlayerInventoryClickEvent;
import com.github.derrop.proxy.api.events.connection.player.PlayerInventoryCloseEvent;
import com.github.derrop.proxy.api.events.connection.player.PlayerLoginEvent;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.util.ItemStack;
import com.github.derrop.proxy.api.util.nbt.NBTTagCompound;
import com.github.derrop.proxy.plugins.betterlogin.connection.LoginServiceConnection;

public class LoginPrepareListener {

    public static final Location SPAWN = new Location(0, 100, 0, 0, 90);
    public static final ItemStack[] PARENT_INVENTORY = new ItemStack[]{
            null,
            new ItemStack(Material.COMPASS.getId(), 1, 0, new NBTTagCompound().setTag("display", new NBTTagCompound().setString("Name", "§cWatch replay"))),
            null,
            new ItemStack(Material.SKULL_ITEM.getId(), 1, 0, new NBTTagCompound().setTag("display", new NBTTagCompound().setString("Name", "§cConnect with client"))),
            null
    };

    private Proxy proxy;

    public LoginPrepareListener(Proxy proxy) {
        this.proxy = proxy;
    }

    @Listener
    public void handleLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        event.setTargetConnection(new LoginServiceConnection(this.proxy, player));

    }

    @Listener
    public void handleInventoryClose(PlayerInventoryCloseEvent event) {
        if (event.getPlayer().getConnectedClient() instanceof LoginServiceConnection) {
            event.cancel(true);
        }
    }

    @Listener
    public void handleInventoryClick(PlayerInventoryClickEvent event) {
        if (event.getPlayer().getConnectedClient() instanceof LoginServiceConnection) {
            event.cancel(true);
        }
    }

}
