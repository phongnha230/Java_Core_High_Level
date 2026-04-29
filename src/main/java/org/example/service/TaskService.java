package org.example.service;
import org.example.exception.DuplicateTaskIdException;
import org.example.exception.TaskNotFoundException;
import org.example.model.*;

import java.util.*;

public class TaskService {
    private final Map<String, Task> taskStore = new LinkedHashMap<>();
    private  final Notifier notifier;
    public TaskService(Notifier notifier) {
        this.notifier = notifier;
    }
    public void addTask(Task task) throws DuplicateTaskIdException {
        if(taskStore.containsKey(task.getId())) throw new DuplicateTaskIdException(task.getId());
        taskStore.put(task.getId(), task);
        notifier.notify(task);
    }
//    public Task findById(String id) {
//        if (taskStore.containsKey(id)) {
//            return taskStore.get(id);
//        }
//        return null;
//    }
     public Optional<Task> findById(String id) {
    return Optional.ofNullable(taskStore.get(id)); // ✅ Thay if(obj != null)
}


    public List<Task> findAll() {
        return List.copyOf(taskStore.values());
    }
    // ✅ Comparator + Stream + Generics type-safe
     public List<Task> findByStatus(TaskStatus status) {
        return taskStore.values().stream()
                .filter(t -> t.getStatus() == status)
                .sorted(Comparator.comparing(Task :: getPriority))
                .toList();
     }
    // ✅ Record DTO mapping
     public List<TaskSummaryDTO> getSummaries() {
        return taskStore.values().stream()
                .map(t -> new TaskSummaryDTO(t.getId(), t.getTitle(), t.getStatus().name(),
                        t.getAssignee() != null ? t.getAssignee().getName() : "Unassigned"))
                .toList();
     }
     public void deleTask(String id, User currentUser) {
        Task task = taskStore.get(id);
        if(task == null) {
            throw new TaskNotFoundException(id);
        }

        //check access

         boolean isAdmin = currentUser instanceof Admin admin && admin.clearanceLevel() >= 2;
         boolean isOwner = task.getAssignee() != null && task.getAssignee().getId().equals(currentUser.getId());

         if(!isAdmin && !isOwner) {
             throw new SecurityException("You not accesses to remove this! (Just Admin or Owner accesses remove ");
         }
         taskStore.remove(id);
         System.out.println("🔔 Task đã được xóa bởi: " + currentUser.getName());
    }
    //orElseThrow muốn dùng thằng này thì phải dùng Optional
    public void assignTask(String taskId, User user,User currentUser) {
        if(!(currentUser instanceof Admin admin)) {
            throw new SecurityException("🚫 Chỉ Admin mới được gán task cho người khác!");
        }
        if (admin.clearanceLevel() < 2) {
            throw new SecurityException("🚫 Chỉ Admin level 2+ mới được gán task!");
        }
        Task task = taskStore.get(taskId);
       if(task == null) {
           throw new TaskNotFoundException("task no exist" + taskId);
       }
       task.assignee(user);
       notifier.notify(task);
        System.out.println("🔔 Task được gán bởi: " + currentUser.getName());
    }
    public void updateTask(String id, String newTitle, Integer newPriority, TaskStatus newStatus) {
        Task existTask = taskStore.get(id);

        if(existTask == null) {
            throw new TaskNotFoundException("haven't task");
        }
        if(newTitle != null && newTitle.isBlank()) {
            existTask.setTitle(newTitle);
        }
        //không được dùng khai báo biến int trong việc so sánh với null nên phải dùng Integer
        if(newPriority != null && newPriority >= 1 && newPriority <=5) {
             existTask.setPriority(newPriority);
        }
        if(newStatus != null) {
            existTask.setStatus(newStatus);
        }
        notifier.notify(existTask);
    }
}

