package base_webSocket_demo.service;

import base_webSocket_demo.dto.UserDTO;
import base_webSocket_demo.dto.request.RegisterRequest;
import base_webSocket_demo.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void createUser(RegisterRequest request);

    User findOrCreateUser(String email, String name);

    UserDTO convertToDto(User user);
}