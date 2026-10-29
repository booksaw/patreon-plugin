package patreonPlugin.patreon.messages;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;

public class ComponentMessage implements Message {

	TextComponent component;
	Message altMessage;

	/**
	 * @param component  The text component to send to the player
	 * @param altMessage The alternative message if the commandSender is not a
	 *                   player
	 */
	public ComponentMessage(TextComponent component, Message altMessage) {
		this.component = component;
		this.altMessage = altMessage;
	}

	@Override
	public void sendMessage(CommandSender sender) {

		if (sender instanceof Player) {
			((Player) sender).spigot().sendMessage(component);
		} else if (altMessage != null) {
			altMessage.sendMessage(sender);
		}

	}

}
