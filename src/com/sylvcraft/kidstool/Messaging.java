package com.sylvcraft.kidstool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Messaging {
  private static String language = "";
  private static File msgFile;
  private static FileConfiguration msgConfig;
  private static Map<String, String> newAdditions = new HashMap<>();

  public static boolean Init() {
    language = Utils.plugin.getConfig().getString(Utils.LOCALIZATION_PATH + "." + Utils.LOCALIZATION_LANGUAGE_PATH, Utils.LOCALIZATION_LANGUAGE_DEFAULT);

    msgFile = new File(Utils.plugin.getDataFolder(), "messages.yml");
    if (!msgFile.exists()) {
      msgFile.getParentFile().mkdirs();
      Utils.plugin.saveResource("messages.yml", false);
     }

    msgConfig = new YamlConfiguration();
    try {
      msgConfig.load(msgFile);
      if (newAdditions.size() == 0) return true;

      for (Map.Entry<String, String> addition : newAdditions.entrySet()) msgConfig.addDefault(addition.getKey(), addition.getValue());
      msgConfig.options().copyDefaults(true);
      msgConfig.save(msgFile);
      msgConfig.load(msgFile);
      return true;
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
      return false;
    }    
  }


  public static List<String> getMessageCodes() {
    List<String> ret = new ArrayList<>();
    ConfigurationSection msgs = msgConfig.getConfigurationSection(language + ".messages");
    if (msgs == null) return ret;

    for (String msgcode : msgs.getKeys(false)) ret.add(msgcode);
    return ret;
  }


  public static void setMessage(String msgCode, String value) {
    msgConfig.set(language + ".messages." + msgCode, value);
    Utils.plugin.saveConfig();
  }


  public static String getMessage(String msgCode) {
    return getMessage(msgCode, null);
  }
  public static String getMessage(String msgCode, String defaultValue) {
    return msgConfig.getString(msgCode.contains(".") ? msgCode : language + ".messages." + msgCode, defaultValue);
  }


  public static List<String> getHelpTopics() {
    return getHelpTopics("");
  }
  public static List<String> getHelpTopics(String topic) {
    ConfigurationSection cfg = msgConfig.getConfigurationSection(language + ".help");
    if (cfg == null) return Collections.emptyList();

    List<String> ret = new ArrayList<>();
    for (String helptopic : cfg.getKeys(false)) {
      if (!topic.equals("")) {
        if (helptopic.length() < topic.length()) continue;
        if (!helptopic.substring(0, topic.length()).equalsIgnoreCase(topic)) continue;
      }

      ret.add(helptopic);
    }
    return ret;
  }


  public static void showHelp(CommandSender sender) {
    showHelp(sender, "");
  }
  public static void showHelp(CommandSender sender, String topic) {
    int displayed = 0;
    for (String sc : getHelpTopics(topic)) {
      String permNode = "deathinvlimiter." + sc.replace("-", ".");
      if (!Utils.hasPermission(sender, permNode, 
          permNode + ".view",
          permNode + ".set",
          permNode + ".del")) continue; 

      send(language + ".help." + sc.replace(".", "-"), sender);
      displayed++;
    }
    if (displayed == 0) send("access-denied", sender);
  }  


  public static void send(String msgCode, Object sender) {
    if (sender == null) return;
    if (getMessage(msgCode) == null) return;

    msgTransmit(getMessage(msgCode), sender);
  }
  public static void send(String msgCode, Object sender, Map<String, String> data) {
    if (sender == null) return;
    if (getMessage(msgCode) == null) return;

    String tmp = getMessage(msgCode, msgCode);
    for (Map.Entry<String, String> mapData : data.entrySet()) {
      if (mapData.getKey() == null) continue;

      tmp = tmp.replace(mapData.getKey(), mapData.getValue() == null ? "" : mapData.getValue());
    }
    if (data.containsKey("%pluralize.value%") || (data.containsKey("%value%") && Utils.isNumeric(data.get("%value%")))) {
      try {
        int value = Integer.valueOf(data.containsKey("%pluralize.value%") ? data.get("%pluralize.value%") : data.get("%value%"));
        tmp = Utils.pluralize(tmp, value);
      } catch (NumberFormatException ex) {
      }
    }
    msgTransmit(tmp, sender);
  }


  public static void msgTransmit(String msg, Object target) {
    if (target == null) return;
    if (target instanceof CommandSender) {
      for (String m : (msg + " ").split("%br%")) {
        ((CommandSender)target).sendMessage(ChatColor.translateAlternateColorCodes('&', m));
      }
      return;
    }

    if (!(target instanceof String) || target.toString().trim().equals("")) return;

    switch (target.toString().toLowerCase()) {
    case "console":
    case "console-i":
      for (String m : (msg + " ").split("%br%")) Utils.plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&', m));
      break;

    case "console-w":
      for (String m : (msg + " ").split("%br%")) Utils.plugin.getLogger().warning(ChatColor.translateAlternateColorCodes('&', m));
      break;

    case "console-s":
      for (String m : (msg + " ").split("%br%")) Utils.plugin.getLogger().severe(ChatColor.translateAlternateColorCodes('&', m));
      break;

    default:
      Utils.writeToFile(target.toString(), msg);
      break;
    }
  }

  public static Map<String, String> getLocalizedWords(String path, String... words) {
    List<String> opts = Arrays.stream(words).map((String s) -> s.toLowerCase()).collect(Collectors.toList());
    Map<String, String> ret = new HashMap<>();
    for (String word : words) ret.put(word, word);
    if (msgConfig == null) return ret;

    ConfigurationSection cfg = msgConfig.getConfigurationSection(language + ".words" + (path.trim().equals("") ? "" : "." + path.trim()));
    if (cfg == null) return ret;

    for (String word : words) {
      String localizedWord = cfg.getString(word, word);
      ret.put(word, opts.contains("*nocolor") ? localizedWord : Utils.colorize(localizedWord));
    }
    return ret;
  }
}
