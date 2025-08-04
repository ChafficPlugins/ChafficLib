package de.chafficplugins.chafficLib.gui.templates

import de.chafficplugins.chafficLib.gui.InventoryBuilder
import de.chafficplugins.chafficLib.gui.InventoryTemplate
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * Template for creating a navigation bar at the bottom of an inventory
 * @property buttons List of navigation buttons with their slots and items
 * @property onNavigate Callback when navigation occurs
 */
class NavigationBarTemplate(
    private val buttons: List<NavigationButton>,
    private val onNavigate: ((String) -> Unit)? = null
) : InventoryTemplate {
    
    data class NavigationButton(
        val id: String,
        val slot: Int,
        val item: ItemStack,
        val enabled: Boolean = true
    )

    override fun apply(builder: InventoryBuilder) {
        buttons.forEach { button ->
            if (button.enabled) {
                builder.setItem(button.slot, button.item) { event ->
                    onNavigate?.invoke(button.id)
                    event.isCancelled = true
                }
            } else {
                // Disabled button - gray out
                val disabledItem = button.item.clone()
                val meta = disabledItem.itemMeta
                meta?.setDisplayName("§7${meta.displayName}")
                meta?.lore = meta.lore?.map { "§7$it" } ?: listOf("§7Disabled")
                disabledItem.itemMeta = meta
                builder.setItem(button.slot, disabledItem)
            }
        }
    }
}

/**
 * Template for creating a button grid
 * @property startSlot The starting slot for the grid
 * @property columns Number of columns in the grid
 * @property buttons List of buttons to place in the grid
 */
class ButtonGridTemplate(
    private val startSlot: Int,
    private val columns: Int,
    private val buttons: List<GridButton>
) : InventoryTemplate {
    
    data class GridButton(
        val item: ItemStack,
        val onClick: (InventoryClickEvent) -> Unit,
        val enabled: Boolean = true
    )

    override fun apply(builder: InventoryBuilder) {
        buttons.forEachIndexed { index, button ->
            val row = index / columns
            val col = index % columns
            val slot = startSlot + (row * 9) + col
            
            if (button.enabled) {
                builder.setItem(slot, button.item, button.onClick)
            } else {
                // Disabled button
                val disabledItem = button.item.clone()
                val meta = disabledItem.itemMeta
                meta?.setDisplayName("§7${meta.displayName}")
                meta?.lore = meta.lore?.map { "§7$it" } ?: listOf("§7Disabled")
                disabledItem.itemMeta = meta
                builder.setItem(slot, disabledItem)
            }
        }
    }
}

/**
 * Template for creating an action bar with common action buttons
 * @property actions List of action buttons
 * @property position The position of the action bar (TOP, BOTTOM, LEFT, RIGHT)
 */
class ActionBarTemplate(
    private val actions: List<ActionButton>,
    private val position: ActionBarPosition = ActionBarPosition.BOTTOM
) : InventoryTemplate {
    
    enum class ActionBarPosition {
        TOP, BOTTOM, LEFT, RIGHT
    }
    
    data class ActionButton(
        val id: String,
        val item: ItemStack,
        val onClick: (InventoryClickEvent) -> Unit,
        val enabled: Boolean = true
    )

    override fun apply(builder: InventoryBuilder) {
        val inventorySize = builder.getSize()
        
        val slots = when (position) {
            ActionBarPosition.TOP -> (0..8).toList()
            ActionBarPosition.BOTTOM -> {
                (inventorySize - 9 until inventorySize).toList()
            }
            ActionBarPosition.LEFT -> {
                (0 until inventorySize step 9).toList()
            }
            ActionBarPosition.RIGHT -> {
                (8 until inventorySize step 9).toList()
            }
        }
        
        actions.forEachIndexed { index, action ->
            if (index < slots.size) {
                val slot = slots[index]
                if (action.enabled) {
                    builder.setItem(slot, action.item, action.onClick)
                } else {
                    // Disabled action
                    val disabledItem = action.item.clone()
                    val meta = disabledItem.itemMeta
                    meta?.setDisplayName("§7${meta.displayName}")
                    meta?.lore = meta.lore?.map { "§7$it" } ?: listOf("§7Disabled")
                    disabledItem.itemMeta = meta
                    builder.setItem(slot, disabledItem)
                }
            }
        }
    }
}

