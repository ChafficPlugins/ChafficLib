package de.chafficplugins.chafficLib.gui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class InventorySizeTest {
    @Nested
    inner class FromRowsTests {
        @ParameterizedTest
        @ValueSource(ints = [1, 2, 3, 4, 5, 6])
        fun `should return correct size for valid rows`(rows: Int) {
            val size = InventorySize.fromRows(rows)
            assertEquals(rows, size.rows)
            assertEquals(rows * 9, size.size)
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, 0, 7, 8, 100])
        fun `should throw exception for invalid rows`(rows: Int) {
            assertThrows<IllegalArgumentException> {
                InventorySize.fromRows(rows)
            }
        }
    }

    @Nested
    inner class FromSizeTests {
        @ParameterizedTest
        @ValueSource(ints = [9, 18, 27, 36, 45, 54])
        fun `should return correct size for valid sizes`(size: Int) {
            val inventorySize = InventorySize.fromSize(size)
            assertEquals(size, inventorySize.size)
            assertEquals(size / 9, inventorySize.rows)
        }

        @ParameterizedTest
        @ValueSource(ints = [-9, 0, 8, 10, 55, 63])
        fun `should throw exception for invalid sizes`(size: Int) {
            assertThrows<IllegalArgumentException> {
                InventorySize.fromSize(size)
            }
        }
    }

    @Test
    fun `should have correct values for all enum constants`() {
        assertEquals(9, InventorySize.ONE_ROW.size)
        assertEquals(18, InventorySize.TWO_ROWS.size)
        assertEquals(27, InventorySize.THREE_ROWS.size)
        assertEquals(36, InventorySize.FOUR_ROWS.size)
        assertEquals(45, InventorySize.FIVE_ROWS.size)
        assertEquals(54, InventorySize.SIX_ROWS.size)
    }
}
