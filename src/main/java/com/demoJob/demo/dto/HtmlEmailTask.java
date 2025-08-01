package com.demoJob.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class HtmlEmailTask {
    private String to;
    private String subject;
    private String template;
    private Map<String, Object> model;
    private int retryCount;
}