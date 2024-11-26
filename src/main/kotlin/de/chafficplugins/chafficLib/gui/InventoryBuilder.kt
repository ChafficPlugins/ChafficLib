package de.chafficplugins.chafficLib.gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * Fluent builder for creating custom inventories.
 *
 * @property title The title of the inventory
 * @property size The size of the inventory
 */
class InventoryBuilder private constructor(
    private val title: String,
    private val size: InventorySize,
) {
    private val inventory: Inventory = Bukkit.createInventory(null, size.size, title)
    private val clickHandlers: MutableMap<Int, (InventoryClickEvent) -> Unit> = mutableMapOf()

    companion object {
        /**
         * Create a new InventoryBuilder
         * @param title The title of the inventory
         * @param size The size of the inventory
         * @return A new InventoryBuilder instance
         */
        @JvmStatic
        fun create(
            title: String,
            size: InventorySize,
        ): InventoryBuilder = InventoryBuilder(title, size)
    }

    /**
     * Set an item at the specified slot
     * @param slot The slot to place the item in
     * @param item The item to place
     * @param onClick Optional click handler for the item
     * @return This builder instance
     * @throws IllegalArgumentException if slot is out of bounds
     */
    fun setItem(
        slot: Int,
        item: ItemStack,
        onClick: ((InventoryClickEvent) -> Unit)? = null,
    ): InventoryBuilder {
        validateSlot(slot)
        inventory.setItem(slot, item)
        onClick?.let { clickHandlers[slot] = it }
        return this
    }

    /**
     * Fill all empty slots with the specified item
     * @param item The item to fill with
     * @param onClick Optional click handler for the filled items
     * @return This builder instance
     */
    fun fillEmpty(
        item: ItemStack = ItemStack(Material.AIR),
        onClick: ((InventoryClickEvent) -> Unit)? = null,
    ): InventoryBuilder {
        for (slot in 0 until size.size) {
            if (inventory.getItem(slot) == null) {
                setItem(slot, item, onClick)
            }
        }
        return this
    }

    /**
     * Apply a template to the inventory
     * @param template The template to apply
     * @return This builder instance
     */
    fun applyTemplate(template: InventoryTemplate): InventoryBuilder {
        template.apply(this)
        return this
    }

    /**
     * Build the inventory
     * @return The built inventory
     */
    fun build(): Inventory = inventory

    /**
     * Get the click handler for a specific slot
     * @param slot The slot to get the handler for
     * @return The click handler for the slot, or null if none exists
     */
    fun getClickHandler(slot: Int): ((InventoryClickEvent) -> Unit)? = clickHandlers[slot]

    /**
     * Validate that a slot is within bounds
     * @param slot The slot to validate
     * @throws IllegalArgumentException if slot is out of bounds
     */
    private fun validateSlot(slot: Int) {
        if (slot < 0 || slot >= size.size) {
            throw IllegalArgumentException("Slot $slot is out of bounds for inventory size ${size.size}")
        }
    }
}
