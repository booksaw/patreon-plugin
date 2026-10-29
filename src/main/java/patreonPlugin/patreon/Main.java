package patreonPlugin.patreon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.patreon.resources.Pledge;

import patreonPlugin.patreon.commands.LinkPatreonCommand;
import patreonPlugin.patreon.commands.ParentCommand;
import patreonPlugin.patreon.commands.UnlinkPatreonCommand;
import patreonPlugin.patreon.commands.patreon.CodeCommand;
import patreonPlugin.patreon.commands.patreon.LinkCommand;
import patreonPlugin.patreon.commands.patreon.ReloadCommand;
import patreonPlugin.patreon.commands.patreon.TokenCommand;
import patreonPlugin.patreon.commands.patreon.UnlinkCommand;
import patreonPlugin.patreon.commands.patreon.UpdateCommand;
import patreonPlugin.patreon.messages.MessageManager;
import patreonPlugin.patreon.verification.EmailVerificationController;
import patreonPlugin.patreon.verification.IVerificationController;
import patreonPlugin.patreon.verification.TrustVerificationController;

public class Main extends JavaPlugin {

	public static Main plugin;

	public ConfigManager messagesConfigManager, dataConfigManager, commandsConfigManager, configManager;
	public YamlConfiguration data, commands;
	public IVerificationController verificationController;

