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
import org.junit.jupiter.params.provider.ValueSource

class ButtonTemplateTest : BaseTest() {
    private lateinit var builder: InventoryBuilder

    @BeforeEach
    fun setup() {
        builder = InventoryBuilder.create("Test Inventory", InventorySize.THREE_ROWS)
    }

    @Nested
    inner class ButtonItemsTests {
        @Test
        fun `should create button with correct properties`() {
            // When
            val button = ButtonItems.createButton(
                Material.STONE_BUTTON,
                "Test Button",
                "Line 1",
                "Line 2"
            )

            // Then
            assertEquals(Material.STONE_BUTTON, button.type)
            assertEquals("Test Button", button.itemMeta?.displayName)
            assertEquals(listOf("Line 1", "Line 2"), button.itemMeta?.lore)
        }

        @Test
        fun `should create toggle button for enabled state`() {
            // When
            val enabledToggle = ButtonItems.createToggleButton(
                enabled = true,
                enabledName = "Enabled",
                disabledName = "Disabled",
                enabledLore = listOf("Click to disable"),
                disabledLore = listOf("Click to enable")
            )

            // Then
            assertEquals(Material.LIME_STAINED_GLASS_PANE, enabledToggle.type)
            assertEquals("Enabled", enabledToggle.itemMeta?.displayName)
            assertEquals(listOf("Click to disable"), enabledToggle.itemMeta?.lore)
        }

        @Test
        fun `should create toggle button for disabled state`() {
            // When
            val disabledToggle = ButtonItems.createToggleButton(
                enabled = false,
                enabledName = "Enabled",
                disabledName = "Disabled",
                enabledLore = listOf("Click to disable"),
                disabledLore = listOf("Click to enable")
            )

            // Then
            assertEquals(Material.RED_STAINED_GLASS_PANE, disabledToggle.type)
            assertEquals("Disabled", disabledToggle.itemMeta?.displayName)
            assertEquals(listOf("Click to enable"), disabledToggle.itemMeta?.lore)
        }

        @Test
        fun `should create confirmation button with additional lore`() {
            // When
            val confirmButton = ButtonItems.createConfirmationButton(
                Material.DIAMOND,
                "Confirm Action",
                "This action cannot be undone"
            )

            // Then
            assertEquals(Material.DIAMOND, confirmButton.type)
            assertEquals("Confirm Action", confirmButton.itemMeta?.displayName)
            assertEquals(
                listOf("This action cannot be undone", "§eClick twice to confirm"),
                confirmButton.itemMeta?.lore
            )
        }
    }

