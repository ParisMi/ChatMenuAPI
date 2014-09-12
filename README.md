ChatMenuAPI
===========

Lightweight API for Bukkit plugins to create intractable chat menus.

##Features

*Simple API* - 2 classes (ChatMenuAPI and ChatMenu) are all you need to access the full range of the library.

*Chat Catchup* - After the menu closes, players will still have access to any chat they missed while using the menu.

*Easily Integrated* - Only requires ProtocolLib (already installed on most servers). One line will automatically initialize the plugin. No .yml dependencies, no boilerplate, no startup logic.

*Extensible Menus* - Menus are easily programmed with the implementation of 3 methods. Almost all functionality is customizable.

##Usage

####Initialization
Add this line to onEnable() in your plugin:

<code>ChatMenuAPI.getInstance();</code>

####Building Menus
Building a menu consists of two steps: 1, declaring any usable commands (functionality), and 2, displaying the menu. All of this is accomplished by creating a new class that extends ChatMenu.

Commands are snippets of code that are run when a player interacts with a menu (usually clicking a button). They are in the form of JSON clickable text that will send a specified string through chat to the server (all chat is hidden and managed by the server during this time). The server then runs the associated command.

The code run is specified by ChatMenuCommand, an interface whose only method is execute(List<String> arguments). It is probably best to declare these as an anonymous class to improve readibility and compact code.

They are declared in setupCommands() in ChatMenu.java. It requires a populated HashMap of commands to be returned:
```
public class ExampleMenu extends ChatMenu {

  public HashMap<String, ChatMenuCommand> setupCommands() {
  
    HashMap<String, ChatMenuCommand> commands = new HashMap<String, ChatMenuCommand>();
    
    //register a new command called "close-server"
    commands.put("close_server", new ChatMenuCommand() {
				public void execute(List<String> words) {
					Bukkit.getPlayer(words.get(0)).damage(999);
				}
			});
    
    commands.put("kill_person", new ChatMenuCommand() {
				public void execute(List<String> words) {
					Bukkit.getPlayer(words.get(0)).damage(999);
				}
			});
			
    return commands;
  }
}
```

Displaying text requires overriding createMenu(), which is called to render the menu whenever the player interacts with the menu or when the menu is initially displayed.
To render the screen, two methods are used: sendLine(String line) and sendCommandLine(String command, String text).
sendLine() will display that text to the player (JSON formatting is supported, so advanced users could leverage chat format libraries like Fanciful to display complex text or command linking here). sendCommandLine will create a clickable line of text that sends the string "command" to the server.

```
		@Override
		protected void createMenu(List<String> lastArgs) {
			sendLine("-----Chat Menu API Example-----");
			sendLine("Hey there!");
			sendCommandLine("exit","               " + ChatColor.RED + "[X]");
			sendCommandLine("close_server","Shut down the server");
			sendCommandLine("kill_person Jerry", "Kill the player named Jerry!");
		}

```
Every menu has a built in command, "exit", which allows players to easily exit the menu. In this case it appears as a red X at the top right of the menu.

sendCommandLine() simply uses JSON formatting to create clickable command text. It's possible using raw json or formatting libraries described previously. It is important to note that during this time, the player is not capable of receiving chat. To send players text, use the methods for displaying text above.

Finally, ChatMenu also requires onTextTyped(List<String> words) to be implemented. This method is run whenever the player manually sends text to the server and is useful for allowing players to type names, arguments, options, etc. You can see an example of this in com/sparrow/example/ExampleChatMenuPlugin.java.

####Managing Menus
Once you have a menu built, managing it is easy using just a few methods:
```
ChatMenuAPI api = ChatMenuAPI.getInstance();
Player jerry = Bukkit.getPlayer("Jerry");

api.displayMenu(new ExampleMenu(jerry)); //will show Jerry the chat menu

...

if(api.isInMenu(jerry)) {
  ChatMenu exampleMenu = api.getMenu(jerry); //get the menu at any time
}

...

api.exitMenu(jerry); //closes the menu gracefully and returns chat to normal
```

##Example

See com/sparrow/menuapi/example for a cohesive example.
