package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.interfaces.RgpdManager;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.repositories.ClientRepository;
import fr.unice.polytech.cf.repositories.OrderRepository;
import fr.unice.polytech.cf.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RgpdServiceTest {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    private ClientRepository clientRepository;


    @Autowired
    private RgpdManager rgpdManager;



    @BeforeEach
    void setUp()  {
        userRepository.deleteAll();
        orderRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    public void deleteClient() throws Exception {
        Client client =new Client();
        clientRepository.save(client,client.getId());
        assertTrue(clientRepository.existsById(client.getId()));
        rgpdManager.deleteClient(client.getId());
        assertFalse(userRepository.existsById(client.getId()));
    }

    @Test
    public void deleteOrder() throws Exception {
       Order order = new Order();
       orderRepository.save(order,order.getId());
        assertTrue(orderRepository.existsById(order.getId()));
        rgpdManager.deleteOrder(order.getId());
        assertFalse(orderRepository.existsById(order.getId()));
    }
}
