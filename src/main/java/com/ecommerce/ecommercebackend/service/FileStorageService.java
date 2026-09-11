package com.ecommerce.ecommercebackend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageService {

    private final String uploadDirectory = "uploads/";

    public String saveFile(MultipartFile file) throws IOException {

        // Create uploads folder if not present
        Path uploadPath = Paths.get(uploadDirectory);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }


        // Generate unique file name
        String fileName =
                System.currentTimeMillis()
                        + "_"
                        + file.getOriginalFilename();


        // File location
        Path filePath = uploadPath.resolve(fileName);


        // Save file
        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );


        // Return path stored in database
        return "/images/" + fileName;
    }
}