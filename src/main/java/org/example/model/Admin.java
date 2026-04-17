package org.example.model;

public class Admin extends User{
    private int clearanceLevel;

    public Admin(String id, String name, String role, int clearanceLevel) {
        super(id, name, role);
        if(clearanceLevel < 1 || clearanceLevel > 3) {
            throw new IllegalArgumentException("clearanceLevel invalid in role");
        }
        this.clearanceLevel = clearanceLevel;
    }
    public boolean canDeleteTask() {
        return clearanceLevel >= 2;
    }
    public int clearanceLevel() { return clearanceLevel;}

    @Override
    public String toString() {
        return "Admin{" +
                "clearanceLevel=" + clearanceLevel +
                "} " + super.toString();
    }
}
