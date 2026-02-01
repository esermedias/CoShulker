package net.coreviabilisim.coshulker;

import java.util.UUID;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OpenSession {
  private final UUID playerId;
  private final EquipmentSlot hand;
  private final int slot;
  private final Inventory inventory;
  private final ItemStack item;

  public OpenSession(UUID playerId, EquipmentSlot hand, int slot, Inventory inventory, ItemStack item) {
    this.playerId = playerId;
    this.hand = hand;
    this.slot = slot;
    this.inventory = inventory;
    this.item = item;
  }

  public UUID getPlayerId() {
    return playerId;
  }

  public EquipmentSlot getHand() {
    return hand;
  }

  public int getSlot() {
    return slot;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public ItemStack getItem() {
    return item;
  }
}
