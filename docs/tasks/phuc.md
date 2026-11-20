# Vương Hoàng Phúc – Lập trình mạng, giao diện chung, đóng gói

Nhánh: `phuc/mang-giao-dien`. Mã tham khảo: repo SportBooking-Team-Linh/SportBooking, tag `v1-tham-khao`.

## Module

Socket server nhúng trong ứng dụng (TCP, JSON từng dòng, nhiều client cùng lúc) và chương trình client Java tách biệt; hệ thống CSS / JS dùng chung cho mọi trang; script chạy và kiểm thử trên Windows.

## File cũ → file mới

| File cũ | File mới trong v2 |
|---|---|
| `networking/SocketServer.java` – `SocketServer` (constructor), `port`, `start`, `accept`, `serve`, `readBounded`, `stop`, `isRunning` | giữ tên |
| `networking/SocketGateway.java` – `handle` | giữ tên |
| `socket-client/pom.xml`, `socket-client/src/main/java/com/sportbooking/client/SocketClient.java` – `main` | giữ đường dẫn |
| `docs/socket-protocol.md` | đã có trong `main`, cập nhật khi đổi giao thức |
| `static/css/variables.css`, `base.css`, `components.css`, `responsive.css`, `fonts.css` (+ font Be Vietnam Pro trong `static/css/fonts/`) | giữ đường dẫn |
| `static/js/app.js` | giữ đường dẫn |
| `run-local.ps1`, `test-all.ps1` | `scripts/run-local.ps1`, `scripts/test-all.ps1` |
| `tools/setup-local.ps1`, `tools/enable-tcp-admin.ps1` | `scripts/setup-local.ps1`, `scripts/enable-tcp-admin.ps1` |

### Phương thức lấy từ file dùng chung

| File cũ | Phương thức | Lớp mới |
|---|---|---|
| `SqlServerIntegrationTest.java` | phần khai báo chung (field, `@Autowired`, `@SpringBootTest`), `setup()`, `cleanup()` | `src/test/.../IntegrationTestBase.java` (lớp cha cho test tích hợp của cả nhóm) |
| `SqlServerIntegrationTest.java` | `threeSocketClientsAndAbnormalDisconnect` | `src/test/.../SocketIntegrationTest.java` |

## Việc cải tiến bắt buộc

- **Client có menu chọn lệnh:** người dùng chọn `LIST_COURTS`, `CHECK_AVAILABLE` (nhập id sân, ngày), `SERVER_INFO` thay vì gõ JSON.
- **Chế độ mở 3 client cùng lúc** (ví dụ `java -jar socket-client.jar --demo 3`) để demo rubric 5.4, in tên luồng phía server.
- **Kiểm tra hiển thị trên điện thoại** (độ rộng 375px): không cuộn ngang, menu thu gọn hoạt động, chụp ảnh cho báo cáo.
- Đường dẫn trong script sau khi chuyển vào `scripts/` phải chạy đúng từ gốc repo.

## Hạng mục rubric

1.5 (mô hình client/server, giao thức), 3.2, 5.3, 5.4.

## Phần báo cáo

- Chương 2 – Thiết kế phần mạng: mô hình client/server, giao thức (lệnh, định dạng JSON, mã lỗi, giới hạn), dùng `docs/diagrams/socket.svg`.
- Chương 3 – Mã tiêu biểu `SocketServer.accept` / `serve`, client; hệ thống CSS dùng chung; ảnh giao diện trên điện thoại.
- Chương 4 – Kiểm thử 3 client cùng lúc, client ngắt đột ngột, JSON sai, dòng quá dài.

## Câu hỏi vấn đáp cần chuẩn bị

1. Vì sao server dùng pool 8 luồng và hàng đợi 32 thay vì tạo một luồng cho mỗi kết nối?
2. Khi hàng đợi đầy hoặc client im lặng quá 10 giây thì server làm gì?
3. Client ngắt đột ngột thì server phát hiện ra sao và giải phóng tài nguyên thế nào?
4. Vì sao socket chỉ cung cấp dữ liệu công khai, không có lệnh xem đơn của khách?
5. `SmartLifecycle` giúp server socket khởi động và dừng cùng ứng dụng Spring như thế nào?
6. CSS được chia theo những file nào, biến (`variables.css`) giúp giao diện nhất quán ra sao?

## Lệnh kiểm thử

```powershell
. ./.env.local.ps1
$env:RUN_SQLSERVER_TESTS='true'; ./mvnw.cmd -B '-Dtest=SocketIntegrationTest' test
./mvnw.cmd -B -f socket-client/pom.xml clean package
./scripts/test-all.ps1
```

## Checklist

- [ ] `networking/SocketServer.java`
- [ ] `networking/SocketGateway.java`
- [ ] `socket-client/pom.xml`
- [ ] `socket-client/.../SocketClient.java` (menu + chế độ 3 client)
- [ ] `docs/socket-protocol.md`
- [ ] `static/css/variables.css`
- [ ] `static/css/base.css`
- [ ] `static/css/components.css`
- [ ] `static/css/responsive.css`
- [ ] `static/css/fonts.css` + font Be Vietnam Pro
- [ ] `static/js/app.js`
- [ ] `scripts/run-local.ps1`
- [ ] `scripts/test-all.ps1`
- [ ] `scripts/setup-local.ps1`
- [ ] `scripts/enable-tcp-admin.ps1`
- [ ] `IntegrationTestBase.java`
- [ ] `SocketIntegrationTest.java`
- [ ] Ảnh chụp giao diện trên điện thoại
