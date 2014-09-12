package com.sparrow.menuapi;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class ChatUtil {
	
	private static final String META_MENU = "chatmenuapi.menu";
	private static final String META_MISSED_CHAT = "chatmenuapi.missed_chat";
	
	/**
	 * The magic string. This string will never be seen in chat since it is removed by this library.
	 * It allows messages to be sent to a player inside a menu.
	 */
	protected static final String HIDE_CHAT_BYPASS = "[cmapi™]";
	
	protected static void setMenu(ChatMenu menu) {
		setMetadata(menu.getOwner(), META_MENU, menu);
	}
	
	protected static ChatMenu getMenu(Player p) {
		return (ChatMenu) getMetadata(p, META_MENU, null);
	}
	
	protected static void exitMenu(Player p) {
		setMetadata(p, ChatUtil.META_MENU, null);
		for(String s : ChatUtil.getMissedChat(p)) {
			p.sendMessage(s);
		}
	}
	
	protected static void addLineToMissedChat(Player p, String line) {
		getMissedChat(p).add(line);
	}
	
	@SuppressWarnings("unchecked")
	protected static ArrayList<String> getMissedChat(Player p) {
		return (ArrayList<String>) getMetadata(p, META_MISSED_CHAT, new ArrayList<String>());
	}

	protected static void clearChat(Player p) {
		for(int i = 0; i < 35; i++) {
			sendLine(p, "                           ");
		}
	}
	
	protected static void sendCommandLine(Player p, String cmd, String text, String addlText) {
		text = ChatColor.DARK_AQUA + text + ChatColor.RESET;
		//TODO remove raw json, or format it
		tellRaw(p, "{text:" +
		"\"" + HIDE_CHAT_BYPASS + text + "\"," +
		"clickEvent:{action:run_command,value:\"" + cmd + "\"}," +
		"extra:[{text:\" " + addlText + "\"," +
		"clickEvent:{action:suggest_command,value:\"\"}}]}");

	}
	
	protected static void sendLine(Player p, String line) {
		tellRaw(p, HIDE_CHAT_BYPASS + line);
	}
	
	private static void setMetadata(Player p, String path, Object val) {
		p.setMetadata(path, new FixedMetadataValue(ChatMenuAPI.getInstance().getHostPlugin(), val));
	}
	
	private static Object getMetadata(Player p, String path, Object defaul) {
		List<MetadataValue> values = p.getMetadata(path);
		if(values.size() == 0) {
			p.setMetadata(path, new FixedMetadataValue(ChatMenuAPI.getInstance().getHostPlugin(), defaul));
			return defaul;
		}
		if(values.size() > 1) {
			Bukkit.getLogger().log(Level.WARNING, "[ChatMenuAPI] Metadata ' " + path + " returned more than 1 value.");
		}
		return values.get(0).value();
	}
	
	
	/**
	 * Sends a packet to the player with the raw, unedited string to be displayed in chat.
	 */
	private static void tellRaw(Player p, String text) {
		PacketContainer chat = new PacketContainer(PacketType.Play.Server.CHAT);
		chat.getChatComponents().write(0, WrappedChatComponent.fromJson(text));
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, chat);
		} catch (InvocationTargetException e) { e.printStackTrace(); }
	}
}
