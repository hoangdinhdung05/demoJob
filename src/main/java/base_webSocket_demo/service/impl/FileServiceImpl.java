package base_webSocket_demo.service.impl;

import base_webSocket_demo.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final String basePath = "src/main/resources/static";

    @Override
    public void createDirectory(String folder) {
        try {
            Path path = Paths.get(basePath, folder);
            Files.createDirectories(path);
            log.info("Created directory: {}", path.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to create directory {}", folder, e);
        }
    }

    @Override
    public String store(MultipartFile file, String folder) {
        try {
            // Tạo tên file duy nhất
            String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

            // Đường dẫn đầy đủ
            Path dirPath = Paths.get(basePath, folder);
            Files.createDirectories(dirPath); // đảm bảo folder tồn tại

            Path filePath = dirPath.resolve(finalName);

            // Lưu file
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return finalName;

        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new RuntimeException("Failed to store file");
        }
    }

    @Override
    public long getFileLength(String fileName, String folder) {
        Path filePath = Paths.get(basePath, folder, fileName);
        File file = filePath.toFile();

        return (file.exists() && file.isFile()) ? file.length() : 0;
    }

    @Override
    public InputStreamResource getResource(String fileName, String folder) {
        try {
            Path filePath = Paths.get(basePath, folder, fileName);
            File file = filePath.toFile();

            if (!file.exists() || !file.isFile()) {
                log.warn("File not found: {}", filePath);
                return null;
            }

            return new InputStreamResource(new FileInputStream(file));
        } catch (IOException e) {
            log.error("Error reading file", e);
            return null;
        }
    }
}
