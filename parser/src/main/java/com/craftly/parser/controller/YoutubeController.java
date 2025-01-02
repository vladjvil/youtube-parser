package com.craftly.parser.controller;

import com.craftly.parser.service.CSVGenerationService;
import com.craftly.parser.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
public class YoutubeController {
    private final YoutubeService youTubeService;
    private final CSVGenerationService csvGenerationService;

    public YoutubeController(YoutubeService youTubeService, CSVGenerationService csvGenerationService) {
        this.youTubeService = youTubeService;
        this.csvGenerationService = csvGenerationService;
    }

    @GetMapping("/youtube")
    public ResponseEntity<Resource> getVideos(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int maxResults
    ) throws IOException {
        List<Map<String, String>> videos = youTubeService.searchVideosWithStats(query, maxResults);

        String fileName = "youtube_videos.csv";
        csvGenerationService.generateCSV(videos, fileName);

        Path filePath = Paths.get(fileName).toAbsolutePath().normalize();
        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(resource);
    }
}
