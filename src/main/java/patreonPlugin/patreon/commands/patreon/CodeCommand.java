package patreonPlugin.patreon.commands.patreon;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.patreon.PatreonOAuth.TokensResponse;

import patreonPlugin.patreon.APIusage;
import patreonPlugin.patreon.CommandResponse;
import patreonPlugin.patreon.Main;
import patreonPlugin.patreon.commands.SubCommand;

public class CodeCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		String[] split = args[0].split("\\?code=");
		if (split.length != 2) {
			return new CommandResponse("admin.code.invalid");
		}

		split = split[1].split("&state=");

		if (split.length != 2) {
			return new CommandResponse("admin.code.invalid");
		}

		TokensResponse token = APIusage.getToken(split[0]);

		if (token == null) {
			return new CommandResponse("admin.code.err");
		}

		Main.plugin.data.set("token", token.getAccessToken());
		Main.plugin.data.set("expires", System.currentTimeMillis() + (token.getExpiresIn() * 1000));
		Main.plugin.dataConfigManager.save();

		return new CommandResponse("admin.code.success");
	}

	@Override
	public String getCommand() {
		return "code";
	}

	@Override
	public String getNode() {
		return "admin";
	}

	@Override
	public String getHelp() {
		return "Input the redirect URL so the token can be extracted";
	}

	@Override
	public String getArguments() {
		return "<URL>";
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
