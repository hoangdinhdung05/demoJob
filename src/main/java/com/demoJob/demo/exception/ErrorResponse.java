package com.demoJob.demo.exception;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class ErrorResponse {
    private Date timestamp;
    private int status;
    private List<String> message;
}
