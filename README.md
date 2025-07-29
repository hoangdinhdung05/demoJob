
# 💼 demoJob - Job Platform Backend

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![MySQL](https://img.shields.io/badge/Database-MySQL-orange)
![OAuth2](https://img.shields.io/badge/Auth-Google%20OAuth2-red)
![License](https://img.shields.io/badge/License-MIT-blue)

---

## 📌 Mô tả dự án

`demoJob` là một hệ thống web tìm việc làm **IT-focused**, phát triển với mục tiêu học tập và xây dựng một nền tảng tuyển dụng chuyên nghiệp, gồm:

- Quản lý người dùng, phân quyền chi tiết (RBAC)
- Tìm kiếm, lọc, sắp xếp việc làm
- Hệ thống hồ sơ ứng viên (CV / Resume)
- Giao tiếp giữa ứng viên và công ty
- Xác thực qua Google OAuth2
- OTP, OTT, JWT, Refresh Token, Blacklist Token

> **Tác giả:** [Hoàng Đình Dũng](https://www.facebook.com/hoangdinhdung2208)  
> **GitHub:** [@hoangdinhdung05](https://github.com/hoangdinhdung05)  
> **Sinh viên năm 3** – Trường Đại học Công nghệ GTVT  
> **Backend:** Java + Spring Boot | **Frontend:** HTML, CSS, JS (React đang phát triển)

---

## 🧠 Tính năng chính

### 🧑‍💼 Xác thực & Phân quyền
- ✅ Đăng ký / Đăng nhập qua Email + Password
- ✅ Đăng nhập với Google (OAuth2)
- ✅ Xác minh Email qua link (OTT)
- ✅ Đăng nhập 2 lớp OTP (One-Time Password)
- ✅ Quản lý **Access Token**, **Refresh Token**, **Token Blacklist**
- ✅ RBAC với Role, Permission (Bảng trung gian chuẩn chỉnh)

### 📄 Quản lý hồ sơ
- CRUD **Hồ sơ ứng viên (Resume)**
- Lưu CV theo dạng draft hoặc apply

### 🏢 Quản lý công ty và việc làm
- CRUD công ty + thông tin chi tiết
- CRUD job + kỹ năng + yêu cầu
- Ứng viên **lưu việc làm**, **tìm kiếm**, **lọc theo kỹ năng, công ty, vị trí,...**

### 🔍 Tìm kiếm nâng cao
- Tìm kiếm theo **keyword**
- Filter theo **skill, location, company**
- Sắp xếp theo **ngày tạo, mức lương, deadline**

### 📊 Phân tích kỹ thuật
- Spring Boot + JWT + OAuth2 + OTP + Scheduler
- JPA với cấu trúc chuẩn hóa (`tbl_` prefix)
- Pagination, Sorting, Searching
- Logging, Exception Handling đầy đủ

---

## 🗃️ Sơ đồ Database

![Database ERD](https://github.com/hoangdinhdung05/demoJob/blob/main/ERD.png)

> Tổng cộng hơn **20 bảng**, được chuẩn hóa và liên kết chặt chẽ qua `user`, `job`, `company`, `resume`, `skill`,...

---

## 🔧 Công nghệ sử dụng

| Backend            | Frontend         | Khác             |
|--------------------|------------------|------------------|
| Java 17            | HTML/CSS/JS      | JWT, OTP, OTT    |
| Spring Boot 3.x    | React (WIP)      | OAuth2 Google    |
| Spring Security    |                  | Mail (Thymeleaf) |
| Spring Data JPA    |                  | Redis (dự kiến)  |
| MySQL              |                  | Docker (sắp có)  |

---

## 🚀 Cách chạy project

### ⚙️ Yêu cầu
- Java 17+
- MySQL
- IDE: IntelliJ / Eclipse / VSCode

### 📥 Clone & Build

```bash
git clone https://github.com/hoangdinhdung05/demoJob.git
cd demoJob
```

### 🛠 Cấu hình file `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo_job
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  mail:
    host: smtp.gmail.com
    username: your_email
    password: your_app_password

jwt:
  secret: your_secret
  expiration: 3600000

oauth2:
  google:
    client-id: your_google_client_id
    client-secret: your_google_client_secret
```

### ▶️ Chạy project

```bash
mvn spring-boot:run
```

---

## 🧪 Một số API tiêu biểu

| Method | Endpoint                         | Chức năng                     |
|--------|----------------------------------|-------------------------------|
| POST   | `/api/auth/login`                | Đăng nhập (JWT)               |
| POST   | `/api/auth/oauth2/google`        | Đăng nhập Google              |
| POST   | `/api/auth/send-otp`             | Gửi OTP                       |
| POST   | `/api/auth/verify-otp`           | Xác thực OTP                  |
| POST   | `/api/resumes`                   | Tạo Resume                    |
| GET    | `/api/jobs?keyword=java`         | Tìm kiếm việc làm             |
| GET    | `/api/jobs?sort=salary,desc`     | Sắp xếp theo lương            |
| POST   | `/api/jobs/save`                 | Lưu công việc yêu thích       |

---

## 📅 Lịch trình phát triển

| Mốc       | Nội dung                                           |
|-----------|----------------------------------------------------|
| Tháng 7   | Hoàn thiện backend, xác thực, quản lý dữ liệu      |
| Tháng 8   | Tích hợp frontend (React + Tailwind), REST APIs    |
| Tháng 9   | Docker hóa, CI/CD, bổ sung ElasticSearch, Redis    |

---

## 🧾 License

MIT © [Hoàng Đình Dũng](https://github.com/hoangdinhdung05)

---

> 📬 Mọi góp ý, hợp tác, kết nối vui lòng liên hệ:  
> [Facebook cá nhân](https://www.facebook.com/hoangdinhdung2208)  
> [GitHub](https://github.com/hoangdinhdung05)
