package com.tebra.internalservice.infraestructure.discord;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DiscordBotClient {

    private final RestClient restClient;

    public DiscordBotClient(RestClient.Builder restClientBuilder, @Value("${discord.bot.url}") String url) {
        this.restClient = restClientBuilder.baseUrl(url).build();
    }

    public void sendMessage(String ownerDiscordId, String title, String message) {
        restClient.post()
                .uri("/discordbot-alts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new DiscordMessageRequest(ownerDiscordId, title, message))
                .retrieve()
                .toBodilessEntity();
    }

    private record DiscordMessageRequest(String ownerDiscordId, String title, String message) {}
}
