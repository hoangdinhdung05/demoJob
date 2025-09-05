package com.demoJob.demo.dto.response.system;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import java.io.Serializable;

@Getter
public class ResponseData<T> implements Serializable {

    private final int status;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /**
     * Response data for the API to retrieve data successfully. For GET, Post only
     * @param status mã trạng thái
     * @param message thông điệp
     * @param data dữ liệu trả về
     */
    public ResponseData(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * Response data when API executes successfully or getting error. For PUT, PATCH, DELETE
     * @param status mã trạng thái
     * @param message thông điệp
     */
    public ResponseData(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
