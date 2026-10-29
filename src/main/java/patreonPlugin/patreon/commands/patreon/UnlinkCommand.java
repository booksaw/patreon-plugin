/**
 * 
 */
package patreonPlugin.patreon.commands.patreon;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import patreonPlugin.patreon.CommandResponse;
import patreonPlugin.patreon.Main;
import patreonPlugin.patreon.commands.SubCommand;
import patreonPlugin.patreon.messages.MessageManager;

public class UnlinkCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		Player p = Bukkit.getPlayer(args[0]);

		if (p == null) {
			return new CommandResponse("admin.noPlayer");
		}

		HashMap<UUID, String> links = Main.plugin.getLinks();

		if (!links.containsKey(p.getUniqueId())) {
			return new CommandResponse(true, "admin.unlink.not");
		}

		links.remove(p.getUniqueId());
		Main.plugin.setLinks(links);
		MessageManager.sendMessage(sender, "admin.unlink.success");

		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

			@Override
			public void run() {
				Main.plugin.update();
			}
		});

		return new CommandResponse(true);
	}

	@Override
	public String getCommand() {
		return "unlink";
	}

	@Override
	public String getNode() {
		return "patreonplugin.admin";
	}

	@Override
	public String getHelp() {
		return "Unlink a online players Patreon account";
	}

	@Override
	public String getArguments() {
		return "<player>";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public boolean needPlayer() {
		return false;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
	}

}
