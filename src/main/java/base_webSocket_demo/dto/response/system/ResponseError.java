package base_webSocket_demo.dto.response.system;

public class ResponseError extends ResponseData {
    public ResponseError(int status, String message) {
        super(status, message);
    }
}
