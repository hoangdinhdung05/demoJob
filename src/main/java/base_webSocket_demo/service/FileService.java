package base_webSocket_demo.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    void createDirectory(String folder);

    String store(MultipartFile file, String folder);

    long getFileLength(String fileName, String folder);

    InputStreamResource getResource(String fileName, String folder);

}
