package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.interfaces.ShopHumanRessources;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.model.shop.ShopManager;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.ShopManagerRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ShopPersonalServiceTest {

    @Autowired
    private ShopHumanRessources shopHumanRessources;


    @Autowired
    CookRepository cookRepository;


    @Autowired
    ShopManagerRepository shopManagerRepository;


    @Autowired
    ShopRepository shopRepository;

    Shop shop ;




    @BeforeEach
    void setUp()  {
        cookRepository.deleteAll();
        shopManagerRepository.deleteAll();
        shopRepository.deleteAll();
        shop = new Shop("nice");
        shopRepository.save(shop,shop.getId());
    }

    @Test
    public void hireShopManager() throws Exception {
        shopHumanRessources.hireShopManager("claire",shop);
        boolean isPresent = StreamSupport.stream(shopManagerRepository.findAll().spliterator(), false)
                .anyMatch(factoryManager -> "claire".equals(factoryManager.getName()));
        assertTrue(isPresent);
    }


    @Test
    public void fireShopManager() throws Exception {
        shopHumanRessources.hireShopManager("claire",shop);
        boolean isPresent = StreamSupport.stream(shopManagerRepository.findAll().spliterator(), false)
                .anyMatch(shopManager -> "claire".equals(shopManager.getName()));
        assertTrue(isPresent);
        ShopManager shopManager = StreamSupport.stream(shopManagerRepository.findAll().spliterator(), false)
                .filter(shopManager2 -> "claire".equals(shopManager2.getName())).findFirst().get();
        shopHumanRessources.fireShopManager(shopManager);
        isPresent = StreamSupport.stream(shopManagerRepository.findAll().spliterator(), false)
                .anyMatch(shopManager3 -> "claire".equals(shopManager3.getName()));
        assertFalse(isPresent);
    }

    @Test
    public void hireCook() throws Exception {
        shopHumanRessources.hireCook("claire",shop);
        boolean isPresent = StreamSupport.stream(cookRepository.findAll().spliterator(), false)
                .anyMatch(cook -> "claire".equals(cook.getName()));
        assertTrue(isPresent);
    }


    @Test
    public void fireCook() throws Exception {
        shopHumanRessources.hireCook("claire",shop);
        boolean isPresent = StreamSupport.stream(cookRepository.findAll().spliterator(), false)
                .anyMatch(cook -> "claire".equals(cook.getName()));
        assertTrue(isPresent);
        Cook cook = StreamSupport.stream(cookRepository.findAll().spliterator(), false)
                .filter(cook2 -> "claire".equals(cook2.getName())).findFirst().get();
        shopHumanRessources.fireCook(cook);
        isPresent = StreamSupport.stream(cookRepository.findAll().spliterator(), false)
                .anyMatch(cook3 -> "claire".equals(cook3.getName()));
        assertFalse(isPresent);
    }


}
