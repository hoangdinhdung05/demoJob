package com.demoJob.demo.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    void createDirectory(String folder);

    String store(MultipartFile file, String folder);

    long getFileLength(String fileName, String folder);

    InputStreamResource getResource(String fileName, String folder);

}
