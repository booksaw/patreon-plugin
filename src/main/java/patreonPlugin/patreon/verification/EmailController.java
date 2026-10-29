package patreonPlugin.patreon.verification;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import patreonPlugin.patreon.Main;

public class EmailController {

	private final String password;
	private final String from;
	private final String subject;
	private final List<String> body;
	private final String host;

	public EmailController() {
		password = Main.plugin.getConfig().getString("email.password");
		from = Main.plugin.getConfig().getString("email.address");
		subject = Main.plugin.getConfig().getString("email.subject");
		body = Main.plugin.getConfig().getStringList("email.body");
		host = Main.plugin.getConfig().getString("email.host");
	}

	public void sendEmail(String email, String playerUsername, String code) throws MessagingException {
		Properties prop = getProperties();

		Authenticator authenticator = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		};

		Session session = Session.getInstance(prop, authenticator);
		session.setDebug(false);

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		InternetAddress[] address = { new InternetAddress(email) };
		message.setRecipients(Message.RecipientType.TO, address);
		message.setSubject(subject);
		message.setSentDate(new Date());

		message.setContent(buildMessage(playerUsername, code), "text/html; charset=utf-8");
		Transport.send(message);
	}

	private Properties getProperties() {
		Properties prop = new Properties();
		prop.put("mail.smtp.host", host);
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");
		return prop;
	}

	private String buildMessage(String username, String code) {
		StringBuilder builder = new StringBuilder();

		for (String line : body) {
			line = line.replace("{USERNAME}", username);
			line = line.replace("{CODE}", code);
			builder.append(line).append("<br />");
		}
		return builder.toString();
	}

	public boolean isEmailAddress(String email) {
		// Regular Expression
		String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
		// Compile regular expression to get the pattern
		Pattern pattern = Pattern.compile(regex);

		return pattern.matcher(email).matches();
	}

}
