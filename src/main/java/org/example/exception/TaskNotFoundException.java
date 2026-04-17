package org.example.exception;

public class TaskNotFoundException extends RuntimeException //Là lỗi xảy ra lúc runtime và KHÔNG bắt buộc phải try-catch
 {
    public TaskNotFoundException(String id) {
        super("Task not found" + id);
    }
}
//        🔥 Đặc điểm:
//Không cần throws
//Không cần try-catch
//Thường là lỗi do dev code sai logic
// Các loại phổ biến:
//NullPointerException
//IllegalArgumentException
//IndexOutOfBoundsException

//Dùng RuntimeException khi:
//Validate input
//Business logic sai
//Lỗi do code