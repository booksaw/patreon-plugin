package patreonPlugin.patreon.commands.patreon;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import patreonPlugin.patreon.CommandResponse;
import patreonPlugin.patreon.Main;
import patreonPlugin.patreon.commands.SubCommand;

public class UpdateCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

			@Override
			public void run() {
				Main.plugin.update();
			}
		});

		return new CommandResponse("admin.update.success");
	}

	@Override
	public String getCommand() {
		return "update";
	}

	@Override
	public String getNode() {
		return "admin";
	}

	@Override
	public String getHelp() {
		return "Check for new patreons";
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
