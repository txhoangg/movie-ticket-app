# CineBook 🎬
Ứng dụng đặt vé xem phim Android sử dụng Firebase.

## Chức năng
- Đăng ký / Đăng nhập (Firebase Authentication)
- Xem danh sách phim đang chiếu
- Xem chi tiết phim & lịch chiếu theo rạp
- Chọn ghế & đặt vé (Firestore Transaction, chống đặt trùng)
- Xem lại vé đã đặt
- Push Notification nhắc giờ chiếu (FCM + AlarmManager)

## Công nghệ
| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java |
| Authentication | Firebase Auth (Email/Password) |
| Database | Cloud Firestore |
| Push Notification | Firebase Cloud Messaging + AlarmManager |
| Load ảnh | Glide |
| UI | Material Design 3 |

## Cấu trúc Firestore
```
movies/          → Danh sách phim
showtimes/       → Lịch chiếu (movieId, theaterName, date, time, bookedSeats, price)
tickets/         → Vé đã đặt (userId, seats, totalPrice, status)
users/           → Thông tin người dùng
config/appConfig → Flag seed dữ liệu
```
