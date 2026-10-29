package patreonPlugin.patreon.commands.patreon;

import java.util.List;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import patreonPlugin.patreon.APIusage;
import patreonPlugin.patreon.CommandResponse;
import patreonPlugin.patreon.commands.SubCommand;
import patreonPlugin.patreon.messages.MessageManager;

public class TokenCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		MessageManager.sendMessage(sender, "admin.token.send");
		MessageManager.sendFullMessage(sender, ChatColor.GREEN + APIusage.getPermissionURL());
		MessageManager.sendMessage(sender, "admin.token.end");

		return new CommandResponse(true);
	}

	@Override
	public String getCommand() {
		return "token";
	}

	@Override
	public String getNode() {
		return "admin";
	}

	@Override
	public String getHelp() {
		return "Get the link to refresh the current token";
	}

	@Override
	public String getArguments() {
		return "";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public int getMaximumArguments() {
		return 0;
	}

	@Override
	public boolean needPlayer() {
		return false;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
	}

}
