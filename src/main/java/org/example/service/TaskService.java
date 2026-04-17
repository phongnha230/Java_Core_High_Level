package org.example.service;
import org.example.exception.DuplicateTaskIdException;
import org.example.exception.TaskNotFoundException;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.model.TaskSummaryDTO;
import org.example.model.User;

import java.util.*;
import java.util.stream.Collectors;
public class TaskService {
    private final Map<String, Task> taskStore = new LinkedHashMap<>();
    private  Notifier notifier;
    public TaskService(Notifier notifier) {
        this.notifier = notifier;
    }
    public void addTask(Task task) throws DuplicateTaskIdException {
        if(taskStore.containsKey(task.getId())) throw new DuplicateTaskIdException(task.getId());
        taskStore.put(task.getId(), task);
        notifier.notify(task);
    }
    public Task findById(String id) {
        if (taskStore.containsKey(id)) {
            return taskStore.get(id);
        }
        return null;//    }
    }
// Optional<Task> findById(String id) {
//    return Optional.ofNullable(taskStore.get(id)); // ✅ Thay if(obj != null)
//}


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
     public void deleTask(String id) {
        if(taskStore.remove(id) == null) {
            throw new TaskNotFoundException(id);
        } else {
            System.out.println("Remove success!");
        }

    }
    //orElseThrow muốn dùng thằng này thì phải dùng Optional
    public void assignTask(String taskId, User user) {
       Task task = taskStore.get(taskId);
       if(task == null) {
           throw new TaskNotFoundException("task no exist" + taskId);
       }
       task.assignee(user);
       notifier.notify(task);
    }
}

