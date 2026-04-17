package org.example.model;

public class Task {
    private final String id;
    private String title;
    private TaskStatus status;
    private final int priority;
    private User assignee;  //Composition: Task HAS-A User

    public Task(String id, String title, int priority, TaskStatus status) {
        if(id == null || id.isBlank()) throw new IllegalArgumentException("id is empty");
        if(title == null || title.isBlank()) throw new IllegalArgumentException("title is empty");
        if (priority < 1 || priority > 5) throw new IllegalArgumentException("priority must 1 - 5");
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public int getPriority() {
        return priority;
    }

    public User getAssignee() {
        return assignee;
    }
    public void setTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Tiêu đề không hợp lệ");
        this.title = title;
    }
    public void assignee(User user) { //Kiểu dữ liệu. Báo Java: "Method này chỉ nhận object thuộc class User".
        if(user == null) throw new IllegalArgumentException("User no null");
        this.assignee = user;
    }
   // user → Dữ liệu đầu vào (input parameter)
//    assignee → Chỗ lưu trữ nội bộ (class field)
    public void markDone() {this.status = TaskStatus.DONE; }

    @Override public String toString() {
        return "[%s] %-15s | %-10s | P:%d | Assigned: %s"
                .formatted(id, title, status, priority, assignee != null ? assignee.getName() : "Unassigned");
    }
}
