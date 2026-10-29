package patreonPlugin.patreon.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import patreonPlugin.patreon.Main;
import patreonPlugin.patreon.messages.MessageManager;
import patreonPlugin.patreon.verification.EmailVerificationController;
import patreonPlugin.patreon.verification.IVerificationController;

public class LinkPatreonCommand implements CommandExecutor {

	private final HashMap<Player, Long> lastCommandUse = new HashMap<>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {

		if (args.length == 0) {
			MessageManager.sendMessage(sender, "link.help");
			return true;
		}

		
		if (!(sender instanceof Player)) {
			MessageManager.sendMessage(sender, "needPlayer");
			return true;
		}

		Player p = (Player) sender;

		if (lastCommandUse.get(p) != null && System.currentTimeMillis() - lastCommandUse.get(p) < Main.plugin.getConfig().getInt("linkPatreonDelaySeconds") * 1000) {
			MessageManager.sendMessage(sender, "link.cooldown");
			return true; 
		}
		
		if (args[0].equals("verify") && Main.plugin.getVerificationController() instanceof EmailVerificationController
				&& args.length >= 2) {
			EmailVerificationController controller = (EmailVerificationController) Main.plugin
					.getVerificationController();
			controller.testVerification(p, args[1]);
			return true;
		}

		HashMap<UUID, String> links = Main.plugin.getLinks();

		if (links.containsValue(args[0])) {
			MessageManager.sendMessage(sender, "link.linked");
			return true;
		}

		if (links.containsKey(p.getUniqueId())) {
			MessageManager.sendMessage(sender, "link.mcLinked");
			return true;
		}

		IVerificationController controller = Main.plugin.getVerificationController();
		controller.verifyPatreon(p, args[0]);

		lastCommandUse.put(p, System.currentTimeMillis());
		
		return true;
	}

}
