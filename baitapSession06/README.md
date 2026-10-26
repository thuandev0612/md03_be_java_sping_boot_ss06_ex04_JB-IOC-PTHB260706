# Session06 - Bài 1 đến 4: Hệ thống bãi đỗ xe

Java 21, Spring Boot, JPA và PostgreSQL.

## Chạy

1. Mở project bằng IntelliJ, chọn JDK 21.
2. Chạy `BaitapSession06Application`.
3. Trong pgAdmin, mở database `parking_management_session06` → Schemas → public → Tables → Refresh.

Database và kết nối đã được chuẩn bị trên máy hiện tại. Mật khẩu nằm ngoài project tại `../.local-config/baitapSession06.properties`.

Máy khác cần tạo database bằng `CREATE DATABASE parking_management_session06;`, rồi đặt biến môi trường `DB_USERNAME`, `DB_PASSWORD` cho tài khoản PostgreSQL của máy đó. Có thể dùng `DB_URL` để đổi URL kết nối. Hibernate tự tạo bảng khi khởi động nhờ `ddl-auto=update`.

## Mapping

- `Zone` → `zones`: id, name, capacity, occupied_spots.
- `Vehicle` → `vehicles`: id, license_plate, color, type (CAR/BIKE).
- `ParkingTicket` → `parking_tickets`: id, check_in_time, check_out_time, vehicle_id, zone_id.
- Zone và Vehicle có `@OneToMany(mappedBy = ...)` trỏ về ParkingTicket.
- ParkingTicket dùng hai `@ManyToOne`, khóa ngoại vehicle_id và zone_id.
- check_out_time cho phép NULL khi xe chưa ra bãi.

## API tạo xe

POST `http://localhost:8080/api/v1/vehicles`, chọn Body → raw → JSON trong Postman:

```json
{
  "licensePlate": "18A-12345",
  "color": "blue",
  "vehicleType": "CAR"
}
```

`vehicleType` nhận CAR hoặc BIKE. Thành công trả HTTP 201 và `ApiResponse<VehicleResponse>`; không trả trực tiếp entity.

## API danh sách xe

```text
GET http://localhost:8080/api/v1/vehicles?page=0&size=5&sortBy=licensePlate&direction=ASC&keyword=18
```

- page mặc định 0; số âm tự về 0. size mặc định 10, phải lớn hơn 0.
- Chỉ sắp xếp khi có cả sortBy và direction. Nếu thiếu một trong hai, dùng Sort.unsorted().
- sortBy hỗ trợ id, licensePlate, color, vehicleType; direction nhận ASC/DESC.
- keyword tìm gần đúng theo biển số, không phân biệt hoa/thường. Bỏ keyword hoặc để rỗng sẽ lấy toàn bộ xe.
- Kết quả được bọc trong ApiResponse → PageResponse, gồm items, page, size, totalItems, totalPages, isLast.
- Mỗi item chỉ có id, licensePlate, color, vehicleType; totalItems dùng Long.

Repository dùng JPQL `SELECT new ...VehicleResponse(...)` kết hợp điều kiện `:keyword IS NULL OR ...` và Pageable. CAST tham số keyword sang string giúp PostgreSQL xử lý đúng trường hợp keyword=null.

Build/cache được tạo ngoài project khi kiểm tra để giữ số file nộp thấp. Khởi động lại ứng dụng để thử Postman; dữ liệu demo kiểm tra API được giữ trong database.

## Gửi xe và trả xe

Tạo khu vực trong pgAdmin nếu chưa có (sử dụng Query Tool của database parking_management_session06):

```sql
INSERT INTO zones(name, capacity, occupied_spots)
VALUES ('Khu A', 10, 0)
RETURNING id;
```

Tạo xe bằng POST /api/v1/vehicles ở trên. Dùng vehicleId và zoneId thực tế nhận được.

POST `http://localhost:8080/api/v1/tickets/check-in`:

```json
{
  "vehicleId": 1,
  "zoneId": 1
}
```

Check-in kiểm tra xe/khu vực tồn tại, xe chưa ở trong bãi, khu vực còn chỗ. Sau đó tạo ParkingTicket, gán checkInTime và tăng occupiedSpots. Thành công trả HTTP 201.

PUT `http://localhost:8080/api/v1/tickets/check-out/1` (1 là vehicleId, không phải ticketId), không cần body.

Check-out tìm vé chưa có checkOutTime mới nhất theo checkInTime rồi id giảm dần, cập nhật thời gian trả xe và giảm occupiedSpots. Thành công trả HTTP 200. Gửi lại xe sẽ tạo vé mới, giữ vé cũ trong lịch sử.

Cả hai API dùng ApiResponse<TicketResponse>. Vé trả về gồm id, licensePlate, zoneName, checkInTime, checkOutTime. File DTO nhận dữ liệu có tên TicketRequest theo bước 1 của đề.

ParkingService dùng @Transactional cho cả hai thao tác. Vehicle/Zone được khóa khi cập nhật để giữ số chỗ nhất quán khi có yêu cầu đồng thời.

- Thiếu/sai ID hoặc JSON: 400.
- Không tìm thấy xe, khu vực hoặc vé đang gửi: 404.
- Xe đã ở trong bãi, khu vực đầy hoặc số chỗ không hợp lệ: 409.

Ví dụ kiểm tra số chỗ và lịch sử trong pgAdmin:

```sql
SELECT id, name, capacity, occupied_spots FROM zones;
SELECT id, vehicle_id, zone_id, check_in_time, check_out_time
FROM parking_tickets ORDER BY id DESC;
```

## Tổng hợp vé xe trong ngày (Bài 4)

```text
GET http://localhost:8080/api/v1/tickets/summary
```

Trả HTTP 200 với `ApiResponse<List<TicketSummaryResponse>>`, không phân trang. Mỗi vé chỉ có id, licensePlate, zoneName, checkInTime, checkOutTime. Nếu hôm nay chưa có vé, data là mảng rỗng `[]`.

"Trong ngày" tính theo checkInTime từ 00:00 hôm nay (bao gồm) đến 00:00 ngày mai (không bao gồm), theo ngày của máy chạy ứng dụng. Gồm cả vé đã trả và vé còn đang gửi; vé mới nhất đứng trước.

TicketService xác định khoảng thời gian rồi gọi ParkingTicketRepository. Repository dùng JPQL `SELECT new ...TicketSummaryResponse(...)` với JOIN Vehicle và Zone để chỉ lấy 5 trường cần thiết trong một truy vấn, không tải toàn bộ entity rồi mới map DTO.

Có thể gửi xe bằng API check-in ở trên rồi gọi summary; console sẽ hiện một câu SELECT có JOIN vehicles và zones, không có các SELECT riêng để tải từng xe/khu vực.
