package net.coreviabilisim.coshulker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.block.Action;

public class CoShulkerPlugin extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {
  private final Map<UUID, OpenSession> openSessions = new HashMap<>();
  private MessageManager messages;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    this.messages = new MessageManager(this);

    getServer().getPluginManager().registerEvents(this, this);

    if (getCommand("cos") != null) {
      getCommand("cos").setExecutor(this);
      getCommand("cos").setTabCompleter(this);
    }

  }

  @Override
  public void onDisable() {
    for (OpenSession session : new ArrayList<>(openSessions.values())) {
      Player player = Bukkit.getPlayer(session.getPlayerId());
      if (player != null && player.isOnline()) {
        applyShulkerContents(player, session);
        if (player.getOpenInventory().getTopInventory().equals(session.getInventory())) {
          player.closeInventory();
        }
      }
    }
    openSessions.clear();
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR) {
      return;
    }

    if (!getConfig().getBoolean("settings.open-in-air", true)) {
      return;
    }

    if (event.getHand() == EquipmentSlot.OFF_HAND
        && !getConfig().getBoolean("settings.allow-offhand", true)) {
      return;
    }

    Player player = event.getPlayer();
    if (getConfig().getBoolean("settings.require-sneak", false) && !player.isSneaking()) {
      return;
    }

    ItemStack item = getItemInHand(player, event.getHand());
    if (item == null || !isShulker(item.getType())) {
      return;
    }

    String openPerm = getConfig().getString("settings.permission.open", "cos.use");
    if (openPerm != null && !openPerm.isEmpty() && !player.hasPermission(openPerm)) {
      messages.send(player, "messages.no-permission", null);
      return;
    }

    if (openSessions.containsKey(player.getUniqueId())) {
      messages.send(player, "messages.already-open", null);
      return;
    }

    event.setCancelled(true);
    openShulker(player, event.getHand(), item);
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    if (!(event.getPlayer() instanceof Player player)) {
      return;
    }

    OpenSession session = openSessions.get(player.getUniqueId());
    if (session == null) {
      return;
    }

    if (!event.getInventory().equals(session.getInventory())) {
      return;
    }

    applyShulkerContents(player, session);
    openSessions.remove(player.getUniqueId());
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }
    OpenSession session = openSessions.get(player.getUniqueId());
    if (session == null) {
      return;
    }

    int lockedSlot = getLockedSlot(session);
    if (event.getClickedInventory() != null
        && event.getClickedInventory().equals(player.getInventory())
        && event.getSlot() == lockedSlot) {
      event.setCancelled(true);
      return;
    }

    int hotbarButton = event.getHotbarButton();
    if (hotbarButton >= 0 && hotbarButton == lockedSlot) {
      event.setCancelled(true);
      return;
    }

    Inventory top = player.getOpenInventory().getTopInventory();
    if (!top.equals(session.getInventory())) {
      return;
    }

    if (event.getClickedInventory() != null && event.getClickedInventory().equals(top)) {
      boolean allowShulkerInside = getConfig().getBoolean("settings.allow-shulker-inside", false);
      ItemStack cursor = event.getCursor();
      ItemStack current = event.getCurrentItem();
      if (!allowShulkerInside
          && (isShulker(cursor != null ? cursor.getType() : null)
              || isShulker(current != null ? current.getType() : null))) {
        event.setCancelled(true);
      }
      return;
    }

    if (event.isShiftClick()) {
      boolean allowShulkerInside = getConfig().getBoolean("settings.allow-shulker-inside", false);
      ItemStack current = event.getCurrentItem();
      if (!allowShulkerInside && isShulker(current != null ? current.getType() : null)) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onInventoryDrag(InventoryDragEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }
    OpenSession session = openSessions.get(player.getUniqueId());
    if (session == null) {
      return;
    }

    int lockedSlot = getLockedSlot(session);
    if (event.getRawSlots().contains(lockedSlot)) {
      event.setCancelled(true);
      return;
    }

    Inventory top = player.getOpenInventory().getTopInventory();
    if (!top.equals(session.getInventory())) {
      return;
    }

    ItemStack dragged = event.getOldCursor();
    boolean allowShulkerInside = getConfig().getBoolean("settings.allow-shulker-inside", false);
    if (!allowShulkerInside && isShulker(dragged != null ? dragged.getType() : null)) {
      for (int slot : event.getRawSlots()) {
        if (slot < top.getSize()) {
          event.setCancelled(true);
          return;
        }
      }
    }
  }

  @EventHandler
  public void onDrop(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    OpenSession session = openSessions.get(player.getUniqueId());
    if (session == null) {
      return;
    }

    int lockedSlot = getLockedSlot(session);
    int currentSlot = player.getInventory().getHeldItemSlot();
    if (session.getHand() == EquipmentSlot.OFF_HAND && lockedSlot == 40) {
      event.setCancelled(true);
    } else if (currentSlot == lockedSlot) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onSwapHands(PlayerSwapHandItemsEvent event) {
    Player player = event.getPlayer();
    OpenSession session = openSessions.get(player.getUniqueId());
    if (session == null) {
      return;
    }
    event.setCancelled(true);
  }

  private void openShulker(Player player, EquipmentSlot hand, ItemStack item) {
    if (!(item.getItemMeta() instanceof BlockStateMeta meta)) {
      return;
    }

    if (!(meta.getBlockState() instanceof ShulkerBox shulker)) {
      return;
    }

    Inventory inv = Bukkit.createInventory(player, 27,
        messages.parseToComponent(getConfig().getString("settings.inventory-title", "<gold>cos</gold> <gray>shulker</gray>"),
            player, null));
    inv.setContents(shulker.getInventory().getContents());

    int slot = hand == EquipmentSlot.OFF_HAND ? -1 : player.getInventory().getHeldItemSlot();
    OpenSession session = new OpenSession(player.getUniqueId(), hand, slot, inv, item);
    openSessions.put(player.getUniqueId(), session);

    player.openInventory(inv);
    messages.send(player, "messages.open", null);
  }

  private void applyShulkerContents(Player player, OpenSession session) {
    ItemStack item = session.getItem();
    if (item == null || !isShulker(item.getType())) {
      return;
    }

    if (!(item.getItemMeta() instanceof BlockStateMeta meta)) {
      return;
    }

    if (!(meta.getBlockState() instanceof ShulkerBox shulker)) {
      return;
    }

    shulker.getInventory().setContents(session.getInventory().getContents());
    meta.setBlockState(shulker);
    item.setItemMeta(meta);

    if (session.getHand() == EquipmentSlot.OFF_HAND) {
      player.getInventory().setItemInOffHand(item);
    } else {
      int slot = session.getSlot();
      if (slot >= 0 && slot < player.getInventory().getSize()) {
        player.getInventory().setItem(slot, item);
      }
    }
  }

  private ItemStack getItemInHand(Player player, EquipmentSlot hand) {
    if (hand == EquipmentSlot.OFF_HAND) {
      return player.getInventory().getItemInOffHand();
    }
    return player.getInventory().getItemInMainHand();
  }

  private boolean isShulker(Material material) {
    if (material == null) {
      return false;
    }
    return material.name().endsWith("_SHULKER_BOX") || material == Material.SHULKER_BOX;
  }

  private int getLockedSlot(OpenSession session) {
    if (session.getHand() == EquipmentSlot.OFF_HAND) {
      return 40;
    }
    return session.getSlot();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
      messages.sendList(sender, "messages.help", null);
      return true;
    }

    String sub = args[0].toLowerCase(Locale.ROOT);
    if (sub.equals("reload")) {
      if (!sender.hasPermission("cos.reload")) {
        messages.send(sender, "messages.no-permission", null);
        return true;
      }
      reloadConfig();
      this.messages = new MessageManager(this);
      messages.send(sender, "messages.reloaded", null);
      return true;
    }

    messages.send(sender, "messages.unknown-command", null);
    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    if (args.length == 1) {
      return filterStartsWith(List.of("help", "reload"), args[0]);
    }

    return Collections.emptyList();
  }

  private List<String> filterStartsWith(List<String> items, String input) {
    String lower = input.toLowerCase(Locale.ROOT);
    List<String> out = new ArrayList<>();
    for (String item : items) {
      if (item.toLowerCase(Locale.ROOT).startsWith(lower)) {
        out.add(item);
      }
    }
    return out;
  }

}

