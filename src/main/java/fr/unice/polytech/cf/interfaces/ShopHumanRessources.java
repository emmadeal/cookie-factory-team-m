package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.model.shop.ShopManager;

public interface ShopHumanRessources {

    ShopManager hireShopManager(String name, Shop shop) ;
    void fireShopManager(ShopManager shopManager) throws ResourceNotFoundException;

    Cook hireCook(String name, Shop shop) ;
    void fireCook(Cook cook) throws ResourceNotFoundException;
}
