package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.CookAvailabilityVerifier;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.IngredientCreator;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.ClientRepository;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.OrderRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import fr.unice.polytech.cf.utils.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PlanningTest {

    @Autowired
    private CookRepository cookRepository;

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    private CookAvailabilityVerifier cookAvailabilityVerifier;

    @Autowired
    RecipeRepository recipeRepository;


    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    Shop shop;

    Cook cook;

    Client claire ;

    Order order ;

    Order order2 ;

    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;

    Recipe recipe;


    @BeforeEach
    void setUp() throws ResourceNotFoundException {
        clientRepository.deleteAll();
        orderRepository.deleteAll();
        cookRepository.deleteAll();
        recipeRepository.deleteAll();
        shopRepository.deleteAll();

        dough = IngredientCreator.createIngredient(1.5f,"dough", Constant.TYPE_DOUGH);
        flavor = IngredientCreator.createIngredient(1.5f,"flavor", Constant.TYPE_FLAVOR);
        topping = IngredientCreator.createIngredient(1.5f,"topping", Constant.TYPE_TOPPING);

        recipe = new Recipe();
        recipe.setId(UUID.randomUUID());
        recipe.getIngredients().put(dough,1);
        recipe.getIngredients().put(topping,1);
        recipe.getIngredients().put(flavor,1);
        recipe.setPreparingTime(10);

        claire = new Client();
        shop = new Shop("nice");
        cook = new Cook("marie",shop.getId());
        cook.setEndHour(shop.getClosingHour());
        cook.setBeginHour(shop.getOpeningHour());

        order = new Order();
        order.setClientId(claire.getId());
        order.setShop(shop);
        order.setLoginClient(false);
        order.getBasket().add(new Item(recipe,2));
        order.setPickUpHour(LocalDateTime.of(2025,12,10,10,10,10));
        claire.setActualOrder(order);
        shopRepository.save(shop,shop.getId());
        cookRepository.save(cook,cook.getId());
        clientRepository.save(claire,claire.getId());

        order2 = new Order();
        order2.setClientId(claire.getId());
        order2.setShop(shop);
        order2.setLoginClient(false);
        order2.getBasket().add(new Item(recipe,2));
        order2.setPickUpHour(LocalDateTime.of(2025,12,10,10,10,10));

    }


    @Test
    public void SearchCook() throws Exception {
        Cook cook1 =cookAvailabilityVerifier.SearchCook(order);
        assertTrue(cook1!=null);
        cook1 = cookRepository.findById(cook1.getId()).get();
    }

    @Test
    public void SearchCookErrorBeginHour() throws Exception {
        order.setPickUpHour(LocalDateTime.of(2025,12,10,7,10,10));
        claire.setActualOrder(order);
        clientRepository.save(claire,claire.getId());
        Cook cook1 =cookAvailabilityVerifier.SearchCook(order);
        assertNull(cook1);
    }

    @Test
    public void SearchCookErrorEndHour() throws Exception {
        order.setPickUpHour(LocalDateTime.of(2025,12,10,22,10,10));
        claire.setActualOrder(order);
        clientRepository.save(claire,claire.getId());
        Cook cook1 =cookAvailabilityVerifier.SearchCook(order);
        assertNull(cook1);
    }


    @Test
    public void SearchCookErrorAlreadyOrder() throws Exception {
        Cook cook1 =cookAvailabilityVerifier.SearchCook(order);
        assertNotNull(cook1);
        cook.getOrdersInProgress().add(order);
        cookRepository.save(cook,cook.getId());
        Cook cook2 =cookAvailabilityVerifier.SearchCook(order2);
        assertNull(cook2);

        order2.setPickUpHour(LocalDateTime.of(2025,12,10,10,15,10));
         cook2 =cookAvailabilityVerifier.SearchCook(order2);
        assertNull(cook2);

        order2.setPickUpHour(LocalDateTime.of(2025,12,10,10,5,10));
        cook2 =cookAvailabilityVerifier.SearchCook(order2);
        assertNull(cook2);
    }
}
