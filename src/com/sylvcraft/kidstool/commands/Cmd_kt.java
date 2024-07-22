package com.sylvcraft.kidstool.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import com.sylvcraft.kidstool.Messaging;
import com.sylvcraft.kidstool.Utils;

public class Cmd_kt implements TabExecutor {
  private List<String> subcommands = new ArrayList<>(Arrays.asList("list","reload"));

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length == 0) {
      Messaging.showHelp(sender);
      return true;
    }

    if (!sender.hasPermission("kidstool." + args[0])) {
      Messaging.send("access-denied", sender);
      return true;
    }

    switch (args[0].toLowerCase()) {
    case "reload":
      new Subcmd_kt_reload(sender, Arrays.copyOfRange(args, 1, args.length));
      return true;
    }
    return true;
  }


  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    List<String> ret = new ArrayList<>();

    switch (args.length) {
    case 1:
      for (String subcmd : subcommands) {
        if (!sender.hasPermission("kidstool." + subcmd)) continue;
        ret.add(subcmd);
      }
      return StringUtil.copyPartialMatches(args[0].toLowerCase(), ret, new ArrayList<String>());
    }
    return ret;
  }
}
