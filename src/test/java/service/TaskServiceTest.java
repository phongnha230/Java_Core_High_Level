package service;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.model.User;
import org.example.model.Admin;
import org.example.exception.DuplicateTaskIdException;
import org.example.exception.TaskNotFoundException;
import org.example.service.Notifier;
import org.example.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*; //“đóng giả nhân vật phụ để test nhân vật chính”
public class TaskServiceTest {
    private TaskService service;
    private Notifier mocNotifier;

    @BeforeEach
    void setUp() {
        // ✅ Mock Notifier để không in ra console khi test
        mocNotifier = Mockito.mock(Notifier.class);
        service = new TaskService(mocNotifier);
    }
    @Test
    @DisplayName("✓ Thêm task thành công")
    void addTask_Success() throws DuplicateTaskIdException{
        //tại sao lại dùng cái hàm bỏi vì trong taskSevices viết code có hàm throws DuplicateTaskIdException
        // trong đó nên trong khi viết test thêm chắc chắn phair thêm hàm không thì nó lỗi checkError khi nó trống hoặc không hợp lệ
      //Given
        Task task = new Task("T002", "Nha chs game", 3, TaskStatus.TODO);
      //when
        service.addTask(task);
        //then
        Optional<Task> found = service.findById("T002");
        assertTrue(found.isPresent());
        assertEquals("Nha chs game", found.get().getTitle());
           //"Hãy kiểm tra rằng: mockNotifier.notify(task) đã được gọi đúng 1 lần trong quá trình chạy test.
        verify(mocNotifier, times(1)).notify(task);
    }
    @Test
    @DisplayName("✗ Ném lỗi khi thêm task trùng ID")
    void addTask_DedupdicateId() throws DuplicateTaskIdException {
        Task task = new Task("T002", "Nha chs game", 3, TaskStatus.TODO);
        Task task2 = new Task("T002", "Nha chs game", 3, TaskStatus.TODO);

        service.addTask(task);
        assertThrows(DuplicateTaskIdException.class, () -> service.addTask(task2));


    }

    @Test
    @DisplayName("✓ Trả Optional.empty khi không tìm thấy")
    void findById_NotFound_ReturnsEmpty() {
        Optional<Task> found = service.findById("Not exist");

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("✓ Lấy tất cả tasks")
    void findAll_Success() throws DuplicateTaskIdException{
        service.addTask(new Task("T001", "Task 1", 3, TaskStatus.TODO));
        service.addTask(new Task("T002", "Task 2", 4, TaskStatus.IN_PROGRESS));

        List<Task> tasks = service.findAll();
        assertEquals(2, tasks.size());
    }

    @Test
    @DisplayName("Filter theo status")
    void filter_TaskStatus() throws DuplicateTaskIdException{
        service.addTask(new Task("T001", "Task 1", 3, TaskStatus.TODO));
        service.addTask(new Task("T002", "Task 2", 4, TaskStatus.DONE));
        service.addTask(new Task("T003", "Task 2", 4, TaskStatus.DONE));

        List<Task> doneTask = service.findByStatus(TaskStatus.DONE);
        assertEquals(2, doneTask.size());
        assertTrue(doneTask.stream().allMatch(t -> t.getStatus() == TaskStatus.DONE));


    }
    @Test
    @DisplayName("✓ Filter trả về list rỗng khi không có task nào")
    void findByStatus_NoMatch_ReturnsEmpty() throws DuplicateTaskIdException {
        service.addTask(new Task("T001", "Task 1", 3, TaskStatus.TODO));
        List<Task> progressTask = service.findByStatus(TaskStatus.IN_PROGRESS);
        assertTrue(progressTask.isEmpty());
    }

   @Test
    @DisplayName("Remove task success")
    void remove_Task() throws DuplicateTaskIdException {
        Task task = new Task("T001", "Task 1", 3, TaskStatus.TODO);
        User owner = new User("U001", "Phong", "MEMBER");
        task.assignee(owner);

        service.addTask(task);
       User admin = new Admin("A001", "Boss", "ADMIN", 2);
        service.deleTask("T001", owner);
        assertTrue(service.findById("T001").isEmpty());


        //Không có quyền xóa chỉ được admin và owner remove
       Task task2 = new Task("T002", "Task 1", 3, TaskStatus.TODO);
       User owner2 = new User("U002", "Nha", "MEMBER");
       task.assignee(owner2);

       service.addTask(task2);
        User stranger = new User("U999", "NguoiLa", "MEMBER");
        SecurityException exception = assertThrows(SecurityException.class, () -> service.deleTask("T002", stranger));
//       System.out.println("Actual message: " + exception.getMessage()); dòng này để debug khá hay về việc in ra mesage
       assertTrue(exception.getMessage().contains("You not accesses to remove this! (Just Admin or Owner accesses remove"));
       assertThrows(TaskNotFoundException.class, () -> service.deleTask("T003", owner2));

   }

   @Test
    @DisplayName("Assgnee Task")
    void assginee_Task() throws DuplicateTaskIdException{
       Task task = new Task("T001", "Task 1", 3, TaskStatus.TODO);
       service.addTask(task);
       User assignee = new User("U002", "Phong", "MEMBER");
       User admin = new Admin("A001", "Boss", "ADMIN", 2);

       // ✅ Reset mock để quên hết lịch sử gọi trước đó để không cho lặp lại 2 lần trong một function
       Mockito.reset(mocNotifier);

       service.assignTask("T001", assignee, admin);
       Optional<Task> update = service.findById("T001");
       assertTrue(update.isPresent());
       assertEquals(assignee, update.get().getAssignee());

       // ✅ Giờ chỉ verify 1 lần từ assignTask() thêm (any(Task.class) cho nó đúng
       verify(mocNotifier, times(1)).notify((any(Task.class)));

       assertThrows(TaskNotFoundException.class, ()-> service.assignTask("T002", assignee, admin));
   }
    @Test
    @DisplayName("✓ Update task - Chỉ đổi title, giữ nguyên priority & status")
    void updateTask_OnlyTitle() throws DuplicateTaskIdException {
        // Given
        Task task = new Task("T001", "Old Title", 3, TaskStatus.TODO);
        service.addTask(task);
        Mockito.reset(mocNotifier);

        // When - Pass null cho priority & status → không đổi
        service.updateTask("T001", "New Title", null, null);

        // Then
        Optional<Task> update = service.findById("T001");
        assertTrue(update.isPresent());
        assertEquals("New Title", update.get().getTitle());

        assertEquals(3, update.get().getPriority());          // Giữ nguyên
        assertEquals(TaskStatus.TODO, update.get().getStatus()); // Giữ nguyên
    }
}
