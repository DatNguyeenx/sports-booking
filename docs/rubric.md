# Đối chiếu rubric 10 điểm

Nguồn: Khung_cham_diem_do_an_Cong_nghe_Java.pdf trong BaiGiang, bản trích source-materials.txt. Điểm dưới đây là **điểm tối đa**, không phải điểm tự chấm.

| Mã | Điểm tối đa | Minh chứng / trạng thái |
|---|---:|---|
| 1.1 Bài toán và phạm vi | 0,4 | Báo cáo chương 1; requirements.md |
| 1.2 Chức năng và phi chức năng | 0,4 | requirements.md: 21 nhóm chức năng; diagrams/use-case.mmd |
| 1.3 CSDL, khóa, ràng buộc | 0,4 | database.md; diagrams/erd.mmd; sql/01_create_database.sql |
| 1.4 Kiến trúc, luồng request | 0,4 | diagrams/architecture.mmd và booking-sequence.mmd |
| 1.5 Thiết kế đồng thời, mạng | 0,4 | diagrams/concurrency.mmd, socket.mmd; socket-protocol.md |
| 2.1 Package và cấu hình | 0,3 | controller/service/repository/entity/dto/config; application*.properties |
| 2.2 Controller, Model, validation | 0,4 | WebController, AdminController, DTO @Valid, BindingResult; HTTP tests |
| 2.3 Service, transaction | 0,4 | BookingService, CatalogService, AccountService; rollback test |
| 2.4 Đủ chức năng | 0,3 | Ba vai trò; CRUD, booking, trạng thái, payment, thống kê |
| 2.5 Ổn định và lỗi | 0,2 | Trang 403/404/500, exception handler, kiểm thử thực tế |
| 3.1 Fragment dùng chung | 0,3 | templates/fragments.html: head/header/sidebar/footer/alerts |
| 3.2 Bootstrap, responsive | 0,3 | screenshots/dashboard.png, mobile-dashboard.png, mobile-courts.png |
| 3.3 Form và phản hồi | 0,4 | Đăng ký/hồ sơ/sân có field errors; flash success, confirm xóa. Lỗi nghiệp vụ loại/khung giờ còn ở trang lỗi chung — chưa hoàn thiện mọi lỗi cạnh ô nhập |
| 3.4 Search, pagination, sort | 0,4 | Danh sách sân đầy đủ; đơn có tìm/lọc/phân trang. Danh mục nhỏ chưa có bộ search/sort riêng |
| 3.5 Tiếng Việt, menu role | 0,2 | Thymeleaf Security; ảnh trình duyệt thật; NVARCHAR, UTF-8 |
| 4.1 Kết nối và SQL | 0,2 | TCP 1433 đã xác minh; SQL 01–03 seed danh mục + 22 users, 32 đơn, 20 thanh toán; DemoData tạo tài khoản demo; log startup |
| 4.2 Entity mapping | 0,4 | 9 entity; Booking.details OneToMany, ManyToOne; schema validate |
| 4.3 Repository, ≥2 custom queries | 0,3 | 9 JpaRepository; Court.search, Booking.search, Payment.revenue/daily |
| 4.4 CRUD ≥3 bảng | 0,4 | Sân, loại sân, khung giờ, tài khoản; tests CRUD/FK |
| 4.5 Nhất quán | 0,3 | Transaction, giá snapshot, UNIQUE; xóa có FK guard; sql-audit.txt |
| 5.1 Đa luồng nghiệp vụ | 0,4 | Test 10 worker, CountDownLatch, log thread |
| 5.2 Đồng bộ đúng | 0,4 | Pessimistic lock + UNIQUE; 1 thành công / 9 conflict, SQL 1 đơn / 1 reservation |
| 5.3 Client riêng, dữ liệu DB | 0,5 | socket-client JAR; LIST_COURTS/CHECK_AVAILABLE đọc SQL |
| 5.4 ≥3 client, lỗi kết nối | 0,3 | Integration test ba client, RST, invalid JSON; standalone-client-smoke.json: ba tiến trình Java |
| 6.1 Báo cáo | 0,4 | PDF 28 trang, 5 chương, hình và bảng đánh số |
| 6.2 Thuyết trình 15 phút | 0,4 | PPTX 16 slide, speaker notes, demo/demo-script.md; **chưa có buổi trình bày thật** |
| 6.3 Vấn đáp | 0,4 | Câu hỏi trong task-assignment; **nhóm phải tự học và trả lời** |
| 6.4 Phân công và Git | 0,4 | Phân công đề xuất 20% mỗi người; **chưa có chữ ký, remote hoặc lịch sử commit thực của từng người** |
| **Tổng** | **10,0** | Giảng viên đánh giá chất lượng từng mục; không tự suy ra 10/10 |

Trước khi nộp: nhóm rà soát nội dung, xác nhận phân công, tạo remote, thực hiện commit thật cho các lần chỉnh sửa, luyện demo và vấn đáp. Không tạo lịch sử lùi ngày hoặc ký thay người khác. Video 5–8 phút là mục khuyến khích, đã có kịch bản nhưng chưa quay.
