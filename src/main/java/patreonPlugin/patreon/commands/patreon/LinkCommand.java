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
import patreonPlugin.patreon.verification.IVerificationController;
import patreonPlugin.patreon.verification.TrustVerificationController;

/**
 * @author booksaw
 *
 */
public class LinkCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		
		IVerificationController verificationController = new TrustVerificationController();
	

		Player p = Bukkit.getPlayer(args[0]);
		
		if(p == null) {
			return new CommandResponse("admin.noPlayer");
		}
		
		HashMap<UUID, String> links = Main.plugin.getLinks();

		if (links.containsValue(args[0])) {
			return new CommandResponse(true, "admin.link.linked");
		}

		if (links.containsKey(p.getUniqueId())) {
			return new CommandResponse(true, "admin.link.mcLinked");
		}

		verificationController.verifyPatreon(p, args[0]);
		
		return new CommandResponse(true, "admin.link.success");
	}

	@Override
	public String getCommand() {
		return "link";
	}

	@Override
	public String getNode() {
		return "patreonplugin.admin";
	}

	@Override
	public String getHelp() {
		return "Link a players patreon account - Must be used with online players";
	}

	@Override
	public String getArguments() {
		return "<player> <email>";
	}

	@Override
	public int getMinimumArguments() {
		return 2;
	}

	@Override
	public int getMaximumArguments() {
		return 2;
	}

	@Override
	public boolean needPlayer() {
		return false;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
	}

}
