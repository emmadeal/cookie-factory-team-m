package fr.unice.polytech.cf.component;


import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.FactoryHumanRessources;
import fr.unice.polytech.cf.interfaces.FactoryRessources;
import fr.unice.polytech.cf.model.factory.Chef;
import fr.unice.polytech.cf.model.factory.FactoryManager;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.repositories.CatalogRepository;
import fr.unice.polytech.cf.repositories.ChefRepository;
import fr.unice.polytech.cf.repositories.FactoryManagerRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FactoryAllRessourcesServiceTest {


    @Autowired
    private FactoryHumanRessources factoryHumanRessources;


    @Autowired
    private FactoryRessources factoryRessources;


    @Autowired
    ChefRepository chefRepository;


    @Autowired
    FactoryManagerRepository factoryManagerRepository;


    @Autowired
    ShopRepository shopRepository;

    @Autowired
    private CatalogRepository catalogRepository;


    @BeforeEach
    void setUp()  {
        chefRepository.deleteAll();
        factoryManagerRepository.deleteAll();
        shopRepository.deleteAll();
        catalogRepository.deleteAll();
    }

    @Test
    public void hireFactoryManager() throws Exception {
        factoryHumanRessources.hireFactoryManager("claire");
        boolean isPresent = StreamSupport.stream(factoryManagerRepository.findAll().spliterator(), false)
                .anyMatch(factoryManager -> "claire".equals(factoryManager.getName()));
        assertTrue(isPresent);
    }


    @Test
    public void fireFactoryManager() throws Exception {
        factoryHumanRessources.hireFactoryManager("claire");
        boolean isPresent = StreamSupport.stream(factoryManagerRepository.findAll().spliterator(), false)
                .anyMatch(factoryManager -> "claire".equals(factoryManager.getName()));
        assertTrue(isPresent);
        FactoryManager factoryManager = StreamSupport.stream(factoryManagerRepository.findAll().spliterator(), false)
                .filter(factoryManager2 -> "claire".equals(factoryManager2.getName())).findFirst().get();
        factoryHumanRessources.fireFactoryManager(factoryManager);
        isPresent = StreamSupport.stream(factoryManagerRepository.findAll().spliterator(), false)
                .anyMatch(factoryManager3 -> "claire".equals(factoryManager3.getName()));
        assertFalse(isPresent);
    }


    @Test
    public void fireFactoryManagerError() throws Exception {
        factoryHumanRessources.hireFactoryManager("claire");
        boolean isPresent = StreamSupport.stream(factoryManagerRepository.findAll().spliterator(), false)
                .anyMatch(factoryManager -> "claire".equals(factoryManager.getName()));
        assertTrue(isPresent);
        Assertions.assertThrows( ResourceNotFoundException.class, () -> {
            factoryHumanRessources.fireFactoryManager(new FactoryManager("livreur de pizza"));
        });

    }



    @Test
    public void hireChef() throws Exception {
        factoryHumanRessources.hireChef("claire");
        boolean isPresent = StreamSupport.stream(chefRepository.findAll().spliterator(), false)
                .anyMatch(factoryManager -> "claire".equals(factoryManager.getName()));
        assertTrue(isPresent);
    }


    @Test
    public void fireChef() throws Exception {
        factoryHumanRessources.hireChef("claire");
        boolean isPresent = StreamSupport.stream(chefRepository.findAll().spliterator(), false)
                .anyMatch(chef -> "claire".equals(chef.getName()));
        assertTrue(isPresent);
        Chef chef = StreamSupport.stream(chefRepository.findAll().spliterator(), false)
                .filter(chef2 -> "claire".equals(chef2.getName())).findFirst().get();
        factoryHumanRessources.fireChef(chef);
        isPresent = StreamSupport.stream(chefRepository.findAll().spliterator(), false)
                .anyMatch(chef3 -> "claire".equals(chef3.getName()));
        assertFalse(isPresent);
    }

    @Test
    public void fireChefError() throws Exception {
        factoryHumanRessources.hireChef("claire");
        boolean isPresent = StreamSupport.stream(chefRepository.findAll().spliterator(), false)
                .anyMatch(chef -> "claire".equals(chef.getName()));
        assertTrue(isPresent);
        Assertions.assertThrows( ResourceNotFoundException.class, () -> {
            factoryHumanRessources.fireChef(new Chef("livreur de pizza"));
        });

    }

    @Test
    public void addShop() throws Exception {
        factoryRessources.addShop("nice");
        boolean isPresent = StreamSupport.stream(shopRepository.findAll().spliterator(), false)
                .anyMatch(factoryManager -> "nice".equals(factoryManager.getCity()));
        assertTrue(isPresent);
    }


    @Test
    public void addIngredientsToCatalog() throws Exception {
        factoryRessources.addIngredientsToCatalog();
        List<Ingredient> ingredientList = StreamSupport.stream(catalogRepository.findAll().spliterator(), false).toList();
        assertTrue(ingredientList.size()>0);
    }


}
