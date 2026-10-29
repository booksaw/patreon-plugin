package patreonPlugin.patreon.messages;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import patreonPlugin.patreon.commands.HelpCommand;
import patreonPlugin.patreon.commands.SubCommand;

public class HelpMessage implements Message {

	SubCommand command;
	String label;

	public HelpMessage(SubCommand command, String label) {
		this.command = command;
		this.label = label;
	}

	@Override
	public void sendMessage(CommandSender sender) {
		MessageManager.sendFullMessage(sender, createHelpMessage(label,
				command.getCommand() + " " + command.getArguments(), command.getHelpMessage()));
	}

	/**
	 * HelpCommand.java ParentCommand.java SubCommand.java Used to create a
	 * formatted help message to explain what a command does to the user
	 * 
	 * @param label       the base command
	 * @param commandPath the rest of the command (i.e. help [param])
	 * @param description the description of the command
	 * @return the created message relating to that command
	 */
	public String createHelpMessage(String label, String commandPath, String description) {
		return HelpCommand.prefix + "/" + label + " " + commandPath + ChatColor.WHITE + " - " + HelpCommand.description
				+ description;
	}

}
