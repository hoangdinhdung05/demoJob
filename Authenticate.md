# 🔐 AuthController - Authentication API Documentation

`AuthController` quản lý các luồng xác thực người dùng trong hệ thống `demoJob`. Các chức năng chính bao gồm: đăng nhập, đăng ký, xác minh email, làm mới token, đăng xuất và khôi phục mật khẩu.

| Chức năng      | Mô tả                                                  |
| -------------- | ------------------------------------------------------ |
| Đăng ký        | Tạo tài khoản người dùng và gửi OTP xác minh email     |
| Xác minh email | Xác thực mã OTP được gửi đến email người dùng          |
| Đăng nhập      | Xác thực tài khoản và cấp token                        |
| Làm mới token  | Dùng refresh token để lấy access token mới             |
| Đăng xuất      | Thu hồi refresh token, thêm access token vào blacklist |
| Gửi lại OTP    | Gửi lại mã OTP khi hết hạn hoặc chưa nhận              |
| Quên mật khẩu  | Gửi OTP xác minh + thay đổi mật khẩu mới               |

---

## 📌 Base URL

```
/api/auth
```

---

## ⚙️ Authenticate Flow Diagram

### 1. Luồng Đăng ký (Register) + Xác minh email

```
Client: nhập email, password, username (Register)
    |
    |-- POST /api/auth/register --> [AuthController.register()]
    |
    |-- userService.createUser() (status: emailVerified=false)
    |-- otpService.sendOtp(type=VERIFY_EMAIL)
    |
<-- 201 Created + "Vui lòng xác minh email"

Client: (chưa nhận OTP) -> Gửi yêu cầu resend
    |
    |-- POST /api/auth/resend-otp { email, type: VERIFY_EMAIL }
    |
    |-- otpService.sendOtp(type=VERIFY_EMAIL)
    |
<-- 200 OK + "OTP đã được gửi lại"

Client: nhập email + OTP (Verify Email)
    |
    |-- POST /api/auth/verify-email?email=...&code=... --> [AuthController.verifyEmailOtp()]
    |
    |-- otpService.verifyOtp()
    |-- user.setEmailVerified(true)
    |
<-- 200 OK + "Xác minh thành công"
```

### 2. Luồng Đăng nhập (Login)

```
Client: nhập username + password
  |
  |-- POST /api/auth/login --> [AuthController.login()]
        |
        |-- authenticationManager.authenticate()
        |-- checkEmailVerified()
        |-- jwtProvider.generateAccessToken()
        |-- refreshTokenService.createRefreshToken()
        |
  <-- 200 OK + { access_token, refresh_token, userInfo }
```

### 3. Quên mật khẩu (Forgot Password)

```
Client: nhập email để lấy OTP
  |
  |-- POST /api/auth/resend-otp {"email":..., "type":"RESET_PASSWORD"}
        |
        |-- otpService.sendOtp(type=RESET_PASSWORD)
  <-- 200 OK + "OTP đã được gửi"

Client: nhập email + otp + mật khẩu mới
  |
  |-- POST /api/auth/forgot-password/reset --> [AuthController.resetPassword()]
        |
        |-- otpService.verifyOtp()
        |-- user.setPassword(newPassword)
        |
  <-- 200 OK + "Mật khẩu đã được cập nhật"
```

### 4. Refresh Token

```
Client: Gửi refresh_token
  |
  |-- POST /api/auth/refresh-token
        |
        |-- refreshTokenService.findByToken()
        |-- jwtProvider.generateAccessToken()
        |
  <-- 200 OK + new access_token
```

### 5. Đăng xuất (Logout)

```
Client: Gửi Authorization: Bearer <access_token>
  |
  |-- POST /api/auth/logout
        |
        |-- revoke refresh token in DB
        |-- blacklist access token
        |
  <-- 200 OK + "Đăng xuất thành công"
```

---

## ✅ API Chi tiết

### 1. `POST /login`

**Request:**

```json
{
  "username": "example@gmail.com",
  "password": "123456"
}
```

**Success Response (200):**

