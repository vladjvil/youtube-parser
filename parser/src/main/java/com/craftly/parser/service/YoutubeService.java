package com.craftly.parser.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class YoutubeService {

    @Value("${youtube.api-key}")
    private String apiKey;

    @Value("${youtube.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, String>> searchVideosWithStats(String query, int maxResults) {
        String searchUrl = String.format("%s/search?q=%s&part=snippet&type=video&maxResults=%d&key=%s",
                baseUrl, query, maxResults, apiKey);

        Map response = restTemplate.getForObject(searchUrl, Map.class);
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

        String videoIds = items.stream()
                .map(item -> (String) ((Map) item.get("id")).get("videoId"))
                .collect(Collectors.joining(","));

        String statsUrl = String.format("%s/videos?part=snippet,statistics&id=%s&key=%s",
                baseUrl, videoIds, apiKey);

        Map statsResponse = restTemplate.getForObject(statsUrl, Map.class);
        List<Map<String, Object>> videoItems = (List<Map<String, Object>>) statsResponse.get("items");

        return videoItems.stream().map(item -> {
            Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
            Map<String, Object> statistics = (Map<String, Object>) item.get("statistics");

            return Map.of(
                    "Title", String.valueOf(snippet.getOrDefault("title", "")),
                    "Published At", String.valueOf(snippet.getOrDefault("publishedAt", "")),
                    "Channel Title", String.valueOf(snippet.getOrDefault("channelTitle", "")),
                    "View Count", String.valueOf(statistics.getOrDefault("viewCount", "0")),
                    "Like Count", String.valueOf(statistics.getOrDefault("likeCount", "0"))
            );
        }).collect(Collectors.toList());
    }
}