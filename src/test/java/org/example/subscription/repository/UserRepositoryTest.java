package org.example.subscription.repository;

import org.example.subscription.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser_success() {

        // Arrange
        User user = new User();
        user.setName("Aman");
        user.setEmail("aman@test.com");

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser.getId());
        assertEquals("Aman", savedUser.getName());
    }

    @Test
    void findById_success() {

        User user = new User();
        user.setName("Rahul");
        user.setEmail("rahul@test.com");

        User saved = userRepository.save(user);

        Optional<User> found =
                userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Rahul", found.get().getName());
    }
}
