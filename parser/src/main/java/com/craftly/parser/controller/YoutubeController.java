package com.craftly.parser.controller;

import com.craftly.parser.service.ExcelGenerationService;
import com.craftly.parser.service.YoutubeService;
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
@RequestMapping("/youtube")
public class YoutubeController {
    private final YoutubeService youTubeService;
    private final ExcelGenerationService excelGenerationService;

    public YoutubeController(YoutubeService youTubeService, ExcelGenerationService excelGenerationService) {
        this.youTubeService = youTubeService;
        this.excelGenerationService = excelGenerationService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getVideos(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int maxResults
    ) {
        return ResponseEntity.ok(youTubeService.searchVideosWithStats(query, maxResults));
    }

    @GetMapping("/excel")
    public ResponseEntity<Resource> getVideosExcel(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int maxResults
    ) throws IOException {
        var videos = youTubeService.searchVideosWithStats(query, maxResults);

        String tempFileName = "temp.xlsx";
        excelGenerationService.generateExcel(tempFileName, videos);

        Path filePath = Paths.get(tempFileName).toAbsolutePath().normalize();
        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=videos.xlsx")
                .body(resource);
    }
}
