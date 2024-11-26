package de.chafficplugins.chafficLib.gui

/**
 * Interface for creating reusable inventory templates
 */
interface InventoryTemplate {
    /**
     * Apply this template to an inventory builder
     * @param builder The builder to apply the template to
     */
    fun apply(builder: InventoryBuilder)
}
