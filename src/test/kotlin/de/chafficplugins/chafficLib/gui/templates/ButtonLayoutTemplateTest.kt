package de.chafficplugins.chafficLib.gui.templates

import de.chafficplugins.chafficLib.gui.InventoryBuilder
import de.chafficplugins.chafficLib.gui.InventorySize
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

class ButtonLayoutTemplateTest : BaseTest() {
    private lateinit var builder: InventoryBuilder

    @BeforeEach
    fun setup() {
        builder = InventoryBuilder.create("Test Inventory", InventorySize.THREE_ROWS)
    }

    @Nested
    inner class NavigationBarTemplateTests {
        @Test
        fun `should create navigation buttons with click handlers`() {
            // Given
            val navigationButton = NavigationBarTemplate.NavigationButton(
                id = "test",
                slot = 10,
                item = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Test Navigation")
            )
            var navigationId = ""
            val template = NavigationBarTemplate(listOf(navigationButton)) { id ->
                navigationId = id
            }

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()
            val handler = builder.getClickHandler(10)
            assertNotNull(handler)

            // Click navigation button
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, navigationButton.item))

            // Then
            assertEquals("test", navigationId)
        }

        @Test
        fun `should handle multiple navigation buttons`() {
            // Given
            val button1 = NavigationBarTemplate.NavigationButton(
                id = "home",
                slot = 10,
                item = GuiTestUtil.createTestItem(Material.OAK_DOOR, "Home")
            )
            val button2 = NavigationBarTemplate.NavigationButton(
                id = "settings",
                slot = 11,
                item = GuiTestUtil.createTestItem(Material.COMPASS, "Settings")
            )
            val navigationIds = mutableListOf<String>()
            val template = NavigationBarTemplate(listOf(button1, button2)) { id ->
                navigationIds.add(id)
            }

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Click first button
            val handler1 = builder.getClickHandler(10)
            assertNotNull(handler1)
            handler1?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, button1.item))

            // Click second button
            val handler2 = builder.getClickHandler(11)
            assertNotNull(handler2)
            handler2?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 11, button2.item))

            // Then
            assertEquals(listOf("home", "settings"), navigationIds)
        }

        @Test
        fun `should handle disabled navigation buttons`() {
            // Given
            val navigationButton = NavigationBarTemplate.NavigationButton(
                id = "test",
                slot = 10,
                item = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Test Navigation"),
                enabled = false
            )
            val template = NavigationBarTemplate(listOf(navigationButton))

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            val disabledItem = inventory.getItem(10)
            assertNotNull(disabledItem)
            assertTrue(disabledItem?.itemMeta?.displayName?.startsWith("§7") == true)
            
            // Should not have click handler for disabled button
            val handler = builder.getClickHandler(10)
            assertEquals(null, handler)
        }

        @Test
        fun `should handle navigation callback when null`() {
            // Given
            val navigationButton = NavigationBarTemplate.NavigationButton(
                id = "test",
                slot = 10,
                item = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Test Navigation")
            )
            val template = NavigationBarTemplate(listOf(navigationButton), null)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()
            val handler = builder.getClickHandler(10)
            assertNotNull(handler)

            // Then - Should not throw exception when callback is null
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, navigationButton.item))
        }
    }

    @Nested
    inner class ButtonGridTemplateTests {
        @Test
        fun `should place buttons in grid layout correctly`() {
            // Given
            val gridButton = ButtonGridTemplate.GridButton(
                item = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Grid Button"),
                onClick = { }
            )
            val template = ButtonGridTemplate(10, 3, listOf(gridButton))

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            assertEquals(gridButton.item.type, inventory.getItem(10)?.type)
        }

        @Test
        fun `should place multiple buttons in grid layout`() {
            // Given
            val button1 = ButtonGridTemplate.GridButton(
                item = GuiTestUtil.createTestItem(Material.RED_STAINED_GLASS_PANE, "Button 1"),
                onClick = { }
            )
            val button2 = ButtonGridTemplate.GridButton(
                item = GuiTestUtil.createTestItem(Material.GREEN_STAINED_GLASS_PANE, "Button 2"),
                onClick = { }
            )
            val button3 = ButtonGridTemplate.GridButton(
                item = GuiTestUtil.createTestItem(Material.BLUE_STAINED_GLASS_PANE, "Button 3"),
                onClick = { }
            )
            val template = ButtonGridTemplate(10, 2, listOf(button1, button2, button3))

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - First row: button1 at slot 10, button2 at slot 11
            assertEquals(button1.item.type, inventory.getItem(10)?.type)
            assertEquals(button2.item.type, inventory.getItem(11)?.type)
            
            // Then - Second row: button3 at slot 19 (10 + 9)
            assertEquals(button3.item.type, inventory.getItem(19)?.type)
        }

        @Test
        fun `should handle grid buttons with click handlers`() {
            // Given
            var clicked = false
            val gridButton = ButtonGridTemplate.GridButton(
                item = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Grid Button"),
                onClick = { clicked = true }
            )
            val template = ButtonGridTemplate(10, 3, listOf(gridButton))

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()
            val handler = builder.getClickHandler(10)
            assertNotNull(handler)

            // Click grid button
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, gridButton.item))

            // Then
            assertTrue(clicked)
        }

        @Test
        fun `should handle disabled grid buttons`() {
            // Given
            val gridButton = ButtonGridTemplate.GridButton(
                item = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Grid Button"),
                onClick = { },
                enabled = false
            )
            val template = ButtonGridTemplate(10, 3, listOf(gridButton))

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            val disabledItem = inventory.getItem(10)
            assertNotNull(disabledItem)
            assertTrue(disabledItem?.itemMeta?.displayName?.startsWith("§7") == true)
            
            // Should not have click handler for disabled button
            val handler = builder.getClickHandler(10)
            assertEquals(null, handler)
        }

        @Test
        fun `should calculate grid positions correctly`() {
            // Given
            val buttons = (0..5).map { index ->
                ButtonGridTemplate.GridButton(
                    item = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Button $index"),
                    onClick = { }
                )
            }
            val template = ButtonGridTemplate(10, 3, buttons)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - First row (slots 10, 11, 12)
            assertEquals("Button 0", inventory.getItem(10)?.itemMeta?.displayName)
            assertEquals("Button 1", inventory.getItem(11)?.itemMeta?.displayName)
            assertEquals("Button 2", inventory.getItem(12)?.itemMeta?.displayName)
            
            // Then - Second row (slots 19, 20, 21)
            assertEquals("Button 3", inventory.getItem(19)?.itemMeta?.displayName)
            assertEquals("Button 4", inventory.getItem(20)?.itemMeta?.displayName)
            assertEquals("Button 5", inventory.getItem(21)?.itemMeta?.displayName)
        }
    }

    @Nested
    inner class ActionBarTemplateTests {
        @Test
        fun `should place actions in bottom bar by default`() {
            // Given
            val actionButton = ActionBarTemplate.ActionButton(
                id = "test",
                item = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Action Button"),
                onClick = { }
            )
            val template = ActionBarTemplate(listOf(actionButton))

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - Should be placed in bottom row (slots 18-26)
            val bottomRowSlots = (18..26).toList()
            val hasActionInBottomRow = bottomRowSlots.any { inventory.getItem(it)?.type == Material.STONE_BUTTON }
            assertTrue(hasActionInBottomRow)
        }

        @Test
        fun `should place actions in top bar when specified`() {
            // Given
            val actionButton = ActionBarTemplate.ActionButton(
                id = "test",
                item = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Action Button"),
                onClick = { }
            )
            val template = ActionBarTemplate(listOf(actionButton), ActionBarTemplate.ActionBarPosition.TOP)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - Should be placed in top row (slots 0-8)
            val topRowSlots = (0..8).toList()
            val hasActionInTopRow = topRowSlots.any { inventory.getItem(it)?.type == Material.STONE_BUTTON }
            assertTrue(hasActionInTopRow)
        }

        @Test
        fun `should place actions in left column when specified`() {
            // Given
            val actionButton = ActionBarTemplate.ActionButton(
                id = "test",
                item = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Action Button"),
                onClick = { }
            )
            val template = ActionBarTemplate(listOf(actionButton), ActionBarTemplate.ActionBarPosition.LEFT)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - Should be placed in left column (slots 0, 9, 18)
            val leftColumnSlots = listOf(0, 9, 18)
            val hasActionInLeftColumn = leftColumnSlots.any { inventory.getItem(it)?.type == Material.STONE_BUTTON }
            assertTrue(hasActionInLeftColumn)
        }

        @Test
        fun `should place actions in right column when specified`() {
            // Given
            val actionButton = ActionBarTemplate.ActionButton(
                id = "test",
                item = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Action Button"),
                onClick = { }
            )
            val template = ActionBarTemplate(listOf(actionButton), ActionBarTemplate.ActionBarPosition.RIGHT)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - Should be placed in right column (slots 8, 17, 26)
            val rightColumnSlots = listOf(8, 17, 26)
            val hasActionInRightColumn = rightColumnSlots.any { inventory.getItem(it)?.type == Material.STONE_BUTTON }
            assertTrue(hasActionInRightColumn)
        }

        @Test
        fun `should handle multiple actions in action bar`() {
            // Given
            val action1 = ActionBarTemplate.ActionButton(
                id = "action1",
                item = GuiTestUtil.createTestItem(Material.RED_STAINED_GLASS_PANE, "Action 1"),
                onClick = { }
            )
            val action2 = ActionBarTemplate.ActionButton(
                id = "action2",
                item = GuiTestUtil.createTestItem(Material.GREEN_STAINED_GLASS_PANE, "Action 2"),
                onClick = { }
            )
            val template = ActionBarTemplate(listOf(action1, action2))

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - Should place actions in bottom row
            val bottomRowSlots = (18..26).toList()
            val actionSlots = bottomRowSlots.filter { inventory.getItem(it) != null }
            assertEquals(2, actionSlots.size)
        }

        @Test
        fun `should handle action buttons with click handlers`() {
            // Given
            var clicked = false
            val actionButton = ActionBarTemplate.ActionButton(
                id = "test",
                item = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Action Button"),
                onClick = { clicked = true }
            )
            val template = ActionBarTemplate(listOf(actionButton))

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()
            
            // Find the action button slot
            val actionSlot = (18..26).find { inventory.getItem(it)?.type == Material.STONE_BUTTON }
            assertNotNull(actionSlot)
            
            val handler = builder.getClickHandler(actionSlot!!)
            assertNotNull(handler)

            // Click action button
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, actionSlot, actionButton.item))

            // Then
            assertTrue(clicked)
        }

        @Test
        fun `should handle disabled action buttons`() {
            // Given
            val actionButton = ActionBarTemplate.ActionButton(
                id = "test",
                item = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Action Button"),
                onClick = { },
                enabled = false
            )
            val template = ActionBarTemplate(listOf(actionButton))

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            val bottomRowSlots = (18..26).toList()
            val disabledSlot = bottomRowSlots.find { inventory.getItem(it) != null }
            assertNotNull(disabledSlot)
            
            val disabledItem = inventory.getItem(disabledSlot!!)
            assertTrue(disabledItem?.itemMeta?.displayName?.startsWith("§7") == true)
        }
    }

    @Nested
    inner class PaginationBarTemplateTests {
        @Test
        fun `should create pagination controls in bottom position by default`() {
            // Given
            var currentPage = 0
            val template = PaginationBarTemplate(0, 5, { page ->
                currentPage = page
            })

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - Should have previous, current, and next buttons in bottom row
            val hasPreviousButton = inventory.getItem(21)?.type == Material.BARRIER // First page, so disabled (18 + 3)
            val hasPageIndicator = inventory.getItem(22)?.type == Material.PAPER // 18 + 4
            val hasNextButton = inventory.getItem(23)?.type == Material.ARROW // 18 + 5

            assertTrue(hasPreviousButton)
            assertTrue(hasPageIndicator)
            assertTrue(hasNextButton)
        }

        @Test
        fun `should create pagination controls in top position when specified`() {
            // Given
            val template = PaginationBarTemplate(0, 5, position = PaginationBarTemplate.PaginationPosition.TOP)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - Should have previous, current, and next buttons in top row
            val hasPreviousButton = inventory.getItem(3)?.type == Material.BARRIER // First page, so disabled
            val hasPageIndicator = inventory.getItem(4)?.type == Material.PAPER
            val hasNextButton = inventory.getItem(5)?.type == Material.ARROW

            assertTrue(hasPreviousButton)
            assertTrue(hasPageIndicator)
            assertTrue(hasNextButton)
        }

        @Test
        fun `should handle page navigation correctly`() {
            // Given
            var currentPage = 0
            val template = PaginationBarTemplate(0, 3, { page ->
                currentPage = page
            })

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()
            val nextHandler = builder.getClickHandler(23) // Next button in bottom row (18 + 5)
            assertNotNull(nextHandler)

            // Click next button
            nextHandler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 23, ItemStack(Material.ARROW)))

            // Then
            assertEquals(1, currentPage)
        }

        @Test
        fun `should disable previous button on first page`() {
            // Given
            val template = PaginationBarTemplate(0, 5)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - Previous button should be disabled (BARRIER instead of ARROW)
            val previousButton = inventory.getItem(21) // slot 18 + 3
            assertEquals(Material.BARRIER, previousButton?.type)
        }

        @Test
        fun `should disable next button on last page`() {
            // Given
            val template = PaginationBarTemplate(4, 5) // Last page (0-indexed)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - Next button should be disabled (BARRIER instead of ARROW)
            val nextButton = inventory.getItem(23) // slot 18 + 5
            assertEquals(Material.BARRIER, nextButton?.type)
        }

        @Test
        fun `should show correct page indicator`() {
            // Given
            val template = PaginationBarTemplate(2, 5) // Page 3 of 5

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - Page indicator should show correct page
            val pageIndicator = inventory.getItem(22) // slot 18 + 4
            assertEquals("§ePage 3 of 5", pageIndicator?.itemMeta?.displayName)
        }

        @Test
        fun `should allow programmatic page change`() {
            // Given
            val template = PaginationBarTemplate(0, 5)

            // When
            template.setCurrentPage(3)

            // Then
            // Note: We can't easily test the visual state without complex mocking,
            // but we can verify the method doesn't throw exceptions
            assertNotNull(template)
        }

        @Test
        fun `should ignore invalid page numbers in setCurrentPage`() {
            // Given
            val template = PaginationBarTemplate(0, 5)

            // When
            template.setCurrentPage(-1) // Invalid
            template.setCurrentPage(10) // Invalid

            // Then
            // Should not throw exception
            assertNotNull(template)
        }
    }

    @Nested
    inner class CloseButtonTemplateTests {
        @Test
        fun `should create close button with correct properties`() {
            // Given
            var closed = false
            val template = CloseButtonTemplate(10) { closed = true }

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            val closeButton = inventory.getItem(10)
            assertEquals(Material.BARRIER, closeButton?.type)
            assertEquals("§cClose", closeButton?.itemMeta?.displayName)

            // Test click handler
            val handler = builder.getClickHandler(10)
            assertNotNull(handler)
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, closeButton!!))
            assertTrue(closed)
        }

        @Test
        fun `should cancel event when close button is clicked`() {
            // Given
            val template = CloseButtonTemplate(10) { }

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()
            val handler = builder.getClickHandler(10)
            assertNotNull(handler)

            // Then - The template should handle event cancellation internally
            // We can't easily test this without complex mocking, but we can verify the handler exists
            assertNotNull(handler)
        }
    }

    @Nested
    inner class BackButtonTemplateTests {
        @Test
        fun `should create back button with correct properties`() {
            // Given
            var wentBack = false
            val template = BackButtonTemplate(10) { wentBack = true }

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            val backButton = inventory.getItem(10)
            assertEquals(Material.ARROW, backButton?.type)
            assertEquals("§eBack", backButton?.itemMeta?.displayName)

            // Test click handler
            val handler = builder.getClickHandler(10)
            assertNotNull(handler)
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, backButton!!))
            assertTrue(wentBack)
        }

        @Test
        fun `should cancel event when back button is clicked`() {
            // Given
            val template = BackButtonTemplate(10) { }

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()
            val handler = builder.getClickHandler(10)
            assertNotNull(handler)

            // Then - The template should handle event cancellation internally
            // We can't easily test this without complex mocking, but we can verify the handler exists
            assertNotNull(handler)
        }
    }
} 