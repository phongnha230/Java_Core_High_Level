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

        public void start () {
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
                      case "3" -> FindByStatus();
                      case "4" -> assginTask();
                      case "5" -> deleteTask();
                      case "6" -> findById();
                      case "0" -> {running = false;
                          System.out.println("See you again");}
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
            [4] Gán task   [5] Xóa task    [6]Tìm qua id của task [0] Thoát
            """);
        System.out.print("👉 Nhập lựa chọn: ");
    }
        private void createTask () {
            //tại sao dùng try catch chỗ này bởi vì hàm addTassk có dùng cái hàm xử lí lỗi nên bắt buộc phải dùng;
            try {
                System.out.println("ID: ");
                String id = readInput();
                System.out.println("title: ");
                String title = readInput();
                System.out.println("Priority: ");
                int priority = readInt();
                System.out.println("status: ");
                TaskStatus status = EnterStatus();
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
        private void viewTask () {
            var tasks = service.findAll();
            if (tasks.isEmpty()) System.out.println("Present are empty");
            else
                tasks.forEach(System.out::println);  //Method Reference (tham chiếu phương thức), chỉ dùng khi mình có 1 logic còn hơn thì phải viết như java bình thường
        }
        private void FindByStatus () {
            System.out.println("Status (TODO/IN_PROGRESS/DONE");
            TaskStatus status = TaskStatus.valueOf(readInput().toLowerCase());
            var fill = service.findByStatus(status);
            if (fill.isEmpty()) System.out.println("No status in task");
            else fill.forEach(System.out::println);
        }
        private void assginTask () {
            try {
                System.out.println("task id:");
                String tid = readInput();
                System.out.println("User Id: ");
                String Uid = readInput();
                System.out.println("name:");
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
                System.out.println("Id:"); String id = readInput();
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

            if (taskOpt != null) {  //task != null
                System.out.println("✅ Tìm thấy task:");
                System.out.println("─────────────────────");
                System.out.println(taskOpt.getId());
                System.out.println("─────────────────────");
            } else {
                System.out.println("❌ Không tìm thấy task với ID: " + id);
            }

        } catch (Exception e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
        }
    }
        private String readInput () {
            return sc.nextLine().trim();
        }
        // ⚠️ Lỗi thường gặp
        //int priority = scanner.nextInt();
        // Nếu user gõ "abc" → 💥 InputMismatchException crash app
        // Nếu user gõ "3" rồi Enter → \n vẫn nằm trong buffer
        // → Lệnh nextLine() tiếp theo sẽ đọc \n → trả về chuỗi rỗng → bug logic
        private int readInt () {
            try {
                return Integer.parseInt(readInput());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Incorrect format!");
            }
        }
        private TaskStatus EnterStatus() {
            return TaskStatus.TODO;
        }
    public static void main(String[] args) {
        new TaskTrackerApp().start();
    }
    }
