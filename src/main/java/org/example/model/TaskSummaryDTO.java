package org.example.model;

public record TaskSummaryDTO(String id, String title, String status, String assigneeName) {

}
//record = DTO phiên bản "auto code", gọn + sạch + ít bug hơn và nó chỉ dùng khi đọc và không muốn làm gì thêm
//record = dữ liệu cố định, không đổi, chỉ để truyền đi
//Không cần viết getter/setter
//Không cần constructor
//Code sạch như nước suối