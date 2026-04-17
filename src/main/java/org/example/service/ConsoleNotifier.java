package org.example.service;

import org.example.model.Task;

public class ConsoleNotifier implements Notifier {
    @Override
    public void notify(Task task) {
        System.out.println("[Console] task " + task.getTitle() + " " + " are update");
    }
}
