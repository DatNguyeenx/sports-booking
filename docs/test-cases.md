# Kiểm thử thực tế — 26/10/2026

Windows, Oracle JDK 21.0.12.1, target Java 17, SQL Server thật SportBookingDB. RUN_SQLSERVER_TESTS=true. Không thay SQL Server bằng H2.

| Test | Nội dung đã xác minh | Kết quả |
|---|---|---|
| rejectsPast | Ngày quá khứ | PASS |
| rejectsGap | Khung không liên tiếp | PASS |
| rejectsDuplicates | Khung lặp | PASS |
| calculatesSnapshotPrice | Tính tổng giá | PASS |
| rejectsInactiveCourt | Sân khóa | PASS |
| realSqlServerAndSchema | SQL Server, schema, giờ JDBC bằng giờ SQL | PASS |
| registerAndLogin | Email chuẩn hóa, BCrypt, đăng nhập, email trùng | PASS |
| permissionsAndCsrf | URL vượt quyền, POST thiếu CSRF | PASS |
| renderPublicPages | Các trang công khai render | PASS |
| renderRolePages | Các trang vai trò/CRUD render | PASS |
| bookingLifecycleAndPriceSnapshot | Đặt, snapshot giá, hủy và giải phóng lịch | PASS |
| rejectionReleasesReservation | Từ chối giải phóng reservation | PASS |
| paymentIsIdempotentAndOwnershipProtected | Không thu hai lần; ownership | PASS |
| transactionRollsBackEvenAfterInserts | Lỗi sau insert rollback toàn bộ | PASS |
| pastInactiveAndDiscontinuous | Quy tắc thời gian/sân/khung trên SQL thật | PASS |
| courtCrudAndReferencedDeletion | CRUD sân và bảo vệ lịch sử | PASS |
| typeAndSlotCrudAndForeignKeys | CRUD loại/khung và FK | PASS |
| accountCrudAndSessionRevocation | CRUD tài khoản, đổi quyền/khóa thu hồi phiên | PASS |
| previewValidationAndTwoSlotBooking | Validation preview, hai khung 300.000đ | PASS |
| databaseUniqueConstraintAndFutureCheckinGuard | UNIQUE chống trùng; không check-in trước ngày | PASS |
| tenConcurrentBookingsExactlyOneWinner | 10 thread: 1 thành công, 9 conflict; SQL 1 đơn/1 reservation | PASS |
| threeSocketClientsAndAbnormalDisconnect | 3 client, RST, JSON sai, server tiếp tục phục vụ | PASS |

`test-results/acceptance-test.log` và `acceptance-package.log`: **22 tests, 0 failures, 0 errors, 0 skipped**. Client build ở `acceptance-client.log`. Module client không có unit test riêng; đã kiểm tra qua integration và chạy JAR.

`standalone-client-smoke.json`: ba tiến trình Java độc lập đồng thời gửi LIST_COURTS, SERVER_INFO, CHECK_AVAILABLE đến server 9090; cả ba exit 0 và response ok=true.

## Trình duyệt thật

Customer đăng nhập → Sân 01, 27/10/2026, 18:00–20:00 → preview 300.000đ → tạo đơn. Staff đăng nhập → xác nhận → ghi nhận tiền mặt → COMPLETED và nhật ký. Admin dashboard: 21 đơn, 20 sân, 23 tài khoản, doanh thu tháng 3.350.000đ.

Desktop 1366×900 và mobile 390×844: dashboard/danh sách không tràn ngang (scrollWidth=clientWidth=375). Ảnh thật trong screenshots/: home, availability, preview, booking, payment, dashboard, mobile-dashboard, mobile-courts.

## SQL và giới hạn

sql/05_verify.sql chỉ đọc. sql-audit.txt: 0 lịch trùng, 0 reservation mồ côi, 0 reservation giữ đơn hủy, 0 tổng tiền sai, 0 sân TEST còn sót. TestSQL và ClinicManagementDB vẫn tồn tại.

Chưa chạy JVM 17 riêng (đã kiểm tra bytecode major 61), chưa demo GUI IntelliJ, chưa kiểm thử JDBC Windows Authentication, production/TLS, load dài hoặc nhiều JVM tranh lịch. Check-in có kiểm tra guard trước ngày; chưa có bài test tự động riêng cho check-in thành công. Không suy ra mọi tình huống đều được bao phủ từ 22 test.
