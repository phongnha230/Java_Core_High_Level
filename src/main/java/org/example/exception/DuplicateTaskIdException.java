package org.example.exception;

public class DuplicateTaskIdException extends IllegalAccessException{
    public DuplicateTaskIdException(String id) {
        super("Task ID" + id + "it is exist");
    }
}
