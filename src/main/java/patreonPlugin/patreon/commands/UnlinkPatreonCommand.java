package patreonPlugin.patreon.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import patreonPlugin.patreon.Main;
import patreonPlugin.patreon.messages.MessageManager;

public class UnlinkPatreonCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {

		if (!(sender instanceof Player)) {
			MessageManager.sendMessage(sender, "needPlayer");
			return true;
		}

		Player p = (Player) sender;

		HashMap<UUID, String> links = Main.plugin.getLinks();

		if (!links.containsKey(p.getUniqueId())) {
			MessageManager.sendMessage(sender, "unlink.not");
			return true;
		}

		links.remove(p.getUniqueId());
		Main.plugin.setLinks(links);
		MessageManager.sendMessage(sender, "unlink.success");

		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

			@Override
			public void run() {
				Main.plugin.update();
			}
		});
		return true;
	}

}
