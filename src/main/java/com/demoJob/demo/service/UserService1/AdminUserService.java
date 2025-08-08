package com.demoJob.demo.service.UserService1;

import com.demoJob.demo.dto.request.User.Admin.UpdateUserRolesRequest;
import com.demoJob.demo.dto.request.User.Admin.UserRequest;
import com.demoJob.demo.dto.request.User.Admin.UserUpdateRequest;
import com.demoJob.demo.dto.response.User.AdminUserResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.util.UserStatus;

import java.util.Set;

public interface AdminUserService {

    /**
     * Tạo người dùng mới
     * @param request Admin nhập thông tin người dùng
     * @return AdminUserResponse chứa thông tin người dùng đã tạo
     */
    AdminUserResponse adminCreateUser(UserRequest request);

    /**
     * Lấy thông tin người dùng theo ID
     * @param userId ID của người dùng
     * @return AdminUserResponse chứa thông tin người dùng
     */
    AdminUserResponse adminGetUserByUserId(Long userId);

    /**
     * Cập nhật thông tin người dùng
     * @param userId ID của người dùng cần cập nhật
     * @param request Thông tin cập nhật
     * @return AdminUserResponse chứa thông tin người dùng đã cập nhật
     */
    AdminUserResponse adminUpdateUser(Long userId, UserUpdateRequest request);

    /**
     * Cập nhật vai trò của người dùng
     * @param userId ID của người dùng cần cập nhật vai trò
     * @param request Chứa các vai trò mới
     * @return AdminUserResponse chứa thông tin người dùng đã cập nhật vai trò
     */
    AdminUserResponse adminUpdateUserRoles(Long userId, UpdateUserRolesRequest request);


    /**
     * Xóa người dùng theo ID
     * @param userId ID của người dùng cần xóa
     */
    void adminDeleteUser(Long userId);

    /**
     * Xóa mềm người dùng theo ID
     * @param userId ID của người dùng cần xóa mềm
     */
    void adminSoftDeleteUser(Long userId, UserStatus status);

    /**
     * Lấy danh sách người dùng với phân trang
     * @param page Số trang
     * @param size Kích thước trang
     * @return PageResponse chứa danh sách người dùng
     */
    PageResponse<?> adminGetListUser(int page, int size);

}
