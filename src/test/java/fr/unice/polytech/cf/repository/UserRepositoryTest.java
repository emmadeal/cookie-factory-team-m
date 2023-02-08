package fr.unice.polytech.cf.repository;

import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    User john;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        john = new User( "john", "john@gmail.com", "1234", "0640347631");
    }

    @Test
    void testSaveAndFind() {
        UUID genId = john.getId();
        assertNotNull(genId);
        userRepository.save(john,john.getId());
        Optional<User> foundJohnOpt = userRepository.findById(genId);
        assertTrue(foundJohnOpt.isPresent());
        assertEquals(john,foundJohnOpt.get());
    }

    @Test
    void testDeleteAll() {
        assertEquals(0,userRepository.count());
        userRepository.save(john,john.getId());
        assertEquals(1,userRepository.count());
        userRepository.deleteAll();
        assertEquals(0,userRepository.count());
    }
}
