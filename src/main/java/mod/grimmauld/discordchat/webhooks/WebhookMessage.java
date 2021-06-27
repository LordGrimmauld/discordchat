package mod.grimmauld.discordchat.webhooks;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import javax.annotation.Nonnull;

public class WebhookMessage {
	private final String username;
	private final String avatarUrl;
	private final String content;

	public WebhookMessage(@Nonnull String username, @Nonnull String avatarUrl, @Nonnull String content) {
		this.username = username;
		this.avatarUrl = avatarUrl;
		this.content = content;
	}

	@Nonnull
	public JsonObject toJSON() {
		JsonObject json = new JsonObject();
		json.add("username", new JsonPrimitive(this.username));
		json.add("avatar_url", new JsonPrimitive(this.avatarUrl));
		json.add("content", new JsonPrimitive(this.content));
		return json;
	}
}
