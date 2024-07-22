package com.sylvcraft.kidstool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Utils {
  public static Plugin plugin;
  public static final String LOCALIZATION_PATH = "localization";
  public static final String LOCALIZATION_LANGUAGE_PATH = "language";
  public static final String LOCALIZATION_LANGUAGE_DEFAULT = "enUS";
  public static final String LOCALIZATION_DATEFORMAT_PATH = "date-format";
  public static final String LOCALIZATION_DATEFORMAT = "yyyy-MM-dd";
  public static final String LOCALIZATION_TIMEFORMAT_PATH = "time-format";
  public static final String LOCALIZATION_TIMEFORMAT = "HH:mm:ss";


  public static void init(Plugin instance) {
    plugin = instance;
    plugin.saveDefaultConfig();
    if (!Messaging.Init()) plugin.getLogger().severe("** Could not load messaging file!  Plugin will not be able to communicate with the players.");
  }


  public static String getLocalization(String code) {
    if (code.trim().equals("")) return "";

    String lang = plugin.getConfig().getString(LOCALIZATION_PATH + "." + LOCALIZATION_LANGUAGE_PATH, LOCALIZATION_LANGUAGE_DEFAULT);
    return plugin.getConfig().getString(LOCALIZATION_PATH + "." + lang + "." + code, code);
  }


  public static void reloadConfig() {
    plugin.reloadConfig();
  }


  public static Map<String, String> getFormattedDateTime() {
    Map<String, String> ret = new HashMap<>();
    DateFormat dateFormat; DateFormat timeFormat;

    try {
      dateFormat = new SimpleDateFormat(plugin.getConfig().getString(LOCALIZATION_PATH + "." + LOCALIZATION_DATEFORMAT_PATH, LOCALIZATION_DATEFORMAT));
    } catch (IllegalArgumentException ex) {
      plugin.getLogger().warning("** An invalid date format was put in the localization settings!  Defaulting to yyyy-MM-dd");
      dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }
    try {
      timeFormat = new SimpleDateFormat(plugin.getConfig().getString(LOCALIZATION_PATH + "." + LOCALIZATION_TIMEFORMAT_PATH, LOCALIZATION_TIMEFORMAT));
    } catch (IllegalArgumentException ex) {
      plugin.getLogger().warning("** An invalid time format was put in the localization settings!  Defaulting to HH:mm:ss");
      timeFormat = new SimpleDateFormat("HH:mm:ss");
    }

    ret.put("date", dateFormat.format(new Date()));
    ret.put("time", timeFormat.format(new Date()));
    return ret;
  }


  public static int getMaxPermissionNode(String permissionRoot, Permissible permissible) {
    if (permissible.isOp()) return -1;

    final AtomicInteger max = new AtomicInteger(); 
    permissible.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).map(String::toLowerCase).filter(value -> value.startsWith(permissionRoot)).map(value -> value.replace(permissionRoot, "")).forEach(value -> {
      if (value.equalsIgnoreCase("*")) {
        max.set(-1);
        return;
      }

      if (max.get() == -1) return;

      try {
        int amount = Integer.parseInt(value);
        if (amount > max.get()) max.set(amount);
      } catch (NumberFormatException ignored) {
      }
    });

    return max.get();
  }


  public static boolean isNumeric(String value) {
    try {
      Integer.valueOf(value);
      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }


  public static boolean hasPermission(Permissible p, String perm) {
    return hasPermission(p, perm, true);
  }
  public static boolean hasPermission(Permissible p, String perm, boolean isDefault) {
    Permission permission = new Permission(perm, isDefault ? PermissionDefault.TRUE : PermissionDefault.FALSE);
    return p.hasPermission(permission);
  }


  public static boolean hasMoved(Location from, Location to) {
    return (from.getBlockX() != to.getBlockX() ||
        from.getBlockY() != to.getBlockY() ||
        from.getBlockZ() != to.getBlockZ());
  }


  public static String msToTime(long milliseconds) {
    Calendar c = Calendar.getInstance(); 
    c.setTimeInMillis(milliseconds);
    int hr = c.get(Calendar.HOUR);
    int min = c.get(Calendar.MINUTE);
    int sec = c.get(Calendar.SECOND);

    String ret = hr == 0 ? "" : pluralize("!# hour!s", hr);
    ret += min == 0 ? "" : ((ret.equals("") ? "" : ", ") + pluralize("!# minute!s", min));
    ret += sec == 0 ? "" : ((ret.equals("") ? "" : ", ") + pluralize("!# second!s", sec));
    return ret;
  }


  public static String pluralize(String message, int value) {
    value = Math.abs(value);
    String ret = message.replaceAll("!#", String.valueOf(value));
    ret = ret.replaceAll("!s", ((value == 1)?"":"s"));        // swords | swords
    ret = ret.replaceAll("!es", ((value == 1)?"":"es"));      // bus | buses
    ret = ret.replaceAll("!ies", ((value == 1)?"y":"ies"));   // penny | pennies
    ret = ret.replaceAll("!oo", ((value == 1)?"oo":"ee"));    // tooth | teeth
    ret = ret.replaceAll("!an", ((value == 1)?"an":"en"));    // woman | women
    ret = ret.replaceAll("!us", ((value == 1)?"us":"i"));     // cactus | cacti
    ret = ret.replaceAll("!is", ((value == 1)?"is":"es"));    // analysis | analyses
    ret = ret.replaceAll("!o", ((value == 1)?"o":"oes"));     // potato | potatoes
    ret = ret.replaceAll("!on", ((value == 1)?"a":"on"));     // criteria | criterion
    ret = ret.replaceAll("!lf", ((value == 1)?"lf":"lves"));  // elf | elves
    ret = ret.replaceAll("!ia", ((value == 1)?"is":"are"));
    ret = ret.replaceAll("!ww", ((value == 1)?"was":"were"));
    return ret;
  }

  public static int getInt(String value) {
    return getInt(value, Integer.MIN_VALUE);
  }

  public static int getInt(String value, int defaultValue) {
    try {
      return Integer.valueOf(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }


  public static double getDouble(String value) {
    return getDouble(value, Double.MIN_VALUE);
  }

  public static double getDouble(String value, double defaultValue) {
    try {
      return Double.valueOf(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }


  public static long getLong(String value) {
    return getLong(value, Long.MIN_VALUE);
  }

  public static long getLong(String value, long defaultValue) {
    try {
      return Long.valueOf(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }


  public static boolean getBoolean(String value) {
    return getBoolean(value, false);
  }
  public static boolean getBoolean(String value, boolean defaultvalue) {
    if (value.trim().equals("")) return defaultvalue;
    return value.trim().substring(0, 1).equalsIgnoreCase("t") || value.trim().substring(0, 1).equalsIgnoreCase("y") || value.trim().substring(0, 1).equals("1");
  }


  public static String join(final List<String> list, final String delimiter) {
    return join(list.toArray(new String[0]), delimiter);
  }
  public static String join(final String[] array, final String delimiter) {
    if (array == null) return "";

    return join(array, delimiter, 0, array.length);
  }

  public static String join(final List<String> list, final String delimiter, final int startIndex) {
    return join(list.toArray(new String[0]), delimiter, startIndex);
  }
  public static String join(final String[] array, final String delimiter, final int startIndex) {
    if (array == null) return "";

    return join(array, delimiter, startIndex, array.length);
  }

  public static String join(final List<String> list, final String delimiter, final int startIndex, final int endIndex) {
    return join(list.toArray(new String[0]), delimiter, startIndex, endIndex);
  }
  public static String join(final String[] array, final String delimiter, final int startIndex, final int endIndex) {
    if (array == null) return "";
    if (endIndex - startIndex <= 0) return "";

    final StringJoiner joiner = new StringJoiner(delimiter);
    for (int i = startIndex; i < endIndex; i++) joiner.add(String.valueOf(array[i]));
    return joiner.toString();
  }

  public static String sanitizeFilename(String input) {
    int pos = input.lastIndexOf(File.separator);
    return pos > -1 ? input.substring(pos + 1) : input;
  }


  public static boolean writeToFile(String filename, String content) {
    try {
      File folder = plugin.getDataFolder();
      String cleanFilename = folder.getAbsolutePath() + System.getProperty("file.separator") + sanitizeFilename(filename);
      Writer output = new BufferedWriter(new FileWriter(cleanFilename, true));
      output.append(content + System.getProperty("line.separator"));
      output.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }


  public static Location getLocation(String path) {
    ConfigurationSection cfg = plugin.getConfig().getConfigurationSection(path);
    if (cfg == null) return null;

    Map<String, Object> data = new HashMap<>();
    for (String key : cfg.getKeys(false)) data.put(key, cfg.get(key));
    try {
      return Location.deserialize(data);
    } catch (IllegalArgumentException ex) {
      plugin.getLogger().info("** Requested location from " + path + " but it has an invalid world!");
      return null;
    } catch (Exception ex) {
      plugin.getLogger().info("** Requested location from " + path + " but there was an error retrieving it!");
      return null;
    }
  }


  public static void setLocation(String path, Location loc) {
    for (Map.Entry<String, Object> data : loc.serialize().entrySet()) {
      plugin.getConfig().set(path + "." + data.getKey(), data.getValue());
    }
    plugin.saveConfig();
  }


  public static String colorize(String val) {
    return ChatColor.translateAlternateColorCodes('&', val);
  }


  public static void log(String msg) {
    log(msg, "info");
  }
  public static void log(String msg, String level) {
    switch (level.toLowerCase().trim()) {
    case "warning":
      plugin.getLogger().warning(msg);
      break;

    case "severe":
      plugin.getLogger().severe(msg);
      break;

    default:
      plugin.getLogger().info(msg);
      break;
    }
  }


  public static Material getMaterial(String name, Material defaultValue) {
    try {
      Material m = Material.valueOf(name.replace(" ", "_").toUpperCase());
      return m;
    } catch (IllegalArgumentException ex) {
      return defaultValue == null ? Material.BARRIER : defaultValue;
    }
  }


  @Nullable
  public static UUID getUUID(String uuidStr) {
    try {
      if (uuidStr == null) return null;

      return UUID.fromString(uuidStr);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }


  @Nullable
  @SuppressWarnings("deprecation")
  public static UUID getPlayerUUID(String player) {
    if (player == null) return null;

    Player p = plugin.getServer().getPlayerExact(player);
    if (p != null) return p.getUniqueId();

    OfflinePlayer op = plugin.getServer().getOfflinePlayer(player);
    return op == null ? null : op.getUniqueId();
  }


  public static String getPlayerName(UUID u) {
    if (u == null) return "Unknown";

    Player p = plugin.getServer().getPlayer(u);
    if (p != null) return p.getName();

    OfflinePlayer op = plugin.getServer().getOfflinePlayer(u);
    return op == null ? "Unknown" : op.getName();
  }


  public static boolean hasPermission(Permissible permissible, String... permission) {
    return hasPermission(permissible, false, permission);
  }

  public static boolean hasPermission(Permissible permissible, boolean requireExplicit, String... permission) {
    for (String permnode : permission) {
      Permission perm = requireExplicit ? new Permission(permnode, PermissionDefault.FALSE) : new Permission(permnode); 
      if (permissible.hasPermission(perm)) return true;
    }
    return false;
  }


  public static List<String> getWorldNames() {
    return plugin.getServer().getWorlds().stream().filter(Objects::nonNull).map(World::getName).collect(Collectors.toList());
  }


  public static List<String> getMaterialNames() {
    return Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList());
  }


	public static String locationDisplay(Location l) {
	  return l == null ?
	         "Unknown" :
	         (l.getWorld() == null ? "" : "[" + l.getWorld().getName() + "]: ") +
	         l.getBlockX() + "/" + l.getBlockY() + "/" + l.getBlockZ();
	}
}
