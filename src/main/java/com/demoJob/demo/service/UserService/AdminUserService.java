package com.demoJob.demo.service.UserService;

import com.demoJob.demo.dto.request.User.Admin.AdminCreateUserRequest;
import com.demoJob.demo.dto.request.User.Admin.UserAdminUpdateRequest;
import com.demoJob.demo.dto.response.User.UserDetailResponse;
import com.demoJob.demo.dto.response.system.PageResponse;

public interface AdminUserService {

    /**
     * Admin tạo mới người dùng (khác với user tự đăng ký).
     *
     * @param request thông tin tạo mới
     */
    void createUser(AdminCreateUserRequest request);

    /**
     * Admin cập nhật thông tin người dùng theo ID.
     *
     * @param userId  ID của user
     * @param request thông tin cập nhật
     * @return thông tin sau khi cập nhật
     */
    UserDetailResponse updateUser(Long userId, UserAdminUpdateRequest request);

    /**
     * Admin lấy thông tin chi tiết 1 user.
     *
     * @param userId ID user
     * @return thông tin chi tiết
     */
    UserDetailResponse getUserById(Long userId);

    /**
     * Admin lấy danh sách user có phân trang + filter.
     *
     * @param page số trang bắt đầu từ 0
     * @param size kích thước trang
     * @return danh sách user
     */
    PageResponse<?> getAllUsers(int page, int size);

    /**
     * Admin xóa (hoặc deactivate) 1 user.
     *
     * @param userId ID user
     */
    void deleteUser(Long userId);
}