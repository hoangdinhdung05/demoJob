package base_webSocket_demo.dto.request;

public record ResetPasswordRequest(String token, String newPassword) {}
