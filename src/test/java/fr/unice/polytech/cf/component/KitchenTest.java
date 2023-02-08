package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.ClientOrderChoices;
import fr.unice.polytech.cf.interfaces.OrderProcessing;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.IngredientCreator;
import fr.unice.polytech.cf.model.recipe.Occasion;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Theme;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.ClientRepository;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.OccasionRepository;
import fr.unice.polytech.cf.repositories.OrderRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import fr.unice.polytech.cf.repositories.ThemeRepository;
import fr.unice.polytech.cf.utils.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class KitchenTest {

    @Autowired
    private OrderProcessing orderProcessing;


    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    ShopRepository shopRepository;


    @Autowired
    RecipeRepository recipeRepository;


    @Autowired
    OrderRepository orderRepository;


    @Autowired
    CookRepository cookRepository;

    @Autowired
    private OccasionRepository occasionRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ClientOrderChoices clientOrderChoices;

    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;

    Recipe recipe;

    Shop shop;

    Cook cook;

    Theme theme;
    Occasion occasion ;


    @BeforeEach
    void setUp() throws ResourceNotFoundException {
        clientRepository.deleteAll();
        orderRepository.deleteAll();
        cookRepository.deleteAll();
        recipeRepository.deleteAll();
        shopRepository.deleteAll();
        shop = new Shop("nice");

        dough = IngredientCreator.createIngredient(1.5f,"dough", Constant.TYPE_DOUGH);
        flavor = IngredientCreator.createIngredient(1.5f,"flavor", Constant.TYPE_FLAVOR);
        topping = IngredientCreator.createIngredient(1.5f,"topping", Constant.TYPE_TOPPING);


        recipe = new Recipe();
        recipe.setId(UUID.randomUUID());
        recipe.getIngredients().put(dough,1);
        recipe.getIngredients().put(topping,1);
        recipe.getIngredients().put(flavor,1);
        recipe.setPreparingTime(10);

        recipeRepository.save(recipe,recipe.getId());

        shop.getStock().put(dough,10);
        shop.getStock().put(topping,10);
        shop.getStock().put(flavor,10);

        theme = new Theme("fleur");
        themeRepository.save(theme,theme.getId());
        occasion = new Occasion("anniversaire");
        occasionRepository.save(occasion,occasion.getId());
        shop.getOccasionIds().add(occasion.getId());
        cook = new Cook("marie",shop.getId());
        cook.setEndHour(shop.getClosingHour());
        cook.setBeginHour(shop.getOpeningHour());
        cook.getThemeIds().add(theme.getId());

        shopRepository.save(shop,shop.getId());
        cookRepository.save(cook,cook.getId());
    }


    @Test
    public void process() throws Exception {
        Order order = new Order();
        order.setCookId(cook.getId());
        orderProcessing.process(order);
        Cook cook2 = cookRepository.findById(cook.getId()).get();
        boolean isPresent = cook2.getOrdersInProgress().stream().anyMatch(order1 -> order1.getId()==order.getId());
        assertTrue(isPresent);
    }

    @Test
    public void validate() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);;
        LocalDateTime localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.addInCashier(claire.getId(),new Item(recipe,1));
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        clientOrderChoices.validateOrder(claire.getId());
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire.getId().equals(order1.getClientId())).findFirst().get();
        cook = cookRepository.findById(cook.getId()).get();
        boolean isPresent = cook.getOrdersInProgress().stream().anyMatch(order1 -> order.getId()==order1.getId());
        assertTrue(isPresent);
        orderProcessing.readyOrders();
        Order order2 = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire.getId().equals(order1.getClientId())).findFirst().get();

        assertSame(order2.getState(), StateOrder.READY);
        cook = cookRepository.findById(cook.getId()).get();
        isPresent = cook.getOrdersInProgress().stream().anyMatch(order1 -> order1.getId()==order2.getId());
        assertFalse(isPresent);
    }

    @Test
    public void stopOrderProcessing() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);;
        LocalDateTime localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.addInCashier(claire.getId(),new Item(recipe,1));
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        clientOrderChoices.validateOrder(claire.getId());
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire.getId().equals(order1.getClientId())).findFirst().get();
        cook = cookRepository.findById(cook.getId()).get();
        boolean isPresent = cook.getOrdersInProgress().stream().anyMatch(order1 -> order.getId()==order1.getId());
        assertTrue(isPresent);
        orderProcessing.stopOrderProcessing(order);

        Order order2 = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire.getId().equals(order1.getClientId())).findFirst().get();
        assertSame(order.getState(), StateOrder.PAID);
        cook = cookRepository.findById(cook.getId()).get();
        isPresent = cook.getOrdersInProgress().stream().anyMatch(order1 -> order1.getId()==order2.getId());
        assertFalse(isPresent);
    }

}
