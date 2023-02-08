package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.NotificationSender;
import fr.unice.polytech.cf.model.client.Notification;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Phone implements NotificationSender{

    private final UserRepository userRepository;

    @Override
    public void notifyUser(Order order, int time) throws ResourceNotFoundException {
        User user =userRepository.findById(order.getClientId()).orElseThrow(
                () -> new ResourceNotFoundException(String.format("No user found with given id %s",order.getClientId().toString())));
        String timeDisplay = time == 5 ?"5 minutes": "1 heure";
        String message = String.format("Votre commande numéro %s est prête depuis %s , elle vous attend au magasin de %s.",order.getId().toString(),timeDisplay,order.getShop().getCity());
        Notification notification = new Notification("Obsolete order",message);
        user.getNotifications().add(notification);
        userRepository.save(user,user.getId());
    }


}
