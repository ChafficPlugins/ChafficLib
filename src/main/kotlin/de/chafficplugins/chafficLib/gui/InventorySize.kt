package de.chafficplugins.chafficLib.gui

/**
 * Represents standard Minecraft inventory sizes.
 * Each row contains 9 slots, and inventories can have 1-6 rows.
 *
 * @property rows Number of rows in the inventory
 * @property size Total number of slots (rows * 9)
 */
enum class InventorySize(
    val rows: Int,
    val size: Int,
) {
    ONE_ROW(1, 9),
    TWO_ROWS(2, 18),
    THREE_ROWS(3, 27),
    FOUR_ROWS(4, 36),
    FIVE_ROWS(5, 45),
    SIX_ROWS(6, 54),
    ;

    companion object {
        /**
         * Get the appropriate InventorySize for the given number of rows
         * @param rows Number of rows (1-6)
         * @return InventorySize corresponding to the number of rows
         * @throws IllegalArgumentException if rows is not between 1 and 6
         */
        fun fromRows(rows: Int): InventorySize =
            entries.find { it.rows == rows }
                ?: throw IllegalArgumentException("Invalid number of rows: $rows. Must be between 1 and 6.")

        /**
         * Get the appropriate InventorySize for the given size
         * @param size Total number of slots (must be multiple of 9 and between 9 and 54)
         * @return InventorySize corresponding to the size
         * @throws IllegalArgumentException if size is not valid
         */
        fun fromSize(size: Int): InventorySize =
            entries.find { it.size == size }
                ?: throw IllegalArgumentException("Invalid inventory size: $size. Must be multiple of 9 and between 9 and 54.")
    }
}
