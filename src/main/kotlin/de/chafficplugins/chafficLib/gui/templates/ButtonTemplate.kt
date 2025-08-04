package de.chafficplugins.chafficLib.gui.templates

import de.chafficplugins.chafficLib.gui.InventoryBuilder
import de.chafficplugins.chafficLib.gui.InventoryTemplate
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * Template for creating a simple button
 * @property slot The slot where the button should be placed
 * @property item The item to display as the button
 * @property onClick The click handler for the button
 */
class ButtonTemplate(
    private val slot: Int,
    private val item: ItemStack,
    private val onClick: (InventoryClickEvent) -> Unit
) : InventoryTemplate {
    override fun apply(builder: InventoryBuilder) {
        builder.setItem(slot, item, onClick)
    }
}

/**
 * Template for creating a toggle button that switches between two states
 * @property slot The slot where the toggle button should be placed
 * @property enabledItem The item to display when the toggle is enabled
 * @property disabledItem The item to display when the toggle is disabled
 * @property initialState The initial state of the toggle (default: false)
 * @property onToggle The callback when the toggle state changes
 */
class ToggleButtonTemplate(
    private val slot: Int,
    private val enabledItem: ItemStack,
    private val disabledItem: ItemStack,
    private val initialState: Boolean = false,
    private val onToggle: ((Boolean) -> Unit)? = null
) : InventoryTemplate {
    private var isEnabled = initialState

    override fun apply(builder: InventoryBuilder) {
        builder.setItem(slot, if (isEnabled) enabledItem else disabledItem) { event ->
            isEnabled = !isEnabled
            // Update the item in the inventory using the builder
            builder.setItem(slot, if (isEnabled) enabledItem else disabledItem)
            onToggle?.invoke(isEnabled)
            event.isCancelled = true
        }
    }

    /**
     * Get the current state of the toggle
     * @return true if enabled, false if disabled
     */
    fun isEnabled(): Boolean = isEnabled

    /**
     * Set the toggle state programmatically
     * @param enabled The new state
     */
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }
}

/**
 * Template for creating a multi-state button that cycles through different states
 * @property slot The slot where the button should be placed
 * @property states List of button states with their items and callbacks
 * @property initialState The initial state index (default: 0)
 */
class MultiStateButtonTemplate(
    private val slot: Int,
    private val states: List<ButtonState>,
    private val initialState: Int = 0
) : InventoryTemplate {
    private var currentStateIndex = initialState

    data class ButtonState(
        val item: ItemStack,
        val onClick: ((InventoryClickEvent) -> Unit)? = null
    )

    override fun apply(builder: InventoryBuilder) {
        if (states.isEmpty()) return
        
        val currentState = states[currentStateIndex]
        builder.setItem(slot, currentState.item) { event ->
            // Call the state-specific click handler if provided
            currentState.onClick?.invoke(event)
            
            // Cycle to next state
            currentStateIndex = (currentStateIndex + 1) % states.size
            val nextState = states[currentStateIndex]
            // Update the item in the inventory using the builder
            builder.setItem(slot, nextState.item)
            
            event.isCancelled = true
        }
    }

    /**
     * Get the current state index
     * @return The current state index
     */
    fun getCurrentStateIndex(): Int = currentStateIndex

    /**
     * Set the current state programmatically
     * @param stateIndex The new state index
     */
    fun setState(stateIndex: Int) {
        if (stateIndex in states.indices) {
            currentStateIndex = stateIndex
        }
    }
}

/**
 * Template for creating a confirmation button that requires double-click
 * @property slot The slot where the button should be placed
 * @property item The item to display as the button
 * @property onConfirm The callback when the button is confirmed (double-clicked)
 * @property confirmationDelay The delay in milliseconds before resetting the confirmation state
 */
class ConfirmationButtonTemplate(
    private val slot: Int,
    private val item: ItemStack,
    private val onConfirm: (InventoryClickEvent) -> Unit,
    private val confirmationDelay: Long = 2000
) : InventoryTemplate {
    private var isWaitingForConfirmation = false
    private var confirmationTask: org.bukkit.scheduler.BukkitTask? = null

    override fun apply(builder: InventoryBuilder) {
        builder.setItem(slot, item) { event ->
            if (!isWaitingForConfirmation) {
                // First click - start confirmation
                isWaitingForConfirmation = true
                
                // Schedule task to reset confirmation state
                confirmationTask?.cancel()
                confirmationTask = org.bukkit.Bukkit.getScheduler().runTaskLater(
                    org.bukkit.Bukkit.getPluginManager().getPlugin("ChafficLib")!!,
                    Runnable {
                        isWaitingForConfirmation = false
                        // Reset button appearance using builder
                        builder.setItem(slot, item)
                    },
                    confirmationDelay / 50 // Convert to ticks
                )
                
                // Change button appearance to show confirmation state
                val confirmItem = item.clone()
                val meta = confirmItem.itemMeta
                meta?.setDisplayName("${meta.displayName} §a(Click to confirm)")
                confirmItem.itemMeta = meta
                builder.setItem(slot, confirmItem)
                
            } else {
                // Second click - confirm action
                isWaitingForConfirmation = false
                confirmationTask?.cancel()
                onConfirm(event)
            }
            
            event.isCancelled = true
        }
    }
}

/**
 * Utility class for creating common button items
 */
object ButtonItems {
    /**
     * Create a simple button item
     * @param material The material for the button
     * @param name The display name
     * @param lore The lore lines
     * @return The button item
     */
    fun createButton(material: Material, name: String, vararg lore: String): ItemStack {
        return ItemStack(material).apply {
            val meta = itemMeta
            meta?.setDisplayName(name)
            if (lore.isNotEmpty()) {
                meta?.lore = lore.toList()
            }
            itemMeta = meta
        }
    }

    /**
     * Create a toggle button item
     * @param enabled Whether the toggle is enabled
     * @param enabledName The name when enabled
     * @param disabledName The name when disabled
     * @param enabledLore The lore when enabled
     * @param disabledLore The lore when disabled
     * @return The toggle button item
     */
    fun createToggleButton(
        enabled: Boolean,
        enabledName: String,
        disabledName: String,
        enabledLore: List<String> = listOf("§7Click to disable"),
        disabledLore: List<String> = listOf("§7Click to enable")
    ): ItemStack {
        return if (enabled) {
            createButton(Material.LIME_STAINED_GLASS_PANE, enabledName, *enabledLore.toTypedArray())
        } else {
            createButton(Material.RED_STAINED_GLASS_PANE, disabledName, *disabledLore.toTypedArray())
        }
    }

    /**
     * Create a confirmation button item
     * @param material The material for the button
     * @param name The display name
     * @param lore The lore lines
     * @return The confirmation button item
     */
    fun createConfirmationButton(material: Material, name: String, vararg lore: String): ItemStack {
        return createButton(material, name, *lore, "§eClick twice to confirm")
    }
} 