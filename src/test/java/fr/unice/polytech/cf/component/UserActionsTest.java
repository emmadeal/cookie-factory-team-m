package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.AlreadyExistingCustomerException;
import fr.unice.polytech.cf.interfaces.InformationUserGiver;
import fr.unice.polytech.cf.interfaces.LoginProcessor;
import fr.unice.polytech.cf.interfaces.SignInProcessor;
import fr.unice.polytech.cf.interfaces.UserModifier;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.OrderRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import fr.unice.polytech.cf.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserActionsTest {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  OrderRepository orderRepository;

    @Autowired
    private LoginProcessor loginProcessor;


    @Autowired
    private SignInProcessor signInProcessor;


    @Autowired
    private UserModifier userModifier;


    @Autowired
    private InformationUserGiver informationUserGiver;

    @Autowired
    private  ShopRepository shopRepository;

    Shop shop;


    @BeforeEach
    void setUp()  {
        userRepository.deleteAll();
        orderRepository.deleteAll();
        shopRepository.deleteAll();

        shop = new Shop("nice");
        shopRepository.save(shop,shop.getId());
    }

    @Test
    public void signIn() throws AlreadyExistingCustomerException {
        signInProcessor.signIn("claire","1234","0640347631","clairemarini@gmail.com",new Order());
        boolean isPresent = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .anyMatch(user -> user.getUserName().equals("claire") && user.getPassword().equals("1234"));
        assertTrue(isPresent);
    }


    @Test
    public void signInError() throws AlreadyExistingCustomerException {
        signInProcessor.signIn("claire","1234","0640347631","clairemarini@gmail.com",new Order());
        Assertions.assertThrows( AlreadyExistingCustomerException.class, () -> {
            signInProcessor.signIn("claire","aha","0640347631","test@gmail.com",new Order());
        });
    }



    @Test
    public void logIn() throws Exception {
        signInProcessor.signIn("claire","1234","0640347631","clairemarini@gmail.com",new Order());
        User user = loginProcessor.login("claire","1234",new Order());
        user = userRepository.findById(user.getId()).get();
        assertEquals("clairemarini@gmail.com", user.getMail());
    }


    @Test
    public void logInError() throws Exception {
        signInProcessor.signIn("claire","1234","0640347631","clairemarini@gmail.com",new Order());
        Assertions.assertThrows( Exception.class, () -> {
            loginProcessor.login("clara","1234",new Order());
        });

        Assertions.assertThrows( Exception.class, () -> {
            loginProcessor.login("claire","123",new Order());
        });


    }

    @Test
    public void joinLoyaltyProgram() throws Exception {
        signInProcessor.signIn("claire","1234","0640347631","clairemarini@gmail.com",new Order());
        User user = loginProcessor.login("claire","1234",new Order());
        userModifier.joinLoyaltyProgram(user.getId());
        assertTrue(userRepository.findById(user.getId()).get().isMembership());
    }


    @Test
    public void subscribeToShopTooGoodToGONotifications() throws Exception {
        signInProcessor.signIn("claire","1234","0640347631","clairemarini@gmail.com",new Order());
        User user = loginProcessor.login("claire","1234",new Order());
        userModifier.subscribeToShopTooGoodToGONotifications(shop.getId(),user.getId());
        assertTrue(shopRepository.findById(shop.getId()).get().getUserObservers().contains(user));
    }



    @Test
    public void unsubscribeToShopTooGoodToGONotifications() throws Exception {
        signInProcessor.signIn("claire","1234","0640347631","clairemarini@gmail.com",new Order());
        User user = loginProcessor.login("claire","1234",new Order());
        userModifier.subscribeToShopTooGoodToGONotifications(shop.getId(),user.getId());
        assertTrue(shopRepository.findById(shop.getId()).get().getUserObservers().contains(user));

        userModifier.unsubscribeToShopTooGoodToGONotifications(shop.getId(),user.getId());
        assertFalse(shopRepository.findById(shop.getId()).get().getUserObservers().contains(user));
    }







}
