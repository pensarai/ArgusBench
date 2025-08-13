package com.financehub.controller;

import com.financehub.service.ImageProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    
    private final ImageProcessingService imageProcessingService;
    
    @PostMapping("/process")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public ResponseEntity<?> processImage(@RequestBody Map<String, String> request) {
        String imageUrl = request.get("imageUrl");
        String processingType = request.get("processingType");
        
        if (imageUrl == null || imageUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Image URL required");
        }
        
        if (processingType == null || processingType.isEmpty()) {
            processingType = "metadata";
        }
        
        try {
            String result = imageProcessingService.processImageFromUrl(imageUrl, processingType);
            return ResponseEntity.ok(Map.of(
                "message", "Image processed successfully",
                "result", result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public ResponseEntity<?> getImageInfo(@RequestParam("url") String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Image URL required");
        }
        
        try {
            String info = imageProcessingService.fetchImageInfo(imageUrl);
            return ResponseEntity.ok(Map.of("info", info));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/proxy")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> proxyImage(@RequestBody Map<String, String> request) {
        String imageUrl = request.get("imageUrl");
        String outputFormat = request.get("outputFormat");
        
        if (imageUrl == null || imageUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Image URL required");
        }
        
        if (outputFormat == null || outputFormat.isEmpty()) {
            outputFormat = "jpeg";
        }
        
        try {
            String result = imageProcessingService.proxyImageRequest(imageUrl, outputFormat);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/batch-process")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> batchProcessImages(@RequestBody Map<String, Object> request) {
        java.util.List<String> imageUrls = (java.util.List<String>) request.get("imageUrls");
        String processingType = (String) request.get("processingType");
        
        if (imageUrls == null || imageUrls.isEmpty()) {
            return ResponseEntity.badRequest().body("Image URLs required");
        }
        
        if (processingType == null || processingType.isEmpty()) {
            processingType = "metadata";
        }
        
        java.util.List<Map<String, String>> results = new java.util.ArrayList<>();
        
        for (String imageUrl : imageUrls) {
            try {
                String result = imageProcessingService.processImageFromUrl(imageUrl, processingType);
                results.add(Map.of(
                    "url", imageUrl,
                    "status", "success",
                    "result", result
                ));
            } catch (Exception e) {
                results.add(Map.of(
                    "url", imageUrl,
                    "status", "failed",
                    "error", e.getMessage()
                ));
            }
        }
        
        return ResponseEntity.ok(Map.of(
            "message", "Batch processing completed",
            "results", results
        ));
    }
}