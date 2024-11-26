package de.chafficplugins.chafficLib.gui

import de.chafficplugins.chafficLib.gui.templates.BorderTemplate
import de.chafficplugins.chafficLib.test.BaseTest
import de.chafficplugins.chafficLib.test.util.GuiTestUtil
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class InventoryTemplateTest : BaseTest() {
    private lateinit var builder: InventoryBuilder

    @BeforeEach
    fun setup() {
        builder = InventoryBuilder.create("Test Inventory", InventorySize.THREE_ROWS)
    }

    @Nested
    inner class BorderTemplateTests {
        private val borderItem = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        private lateinit var template: BorderTemplate

        @BeforeEach
        fun setupTemplate() {
            template = BorderTemplate(borderItem)
        }

        @ParameterizedTest
        @EnumSource(InventorySize::class)
        fun `should apply border to all inventory sizes`(size: InventorySize) {
            val testBuilder = InventoryBuilder.create("Test", size)
            testBuilder.applyTemplate(template)
            val inventory = testBuilder.build()

            // Check top row
            for (i in 0..8) {
                assertEquals(
                    borderItem.type,
                    inventory.getItem(i)?.type,
                    "Top border missing at slot $i",
                )
            }

            // Check bottom row
            val lastRow = size.size - 9
            for (i in lastRow until size.size) {
                assertEquals(
                    borderItem.type,
                    inventory.getItem(i)?.type,
                    "Bottom border missing at slot $i",
                )
            }

            // Check sides (if more than one row)
            if (size.rows > 1) {
                for (row in 1 until size.rows - 1) {
                    val leftSlot = row * 9
                    val rightSlot = row * 9 + 8
                    assertEquals(
                        borderItem.type,
                        inventory.getItem(leftSlot)?.type,
                        "Left border missing at slot $leftSlot",
                    )
                    assertEquals(
                        borderItem.type,
                        inventory.getItem(rightSlot)?.type,
                        "Right border missing at slot $rightSlot",
                    )
                }
            }
        }

        @Test
        fun `should preserve existing items when applying border`() {
            val testItem = GuiTestUtil.createTestItem(Material.DIAMOND, "Test Item")
            val centerSlot = 13 // Center slot in a 3-row inventory

            builder.setItem(centerSlot, testItem)
            builder.applyTemplate(template)

            val inventory = builder.build()
            assertEquals(
                testItem.type,
                inventory.getItem(centerSlot)?.type,
                "Existing item should not be replaced by border",
            )
            assertEquals(
                testItem.itemMeta?.displayName,
                inventory.getItem(centerSlot)?.itemMeta?.displayName,
                "Existing item metadata should be preserved",
            )
        }

        @Test
        fun `should maintain click handlers when applying border`() {
            var clicked = false
            val centerSlot = 13

            builder.setItem(centerSlot, ItemStack(Material.DIAMOND)) { clicked = true }
            builder.applyTemplate(template)

            val handler = builder.getClickHandler(centerSlot)
            assertNotNull(handler, "Click handler should be preserved")

            handler?.invoke(
                GuiTestUtil.createClickEvent(
                    createPlayer(),
                    builder.build(),
                    centerSlot,
                    ItemStack(Material.DIAMOND),
                ),
            )

            assertTrue(clicked, "Click handler should still work after applying template")
        }

        @Test
        fun `should use custom border item correctly`() {
            val customBorder =
                GuiTestUtil.createTestItem(
                    Material.RED_STAINED_GLASS_PANE,
                    "Custom Border",
                    "Line 1",
                    "Line 2",
                )
            val customTemplate = BorderTemplate(customBorder)

            builder.applyTemplate(customTemplate)
            val inventory = builder.build()

            // Check first slot for custom properties
            val firstSlot = inventory.getItem(0)
            assertNotNull(firstSlot)
            assertEquals(customBorder.type, firstSlot?.type)
            assertEquals(
                customBorder.itemMeta?.displayName,
                firstSlot?.itemMeta?.displayName,
            )
            assertEquals(
                customBorder.itemMeta?.lore,
                firstSlot?.itemMeta?.lore,
            )
        }
    }

    @Nested
    inner class CustomTemplateTests {
        @Test
        fun `should support multiple templates on same inventory`() {
            val template1 = BorderTemplate(ItemStack(Material.BLACK_STAINED_GLASS_PANE))
            val template2 =
                object : InventoryTemplate {
                    override fun apply(builder: InventoryBuilder) {
                        builder.setItem(
                            13,
                            GuiTestUtil.createTestItem(
                                Material.DIAMOND,
                                "Center Item",
                            ),
                        )
                    }
                }

            builder
                .applyTemplate(template1)
                .applyTemplate(template2)

            val inventory = builder.build()

            // Check border is applied
            assertEquals(
                Material.BLACK_STAINED_GLASS_PANE,
                inventory.getItem(0)?.type,
            )

            // Check center item is applied
            assertEquals(Material.DIAMOND, inventory.getItem(13)?.type)
            assertEquals(
                "Center Item",
                inventory.getItem(13)?.itemMeta?.displayName,
            )
        }

        @Test
        fun `should apply template with click handlers`() {
            var clicked = false
            val template =
                object : InventoryTemplate {
                    override fun apply(builder: InventoryBuilder) {
                        builder.setItem(0, ItemStack(Material.DIAMOND)) { clicked = true }
                    }
                }

            builder.applyTemplate(template)
            val handler = builder.getClickHandler(0)
            assertNotNull(handler)

            handler?.invoke(
                GuiTestUtil.createClickEvent(
                    createPlayer(),
                    builder.build(),
                    0,
                    ItemStack(Material.DIAMOND),
                ),
            )

            assertTrue(clicked)
        }
    }
}
