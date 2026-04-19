package org.example.cli;

import org.example.exception.DuplicateTaskIdException;
import org.example.exception.TaskNotFoundException;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.model.User;
import org.example.service.TaskService;
import org.example.service.ConsoleNotifier;

import java.util.Scanner;

public class TaskTrackerApp {


    private final TaskService service = new TaskService(new ConsoleNotifier());

    Scanner sc = new Scanner(System.in);

    public void start() {
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║   🚀 TASK TRACKER CLI (Java 21+)     ║
                ╚══════════════════════════════════════╝
                """);

        boolean running = true;

        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> viewTask();
                    case "2" -> createTask();
                    case "3" -> findByStatus();
                    case "4" -> assginTask();
                    case "5" -> deleteTask();
                    case "6" -> findById();
                    case "7" -> updateTask();
                    case "0" -> {
                        running = false;
                        System.out.println("See you again");
                    }
                    default -> System.out.println("choice invalid");
                }
            } catch (Exception e) {
                System.out.println("error" + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("""
                [1] Xem tất cả  [2] Thêm task  [3] Lọc theo trạng thái
                [4] Gán task   [5] Xóa task    [6]Tìm qua id của task [7]Update task [0] Thoát
                """);
        System.out.print("👉 Nhập lựa chọn: ");
    }

    private void createTask() {
        //tại sao dùng try catch chỗ này bởi vì hàm addTassk có dùng cái hàm xử lí lỗi nên bắt buộc phải dùng;
        try {
            System.out.print("ID: ");
            String id = readInput();
            System.out.print("title: ");
            String title = readInput();
            System.out.print("Priority: ");
            int priority = readInt();
            System.out.print("status: ");
            String statusInput = readInput();
            TaskStatus status = parseStatus(statusInput);
            service.addTask(new Task(id, title, priority, status));
            System.out.println("Create successful!");
        } catch (DuplicateTaskIdException e) {
            System.out.println("Id exists: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Id invalid:" + e.getMessage());
        } catch (Exception e) {
            System.out.println("Id no suit: " + e.getMessage());
        }
    }

    private void viewTask() {
        var tasks = service.findAll();
        if (tasks.isEmpty()) System.out.println("Present are empty");
        else
            tasks.forEach(System.out::println);  //Method Reference (tham chiếu phương thức), chỉ dùng khi mình có 1 logic còn hơn thì phải viết như java bình thường
    }

    private void findByStatus() {
        System.out.print("Status (TODO/IN_PROGRESS/DONE): ");
        TaskStatus status = TaskStatus.valueOf(readInput().toUpperCase().trim());
        var fill = service.findByStatus(status);
        if (fill.isEmpty()) System.out.println("No status in task");
        else fill.forEach(System.out::println);
    }

    private void assginTask() {
        try {
            System.out.print("task id:");
            String tid = readInput();
            System.out.print("User Id: ");
            String Uid = readInput();
            System.out.print("name:");
            String name = readInput();
            service.assignTask(tid, new User(Uid, name, "Member"));
            System.out.println("✅ Đã gán!");
        } catch (TaskNotFoundException e) {
            System.out.println("task no exist " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Id invalid:" + e.getMessage());
        } catch (Exception e) {
            System.out.println("Id no suit: " + e.getMessage());
        }
    }

    private void deleteTask() {
        try {
            System.out.println("Id:");
            String id = readInput();
            service.deleTask(id);
            System.out.println("Delete successful!");
        } catch (TaskNotFoundException e) {
            System.out.println("Id exists: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Id invalid:" + e.getMessage());
        } catch (Exception e) {
            System.out.println("Id no suit: " + e.getMessage());
        }
    }

    private void findById() {
        try {
            System.out.print("🔍 Nhập ID task cần tìm: ");
            String id = readInput();

            // ✅ Gọi service và xử lý Optional
            var taskOpt = service.findById(id);

            if (taskOpt.isPresent()) {  //task != null
                System.out.println("✅ Tìm thấy task:");
                System.out.println("─────────────────────");
                System.out.println(taskOpt.get());
                System.out.println("─────────────────────");
            } else {
                System.out.println("❌ Không tìm thấy task với ID: " + id);
            }

        } catch (Exception e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
        }
    }

    private void updateTask() {
        try {
            System.out.println("Enter id to update task: ");
            String id = readInput();
            var taskOtp = service.findById(id);

            if (taskOtp.isEmpty()) {
                System.out.println("Task not found: " + id);
                return;
            }

            Task current = taskOtp.get();
            System.out.println("Task current: " + current);
            System.out.println("─────────────────────────────");

            System.out.println("Title (Enter == no change");
            String newTitle = readInput();
            if (newTitle.isEmpty()) newTitle = null;

            System.out.println("Priority new: ");
            String priorityInput = readInput();
            Integer newPriority = null;
            if (!priorityInput.isEmpty()) {
                newPriority = Integer.parseInt(priorityInput);
            }
            // Trong method updateTask():

            System.out.print("Status mới (TODO/IN_PROGRESS/DONE, Enter để giữ nguyên): ");
            String statusInput = readInput(); // Đọc input thô trước

            TaskStatus newStatus = null; // Mặc định là null (tức là không đổi)

// Chỉ gọi method parse khi user có nhập gì đó
            if (!statusInput.isEmpty()) {
                newStatus = parseStatus(statusInput);
            }

// Sau đó truyền newStatus (có thể là null) xuống service

            service.updateTask(id, newTitle, newPriority, newStatus);
            System.out.println("✅ Cập nhật task thành công!");

            // ✅ Hiển thị task sau update
            System.out.println("📋 Task mới: " + service.findById(id));

        } catch (NumberFormatException e) {
            System.out.println("❌ Priority phải là số (1-5)");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (TaskNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {

            System.out.println("❌ Lỗi: " + e.getMessage());
        }
    }

    private String readInput() {
        return sc.nextLine().trim();
    }

    // ⚠️ Lỗi thường gặp
    //int priority = scanner.nextInt();
    // Nếu user gõ "abc" → 💥 InputMismatchException crash app
    // Nếu user gõ "3" rồi Enter → \n vẫn nằm trong buffer
    // → Lệnh nextLine() tiếp theo sẽ đọc \n → trả về chuỗi rỗng → bug logic
    private int readInt() {
        try {
            return Integer.parseInt(readInput());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Incorrect format!");
        }
    }
    // Trong method updateTask():

    // Method helper riêng để parse, không in ra thông báo gì cả
    private TaskStatus parseStatus(String input) {
        try {
            return TaskStatus.valueOf(input.trim().toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Status không hợp lệ! Giữ nguyên giá trị cũ.");
            return null; // Trả về null để service biết đường bỏ qua
        }
    }

    public static void main(String[] args) {
        new TaskTrackerApp().start();
    }
}
