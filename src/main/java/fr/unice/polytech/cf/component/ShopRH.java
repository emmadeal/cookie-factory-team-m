package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.ShopHumanRessources;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.model.shop.ShopManager;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.ShopManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopRH implements ShopHumanRessources {
    private final ShopManagerRepository shopManagerRepository;
    private final CookRepository cookRepository;

    @Override
    public ShopManager hireShopManager(String name, Shop shop)  {
            ShopManager shopManager = new ShopManager( name, shop.getId());
            shopManagerRepository.save(shopManager, shopManager.getId());
            return shopManager;
    }

    @Override
    public void fireShopManager(ShopManager shopManager) throws ResourceNotFoundException {
        if(shopManagerRepository.existsById(shopManager.getId())){
            shopManagerRepository.deleteById(shopManager.getId());
            return;
        }
        throw new ResourceNotFoundException("no shop manager with given id");
    }

    @Override
    public Cook hireCook(String name, Shop shop)  {
        Cook cook = new Cook(name, shop.getId());
        cook.setBeginHour(shop.getOpeningHour());
        cook.setEndHour(shop.getClosingHour());
        cookRepository.save(cook, cook.getId());
        return cook;
    }

    @Override
    public void fireCook(Cook cook) throws ResourceNotFoundException {
        if(cookRepository.existsById(cook.getId())){
            cookRepository.deleteById(cook.getId());
            return;
        }
        throw new ResourceNotFoundException("no cook with given id");
    }
}
