# Đỗ Minh Hoàng – Danh mục sân và cơ sở dữ liệu

Nhánh: `hoang/danh-muc-csdl`. Mã tham khảo: repo SportBooking-Team-Linh/SportBooking, tag `v1-tham-khao`.

## Module

Script SQL Server (bảng, ràng buộc, chỉ mục, dữ liệu mẫu, kiểm tra), entity và repository của danh mục (loại sân, sân, khung giờ), nghiệp vụ thêm / sửa / xóa / ngừng hoạt động, trang danh sách sân công khai và các trang quản trị danh mục, dữ liệu demo.

## File cũ → file mới

| File cũ | File mới trong v2 |
|---|---|
| `sql/01_create_database.sql` + `sql/03_indexes_constraints.sql` | `sql/01_schema.sql` |
| `sql/02_sample_data.sql` + `sql/06_court_catalog.sql` | `sql/02_seed.sql` (UTF-8, chuỗi tiếng Việt dùng `N''`) |
| `sql/05_verify.sql` | `sql/03_verify.sql` |
| `entity/SportType.java`, `entity/Court.java`, `entity/TimeSlot.java` | giữ tên |
| `repository/SportTypeRepository.java` – `findByNameIgnoreCase` | giữ tên |
| `repository/CourtRepository.java` – `existsBySportTypeId`, `lockById`, `search` | giữ tên |
| `repository/TimeSlotRepository.java` – `findAllByOrderByStartTimeAsc` | giữ tên |
| `service/CatalogService.java` – `saveCourt`, `deleteCourt`, `saveType`, `deleteType`, `saveSlot`, `deleteSlot` | giữ tên |
| `dto/CourtForm.java` | giữ tên; thêm `dto/SportTypeForm.java`, `dto/TimeSlotForm.java` |
| `config/DemoData.java` – `run` | giữ tên |
| `templates/courts.html`, `admin/courts.html`, `admin/court-form.html`, `admin/types.html`, `admin/slots.html` | giữ đường dẫn |
| `static/css/admin.css` | giữ đường dẫn |
| `src/test/java/com/sportbooking/CatalogServiceTest.java` | giữ tên |

### Phương thức lấy từ file dùng chung

| File cũ | Phương thức | Lớp mới |
|---|---|---|
| `controller/WebController.java` | `courts()` (`GET /courts`) | `controller/CourtController.java` |
| `controller/AdminController.java` | `courts()`, `edit()`, `save()`, `delete()` (sân), `types()`, `type()` (lưu loại), `deleteType()`, `slots()`, `slot()` (lưu khung giờ), `deleteSlot()`, helper `flash()` | `controller/AdminCatalogController.java` |
| `SqlServerIntegrationTest.java` | `realSqlServerAndSchema`, `redesignedAdminFiltersAndValidation`, `courtCrudAndReferencedDeletion`, `typeAndSlotCrudAndForeignKeys` | `src/test/.../CatalogIntegrationTest.java` |

Ghi chú tên: trong `AdminController` cũ, các phương thức lưu / sửa / xóa sân tên là `edit`, `save`, `delete`; lưu loại sân là `type`; lưu khung giờ là `slot`. Trong lớp mới nên đặt rõ: `editCourt`, `saveCourt`, `deleteCourt`, `saveType`, `saveSlot`.

## Việc cải tiến bắt buộc

- Lỗi nghiệp vụ (trùng tên sân / loại sân, khung giờ chồng lấn, xóa bản ghi đang được tham chiếu) hiện **cạnh ô nhập** bằng `BindingResult.rejectValue`, không chỉ là thông báo chung đầu trang.
- Thêm **tìm kiếm** cho trang loại sân và trang khung giờ.
- Mỗi bảng chính có **ít nhất 20 bản ghi** mẫu (rubric 4.1). Hiện `sport_types` có 3 và `time_slots` có 17: thống nhất với nhóm bảng nào là "bảng chính", bổ sung dữ liệu hoặc giải thích trong báo cáo.
- Script chạy được trên máy khác từ đầu tới cuối (database trống → schema → seed → verify).

## Hạng mục rubric

1.3, 3.3, 3.4, 4.1, 4.2, 4.3, 4.4, 4.5.

## Phần báo cáo

- Chương 2 – Thiết kế cơ sở dữ liệu: sơ đồ ERD (`docs/diagrams/erd.svg`), bảng mô tả thuộc tính từng bảng, khóa chính, khóa ngoại, ràng buộc CHECK / UNIQUE.
- Chương 3 – Entity, repository, truy vấn tùy chỉnh (`CourtRepository.search`), giao diện quản trị danh mục.
- Chương 4 – Kịch bản CRUD, xóa bản ghi đang được tham chiếu, trùng tên.

## Câu hỏi vấn đáp cần chuẩn bị

1. Vì sao dùng `ddl-auto=validate` mà không dùng `update`?
2. Quan hệ `@ManyToOne` giữa `Court` và `SportType` ánh xạ ra cột nào? `fetch` mặc định là gì?
3. `CourtRepository.search` viết bằng `@Query` như thế nào, phân trang và sắp xếp truyền vào ra sao?
4. Vì sao sân / loại sân đã có lịch sử thì chỉ ngừng hoạt động chứ không xóa?
5. Ràng buộc nào ở tầng SQL chặn trùng lịch, và nó khác kiểm tra trong Java thế nào?
6. Tại sao chuỗi tiếng Việt trong script cần tiền tố `N''`?

## Lệnh kiểm thử

```powershell
. ./.env.local.ps1
./mvnw.cmd -B '-Dtest=CatalogServiceTest' test
$env:RUN_SQLSERVER_TESTS='true'; ./mvnw.cmd -B '-Dtest=CatalogIntegrationTest' test
```

## Checklist

- [ ] `sql/01_schema.sql`
- [ ] `sql/02_seed.sql`
- [ ] `sql/03_verify.sql`
- [ ] `entity/SportType.java`
- [ ] `entity/Court.java`
- [ ] `entity/TimeSlot.java`
- [ ] `repository/SportTypeRepository.java`
- [ ] `repository/CourtRepository.java`
- [ ] `repository/TimeSlotRepository.java`
- [ ] `service/CatalogService.java`
- [ ] `dto/CourtForm.java`
- [ ] `dto/SportTypeForm.java`
- [ ] `dto/TimeSlotForm.java`
- [ ] `config/DemoData.java`
- [ ] `controller/CourtController.java`
- [ ] `controller/AdminCatalogController.java`
- [ ] `templates/courts.html`
- [ ] `templates/admin/courts.html`
- [ ] `templates/admin/court-form.html`
- [ ] `templates/admin/types.html`
- [ ] `templates/admin/slots.html`
- [ ] `static/css/admin.css`
- [ ] `CatalogServiceTest.java`
- [ ] `CatalogIntegrationTest.java`
