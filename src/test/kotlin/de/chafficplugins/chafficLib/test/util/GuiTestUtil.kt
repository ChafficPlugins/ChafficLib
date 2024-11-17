package de.chafficplugins.chafficLib.test.util

import be.seeseemelk.mockbukkit.entity.PlayerMock
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
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
        whenever(event.currentItem).thenReturn(item)
        whenever(event.view).thenReturn(player.openInventory)

        return event
    }

    /**
     * Creates a test item stack with custom name
     * @param material The material of the item
     * @param name Custom name for the item
     * @return An ItemStack with the specified properties
     */
    fun createTestItem(
        material: Material,
        name: String,
    ): ItemStack =
        ItemStack(material).apply {
            itemMeta =
                itemMeta?.also {
                    it.setDisplayName(name)
                }
        }
}
