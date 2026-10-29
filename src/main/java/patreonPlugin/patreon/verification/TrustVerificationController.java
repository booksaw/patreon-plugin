/**
 * 
 */
package patreonPlugin.patreon.verification;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import patreonPlugin.patreon.Main;
import patreonPlugin.patreon.messages.MessageManager;

/**
 * @author booksaw
 *
 */
public class TrustVerificationController implements IVerificationController{

	@Override
	public void verifyPatreon(Player p, String email) {
		HashMap<UUID, String> links = Main.plugin.getLinks();
		links.put(p.getUniqueId(), email);
		Main.plugin.setLinks(links);
		
		MessageManager.sendMessage(p, "link.success");
		
		VerificationUtils.checkExistingPatreon(p);
		
	}

}
