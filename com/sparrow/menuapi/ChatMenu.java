package com.sparrow.menuapi;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

public abstract class ChatMenu {

	private final Player owner;
	private HashMap<String, ChatMenuCommand> commands = new HashMap<String, ChatMenuCommand>();
	
	///////these methods should be overridden///////
	
	/**
	 * This method is executed whenever the client opens a menu or interacts with it.
	 * It is responsible for generating the menu.
	 * Use {@link #sendLine(String)} to send text and {@link #sendCommandLine(String, String)} to display
	 * clickable commands.
	 * @param lastArgs The arguments sent by the client. If this is a command, the command will be index 0. Otherwise
	 * the list is simply the text sent to the server (split by spaces). It will be empty upon initial creation.
	 */
	protected abstract void createMenu(List<String> lastArgs);
	
	/**
	 * This method is run during instantiation. It should be used to 
	 * register all commands utilizing
	 * hashMap.put(commandName, chatMenuCommandCode);
	 * @return The map of commands and their respective code.
	 */
	protected abstract HashMap<String, ChatMenuCommand> setupCommands();
	
	/**
	 * This method is executed whenever the client sends text to the server,
	 * but the text is not a command.
	 * This useful to, say, ask for a text response
	 * (what is your name? respond in chat.)
	 * @param words
	 */
	protected abstract void onTextTyped(List<String> words);
	
	////////////////////////////////////////////////
	
	protected final void showMenu(List<String> lastCommand) {
		ChatUtil.clearChat(owner);
		createMenu(lastCommand);
	}
	
	public final void recieveText(List<String> words) {
		ChatMenuCommand cmd = commands.get(words.get(0));
		//If the first word is a command, execute it
		if(cmd != null) {
			cmd.execute(words);
		} else {
			onTextTyped(words);
		}
		showMenu(words);
	}
	
	protected final void registerCommand(String name, ChatMenuCommand code) {
		commands.put(name, code);
	}

	public ChatMenu(final Player owner) {
		this.owner = owner;
		commands = setupCommands();
		commands.put("exit", new ChatMenuCommand() {
			@Override
			public void execute(List<String> words) {
				ChatUtil.exitMenu(owner);
			}
		});
	}
	
	public Player getOwner() {
		return owner;
	}
	
	/**
	 * Send a line of raw JSON (or regular text) to the menu owner.
	 * Advanced chat libraries like Fanciful should be used to
	 * convert the JSON to a string and utilize this method.
	 * @param string the raw JSON
	 */
	protected final void sendLine(String string) {
		ChatUtil.sendLine(owner, string);
	}
	
	/**
	 * Send a formatted string to the player. 
	 * When clicked, the player will send 'command' to the server.
	 * @param command The command name plus any arguments, each separated with a space
	 * @param text The clickable text to display
	 */
	protected final void sendCommandLine(String command, String text) {
		ChatUtil.sendCommandLine(owner, command, text, "");
	}
	
	/**
	 * Identical to {@link #sendCommandLine(String, String)},
	 * but additionalText will not be clickable.
	 * @param additionalText the non-clickable text to follow the clickable command.
	 */
	protected final void sendCommandLine(String command, String text, String additionalText) {
		ChatUtil.sendCommandLine(owner, command, text, additionalText);
	}
}
