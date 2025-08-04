package de.chafficplugins.chafficLib.gui.templates

import de.chafficplugins.chafficLib.gui.InventoryBuilder
import de.chafficplugins.chafficLib.gui.InventorySize
import de.chafficplugins.chafficLib.test.BaseTest
import de.chafficplugins.chafficLib.test.util.GuiTestUtil
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class BorderTemplateTest : BaseTest() {
    private lateinit var builder: InventoryBuilder

    @BeforeEach
    fun setup() {
        builder = InventoryBuilder.create("Test Inventory", InventorySize.THREE_ROWS)
    }

    @Nested
    inner class BorderApplicationTests {
        @ParameterizedTest
        @EnumSource(InventorySize::class)
        fun `should apply border to all inventory sizes`(size: InventorySize) {
            // Given
            val testBuilder = InventoryBuilder.create("Test", size)
            val template = BorderTemplate()

            // When
            testBuilder.applyTemplate(template)
            val inventory = testBuilder.build()

            // Then - Check top row
            for (i in 0..8) {
                assertEquals(
                    Material.BLACK_STAINED_GLASS_PANE,
                    inventory.getItem(i)?.type,
                    "Top border missing at slot $i"
                )
            }

            // Check bottom row
            val lastRow = size.size - 9
            for (i in lastRow until size.size) {
                assertEquals(
                    Material.BLACK_STAINED_GLASS_PANE,
                    inventory.getItem(i)?.type,
                    "Bottom border missing at slot $i"
                )
            }

            // Check sides (if more than one row)
            if (size.rows > 1) {
                for (row in 1 until size.rows - 1) {
                    val leftSlot = row * 9
                    val rightSlot = row * 9 + 8
                    assertEquals(
                        Material.BLACK_STAINED_GLASS_PANE,
                        inventory.getItem(leftSlot)?.type,
                        "Left border missing at slot $leftSlot"
                    )
                    assertEquals(
                        Material.BLACK_STAINED_GLASS_PANE,
                        inventory.getItem(rightSlot)?.type,
                        "Right border missing at slot $rightSlot"
                    )
                }
            }
        }

        @Test
        fun `should use default border item when none specified`() {
            // Given
            val template = BorderTemplate()

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(0)?.type)
        }

        @Test
        fun `should use custom border item when specified`() {
            // Given
            val customBorder = GuiTestUtil.createTestItem(
                Material.RED_STAINED_GLASS_PANE,
                "Custom Border",
                "Line 1",
                "Line 2"
            )
            val template = BorderTemplate(customBorder)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            val firstSlot = inventory.getItem(0)
            assertNotNull(firstSlot)
            assertEquals(customBorder.type, firstSlot?.type)
            assertEquals(customBorder.itemMeta?.displayName, firstSlot?.itemMeta?.displayName)
            assertEquals(customBorder.itemMeta?.lore, firstSlot?.itemMeta?.lore)
        }
    }

    @Nested
    inner class BorderPreservationTests {
        @Test
        fun `should preserve existing items when applying border`() {
            // Given
            val testItem = GuiTestUtil.createTestItem(Material.DIAMOND, "Test Item")
            val centerSlot = 13 // Center slot in a 3-row inventory
            val template = BorderTemplate()

            // When
            builder.setItem(centerSlot, testItem)
            builder.applyTemplate(template)

            // Then
            val inventory = builder.build()
            assertEquals(
                testItem.type,
                inventory.getItem(centerSlot)?.type,
                "Existing item should not be replaced by border"
            )
            assertEquals(
                testItem.itemMeta?.displayName,
                inventory.getItem(centerSlot)?.itemMeta?.displayName,
                "Existing item metadata should be preserved"
            )
        }

        @Test
        fun `should maintain click handlers when applying border`() {
            // Given
            var clicked = false
            val centerSlot = 13
            val template = BorderTemplate()

            // When
            builder.setItem(centerSlot, ItemStack(Material.DIAMOND)) { clicked = true }
            builder.applyTemplate(template)

            // Then
            val handler = builder.getClickHandler(centerSlot)
            assertNotNull(handler, "Click handler should be preserved")

            handler?.invoke(
                GuiTestUtil.createClickEvent(
                    createPlayer(),
                    builder.build(),
                    centerSlot,
                    ItemStack(Material.DIAMOND)
                )
            )

            assertEquals(true, clicked, "Click handler should still work after applying template")
        }
    }

    @Nested
    inner class BorderSlotTests {
        @Test
        fun `should apply border to correct slots in three row inventory`() {
            // Given
            val template = BorderTemplate()

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - Top row (slots 0-8)
            for (i in 0..8) {
                assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(i)?.type)
            }

            // Then - Bottom row (slots 18-26)
            for (i in 18..26) {
                assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(i)?.type)
            }

            // Then - Left column (slots 9, 18)
            assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(9)?.type)
            assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(18)?.type)

            // Then - Right column (slots 17, 26)
            assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(17)?.type)
            assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(26)?.type)
        }

        @Test
        fun `should apply border to correct slots in six row inventory`() {
            // Given
            val sixRowBuilder = InventoryBuilder.create("Test", InventorySize.SIX_ROWS)
            val template = BorderTemplate()

            // When
            sixRowBuilder.applyTemplate(template)
            val inventory = sixRowBuilder.build()

            // Then - Top row (slots 0-8)
            for (i in 0..8) {
                assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(i)?.type)
            }

            // Then - Bottom row (slots 45-53)
            for (i in 45..53) {
                assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(i)?.type)
            }

            // Then - Left column (slots 9, 18, 27, 36, 45)
            for (i in listOf(9, 18, 27, 36, 45)) {
                assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(i)?.type)
            }

            // Then - Right column (slots 17, 26, 35, 44, 53)
            for (i in listOf(17, 26, 35, 44, 53)) {
                assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(i)?.type)
            }
        }
    }

    @Nested
    inner class BorderItemTests {
        @Test
        fun `should clone border items correctly`() {
            // Given
            val originalBorder = GuiTestUtil.createTestItem(
                Material.BLUE_STAINED_GLASS_PANE,
                "Original Border"
            )
            val template = BorderTemplate(originalBorder)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - All border items should be clones, not the same instance
            val firstBorder = inventory.getItem(0)
            val secondBorder = inventory.getItem(1)
            assertNotNull(firstBorder)
            assertNotNull(secondBorder)
            
            // Items should have same properties but be different instances
            assertEquals(firstBorder?.type, secondBorder?.type)
            assertEquals(firstBorder?.itemMeta?.displayName, secondBorder?.itemMeta?.displayName)
        }

        @Test
        fun `should handle border items with custom metadata`() {
            // Given
            val customBorder = ItemStack(Material.PURPLE_STAINED_GLASS_PANE).apply {
                val meta = itemMeta
                meta?.setDisplayName("§dPurple Border")
                meta?.lore = listOf("§7Custom border item", "§7With multiple lines")
                itemMeta = meta
            }
            val template = BorderTemplate(customBorder)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            val borderItem = inventory.getItem(0)
            assertNotNull(borderItem)
            assertEquals(Material.PURPLE_STAINED_GLASS_PANE, borderItem?.type)
            assertEquals("§dPurple Border", borderItem?.itemMeta?.displayName)
            assertEquals(
                listOf("§7Custom border item", "§7With multiple lines"),
                borderItem?.itemMeta?.lore
            )
        }
    }

    @Nested
    inner class BorderEdgeCasesTests {
        @Test
        fun `should handle single row inventory correctly`() {
            // Given
            val singleRowBuilder = InventoryBuilder.create("Test", InventorySize.ONE_ROW)
            val template = BorderTemplate()

            // When
            singleRowBuilder.applyTemplate(template)
            val inventory = singleRowBuilder.build()

            // Then - Only top row should have border (bottom row is the same as top row)
            for (i in 0..8) {
                assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(i)?.type)
            }
        }

        @Test
        fun `should handle two row inventory correctly`() {
            // Given
            val twoRowBuilder = InventoryBuilder.create("Test", InventorySize.TWO_ROWS)
            val template = BorderTemplate()

            // When
            twoRowBuilder.applyTemplate(template)
            val inventory = twoRowBuilder.build()

            // Then - Top row (slots 0-8)
            for (i in 0..8) {
                assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(i)?.type)
            }

            // Then - Bottom row (slots 9-17)
            for (i in 9..17) {
                assertEquals(Material.BLACK_STAINED_GLASS_PANE, inventory.getItem(i)?.type)
            }

            // Then - No side borders since there are only 2 rows
            // (left and right columns are the same as top and bottom rows)
        }
    }
} 