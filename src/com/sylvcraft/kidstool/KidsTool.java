package com.sylvcraft.kidstool;

import org.bukkit.plugin.java.JavaPlugin;
import com.sylvcraft.kidstool.commands.Cmd_kt;
import com.sylvcraft.kidstool.events.PlayerChangedWorld;
import com.sylvcraft.kidstool.events.PlayerInteract;

public class KidsTool extends JavaPlugin {

  @Override
  public void onEnable() {
    Utils.init(this);
    getServer().getPluginManager().registerEvents(new PlayerChangedWorld(), this);
    getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
    getCommand("kt").setExecutor(new Cmd_kt());
  }
}
