package base_webSocket_demo.controller;

import base_webSocket_demo.dto.request.Admin.Users.UserRequest;
import base_webSocket_demo.dto.request.Admin.Users.UserUpdateRequest;
import base_webSocket_demo.dto.response.Admin.AdminCreateUserResponse;
import base_webSocket_demo.dto.response.system.PageResponse;
import base_webSocket_demo.dto.response.system.ResponseData;
import base_webSocket_demo.dto.response.system.ResponseError;
import base_webSocket_demo.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/admin/create")
    public ResponseData<?> adminCreateUser(@RequestBody @Valid UserRequest request) {

        log.info("Api admin create user");

        try {
            AdminCreateUserResponse userResponse = userService.adminCreateUser(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Api admin create user successfully", userResponse);
        } catch (Exception e) {
            log.error("Api admin create user error={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Api admin create user fail");
        }
    }

    @GetMapping("/admin/{userId}")
    public ResponseData<?> adminGetUserByUserId(@PathVariable @Min(1) Long userId) {

        log.info("Api admin get user by user id={}", userId);

        try {

            AdminCreateUserResponse response = userService.adminGetUserByUserId(userId);
            return new ResponseData<>(HttpStatus.OK.value(), "Api admin get user id successfully", response);

        } catch (Exception e) {
            log.error("Api admin get user by user id error={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Api admin get user by user id fail");
        }
    }

    @PatchMapping("/admin/{userId}")
    public ResponseData<?> adminUpdateUserByUserId(@PathVariable @Min(1) Long userId, @RequestBody UserUpdateRequest request) {

        log.info("Api admin update user by user id={}", userId);

        try {

            AdminCreateUserResponse response = userService.adminUpdateUser(userId, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Api admin update user by user id successfully", response);

        } catch (Exception e) {
            log.error("Api admin update user by user id error={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Api admin update user by user id fail");
        }
    }

    @DeleteMapping("/admin/{userId}")
    public ResponseData<?> adminDeleteUserByUserId(@PathVariable @Min(1) Long userId) {

        log.info("Api admin delete user by user id={}", userId);

        try {

            userService.adminDeleteUser(userId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Api admin delete user by user id successfully");

        } catch (Exception e) {
            log.error("Api admin update user by user id error={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Api admin update user by user id fail");
        }
    }

    @GetMapping("/admin/getAll")
    public ResponseData<?> adminGetListUser(@RequestParam int page, @RequestParam int size) {

        log.info("Api admin get list user");

        try {

            PageResponse<?> response = userService.adminGetListUser(page, size);
            return new ResponseData<>(HttpStatus.OK.value(), "Api admin get list user successfully", response);

        } catch (Exception e) {
            log.error("Api admin get list user");
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Api admin get list user fail");
        }
    }

}
