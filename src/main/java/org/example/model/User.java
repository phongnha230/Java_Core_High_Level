package org.example.model;

public class User {
    private  String id;
    private String name;
    private final String role;


    public User(String id, String name, String role) {
        if(id == null || id.isBlank()) throw new IllegalArgumentException("id invalid");
        if(name == null || name.isBlank()) throw new IllegalArgumentException("name invalid");
        this.id = id;
        this.name = name;
        this.role = role;

    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name == null || name.isBlank()) throw new IllegalArgumentException("name invalid");
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "[%s] %s (%s)".formatted(id,name,role);
    }
}
