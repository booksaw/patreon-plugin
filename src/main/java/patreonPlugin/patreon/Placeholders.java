/**
 * 
 */
package patreonPlugin.patreon;

import java.util.List;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import patreonPlugin.patreon.messages.MessageManager;

/**
 * @author booksaw
 *
 */
public class Placeholders extends PlaceholderExpansion {

	@Override
	public @NotNull String getAuthor() {
		return Main.plugin.getDescription().getAuthors().toString();
	}

	@Override
	public @NotNull String getIdentifier() {
		return "patreonplugin";
	}

	@Override
	public @NotNull String getVersion() {
		return Main.plugin.getDescription().getVersion();
	}

	@Override
	public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
		identifier = identifier.toLowerCase();

		List<String> awarded = Main.plugin.data.getStringList("award");

		switch (identifier) {
		case "ispledged":

			for (String award : awarded) {
				if (award.startsWith(player.getUniqueId().toString())) {
					return MessageManager.getMessage("placeholder.ispledged");
				}
			}

			return MessageManager.getMessage("placeholder.isnotpledged");
		case "usertier":

			for (String award : awarded) {
				if (award.startsWith(player.getUniqueId().toString())) {
					return award.split(":")[1];
				}
			}

			return MessageManager.getMessage("placeholder.nousertier");
		default:
			return null;
		}
	}

}