    @Nested
    inner class ButtonTemplateTests {
        @Test
        fun `should apply button template correctly`() {
            // Given
            val buttonItem = GuiTestUtil.createTestItem(Material.STONE_BUTTON, "Test Button")
            var clicked = false
            val template = ButtonTemplate(10, buttonItem) { clicked = true }

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            assertEquals(buttonItem.type, inventory.getItem(10)?.type)
            assertEquals(buttonItem.itemMeta?.displayName, inventory.getItem(10)?.itemMeta?.displayName)

            // Test click handler
            val handler = builder.getClickHandler(10)
            assertNotNull(handler)
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, buttonItem))
            assertTrue(clicked)
        }
    }

    @Nested
    inner class ToggleButtonTemplateTests {
        @Test
        fun `should start in disabled state by default`() {
            // Given
            val enabledItem = GuiTestUtil.createTestItem(Material.LIME_STAINED_GLASS_PANE, "Enabled")
            val disabledItem = GuiTestUtil.createTestItem(Material.RED_STAINED_GLASS_PANE, "Disabled")
            val template = ToggleButtonTemplate(10, enabledItem, disabledItem)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            assertEquals(disabledItem.type, inventory.getItem(10)?.type)
            assertEquals(false, template.isEnabled())
        }

        @Test
        fun `should start in enabled state when specified`() {
            // Given
            val enabledItem = GuiTestUtil.createTestItem(Material.LIME_STAINED_GLASS_PANE, "Enabled")
            val disabledItem = GuiTestUtil.createTestItem(Material.RED_STAINED_GLASS_PANE, "Disabled")
            val template = ToggleButtonTemplate(10, enabledItem, disabledItem, initialState = true)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            assertEquals(enabledItem.type, inventory.getItem(10)?.type)
            assertEquals(true, template.isEnabled())
        }

        @Test
        fun `should toggle state when clicked`() {
            // Given
            val enabledItem = GuiTestUtil.createTestItem(Material.LIME_STAINED_GLASS_PANE, "Enabled")
            val disabledItem = GuiTestUtil.createTestItem(Material.RED_STAINED_GLASS_PANE, "Disabled")
            var toggleCallback = false
            val template = ToggleButtonTemplate(10, enabledItem, disabledItem) { enabled ->
                toggleCallback = enabled
            }

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()
            val handler = builder.getClickHandler(10)
            assertNotNull(handler)

            // First click - should enable
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, disabledItem))
            assertEquals(true, template.isEnabled())
            assertEquals(true, toggleCallback)

            // Second click - should disable
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, enabledItem))
            assertEquals(false, template.isEnabled())
            assertEquals(false, toggleCallback)
        }

        @Test
        fun `should allow programmatic state change`() {
            // Given
            val enabledItem = GuiTestUtil.createTestItem(Material.LIME_STAINED_GLASS_PANE, "Enabled")
            val disabledItem = GuiTestUtil.createTestItem(Material.RED_STAINED_GLASS_PANE, "Disabled")
            val template = ToggleButtonTemplate(10, enabledItem, disabledItem)

            // When
            template.setEnabled(true)

            // Then
            assertEquals(true, template.isEnabled())
        }
    }

    @Nested
    inner class MultiStateButtonTemplateTests {
        @Test
        fun `should start with initial state`() {
            // Given
            val state1 = MultiStateButtonTemplate.ButtonState(
                GuiTestUtil.createTestItem(Material.RED_STAINED_GLASS_PANE, "State 1")
            )
            val state2 = MultiStateButtonTemplate.ButtonState(
                GuiTestUtil.createTestItem(Material.GREEN_STAINED_GLASS_PANE, "State 2")
            )
            val template = MultiStateButtonTemplate(10, listOf(state1, state2), initialState = 1)

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            assertEquals(state2.item.type, inventory.getItem(10)?.type)
            assertEquals(1, template.getCurrentStateIndex())
        }

        @Test
        fun `should cycle through states when clicked`() {
            // Given
            val state1 = MultiStateButtonTemplate.ButtonState(
                GuiTestUtil.createTestItem(Material.RED_STAINED_GLASS_PANE, "State 1")
            )
            val state2 = MultiStateButtonTemplate.ButtonState(
                GuiTestUtil.createTestItem(Material.GREEN_STAINED_GLASS_PANE, "State 2")
            )
            val state3 = MultiStateButtonTemplate.ButtonState(
                GuiTestUtil.createTestItem(Material.BLUE_STAINED_GLASS_PANE, "State 3")
            )
            val template = MultiStateButtonTemplate(10, listOf(state1, state2, state3))

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()
            val handler = builder.getClickHandler(10)
            assertNotNull(handler)

            // First click - should go to state 2
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, state1.item))
            assertEquals(1, template.getCurrentStateIndex())

            // Second click - should go to state 3
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, state2.item))
            assertEquals(2, template.getCurrentStateIndex())

            // Third click - should cycle back to state 1
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, state3.item))
            assertEquals(0, template.getCurrentStateIndex())
        }

        @Test
        fun `should call state-specific click handlers`() {
            // Given
            var state1Clicked = false
            val state1 = MultiStateButtonTemplate.ButtonState(
                GuiTestUtil.createTestItem(Material.RED_STAINED_GLASS_PANE, "State 1")
            ) { state1Clicked = true }
            val state2 = MultiStateButtonTemplate.ButtonState(
                GuiTestUtil.createTestItem(Material.GREEN_STAINED_GLASS_PANE, "State 2")
            )
            val template = MultiStateButtonTemplate(10, listOf(state1, state2))

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()
            val handler = builder.getClickHandler(10)
            assertNotNull(handler)

            // Click state 1 - should call state1 handler and switch to state 2
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, state1.item))
            assertTrue(state1Clicked)
            assertEquals(1, template.getCurrentStateIndex()) // Should now be in state 2
        }

        @Test
        fun `should allow programmatic state change`() {
            // Given
            val state1 = MultiStateButtonTemplate.ButtonState(
                GuiTestUtil.createTestItem(Material.RED_STAINED_GLASS_PANE, "State 1")
            )
            val state2 = MultiStateButtonTemplate.ButtonState(
                GuiTestUtil.createTestItem(Material.GREEN_STAINED_GLASS_PANE, "State 2")
            )
            val template = MultiStateButtonTemplate(10, listOf(state1, state2))

            // When
            template.setState(1)

            // Then
            assertEquals(1, template.getCurrentStateIndex())
        }

        @Test
        fun `should ignore invalid state index`() {
            // Given
            val state1 = MultiStateButtonTemplate.ButtonState(
                GuiTestUtil.createTestItem(Material.RED_STAINED_GLASS_PANE, "State 1")
            )
            val template = MultiStateButtonTemplate(10, listOf(state1))

            // When
            template.setState(5) // Invalid index

            // Then
            assertEquals(0, template.getCurrentStateIndex()) // Should remain unchanged
        }
    }

    @Nested
    inner class ConfirmationButtonTemplateTests {
        @Test
        fun `should require double click for confirmation`() {
            // Given
            val buttonItem = GuiTestUtil.createTestItem(Material.DIAMOND, "Confirm Action")
            var confirmed = false
            val template = ConfirmationButtonTemplate(10, buttonItem, { confirmed = true })

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()
            val handler = builder.getClickHandler(10)
            assertNotNull(handler)

            // First click - should not confirm
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, buttonItem))
            assertEquals(false, confirmed)

            // Check that button shows confirmation state
            val confirmItem = inventory.getItem(10)
            assertTrue(confirmItem?.itemMeta?.displayName?.contains("(Click to confirm)") == true)

            // Second click - should confirm
            handler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 10, confirmItem!!))
            assertEquals(true, confirmed)
        }
    }

    @Nested
    inner class NavigationBarTemplateTests {
        @Test
        fun `should create navigation buttons`() {
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
            assertEquals("test", navigationId)
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
            assertTrue(disabledItem?.itemMeta?.displayName?.startsWith("§7") == true)
        }
    }

    @Nested
    inner class ButtonGridTemplateTests {
        @Test
        fun `should place buttons in grid layout`() {
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
        fun `should handle multiple buttons in grid`() {
            // Given
            val button1 = ButtonGridTemplate.GridButton(
                item = GuiTestUtil.createTestItem(Material.RED_STAINED_GLASS_PANE, "Button 1"),
                onClick = { }
            )
            val button2 = ButtonGridTemplate.GridButton(
                item = GuiTestUtil.createTestItem(Material.GREEN_STAINED_GLASS_PANE, "Button 2"),
                onClick = { }
            )
            val template = ButtonGridTemplate(10, 2, listOf(button1, button2))

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then
            assertEquals(button1.item.type, inventory.getItem(10)?.type) // First button at start slot
            assertEquals(button2.item.type, inventory.getItem(11)?.type) // Second button at start slot + 1
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

            // Then - should be placed in bottom row (slots 18-26)
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

            // Then - should be placed in top row (slots 0-8)
            val topRowSlots = (0..8).toList()
            val hasActionInTopRow = topRowSlots.any { inventory.getItem(it)?.type == Material.STONE_BUTTON }
            assertTrue(hasActionInTopRow)
        }
    }

    @Nested
    inner class PaginationBarTemplateTests {
        @Test
        fun `should create pagination controls`() {
            // Given
            var currentPage = 0
            val template = PaginationBarTemplate(0, 5, { page ->
                currentPage = page
            })

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()

            // Then - should have previous, current, and next buttons in bottom row (slots 21, 22, 23)
            val hasPreviousButton = inventory.getItem(21)?.type == Material.BARRIER // First page, so disabled
            val hasPageIndicator = inventory.getItem(22)?.type == Material.PAPER
            val hasNextButton = inventory.getItem(23)?.type == Material.ARROW

            assertTrue(hasPreviousButton)
            assertTrue(hasPageIndicator)
            assertTrue(hasNextButton)
        }

        @Test
        fun `should handle page navigation`() {
            // Given
            var currentPage = 0
            val template = PaginationBarTemplate(0, 3, { page ->
                currentPage = page
            })

            // When
            builder.applyTemplate(template)
            val inventory = builder.build()
            val nextHandler = builder.getClickHandler(23) // Next button in bottom row
            assertNotNull(nextHandler)

            // Click next button
            nextHandler?.invoke(GuiTestUtil.createClickEvent(createPlayer(), inventory, 23, ItemStack(Material.ARROW)))

            // Then
            assertEquals(1, currentPage)
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
    }
} 