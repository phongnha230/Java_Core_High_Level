package model;

import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task Model Tests")
public class TaskTest {

    @Test
    @DisplayName("✓ Tạo task thành công với dữ liệu hợp lệ")
    void createTask_Success() {
        String id = "T001";
        String title = "Study Java";
        int priority = 3;
        TaskStatus status = TaskStatus.TODO;
        // dễ main tain code hơn và chuyên nghiệp hơn trong việc bạn tránh hardCode và chỗ này cũng có thể dùng
        //@ParameterizedTest
        //@CsvSource({
        //    "T001, 'Học Java',       3, TODO",
        //    "T002, 'Viết Unit Test', 5, IN_PROGRESS",
        //    "T003, 'Deploy Prod',    1, DONE"
        //}) khi bạn muốn mapping nhiều giá trị cùng một lúc
        //when
        Task task = new Task(id, title, priority, status);

        assertEquals(id, task.getId());
        assertEquals(title, task.getTitle());
        assertEquals(priority, task.getPriority());
        assertEquals(status, task.getStatus());
        assertNull(task.getAssignee());
    }

    @Test
    @DisplayName("✗ Ném lỗi khi ID rỗn")
    void createTask_EmptyId() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Task("", "Title", 3, TaskStatus.TODO);
        });
    }

    @Test
    @DisplayName("✗ Ném lỗi khi title null")
    void createTask_EmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Task("T001", null, 3, TaskStatus.TODO);
        });
    }

    //cách code priority có thể code như 2 dòng trên nhưng lại khó maintain nên là bạn hãy thêm cách mới là dùng
    //@ParameterizedTest
    //@ValueSource(ints = {0, 6, 10}) cho nó mới lạ và nhìn cáe dễ đọc và chỉ cần thêm số vào {...}
    @ParameterizedTest
    //"JUnit ơi, đừng chạy test này 1 lần thôi, mà hãy chạy nhiều lần, mỗi lần với 1 bộ data khác nhau"
    @ValueSource(ints = {0, 6, 10}) //"Đây là danh sách giá trị truyền vào: 0, 6, 10"
    @DisplayName("✗ Ném lỗi khi priority ngoài range 1-5")
    void createTask_EmptyPriority(int priority) {
        assertThrows(IllegalArgumentException.class, () -> {
            new Task("", "Title", priority, TaskStatus.TODO);
        });
    }
    @Test
    @DisplayName("✓ Gán user thành công")
    void assignee_User_Success() {
        Task task = new Task("T001", "Title", 4, TaskStatus.TODO);
        User user = new User("U001", "Nha", "Member");
        User user2 = new User("U002", "Nha2", "Member");

        //nếu muốn notEquals thì thêm một cái user2 nữa vào mà so sánh nữa bởi vì trong task này nó gán vào user chứ không phải gán user2
        task.assignee(user);

        assertNotEquals(user2, task.getAssignee());
        assertEquals(user, task.getAssignee());

    }

    @Test
    @DisplayName("✓ Update title thành công")
    void update_Title() {
        Task task = new Task("T001", "Title", 4, TaskStatus.TODO);
        task.setTitle("New-Title");
        assertEquals("New-Title", task.getTitle());
    }

    @Test
    @DisplayName("Update done task")
    void update_Done_Task() {
        Task task = new Task("T001", "Title", 4, TaskStatus.TODO);
        task.markDone();
        assertNotEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(TaskStatus.DONE, task.getStatus());
    }
}
