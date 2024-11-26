package de.chafficplugins.chafficLib.test.util

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.mockbukkit.mockbukkit.entity.PlayerMock
import org.mockito.Mockito
import org.mockito.kotlin.whenever

/**
 * Utility class for GUI-related testing functionality.
 */
object GuiTestUtil {
    /**
     * Creates a mock inventory click event
     * @param player The player who clicked
     * @param inventory The inventory that was clicked
     * @param slot The slot that was clicked
     * @param item The item that was clicked (defaults to AIR)
     * @return A mocked InventoryClickEvent
     */
    fun createClickEvent(
        player: PlayerMock,
        inventory: Inventory,
        slot: Int,
        item: ItemStack = ItemStack(Material.AIR),
    ): InventoryClickEvent {
        val event = Mockito.mock(InventoryClickEvent::class.java)

        whenever(event.whoClicked).thenReturn(player)
        whenever(event.clickedInventory).thenReturn(inventory)
        whenever(event.rawSlot).thenReturn(slot)
        whenever(event.slot).thenReturn(slot)
        whenever(event.currentItem).thenReturn(item)
        whenever(event.view).thenReturn(player.openInventory)
        whenever(event.isCancelled).thenReturn(false)

        return event
    }

    /**
     * Creates a test item stack with custom name and optional lore
     * @param material The material of the item
     * @param name Custom name for the item
     * @param lore Optional lore lines for the item
     * @return An ItemStack with the specified properties
     */
    fun createTestItem(
        material: Material,
        name: String,
        vararg lore: String,
    ): ItemStack =
        ItemStack(material).apply {
            itemMeta =
                itemMeta?.also { meta ->
                    meta.setDisplayName(name)
                    if (lore.isNotEmpty()) {
                        meta.lore = lore.toList()
                    }
                }
        }

    /**
     * Creates a mock ItemMeta
     * @param name Display name for the item
     * @param lore Optional lore lines
     * @return A mocked ItemMeta
     */
    fun createMockItemMeta(
        name: String,
        vararg lore: String,
    ): ItemMeta {
        val meta = Mockito.mock(ItemMeta::class.java)
        whenever(meta.displayName).thenReturn(name)
        whenever(meta.hasDisplayName()).thenReturn(true)
        if (lore.isNotEmpty()) {
            whenever(meta.hasLore()).thenReturn(true)
            whenever(meta.lore).thenReturn(lore.toList())
        }
        return meta
    }
}
