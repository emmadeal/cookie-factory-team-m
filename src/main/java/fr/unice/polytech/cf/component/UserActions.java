package fr.unice.polytech.cf.component;


import fr.unice.polytech.cf.exception.AlreadyExistingCustomerException;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.InformationUserGiver;
import fr.unice.polytech.cf.interfaces.LoginProcessor;
import fr.unice.polytech.cf.interfaces.ShopSubscriberController;
import fr.unice.polytech.cf.interfaces.SignInProcessor;
import fr.unice.polytech.cf.interfaces.UserModifier;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.repositories.OrderRepository;
import fr.unice.polytech.cf.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActions implements LoginProcessor, SignInProcessor, UserModifier, InformationUserGiver  {
    
    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final ShopSubscriberController shopSubscriberController;

    @Override
    public User login(String name , String password,Order order) throws Exception {
        Optional<User> optionalUser = findUserByUserName(name);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(user.getPassword().equals(password)){
                // si deja commande en cours
                user.setActualOrder(order);
                userRepository.save(user,user.getId());
                return user;
            }
        }
        throw new Exception("Aucun utlisateur ne correspond a cet user name et ce mot de passe");
    }
    @Override
    public User signIn(String name, String password, String phone, String mail, Order order) throws AlreadyExistingCustomerException {
        if(findUserByUserName(name).isPresent()){
            throw new AlreadyExistingCustomerException(name);
        }
        User user = new User(name,mail ,password,phone);
        user.setActualOrder(order);
        userRepository.save(user,user.getId());
        return user;
    }

    @Override
    public User joinLoyaltyProgram(UUID userId) throws ResourceNotFoundException {
        User user =getUserById(userId);
        user.setMembership(true);
        userRepository.save(user,user.getId());
        return user;
    }

    @Override
    public User subscribeToShopTooGoodToGONotifications(UUID shopId,UUID userId) throws ResourceNotFoundException {
        User user =getUserById(userId);
        shopSubscriberController.addObservers(shopId,user);
        return user;
    }

    @Override
    public User unsubscribeToShopTooGoodToGONotifications(UUID shopId,UUID userId) throws ResourceNotFoundException {
        User user = getUserById(userId);
        shopSubscriberController.removeObservers(shopId, user);
        return user;
    }


    @Override
    public List<Order> getHistoricOrder(User user){
        return StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order -> user.getId().equals(order.getClientId())).toList();
    }

    public User getUserById(UUID userId) throws ResourceNotFoundException {
        return userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("No user found with given id %s",userId.toString()))
        );
    }

    public Optional<User> findUserByUserName(String name) {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(user -> name.equals(user.getUserName())).findAny();
    }


}
