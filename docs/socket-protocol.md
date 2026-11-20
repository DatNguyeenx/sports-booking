# Giao thức Socket SportBooking/1

TCP mặc định 127.0.0.1:9090. UTF-8, JSON một dòng, kết thúc LF. Một connection có thể gửi nhiều request tuần tự. Mỗi request nhận một JSON response. Không có broadcast hoặc shared mutable response. Dữ liệu được đọc qua SocketGateway (Spring service, transaction read-only) và JPA repositories.

| Lệnh | Trường bắt buộc | Response |
|---|---|---|
| LIST_COURTS | command | ok, courts[]: id/name/type/price/active |
| CHECK_AVAILABLE | command, courtId số nguyên, date ISO yyyy-MM-dd | ok, courtId, date, slots[]: id/start/end/available |
| SERVER_INFO | command | ok, protocol, commands[] |

```json
{"command":"CHECK_AVAILABLE","courtId":1,"date":"2026-11-01"}
```

Ngày minh họa cần đổi thành tương lai. available=false nếu sân/khung tắt, khung đã qua hoặc đã có reservation. Đây là ảnh chụp trạng thái tại thời điểm đọc, không phải bảo đảm giữ chỗ. Web kiểm tra và khóa lại khi khách xác nhận.

Lỗi JSON, ngày, ID hoặc dữ liệu: `{"ok":false,"error":"INVALID_REQUEST"}`. Lệnh không hỗ trợ: UNKNOWN_COMMAND. Dòng >4096 ký tự, timeout 10 giây, queue đầy: đóng kết nối. Server có 8 worker cố định, queue 32, theo dõi sockets để đóng khi ứng dụng dừng. Không ghi request/password vào log. Client tự đóng stream và socket bằng try-with-resources; connection timeout 5 giây, read timeout 10 giây.

Không triển khai BOOKING_STATUS công khai vì mã đơn không phải thông tin xác thực. Lịch cá nhân chỉ truy cập qua Web với session và kiểm tra chủ sở hữu. Ba lệnh public đáp ứng phần Networking mà không làm lộ thông tin khách hàng.

Kiểm thử: `SqlServerIntegrationTest.threeSocketClientsAndAbnormalDisconnect` mở 3 connection trước khi nhả CountDownLatch; cả ba phải nhận tên sân test thực có trong SQL. Sau RST của client thứ tư, connection mới vẫn nhận SERVER_INFO và xử lý JSON sai. Log nằm trong `docs/test-results/clean-package.log`.
