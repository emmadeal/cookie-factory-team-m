package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.client.SurpriseBasket;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.model.shop.Shop;

import java.util.UUID;

public interface ShopSubscriberController {

    void addObservers(UUID shopId, User user) throws ResourceNotFoundException;

    void removeObservers(UUID shopId,User user) throws ResourceNotFoundException;

    void notifyObservers(SurpriseBasket surpriseBasket,Shop shop)throws Exception ;
}
