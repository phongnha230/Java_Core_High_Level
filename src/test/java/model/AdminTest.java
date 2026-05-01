package model;
import org.example.model.Admin;
import org.example.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Admin Inheritance Tests")
public class AdminTest {
    @Test
    @DisplayName("✓ Admin kế thừa properties từ User")
    void admin_InheritsFromUser() {
        Admin admin = new Admin("T001", "Nha", "Admin", 3);
        assertEquals("T001", admin.getId());
        assertEquals("Nha", admin.getName());
        assertEquals("Admin", admin.getRole());
        assertEquals(3, admin.clearanceLevel());
    }

    @Test
    @DisplayName("✓ Admin level 2 trở lên có thể xóa!")
    void admin_Level1_canReport() {
        Admin admin = new Admin("T001", "Nha", "Admin", 3);
//        assertFalse(admin.canDeleteTask());
        assertTrue(admin.canDeleteTask());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 4})
    void admin_ThrowsException(int clearanceLevel){
        assertThrows(IllegalArgumentException.class, ()->
            new Admin("T001", "Nha", "Admin", clearanceLevel)
        );
    }
}