/**
 * Template for creating a pagination control bar
 * @property currentPage Current page number
 * @property totalPages Total number of pages
 * @property onPageChange Callback when page changes
 * @property position Position of the pagination bar
 */
class PaginationBarTemplate(
    private var currentPage: Int,
    private val totalPages: Int,
    private val onPageChange: ((Int) -> Unit)? = null,
    private val position: PaginationPosition = PaginationPosition.BOTTOM
) : InventoryTemplate {
    
    enum class PaginationPosition {
        TOP, BOTTOM
    }

    override fun apply(builder: InventoryBuilder) {
        val inventorySize = builder.getSize()
        val baseSlot = when (position) {
            PaginationPosition.TOP -> 0
            PaginationPosition.BOTTOM -> inventorySize - 9
        }
        
        // Previous page button
        val prevButton = if (currentPage > 0) {
            ButtonItems.createButton(
                Material.ARROW,
                "§aPrevious Page",
                "§7Go to page ${currentPage}"
            )
        } else {
            ButtonItems.createButton(
                Material.BARRIER,
                "§7Previous Page",
                "§7No previous page"
            )
        }
        
        builder.setItem(baseSlot + 3, prevButton) { event ->
            if (currentPage > 0) {
                currentPage--
                onPageChange?.invoke(currentPage)
            }
            event.isCancelled = true
        }
        
        // Page indicator
        val pageIndicator = ButtonItems.createButton(
            Material.PAPER,
            "§ePage ${currentPage + 1} of $totalPages",
            "§7Click to refresh"
        )
        
        builder.setItem(baseSlot + 4, pageIndicator) { event ->
            onPageChange?.invoke(currentPage)
            event.isCancelled = true
        }
        
        // Next page button
        val nextButton = if (currentPage < totalPages - 1) {
            ButtonItems.createButton(
                Material.ARROW,
                "§aNext Page",
                "§7Go to page ${currentPage + 2}"
            )
        } else {
            ButtonItems.createButton(
                Material.BARRIER,
                "§7Next Page",
                "§7No next page"
            )
        }
        
        builder.setItem(baseSlot + 5, nextButton) { event ->
            if (currentPage < totalPages - 1) {
                currentPage++
                onPageChange?.invoke(currentPage)
            }
            event.isCancelled = true
        }
    }
    
    /**
     * Set the current page programmatically
     * @param page The new page number
     */
    fun setCurrentPage(page: Int) {
        if (page in 0 until totalPages) {
            currentPage = page
        }
    }
}

/**
 * Template for creating a close button
 * @property slot The slot for the close button
 * @property onClose Callback when close button is clicked
 */
class CloseButtonTemplate(
    private val slot: Int,
    private val onClose: (InventoryClickEvent) -> Unit
) : InventoryTemplate {
    
    override fun apply(builder: InventoryBuilder) {
        val closeButton = ButtonItems.createButton(
            Material.BARRIER,
            "§cClose",
            "§7Click to close this menu"
        )
        
        builder.setItem(slot, closeButton) { event ->
            onClose(event)
            event.isCancelled = true
        }
    }
}

/**
 * Template for creating a back button
 * @property slot The slot for the back button
 * @property onBack Callback when back button is clicked
 */
class BackButtonTemplate(
    private val slot: Int,
    private val onBack: (InventoryClickEvent) -> Unit
) : InventoryTemplate {
    
    override fun apply(builder: InventoryBuilder) {
        val backButton = ButtonItems.createButton(
            Material.ARROW,
            "§eBack",
            "§7Return to previous menu"
        )
        
        builder.setItem(slot, backButton) { event ->
            onBack(event)
            event.isCancelled = true
        }
    }
} 