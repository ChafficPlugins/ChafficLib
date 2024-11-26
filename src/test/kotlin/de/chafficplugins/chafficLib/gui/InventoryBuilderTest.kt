package de.chafficplugins.chafficLib.gui

import de.chafficplugins.chafficLib.test.BaseTest
import de.chafficplugins.chafficLib.test.util.GuiTestUtil
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource

class InventoryBuilderTest : BaseTest() {
    private lateinit var builder: InventoryBuilder
    private val testTitle = "Test Inventory"

    @BeforeEach
    fun setup() {
        builder = InventoryBuilder.create(testTitle, InventorySize.THREE_ROWS)
    }

    @Nested
    inner class CreateTests {
        @ParameterizedTest
        @EnumSource(InventorySize::class)
        fun `should create inventory with correct size`(size: InventorySize) {
            val inventory = InventoryBuilder.create(testTitle, size).build()
            assertEquals(size.size, inventory.size)
        }
    }

    @Nested
    inner class ItemManagementTests {
        @Test
        fun `setItem should place item in correct slot`() {
            val item = GuiTestUtil.createTestItem(Material.DIAMOND, "Test Diamond")
            builder.setItem(0, item)

            val result = builder.build().getItem(0)
            assertNotNull(result)
            assertEquals(item.type, result?.type)
            assertEquals(item.itemMeta?.displayName, result?.itemMeta?.displayName)
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, 27, 100])
        fun `setItem should throw exception for invalid slots`(slot: Int) {
            assertThrows<IllegalArgumentException> {
                builder.setItem(slot, ItemStack(Material.DIAMOND))
            }
        }

        @Test
        fun `fillEmpty should fill all empty slots`() {
            val filler = ItemStack(Material.GLASS)
            val diamond = ItemStack(Material.DIAMOND)

            builder.setItem(0, diamond)
            builder.fillEmpty(filler)

            val inventory = builder.build()
            assertEquals(Material.DIAMOND, inventory.getItem(0)?.type)

            for (i in 1 until inventory.size) {
                assertEquals(Material.GLASS, inventory.getItem(i)?.type)
            }
        }
    }

    @Nested
    inner class ClickHandlerTests {
        @Test
        fun `should register and invoke click handler`() {
            var clicked = false
            val item = GuiTestUtil.createTestItem(Material.DIAMOND, "Test Diamond")

            builder.setItem(0, item) { clicked = true }

            val handler = builder.getClickHandler(0)
            assertNotNull(handler)

            val player = createPlayer()
            handler?.invoke(GuiTestUtil.createClickEvent(player, builder.build(), 0, item))

            assertTrue(clicked)
        }

        @Test
        fun `should not have handler for empty slot`() {
            assertNull(builder.getClickHandler(0))
        }
    }

    @Test
    fun `should build inventory without modifications`() {
        val inventory = builder.build()
        assertNotNull(inventory)
        assertEquals(InventorySize.THREE_ROWS.size, inventory.size)
        assertTrue(inventory.contents.all { it == null })
    }
}
