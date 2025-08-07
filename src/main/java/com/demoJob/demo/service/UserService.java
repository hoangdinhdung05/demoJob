package com.demoJob.demo.service;

import com.demoJob.demo.dto.UserDTO;
import com.demoJob.demo.dto.request.Admin.Users.UserRequest;
import com.demoJob.demo.dto.request.Admin.Users.UserUpdateRequest;
import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.dto.response.Admin.User.AdminCreateUserResponse;
import com.demoJob.demo.dto.response.RegisterResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.util.UserStatus;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User findOrCreateUser(String email, String name);

    RegisterResponse createUser(RegisterRequest request);

    AdminCreateUserResponse adminCreateUser(UserRequest request);

    AdminCreateUserResponse adminUpdateUser(long userId, UserUpdateRequest request);

    AdminCreateUserResponse adminGetUserByUserId(long userId);

    PageResponse<?> adminGetListUser(int page, int size);

    void adminDeleteUser(long userId);

    AdminCreateUserResponse changeUserStatus(long userId, UserStatus userStatus);

}