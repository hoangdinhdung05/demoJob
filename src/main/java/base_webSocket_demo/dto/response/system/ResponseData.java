package base_webSocket_demo.dto.response.system;

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
     * @param status
     * @param message
     * @param data
     */
    public ResponseData(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * Response data when API executes successfully or getting error. For PUT, PATCH, DELETE
     * @param status
     * @param message
     */
    public ResponseData(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
