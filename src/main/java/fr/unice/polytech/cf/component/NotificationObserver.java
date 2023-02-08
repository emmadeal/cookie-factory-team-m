package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.NotificationObserverController;
import fr.unice.polytech.cf.model.client.Notification;
import fr.unice.polytech.cf.model.client.SurpriseBasket;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationObserver implements NotificationObserverController {

    private final UserRepository userRepository;

    @Override
    public void update(SurpriseBasket surpriseBasket, UUID userId) throws Exception {
        User user =getUserById(userId);
        user.getNotifications().add(new Notification("TooGoodToGo notification","Un nouveau panier est disponible : "+surpriseBasket.getDescription()));
        userRepository.save(user,user.getId());
    }

    public User getUserById(UUID userId) throws ResourceNotFoundException {
        return userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("No user found with given id %s",userId.toString()))
        );
    }
}
