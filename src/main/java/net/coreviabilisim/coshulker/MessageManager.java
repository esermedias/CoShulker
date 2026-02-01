package net.coreviabilisim.coshulker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageManager {
  private final CoShulkerPlugin plugin;
  private final MiniMessage miniMessage;
  private final String prefix;
  private final Map<Character, String> smallCapsMap;

  public MessageManager(CoShulkerPlugin plugin) {
    this.plugin = plugin;
    this.miniMessage = MiniMessage.miniMessage();
    this.prefix = plugin.getConfig().getString("messages.prefix", "<gold>cos</gold> ");
    this.smallCapsMap = buildSmallCapsMap();
  }

  public void send(CommandSender sender, String key, String targetName) {
    String raw = plugin.getConfig().getString(key, "");
    if (raw == null || raw.isBlank()) {
      return;
    }
    sender.sendMessage(parseToComponent(raw, sender, targetName));
  }

  public void sendList(CommandSender sender, String key, String targetName) {
    List<String> lines = plugin.getConfig().getStringList(key);
    if (lines == null || lines.isEmpty()) {
      return;
    }
    for (String line : lines) {
      sender.sendMessage(parseToComponent(line, sender, targetName));
    }
  }

  public Component parseToComponent(String input, CommandSender sender, String targetName) {
    String withPlaceholders = applyPlaceholders(input, sender, targetName);
    String smallCaps = toSmallCapsPreservingTags(withPlaceholders);
    return miniMessage.deserialize(smallCaps);
  }

  private String applyPlaceholders(String input, CommandSender sender, String targetName) {
    String playerName = sender instanceof Player p ? p.getName() : "console";
    String out = input;
    out = out.replace("{prefix}", prefix);
    out = out.replace("{player}", playerName);
    if (targetName != null) {
      out = out.replace("{target}", targetName);
    }
    return out;
  }

  private String toSmallCapsPreservingTags(String input) {
    StringBuilder out = new StringBuilder(input.length());
    boolean inTag = false;
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (c == '<') {
        inTag = true;
        out.append(c);
        continue;
      }
      if (c == '>' && inTag) {
        inTag = false;
        out.append(c);
        continue;
      }
      if (inTag) {
        out.append(c);
        continue;
      }
      out.append(toSmallCapsChar(c));
    }
    return out.toString();
  }

  private String toSmallCapsChar(char c) {
    char lower = Character.toLowerCase(c);
    String mapped = smallCapsMap.get(lower);
    return mapped != null ? mapped : String.valueOf(c);
  }

  private Map<Character, String> buildSmallCapsMap() {
    Map<Character, String> map = new HashMap<>();
    map.put('a', "ᴀ");
    map.put('b', "ʙ");
    map.put('c', "ᴄ");
    map.put('d', "ᴅ");
    map.put('e', "ᴇ");
    map.put('f', "ꜰ");
    map.put('g', "ɢ");
    map.put('h', "ʜ");
    map.put('i', "ɪ");
    map.put('j', "ᴊ");
    map.put('k', "ᴋ");
    map.put('l', "ʟ");
    map.put('m', "ᴍ");
    map.put('n', "ɴ");
    map.put('o', "ᴏ");
    map.put('p', "ᴘ");
    map.put('q', "ꞯ");
    map.put('r', "ʀ");
    map.put('s', "ꜱ");
    map.put('t', "ᴛ");
    map.put('u', "ᴜ");
    map.put('v', "ᴠ");
    map.put('w', "ᴡ");
    map.put('x', "x");
    map.put('y', "ʏ");
    map.put('z', "ᴢ");
    return map;
  }
}
