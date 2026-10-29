package patreonPlugin.patreon;

import static com.patreon.PatreonAPI.BASE_URI;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.bukkit.Bukkit;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.patreon.PatreonAPI;
import com.patreon.PatreonOAuth;
import com.patreon.resources.Campaign;
import com.patreon.resources.Pledge;

public class APIusage {

	public static void main(String[] args) {
//		System.out.println(System.currentTimeMillis());
		Bukkit.getLogger().info(getPermissionURL());
//		try {
//			getDetails("rxvGtWDl0jxJznHyRtL0l74COnJ-wbQxDkZaSJdn7vg");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	static String responseType = "code";
	static String clientID;
	static String clientSecret;
	static String redirect;
	static String scope = "campaigns campaigns.members[email] campaigns.members";

	public static void loadInfo() {
		clientID = Main.plugin.getConfig().getString("clientID");
		clientSecret = Main.plugin.getConfig().getString("clientSecret");
		redirect = Main.plugin.getConfig().getString("redirect");
	}

	public static PatreonOAuth.TokensResponse getToken(String code) {
		try {

			PatreonOAuth oauthClient = new PatreonOAuth(clientID, clientSecret, redirect);
			PatreonOAuth.TokensResponse tokens = oauthClient.getTokens(code);

			return tokens;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<Pledge> getDetails(String token) throws IOException {
		Bukkit.getLogger().info("[PatreonPlugin] Attempting to fetch patreon data");

		String path = new URIBuilder().setPath("current_user/campaigns")
				.addParameter("include", "rewards,creator,goals").toString();
		Bukkit.getLogger().info(path);
		String prefix = BASE_URI + "/api/oauth2/api/";
		Bukkit.getLogger().info(prefix);
		URL url = new URL(prefix.concat(path).replace("//", "/"));
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Authorization", "Bearer ".concat(token));
		connection.setRequestProperty("User-Agent", String.format("Patreon-Java, version %s, platform %s %s", "1.0",
				System.getProperty("os.name"), System.getProperty("os.version")));
		Bukkit.getLogger().info(connection.getURL().toString());

		PatreonAPI apiClient = new PatreonAPI(token);
		JSONAPIDocument<List<Campaign>> cs = apiClient.fetchCampaigns();

		// temp

		Bukkit.getLogger().info(cs.get().size() + " campaigns found");
		for (Campaign c : cs.get()) {
			Bukkit.getLogger().info("[PatreonPlugin] The first campaign is " + c.getCreationName() + ", " + c.getOneLiner()
					+ " this should contain patreon data");
			List<Pledge> pledges = apiClient.fetchAllPledges(c.getId());
			apiClient.fet
			return pledges;
		}

		return null;
	}

//	public void getPledges(String id) {
//		try {
//
////			URL url = new URL("https://www.patreon.com/oauth2/authorize?response_type="
////					+ java.net.URLEncoder.encode(responseType, "UTF-8") + "&client_id="
////					+ java.net.URLEncoder.encode(clientID, "UTF-8") + "&redirect_uri="
////					+ java.net.URLEncoder.encode(redirect, "UTF-8"));
//
//			URL url = new URL("https://www.patreon.com/api/oauth2/v2/campaigns/" + id + "/members");
//
//			System.out.println("url = " + url.toString());
//			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
//			con.setRequestMethod("GET");
//			int status = con.getResponseCode();
//			System.out.println("status = " + status);
//			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//			String inputLine;
//			StringBuffer content = new StringBuffer();
//			while ((inputLine = in.readLine()) != null) {
//				content.append(inputLine);
//			}
//			in.close();
//
//			con.disconnect();
//			System.out.println(content);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public static String getPermissionURL() {
		URL url = null;
		try {
			url = new URL("https://www.patreon.com/oauth2/authorize?response_type="
					+ java.net.URLEncoder.encode(responseType, "UTF-8") + "&client_id="
					+ java.net.URLEncoder.encode(clientID, "UTF-8") + "&redirect_uri="
					+ java.net.URLEncoder.encode(redirect, "UTF-8") + "&scope="
					+ java.net.URLEncoder.encode(scope, "UTF-8"));
		} catch (MalformedURLException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url.toString();
	}
}
