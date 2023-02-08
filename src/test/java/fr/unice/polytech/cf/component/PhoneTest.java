package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.LoginProcessor;
import fr.unice.polytech.cf.interfaces.NotificationSender;
import fr.unice.polytech.cf.interfaces.ShopSubscriberController;
import fr.unice.polytech.cf.interfaces.SignInProcessor;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.SurpriseBasket;
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

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PhoneTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private LoginProcessor loginProcessor;


    @Autowired
    private SignInProcessor signInProcessor;


    @Autowired
    private NotificationSender notificationSender;

    @Autowired
     ShopSubscriberController shopSubscriberController;

    @Autowired
    ShopRepository shopRepository;

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
    public void notifyUser5() throws Exception {
        signInProcessor.signIn("claire","1234","0640347631","clairemarini@gmail.com",new Order());
        User user = loginProcessor.login("claire","1234",new Order());
        Order order = new Order();
        order.setClientId(user.getId());
        order.setShop(shop);
        user.setActualOrder(order);
        userRepository.save(user,user.getId());
        notificationSender.notifyUser(order,5);
        user = userRepository.findById(user.getId()).get();
        String timeDisplay = "5 minutes";
        assertEquals(user.getNotifications().size(),1);
        assertEquals(user.getNotifications().get(0).getDescription(),String.format("Votre commande numéro %s est prête depuis %s , elle vous attend au magasin de %s.",user.getActualOrder().getId().toString(),timeDisplay,user.getActualOrder().getShop().getCity()));
    }

    @Test
    public void notifyUser60() throws Exception {
        signInProcessor.signIn("claire","1234","0640347631","clairemarini@gmail.com",new Order());
        User user = loginProcessor.login("claire","1234",new Order());
        Order order = new Order();
        order.setClientId(user.getId());
        order.setShop(shop);
        user.setActualOrder(order);
        userRepository.save(user,user.getId());
        notificationSender.notifyUser(order,60);
        user = userRepository.findById(user.getId()).get();
        String timeDisplay = "1 heure";
        assertEquals(user.getNotifications().size(),1);
        assertEquals(user.getNotifications().get(0).getDescription(),String.format("Votre commande numéro %s est prête depuis %s , elle vous attend au magasin de %s.",user.getActualOrder().getId().toString(),timeDisplay,user.getActualOrder().getShop().getCity()));
    }

    @Test
    public void notifyUserErrorNoClientId() throws Exception {
        Order order = new Order();
        order.setShop(shop);
        order.setClientId(UUID.randomUUID());
        Assertions.assertThrows( ResourceNotFoundException.class, () -> {
            notificationSender.notifyUser(order,60);
        });
    }


    @Test
    public void notifyUserTooGoodToGo() throws Exception {
        signInProcessor.signIn("claire","1234","0640347631","clairemarini@gmail.com",new Order());
        User user = loginProcessor.login("claire","1234",new Order());
        Order order = new Order();
        order.setClientId(user.getId());
        order.setShop(shop);
        user.setActualOrder(order);
        userRepository.save(user,user.getId());
        shopSubscriberController.addObservers(shop.getId(),user);
        shopSubscriberController.notifyObservers(new SurpriseBasket(new HashSet<>(),12,shop,"description"),shop);
        assertEquals(user.getNotifications().size(),1);
        assertEquals(user.getNotifications().get(0).getDescription(),"Un nouveau panier est disponible : "+"description");
    }

}
