package base_webSocket_demo.controller;

import base_webSocket_demo.dto.response.system.ResponseData;
import base_webSocket_demo.dto.response.system.ResponseError;
import base_webSocket_demo.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseData<?> uploadFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam(defaultValue = "upload") String folder) {
        try {
            if (file == null || file.isEmpty()) {
                return new ResponseError(HttpStatus.BAD_REQUEST.value(), "File is empty.");
            }

            String originalName = file.getOriginalFilename();
            List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");

            boolean validExt = allowedExtensions.stream()
                    .anyMatch(ext -> originalName.toLowerCase().endsWith(ext));

            if (!validExt) {
                return new ResponseError(HttpStatus.BAD_REQUEST.value(),
                        "Invalid file extension. Only allowed: " + allowedExtensions);
            }

            fileService.createDirectory(folder);
            String storedFileName = fileService.store(file, folder);

            String fileUrl = "/" + folder + "/" + storedFileName;

            return new ResponseData<>(HttpStatus.OK.value(), "Upload thành công",
                    new UploadFileResponse(fileUrl, Instant.now()));

        } catch (Exception e) {
            log.error("Upload failed", e);
            return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Upload thất bại");
        }
    }

    @GetMapping
    public ResponseData<?> downloadFile(@RequestParam("fileName") String fileName,
                                          @RequestParam(defaultValue = "upload") String folder) {
        try {
            long fileLength = fileService.getFileLength(fileName, folder);

            if (fileLength == 0) {
                return new ResponseError(HttpStatus.NOT_FOUND.value(), "File không tồn tại.");
            }

            InputStreamResource resource = fileService.getResource(fileName, folder);
            if (resource == null) {
                return new ResponseError(HttpStatus.NOT_FOUND.value(), "Không thể đọc file.");
            }

            return new ResponseData<>(HttpStatus.OK.value(), "Download file successfully");

        } catch (Exception e) {
            log.error("Download failed", e);
            return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Tải file thất bại");
        }
    }

    record UploadFileResponse(String filePath, Instant uploadedAt) {}
}
