package com.demoJob.demo.service.UserService;

import com.demoJob.demo.dto.request.User.Client.UserAccountUpdateRequest;
import com.demoJob.demo.dto.request.User.Client.UserProfileUpdateRequest;
import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.dto.request.User.Client.ChangePasswordRequest;
import com.demoJob.demo.dto.response.User.UserInfoResponse;
import com.demoJob.demo.dto.response.User.UserDetailResponse;
import com.demoJob.demo.dto.response.User.UserUpdateResponse;
import com.demoJob.demo.entity.User;

public interface UserClientService {

    /**
     * Tạo người dùng mới từ thông tin đăng ký.
     * Sử dụng cho việc đăng ký người dùng mới.
     * @param request thông tin đăng ký người dùng
     * @return thông tin phản hồi đăng ký
     */
    User createUser(RegisterRequest request);


    /**
     * Tìm kiếm người dùng theo email, nếu không tìm thấy thì tạo mới.
     * Sử dụng cho việc đăng nhập qua mạng xã hội.
     * @param email email của người dùng
     * @param name tên của người dùng
     * @return thông tin người dùng
     */
    User findOrCreateUserBySocial(String email, String name);

    /**
     * User lấy thông tin cơ bản của chính mình.
     *
     * @return thông tin cơ bản của người dùng
     */
    UserInfoResponse getInfo();

    /**
     * User lấy thông tin đầy đủ của chính mình.
     *
     * @return thông tin đầy đủ của người dùng
     */
    UserDetailResponse getInfoDetails();

    /**
     * User cập nhật thông tin cá nhân của chính mình.
     *
     * @param request thông tin cập nhật
     * @return thông tin cập nhật sau khi thực hiện
     */
    UserUpdateResponse updateAccountInfo(UserAccountUpdateRequest request);

    /**
     * User cập nhật thông tin hồ sơ cá nhân của chính mình.
     *
     * @param request thông tin cập nhật
     * @return thông tin cập nhật sau khi thực hiện
     */
    UserUpdateResponse updateProfileInfo(UserProfileUpdateRequest request);

    /**
     * User xóa tài khoản của chính mình.
     * Tài khoản sẽ được đánh dấu là đã xóa (soft delete).
     */
    void softDeleteMyAccount();

    /**
     * User thay đổi mật khẩu của chính mình.
     *
     * @param request thông tin thay đổi mật khẩu
     */
    void changeMyPassword(ChangePasswordRequest request);

    UserInfoResponse getPublicInfo(Long userId);
}

