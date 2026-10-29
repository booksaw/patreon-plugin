/**
 * 
 */
package patreonPlugin.patreon.verification;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import patreonPlugin.patreon.Main;
import patreonPlugin.patreon.messages.MessageManager;

/**
 * @author booksaw
 *
 */
public class VerificationUtils {

	public static void checkExistingPatreon(Player p) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

			@Override
			public void run() {
				Main.plugin.update();
				List<String> links = Main.plugin.data.getStringList("award");
				for (String str : links) {
					if (str.startsWith(p.getUniqueId().toString())) {
						return;
					}
				}

				MessageManager.sendMessage(p, "nolink");
			}
		});
	}
	
}
