package com.sylvcraft.kidstool.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import com.sylvcraft.kidstool.Utils;

public class PlayerMove implements Listener {

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e) {
    if (e.getFrom().getBlockX() == e.getTo().getBlockX() &&
        e.getFrom().getBlockY() == e.getTo().getBlockY() &&
        e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return;

  }

}
