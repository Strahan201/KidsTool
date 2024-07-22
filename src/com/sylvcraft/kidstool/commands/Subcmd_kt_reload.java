package com.sylvcraft.kidstool.commands;

import org.bukkit.command.CommandSender;
import com.sylvcraft.kidstool.Messaging;
import com.sylvcraft.kidstool.Utils;

public class Subcmd_kt_reload {

  // reload
  //
  public Subcmd_kt_reload(CommandSender sender, String[] args) {
    Utils.reloadConfig();
    Messaging.Init();
    Messaging.send("reloaded", sender);
  }
}
