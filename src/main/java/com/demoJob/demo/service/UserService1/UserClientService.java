package com.demoJob.demo.service.UserService1;

import com.demoJob.demo.dto.request.User.Client.ChangePasswordRequest;
import com.demoJob.demo.dto.request.User.Client.UserUpdateRequest;
import com.demoJob.demo.dto.response.User.UserBasicInfoResponse;
import com.demoJob.demo.dto.response.User.UserFullInfoResponse;
import com.demoJob.demo.dto.response.User.UserUpdateResponse;

public interface UserClientService {

    /**
     * User lấy thông tin cơ bản của chính mình.
     *
     * @return thông tin cơ bản của người dùng
     */
    UserBasicInfoResponse getMyBasicInfo();

    /**
     * User lấy thông tin đầy đủ của chính mình.
     *
     * @return thông tin đầy đủ của người dùng
     */
    UserFullInfoResponse getMyFullInfo();

    /**
     * User cập nhật thông tin cá nhân của chính mình.
     *
     * @param request thông tin cập nhật
     * @return thông tin cập nhật sau khi thực hiện
     */
    UserUpdateResponse updateMyInfo(UserUpdateRequest request);

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
}

