package com.financehub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class ImageProcessingService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public String processImageFromUrl(String imageUrl, String processingType) {
        try {
            log.info("Processing image from URL: {}", imageUrl);
            
            // Download image from provided URL
            byte[] imageData = downloadImage(imageUrl);
            
            // Save to temporary location
            String tempId = UUID.randomUUID().toString();
            Path tempPath = Path.of(System.getProperty("java.io.tmpdir"), "image-" + tempId + ".tmp");
            Files.write(tempPath, imageData);
            
            // Process image based on type
            String result = processImage(tempPath, processingType);
            
            // Cleanup
            Files.deleteIfExists(tempPath);
            
            return result;
            
        } catch (Exception e) {
            log.error("Image processing failed for URL: {} - {}", imageUrl, e.getMessage());
            throw new RuntimeException("Image processing failed: " + e.getMessage());
        }
    }
    
    private byte[] downloadImage(String imageUrl) throws Exception {
        log.info("Downloading image from: {}", imageUrl);
        
        ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl, byte[].class);
        if (response.getBody() == null) {
            throw new RuntimeException("No image data received");
        }
        
        return response.getBody();
    }
    
    private String processImage(Path imagePath, String processingType) {
        try {
            switch (processingType.toLowerCase()) {
                case "resize":
                    return resizeImage(imagePath);
                case "compress":
                    return compressImage(imagePath);
                case "metadata":
                    return extractMetadata(imagePath);
                case "convert":
                    return convertImage(imagePath);
                default:
                    return "Unknown processing type: " + processingType;
            }
        } catch (Exception e) {
            return "Processing failed: " + e.getMessage();
        }
    }
    
    private String resizeImage(Path imagePath) {
        // Simulate image resizing
        return "Image resized successfully: " + imagePath.getFileName();
    }
    
    private String compressImage(Path imagePath) {
        // Simulate image compression
        return "Image compressed successfully: " + imagePath.getFileName();
    }
    
    private String extractMetadata(Path imagePath) {
        try {
            long size = Files.size(imagePath);
            return String.format("Image metadata - Size: %d bytes, Path: %s", size, imagePath.toString());
        } catch (Exception e) {
            return "Metadata extraction failed: " + e.getMessage();
        }
    }
    
    private String convertImage(Path imagePath) {
        // Simulate image format conversion
        return "Image converted successfully: " + imagePath.getFileName();
    }
    
    public String fetchImageInfo(String imageUrl) {
        try {
            log.info("Fetching image info from: {}", imageUrl);
            
            ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl, byte[].class);
            
            if (response.getBody() == null) {
                return "No image data available";
            }
            
            int size = response.getBody().length;
            String contentType = response.getHeaders().getContentType() != null 
                ? response.getHeaders().getContentType().toString() 
                : "unknown";
            
            return String.format("Image info - Size: %d bytes, Content-Type: %s, URL: %s", 
                size, contentType, imageUrl);
                
        } catch (Exception e) {
            log.error("Failed to fetch image info from: {} - {}", imageUrl, e.getMessage());
            return "Failed to fetch image info: " + e.getMessage();
        }
    }
    
    public String proxyImageRequest(String imageUrl, String outputFormat) {
        try {
            log.info("Proxying image request to: {} with format: {}", imageUrl, outputFormat);
            
            byte[] imageData = downloadImage(imageUrl);
            
            // Simulate format conversion based on output format
            String result = String.format("Image proxied and converted to %s format. Original size: %d bytes", 
                outputFormat, imageData.length);
                
            return result;
            
        } catch (Exception e) {
            log.error("Image proxy failed for URL: {} - {}", imageUrl, e.getMessage());
            throw new RuntimeException("Image proxy failed: " + e.getMessage());
        }
    }
}