```json
{
  "status": 200,
  "message": "Đăng nhập thành công",
  "data": {
    "accessToken": "...",
    "refreshToken": "...",
    "tokenType": "ACCESS_TOKEN",
    "userId": ...,
    "username": "...",
    "roles": [
      "..."
    ]
  }
}
```

**Error (401):** Tài khoản hoặc mật khẩu không đúng
```json
{
    "timestamp": "2025-08-03T05:54:57.467+00:00",
    "status": 401,
    "path": "/api/auth/login",
    "error": "BAD_CREDENTIALS",
    "message": "Bad credentials"
}
```

**Conflict (409):** Vui lòng xác minh email
```json
{
    "timestamp": "2025-08-03T05:57:44.738+00:00",
    "status": 409,
    "path": "/api/auth/login",
    "error": "INVALID_DATA",
    "message": "Vui lòng xác minh email trước khi đăng nhập."
}
```

---

### 2. `POST /register`

**Request:**

```json
{
    "firstName": "...",
    "lastName": "...",
    "username": "...",
    "email": "exmaple@gmail.com",
    "password": "..."
}
```

**Success (200):** Đăng ký thành công, vui lòng xác minh email
**Error (400):** Email hoặc username đã được sử dụng
```json
{
  "timestamp": "2025-08-03T06:17:34.973+00:00",
  "status": 400,
  "path": "/api/auth/register",
  "error": "DUPLICATE_RESOURCE",
  "message": "Email đã được sử dụng"
}
```

---

### 3. `POST /resend-otp`

**Request:**

```json
{
  "email": "example@gmail.com",
  "type": "VERIFY_EMAIL"
}
```

**Success (200):** OTP đã được gửi lại
**Error (400):** OTP đang tồn tại hoặc yêu cầu quá nhiều
```json
{
    "timestamp": "2025-08-03T06:33:51.850+00:00",
    "status": 400,
    "path": "/api/auth/resend-otp",
    "error": "INVALID_OTP",
    "message": "Bạn đã gửi OTP quá nhiều lần. Vui lòng thử lại sau."
}
```

---

### 4. `POST /verify-email?email=...&code=...`

**Success (200):** Xác minh email thành công
**Error (409):** OTP không hợp lệ hoặc hết hạn
```json
{
    "timestamp": "2025-08-03T06:26:44.192+00:00",
    "status": 409,
    "path": "/api/auth/verify-email",
    "error": "INVALID_DATA",
    "message": "OTP not found"
}

```

---

### 5. `POST /refresh-token`

**Request:**

```json
{
  "refreshToken": "..."
}
```

**Success (200):** Làm mới access token thành công
```json
{
    "status": 200,
    "message": "Làm mới access token thành công",
    "data": {
        "accessToken": "...",
        "refreshToken": "..."
    }
}
```

**Error (401):** Refresh token không hợp lệ
```json
{
    "timestamp": "2025-08-03T06:06:22.970+00:00",
    "status": 401,
    "path": "/api/auth/refresh-token",
    "error": "BLACK_LIST_TOKEN",
    "message": "Invalid refresh token"
}
```

---

### 6. `POST /logout`

**Header:** `Authorization: Bearer <accessToken>`
**Success (200):** Đăng xuất thành công
```json
{
    "status": 200,
    "message": "Logout successful"
}
```
**Error (401):** Token không hợp lệ hoặc đã hết hạn

---

### 7. `POST /forgot-password/reset`

**Request:**

```json
{
  "email": "example@gmail.com",
  "code": "123456",
  "newPassword": "NewPassword123"
}
```

**Success (200):** Mật khẩu đã được thay đổi thành công
**Error (400):** OTP không đúng hoặc đã hết hạn
```json
{
    "timestamp": "2025-08-03T06:36:03.480+00:00",
    "status": 409,
    "path": "/api/auth/forgot-password/reset",
    "error": "INVALID_DATA",
    "message": "OTP not found"
}
```

---

## 📘 Notes

* Tất cả response đều chuẩn hóa theo `ResponseData<T>`:

```json
{
  "status": 200,
  "message": "Nội dung thông báo",
  "data": { ... }
}
```

* Các lỗi đều được xử lý qua `GlobalExceptionHandler`
* OTP được lưu trong Redis kèm DB tracking (đã dùng/chưa, hạn...)
