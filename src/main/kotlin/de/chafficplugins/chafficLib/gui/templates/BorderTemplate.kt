package de.chafficplugins.chafficLib.gui.templates

import de.chafficplugins.chafficLib.gui.InventoryBuilder
import de.chafficplugins.chafficLib.gui.InventoryTemplate
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Template that creates a border around the inventory
 * @property borderItem The item to use for the border
 */
class BorderTemplate(
    private val borderItem: ItemStack = ItemStack(Material.BLACK_STAINED_GLASS_PANE),
) : InventoryTemplate {
    override fun apply(builder: InventoryBuilder) {
        // Top and bottom rows
        for (i in 0..8) {
            builder.setItem(i, borderItem.clone())
            builder.setItem(builder.build().size - 9 + i, borderItem.clone())
        }

        // Left and right columns
        val rows = builder.build().size / 9
        for (i in 1 until rows - 1) {
            builder.setItem(i * 9, borderItem.clone())
            builder.setItem(i * 9 + 8, borderItem.clone())
        }
    }
}
