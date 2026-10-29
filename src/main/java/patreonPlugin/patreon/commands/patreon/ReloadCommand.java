/**
 * 
 */
package patreonPlugin.patreon.commands.patreon;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import patreonPlugin.patreon.CommandResponse;
import patreonPlugin.patreon.Main;
import patreonPlugin.patreon.commands.SubCommand;

/**
 * @author booksaw
 *
 */
public class ReloadCommand extends SubCommand{

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		HandlerList.unregisterAll(Main.plugin);
		Main.plugin.reload();
	
		return new CommandResponse(true, "admin.reload");
		
	}

	@Override
	public String getCommand() {
		return "reload";
	}

	@Override
	public String getNode() {
		return "admin";
	}

	@Override
	public String getHelp() {
		return "Reload the config - In some circumstances this plugin may not work";
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
