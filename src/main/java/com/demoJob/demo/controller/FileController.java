package com.demoJob.demo.controller;

import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.dto.response.system.ResponseError;
import com.demoJob.demo.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "FILE", description = "Quản lý tệp tin")
public class FileController {

    private final FileService fileService;

    /**
     * Upload file to server
     * @param file MultipartFile
     * @param folder Folder to save file
     * @return ResponseData with file path and upload time
     */
    @SuppressWarnings("rawtypes")
    @Operation(summary = "Upload file", description = "Upload file to server")
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

    /**
     * Download file from server
     * @param fileName Name of the file to download
     * @param folder Folder where the file is stored
     * @return ResponseData with status and message
     */
    @SuppressWarnings("rawtypes")
    @Operation(summary = "Download file", description = "Download file from server")
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
