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
    }

    override fun onEnable() {
        instance = this
        logger.info("ChafficLib has been enabled!")
    }

    override fun onDisable() {
        logger.info("ChafficLib has been disabled!")
    }
}
