package com.demoJob.demo.dto.MailDTO;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
public class AttachmentDTO {

    @NotBlank(message = "Filename cannot be blank")
    private String filename;

    @NotBlank(message = "Content type cannot be blank")
    private String contentType;

    private byte[] content;
    private String filePath;

    @Builder.Default
    private boolean inline = false;

    private String contentId;
}