	@Override
	public void onEnable() {
		plugin = this;

		saveCustomConfigs();

		APIusage.loadInfo();

		setupCommands();

		run(getConfig().getInt("delay") * 20);

		if (getConfig().getBoolean("email.verify")) {
			verificationController = new EmailVerificationController();
		} else {
			verificationController = new TrustVerificationController();
		}

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new Placeholders().register();
		}
		// setting up bstats
		int pluginId = 15357;
		new Metrics(this, pluginId);
	}

	private void saveCustomConfigs() {
		configManager = new ConfigManager("config", true);

		messagesConfigManager = new ConfigManager("messages", true);
		MessageManager.addMessages(messagesConfigManager.config);

		dataConfigManager = new ConfigManager("data", true);
		data = dataConfigManager.config;

		commandsConfigManager = new ConfigManager("commands", true);
		commands = commandsConfigManager.config;
	}

	public void setupCommands() {

		getCommand("linkpatreon").setExecutor(new LinkPatreonCommand());
		getCommand("unlinkpatreon").setExecutor(new UnlinkPatreonCommand());

		ParentCommand patreon = new ParentCommand("patreon");

		patreon.addSubCommand(new CodeCommand());
		patreon.addSubCommand(new TokenCommand());
		patreon.addSubCommand(new UpdateCommand());
		patreon.addSubCommand(new ReloadCommand());
		patreon.addSubCommand(new LinkCommand());
		patreon.addSubCommand(new UnlinkCommand());

		new BooksawCommand(getCommand("patreon"), patreon);

	}

	public void run(long delay) {
		new BukkitRunnable() {
			public void run() {
				update();
			}
		}.runTaskTimerAsynchronously(this, delay, delay);
	}

	public void update() {
		// checking token expiary
		if (data.getInt("expires") > System.currentTimeMillis() - 604800000) {
			Bukkit.getScheduler().runTask(this, new Runnable() {
				@Override
				public void run() {
					Bukkit.broadcast(MessageManager.getMessage("admin.soon"), "patreonplugin.admin");
				}
			});
		}

		updatePatreons();
	}

	public void updatePatreons() {

		String token = data.getString("token");
		if (token == null) {
			this.getLogger().severe("TOKEN HAS NOT BEEN SET, CANNOT FETCH PATREON DATA");
			return;
		}

		List<Pledge> pledges = null;
		try {
			pledges = APIusage.getDetails(token);
		} catch (IOException e) {
			this.getLogger().severe("SOMETHING WENT WRONG WHEN FETCHING PATREON DATA");
			e.printStackTrace();
		}

		if (pledges == null) {
			this.getLogger().severe("TOKEN IS EXPIRED");
			return;
		}
		Bukkit.getLogger().info("Found " + pledges.size() + " pledges");

		HashMap<UUID, String> links = getLinks();

		List<String> awarded = data.getStringList("award");
		List<String> newawarded = data.getStringList("award");

		for (Pledge pledge : pledges) {
			if (pledge.getReward() == null || pledge.getReward().getDescription() == null) {
				Bukkit.getLogger().info("Could not find the description of " + pledge.getId());
				continue;
			}

			String rank = getRank(pledge.getReward().getDescription());
			if (rank == null) {
				Bukkit.getLogger().info("Rank could not be found in the pledge with the description "
						+ pledge.getReward().getDescription());
				continue;
			}
			OfflinePlayer p = getPlayer(links, pledge.getPatron().getEmail());
			if (p == null) {
				Bukkit.getLogger().info("A player could not be linked with the email " + pledge.getPatron().getEmail());
				continue;
			}

			if (!contains(awarded, p.getUniqueId() + ":" + rank)) {
				newawarded.add(p.getUniqueId() + ":" + rank);
				Bukkit.getLogger().info("Giving a rank to " + p.getName());
				giveRank(rank, p);
			} else {
				Bukkit.getLogger().info(p.getName() + " already has the rank awarded");
				awarded.remove(p.getUniqueId() + ":" + rank);
			}
		}

		for (String str : awarded) {
			String[] split = str.split(":");

			OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(split[0]));
			if (p == null) {
				Bukkit.getLogger().info("Player with the uuid " + split[0]
						+ " could not be found, so they cannot have their rank removed when it should be");
				continue;
			}
			removeRank(split[1], p);
			Bukkit.getLogger().info("Removing the rank from " + p.getName());
			newawarded.remove(str);
		}

		data.set("award", newawarded);

		dataConfigManager.save();

	}

	public HashMap<UUID, String> getLinks() {
		List<String> dataList = data.getStringList("link");
		HashMap<UUID, String> toReturn = new HashMap<>();

		for (String str : dataList) {
			String[] split = str.split(":");
			toReturn.put(UUID.fromString(split[0]), split[1]);
		}

		return toReturn;

	}

	public void setLinks(Map<UUID, String> links) {
		List<String> dataList = new ArrayList<>();

		for (Entry<UUID, String> ent : links.entrySet()) {
			dataList.add(ent.getKey().toString() + ":" + ent.getValue());
		}

		data.set("link", dataList);
		dataConfigManager.save();

	}

	public OfflinePlayer getPlayer(HashMap<UUID, String> links, String email) {

		for (Entry<UUID, String> entry : links.entrySet()) {
			if (entry.getValue().equals(email)) {
				return Bukkit.getOfflinePlayer(entry.getKey());
			}
		}
		return null;
	}

	public String stripEmail(String email) {
		return email.replaceAll("[^A-Za-z0-9]", "");
	}

	public String getRank(String description) {

		for (String temp : commands.getKeys(false)) {
			if (description.contains(commands.getString(temp + ".promptString"))) {
				return temp;
			}
		}

		return null;
//		String[] split = description.split(getConfig().getString("prompt"));
//		if (split.length == 1) {
//			System.err.println("COULD NOT FIND THE RANK IN THE PLEDGE WITH THE DESCRIPTION " + description);
//			return null;
//		}
//
//		String str = split[1].trim();
//		split = str.split("\\s+");
//
//		return split[0];
	}

	public void giveRank(String rank, OfflinePlayer p) {
		Bukkit.getScheduler().runTask(this, new Runnable() {

			@Override
			public void run() {
				for (String str : commands.getStringList(rank + ".add")) {
					str = str.replaceAll("\\{PLAYER\\}", p.getName());
					str = str.replaceAll("\\{RANK\\}", rank);

					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str);
				}
			}
		});

	}

	public void removeRank(String rank, OfflinePlayer p) {
		Bukkit.getScheduler().runTask(this, new Runnable() {

			@Override
			public void run() {
				for (String str : commands.getStringList(rank + ".remove")) {
					str = str.replaceAll("\\{PLAYER\\}", p.getName());
					str = str.replaceAll("\\{RANK\\}", rank);

					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str);
				}
			}
		});
	}

	public boolean contains(List<String> values, String toCheck) {
		for (String str : values) {
			if (str.equalsIgnoreCase(toCheck)) {
				return true;
			}
		}
		return false;
	}

	public void reload() {

		onDisable();
		reloadConfig();

		onEnable();

	}

	@Override
	public FileConfiguration getConfig() {
		return configManager.config;
	}

	/**
	 * @return
	 */
	public IVerificationController getVerificationController() {
		return verificationController;
	}
}
