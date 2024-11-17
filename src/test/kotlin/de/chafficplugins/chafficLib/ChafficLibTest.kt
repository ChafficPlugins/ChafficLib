package de.chafficplugins.chafficLib

import de.chafficplugins.chafficLib.test.BaseTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ChafficLibTest : BaseTest() {
    @Test
    fun `plugin should be enabled`() {
        assertTrue(plugin.isEnabled, "Plugin should be enabled after loading")
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
    }
}
