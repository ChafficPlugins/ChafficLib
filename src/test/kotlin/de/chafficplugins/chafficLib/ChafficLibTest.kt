package de.chafficplugins.chafficLib

import de.chafficplugins.chafficLib.test.BaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChafficLibTest : BaseTest() {
    @Test
    fun `plugin should be enabled`() {
        assertTrue(plugin.isEnabled, "Plugin should be enabled after loading")
    }

    @Test
    fun `plugin should load properly`() {
        assertNotNull(server.pluginManager.getPlugin("ChafficLib"))
        assertTrue(plugin.isEnabled)
    }

    @Test
    fun `plugin should be initialized after enable`() {
        assertTrue(plugin.isInitialized(), "Plugin should be initialized after enable")
    }

    @Test
    fun `getInstance should return plugin instance`() {
        val instance = ChafficLib.getInstance()
        assertNotNull(instance, "Plugin instance should not be null")
        assertEquals(plugin, instance, "getInstance should return the current plugin instance")
    }

    @Test
    fun `getVersion should return correct version`() {
        val version = ChafficLib.getVersion()
        assertEquals(plugin.description.version, version, "Version should match plugin description")
    }

    @Test
    fun `debug mode should be disabled by default`() {
        assertFalse(ChafficLib.isDebugEnabled(), "Debug mode should be disabled by default")
    }

    @Test
    fun `configuration should load successfully`() {
        plugin.loadConfig()
        assertNotNull(plugin.config, "Config should not be null after loading")
    }

    @Test
    fun `plugin should handle disable lifecycle`() {
        assertTrue(plugin.isEnabled)
        MockBukkit.unmock()

        // Note: We don't test isEnabled here because MockBukkit has already cleaned up
        // Instead we verify that we can get a new instance after re-loading
        server = MockBukkit.mock()
        plugin = MockBukkit.load(ChafficLib::class.java)
        assertTrue(plugin.isEnabled)
    }

    @Nested
    inner class ServerTests {
        @Test
        fun `server should be running`() {
            assertTrue(server.isOnMainThread, "Server should be running on main thread")
        }

        @Test
        fun `server should process ticks`() {
            val initialTicks = server.currentTick
            simulateTicks(5)
            assertTrue(server.currentTick > initialTicks, "Server should process ticks")
        }

        @Test
        fun `plugin should be in server plugin list`() {
            assertTrue(server.pluginManager.plugins.contains(plugin))
        }
    }
}
