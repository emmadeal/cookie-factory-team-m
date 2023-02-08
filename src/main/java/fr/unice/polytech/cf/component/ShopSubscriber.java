package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.NotificationObserverController;
import fr.unice.polytech.cf.interfaces.ShopSubscriberController;
import fr.unice.polytech.cf.model.client.SurpriseBasket;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ShopSubscriber implements ShopSubscriberController {


    private final ShopRepository shopRepository;

    private final NotificationObserverController notificationObserver;


    @Override
    public void addObservers(UUID shopId, User user) throws ResourceNotFoundException {
        Shop shop = getShopById(shopId);
        shop.getUserObservers().add(user);
        shopRepository.save(shop,shopId);
    }

    @Override
    public void removeObservers(UUID shopId,User user) throws ResourceNotFoundException {
        Shop shop = getShopById(shopId);
        shop.getUserObservers().remove(user);
        shopRepository.save(shop,shopId);
    }

    @Override
    public void notifyObservers(SurpriseBasket surpriseBasket,Shop shop) throws ResourceNotFoundException {

        shop.getUserObservers().forEach(user -> {
            try {
                notificationObserver.update(surpriseBasket,user.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Shop getShopById(UUID shopId) throws ResourceNotFoundException {
        return shopRepository.findById(shopId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("No shop found with given id %s", shopId.toString()))
        );
    }

}
