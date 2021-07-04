package mod.grimmauld.discordchat.webhooks;

import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.util.container.LazyOptionalContainer;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Webhook {
	public static final LazyOptionalContainer<Webhook> webhookContainer = new LazyOptionalContainer<>();
	private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:49.0) Gecko/20100101 Firefox/49.0";

	private final String url;
	private final CloseableHttpClient httpClient;

	public Webhook(@Nonnull String url) {
		this.url = url;

		RequestConfig config = RequestConfig.custom()
			.setConnectTimeout(2000)
			.setConnectionRequestTimeout(2000)
			.setSocketTimeout(2000)
			.setAuthenticationEnabled(false)
			.setCircularRedirectsAllowed(false)
			.build();

		this.httpClient = HttpClients.custom()
			.setUserAgent(USER_AGENT)
			.setDefaultRequestConfig(config)
			.build();
	}

	public boolean sendMessage(@Nonnull WebhookMessage message) {
		if (url == null || url.isEmpty())
			return false;

		String data = message.toJSON().toString();
		byte[] payload = data.getBytes(StandardCharsets.UTF_8);

		HttpPost post = new HttpPost(this.url);
		post.setEntity(new ByteArrayEntity(payload, ContentType.APPLICATION_JSON));

		try (CloseableHttpResponse response = this.httpClient.execute(post)) {

			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != 200 && responseCode != 204) {
				DiscordChat.LOGGER.error("Got response code {} from Discord!", responseCode);
				return false;
			}

		} catch (ClientProtocolException ex) {
			DiscordChat.LOGGER.error("HTTP protocol error occurred: {}", ex.getMessage());
			return false;
		} catch (IOException ex) {
			DiscordChat.LOGGER.error("Unable to connect to Discord: {}", ex.getMessage());
			return false;
		}
		return true;
	}
}
