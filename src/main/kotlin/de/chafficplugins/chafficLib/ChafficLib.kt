package de.chafficplugins.chafficLib

import org.bukkit.plugin.java.JavaPlugin

/**
 * Main plugin class for ChafficLib.
 * Must be open to allow MockBukkit to create test instances.
 */
open class ChafficLib : JavaPlugin() {
    companion object {
        private lateinit var instance: ChafficLib

        @JvmStatic
        fun getInstance(): ChafficLib = instance

        /**
         * Get the plugin version
         * @return String containing the plugin version
         */
        @JvmStatic
        fun getVersion(): String = instance.description.version

        /**
         * Checks if debug mode is enabled
         * @return true if debug mode is enabled
         */
        @JvmStatic
        fun isDebugEnabled(): Boolean = instance.config.getBoolean("debug", false)
    }

    private var initialized: Boolean = false

    override fun onEnable() {
        instance = this
        loadConfig()
        initialized = true
        logger.info("ChafficLib has been enabled!")
    }

    override fun onDisable() {
        logger.info("ChafficLib has been disabled!")
    }

    /**
     * Loads or reloads the configuration
     * @return true if config was loaded successfully
     */
    fun loadConfig() {
        saveDefaultConfig()
        reloadConfig()
    }

    /**
     * Checks if the plugin has been fully initialized
     * @return true if the plugin is initialized
     */
    fun isInitialized(): Boolean = initialized
}
