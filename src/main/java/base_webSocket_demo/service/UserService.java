package base_webSocket_demo.service;

import base_webSocket_demo.dto.UserDTO;
import base_webSocket_demo.dto.request.Admin.Users.UserRequest;
import base_webSocket_demo.dto.request.Admin.Users.UserUpdateRequest;
import base_webSocket_demo.dto.request.RegisterRequest;
import base_webSocket_demo.dto.response.Admin.AdminCreateUserResponse;
import base_webSocket_demo.dto.response.system.PageResponse;
import base_webSocket_demo.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User findOrCreateUser(String email, String name);

    UserDTO createUser(RegisterRequest request);

    AdminCreateUserResponse adminCreateUser(UserRequest request);

    AdminCreateUserResponse adminUpdateUser(long userId, UserUpdateRequest request);

    AdminCreateUserResponse adminGetUserByUserId(long userId);

    PageResponse<?> adminGetListUser(int page, int size);

    void adminDeleteUser(long userId);

}