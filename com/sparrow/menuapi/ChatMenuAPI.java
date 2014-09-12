package com.sparrow.menuapi;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Main ChatMenuAPI access point. All library functionality is
 * accessible from an instance of this class plus implementation
 * of ChatMenu.
 *
 */
public class ChatMenuAPI {


	private static ChatMenuAPI instance;
	private ChatAdapter chatAdapter;
	private JavaPlugin hostPlugin;
	private static boolean initialized = false;

	//// ALL ELEMENTS OF THE PUBLIC API HERE ////
	/**
	 * Displays a menu to the player specified.
	 * @param menu The menu to display.
	 */
	public void displayMenu(ChatMenu menu) {
		if(!initialized) {
			Bukkit.getLogger().log(Level.SEVERE, "ChatMenuAPI needs to be initialized in your onEnable() method before"
					+ " it can be used! Your menu has not been displayed.");
			return;
		}
		ChatUtil.setMenu(menu);
		menu.showMenu(new ArrayList<String>());
	}

	/**
	 * Gets the current menu the player is using.
	 * @param p The player.
	 * @return the chatMenu the player is using, or null if they aren't using a menu
	 */
	public ChatMenu getMenu(Player p) {
		return ChatUtil.getMenu(p);
	}

	/**
	 * @param p The Player.
	 * @return If the player has a currently visible menu.
	 */
	public boolean isInMenu(Player p) {
		return getMenu(p)!=null;
	}

	/**
	 * Closes the menu for a player, clearing the menu from chat and
	 * sending the player any missed chat they received.
	 * @param p The player whose menu should be closed.
	 */
	public void exitMenu(Player p) {
		ChatUtil.exitMenu(p);
	}

	/**
	 * Initializes the chatmenu api. Also verifies that ProtocolLib exists.
	 * @param hostPlugin The plugin to parent this library to, usually yours
	 */
	public void initalize(JavaPlugin hostPlugin) {
		if(chatAdapter == null) {
			if(Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
				Bukkit.getLogger().log(Level.SEVERE, "ChatMenuAPI is being used, but ProtocolLib hasn't been installed!"
						+ " Make sure your plugin.yml has 'depend: [ProtocolLib]' and that ProtocolLib is in this server's"
						+ " plugin folder and restart your server.");
				return;
			}
			this.hostPlugin = hostPlugin;
			chatAdapter = new ChatAdapter(hostPlugin);
			Bukkit.getLogger().log(Level.INFO, "ChatMenuAPI has been initialized.");
			initialized = true;
		}
	}

	/**
	 * @return The offical (and only) instance of the api.
	 */
	public static ChatMenuAPI getInstance() {
		if(instance == null) {
			instance =  new ChatMenuAPI();
		}
		return instance;
	}

	//////PUBLIC API END///////////////////////

	
	protected JavaPlugin getHostPlugin() {
		return hostPlugin;
	}
	
}
