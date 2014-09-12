package com.sparrow.example;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sparrow.menuapi.ChatMenu;
import com.sparrow.menuapi.ChatMenuAPI;
import com.sparrow.menuapi.ChatMenuCommand;

public class ExampleChatMenuPlugin extends JavaPlugin {

	private ChatMenuAPI chatMenuAPI;
	
	@Override
	public void onEnable() {
		//all that is required to begin use of the API is initialization.
		chatMenuAPI = ChatMenuAPI.getInstance();
		chatMenuAPI.initalize(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args ) {
		if(!(sender instanceof Player)) { //sent from console
			return true;
		}

		Player origin = (Player)sender;
		if(command.getName().equals("menu")) {
			chatMenuAPI.displayMenu(new ExampleChatMenu(origin));
			return true;
		}
		return false;
	}
	
	private class ExampleChatMenu extends ChatMenu {

		private boolean enterNameMode = false;
		private String name;
		
		public ExampleChatMenu(Player owner) {
			super(owner);
		}

		@Override
		protected void createMenu(List<String> lastArgs) {
			sendLine("-----Chat Menu API Example-----");
			if(name != null) {
				sendLine("Hey there, " + name +"!");
			}
			sendCommandLine("exit","Exit this menu");
			sendCommandLine("say_hi","Say Hello!");
			sendCommandLine("kill_self","Kill yourself");
			sendCommandLine("enter_name","Click this, ", " but not this, to enter your name.");
			if(enterNameMode == true) {
				sendLine("Enter your name in chat:");
			}
		}

		@Override
		protected HashMap<String, ChatMenuCommand> setupCommands() {
			HashMap<String, ChatMenuCommand> commands = new HashMap<String, ChatMenuCommand>();
			
			
			commands.put("exit", new ChatMenuCommand() {

				@Override
				public void execute(List<String> words) {
					chatMenuAPI.exitMenu(getOwner());
				}
				
			});
			
			commands.put("say_hi", new ChatMenuCommand() {

				@Override
				public void execute(List<String> words) {
					sendLine("Hi there!");
				}
				
			});
			
			commands.put("kill_self", new ChatMenuCommand() {

				@Override
				public void execute(List<String> words) {
					getOwner().damage(999);
				}
				
			});
			
			commands.put("kill_person", new ChatMenuCommand() {

				@SuppressWarnings("deprecation")
				@Override
				public void execute(List<String> words) {
					Bukkit.getPlayer(words.get(0)).damage(999);
				}
				
			});
			
			commands.put("enter_name", new ChatMenuCommand() {

				@Override
				public void execute(List<String> words) {
					enterNameMode = true;
				}
				
			});
			
			return commands;
		}

		@Override
		protected void onTextTyped(List<String> words) {
			if(enterNameMode) {
				name = words.get(0);
				enterNameMode = false;
			}
		}
		
	}
	
}


