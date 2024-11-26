package de.chafficplugins.chafficLib.test

import de.chafficplugins.chafficLib.ChafficLib
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Base test class providing common test utilities and setup/teardown functionality.
 * All test classes should extend this class to ensure consistent test environment.
 */
abstract class BaseTest {
    protected lateinit var server: ServerMock
    protected lateinit var plugin: ChafficLib

    @BeforeEach
    fun setUp() {
        // Ensure clean state
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock()
        }

        // Mock server with debug logging
        server =
            MockBukkit.mock().apply {
                logger.level = Level.ALL
            }

        try {
            // Load plugin
            plugin = MockBukkit.load(ChafficLib::class.java)

            // Ensure plugin is enabled
            if (!plugin.isEnabled) {
                throw IllegalStateException("Plugin failed to enable!")
            }
        } catch (e: Exception) {
            Logger.getLogger(this::class.java.name).log(Level.SEVERE, "Failed to load plugin", e)
            throw e
        }
    }

    @AfterEach
    fun tearDown() {
        // Ensure cleanup
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock()
        }
    }

    /**
     * Utility method to simulate server ticks
     * @param ticks Number of ticks to simulate
     */
    protected fun simulateTicks(ticks: Long) {
        server.scheduler.performTicks(ticks)
    }

    /**
     * Utility method to advance one tick and run scheduled tasks
     */
    protected fun tick() {
        server.scheduler.performTicks(1)
    }

    /**
     * Creates a test player
     * @param name Player name
     * @return The created player mock
     */
    protected fun createPlayer(name: String = "TestPlayer") = server.addPlayer(name)
}
