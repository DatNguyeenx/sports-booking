# Đặc tả yêu cầu

## Bối cảnh và phạm vi
Cơ sở có nhiều sân bóng đá, cầu lông, pickleball. Khách đặt lịch theo khung giờ, nhân viên xác nhận/thu tiền, quản trị viên quản lý danh mục. Một cơ sở, một múi giờ Asia/Ho_Chi_Minh, giá VND theo khung. Không xử lý thanh toán ngân hàng tự động.

| Mã | Vai trò | Chức năng và tiêu chí chấp nhận |
|---|---|---|
| F01 | Public | Đăng ký email duy nhất, mật khẩu 8–72 ký tự, BCrypt, role CUSTOMER |
| F02 | All | Form login, logout POST có CSRF, session bị thu hồi khi khóa/đổi role |
| F03 | All | Cập nhật tên và điện thoại của chính mình, validation cạnh trường |
| F04 | Public | Tìm sân theo tên, loại, giá tối đa, trạng thái; phân trang 9; sort tên/giá |
| F05 | Public | Xem chi tiết sân và lịch theo ngày, đánh dấu khung đã đặt |
| F06 | Customer | Chọn giờ liên tiếp và xem tổng tiền trước khi xác nhận |
| F07 | Customer | Đặt sân tương lai còn hoạt động, lưu snapshot giá, giữ chỗ atomically |
| F08 | Customer | Xem lịch sử/chi tiết của mình, chặn IDOR khi đổi ID trên URL |
| F09 | Customer | Hủy đơn chưa trả tiền trước giờ bắt đầu >2 giờ, giải phóng reservation |
| F10 | Staff | Xem tất cả đơn, tìm mã/tên khách, lọc ngày/trạng thái, phân trang |
| F11 | Staff | Xác nhận PENDING -> CONFIRMED hoặc từ chối -> REJECTED |
| F12 | Staff | Đặt hộ tài khoản khách tại quầy qua cùng BookingService |
| F13 | Staff | Check-in CONFIRMED trong đúng ngày đặt |
| F14 | Staff | Ghi nhận CASH/TRANSFER, unique payment, COMPLETED |
| F15 | Staff | Xem actor/thời gian/action trong nhật ký đơn |
| F16 | Admin | CRUD sân và giá; sân có lịch sử chỉ ngừng hoạt động |
| F17 | Admin | CRUD loại sân, FK chặn xóa loại đang có sân |
| F18 | Admin | Tạo/xem/khóa/phân quyền/xóa tài khoản chưa được tham chiếu |
| F19 | Admin | Quản lý khung giờ không chồng lấn, bảo toàn giờ lịch sử |
| F20 | Admin/Staff | Tổng quan số đơn/sân/user, doanh thu từ payments theo khoảng ngày/tháng |
| F21 | Public Socket | 3 lệnh dữ liệu công khai, phục vụ ≥3 client, timeout/đóng tài nguyên |

## Phi chức năng
Không lưu mật khẩu dạng plain text trong database. Không hardcode SQL password trong source. Mọi thay đổi Web qua POST có CSRF. Mỗi booking gồm các bước được transaction bao trọn. Tầng SQL có UNIQUE(court,date,slot) và FK. Giao diện responsive, static assets local. Java release 17, cấu hình qua environment, không phụ thuộc Maven toàn hệ thống. Không dùng create/drop tự động ở demo.

## Chuyển trạng thái
PENDING -> CONFIRMED/REJECTED/CANCELLED. CONFIRMED -> CHECKED_IN/CANCELLED/COMPLETED (thu tiền). CHECKED_IN -> COMPLETED. Các trạng thái kết thúc không cho quay lại. CANCELLED/REJECTED giải phóng lịch. COMPLETED giữ lịch và lịch sử thanh toán. Mỗi thao tác ghi BookingEvent. Hủy cần trước giờ chơi ít nhất 2 giờ; thông báo lỗi hiện tại dùng điều kiện >2 giờ, đúng mốc 2 giờ bị từ chối.

## Đồng thời
Tài nguyên chung là lịch sân. Khóa hàng Court trước mọi thao tác thay đổi booking, sau đó khóa Booking nếu cần. Khóa giữ đến commit/rollback. Mức khóa theo sân đơn giản và bảo vệ nhiều JVM, nhưng tuần tự hóa cả các ngày khác nhau của cùng sân. Có thể cải tiến thành khóa theo sân/ngày khi mở rộng tải.
