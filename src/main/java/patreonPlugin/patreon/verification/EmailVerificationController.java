/**
 * 
 */
package patreonPlugin.patreon.verification;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import jakarta.mail.MessagingException;
import patreonPlugin.patreon.Main;
import patreonPlugin.patreon.messages.MessageManager;

/**
 * @author booksaw
 *
 */
public class EmailVerificationController implements IVerificationController {

	Map<UUID, CodeLink> sentCodes = new HashMap<>();

	@Override
	public void verifyPatreon(Player player, String email) {
		
		// invalidating any requests with that email or player UUID
		sentCodes.entrySet().removeIf(entry -> entry.getKey().equals(player.getUniqueId()) || entry.getValue().email.equals(email));
		
		MessageManager.sendMessage(player, "email.attempting");

		new BukkitRunnable() {

			@Override
			public void run() {

				try {
					sendVerificationEmail(email, player);
				} catch (MessagingException e) {
					MessageManager.sendMessage(player, "email.unable");
					e.printStackTrace();
					return;
				} catch (IllegalArgumentException e) {
					return;
				}
				MessageManager.sendMessage(player, "email.success");
			}
		}.runTaskAsynchronously(Main.plugin);

	}

	private void sendVerificationEmail(String email, Player player) throws MessagingException {

		EmailController emailController = new EmailController();

		if (!emailController.isEmailAddress(email)) {
			MessageManager.sendMessage(player, "email.invalid");
			throw new IllegalArgumentException();
		}

		String code = generateCode();
		CodeLink link = new CodeLink(email, code);

		emailController.sendEmail(email, player.getName(), code);
		sentCodes.put(player.getUniqueId(), link);
	}

	private String generateCode() {
		String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lower = upper.toLowerCase(Locale.ROOT);
		String digits = "0123456789";
		String alphanum = upper + lower + digits;

		Random rnd = new Random();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			builder.append(alphanum.charAt(rnd.nextInt(alphanum.length())));
		}

		return builder.toString();
	}

	public void testVerification(Player player, String code) {
		CodeLink link = sentCodes.get(player.getUniqueId());
		if (link == null) {
			MessageManager.sendMessage(player, "email.noverify");
			return;
		}

		if (!code.equals(link.code)) {
			MessageManager.sendMessage(player, "email.wrongcode");
			return;
		}

		sentCodes.remove(player.getUniqueId());
		HashMap<UUID, String> links = Main.plugin.getLinks();
		links.put(player.getUniqueId(), link.email);
		Main.plugin.setLinks(links);

		MessageManager.sendMessage(player, "link.success");

		VerificationUtils.checkExistingPatreon(player);

	}

	private class CodeLink {
		public final String email;
		public final String code;

		/**
		 * 
		 */
		public CodeLink(String email, String code) {
			this.email = email;
			this.code = code;
		}
	}

}
