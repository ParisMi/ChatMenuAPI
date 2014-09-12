package com.sparrow.menuapi;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
 * To intercept chat at a high level,
 * this library utilizes ProtocolLib. This class is responsible for all
 * chat interactions.
 *
 */
public class ChatAdapter extends PacketAdapter{

	protected ChatAdapter(JavaPlugin p) {
		super(p, ListenerPriority.HIGHEST, PacketType.Play.Server.CHAT, PacketType.Play.Client.CHAT);
	}

	@Override
	public void onPacketReceiving(PacketEvent e) {
		PacketContainer pack = e.getPacket();
		String chat = pack.getChatComponents().read(0).getJson();
		Player player = e.getPlayer();
		ChatMenuAPI api = ChatMenuAPI.getInstance();

		//The player is NOT allowed to have the hide chat bypass string in his messages to the server.
		pack.getChatComponents().write(0, WrappedChatComponent.fromJson(chat.replace(ChatUtil.HIDE_CHAT_BYPASS, "")));

		if(api.isInMenu(player)) {
			api.getMenu(player).recieveText(Arrays.asList(chat.split(" ")));
			e.setCancelled(true); //do not broadcast this to the server
		}
	}

	@Override
	public void onPacketSending(PacketEvent e) {
		PacketContainer pack = e.getPacket();
		String chat = pack.getChatComponents().read(0).getJson();
		Player player = e.getPlayer();

		if(ChatMenuAPI.getInstance().isInMenu(player)) {
			//if the player is in a menu and this chat doesn't have the chat bypass, don't display it
			if(!chat.contains(ChatUtil.HIDE_CHAT_BYPASS)) {
				e.setCancelled(true);
				ChatUtil.addLineToMissedChat(player, chat);
			} else {
				//otherwise, send it to them with the bypass erased
				pack.getChatComponents().write(0, WrappedChatComponent.fromJson(chat.replace(ChatUtil.HIDE_CHAT_BYPASS, "")));
			}
		}
	}
}
