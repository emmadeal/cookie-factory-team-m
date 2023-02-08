package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.BadStateException;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.CheckRetrieveOrder;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest
public class CounterCollectTest {

    @Autowired
    private CheckRetrieveOrder checkRetrieveOrder;


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

    @Autowired
    private OrderProcessing orderProcessing;


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
    public void canGiveOrder() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);;
        LocalDateTime localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.addInCashier(claire.getId(),new Item(recipe,1));
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        Client claire2 = clientRepository.findById(claire.getId()).get();
        clientOrderChoices.validateOrder(claire.getId());
        claire = clientRepository.findById(claire.getId()).get();
        orderProcessing.readyOrders();
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire2.getId().equals(order1.getClientId())).findFirst().get();

        assertSame(order.getState(), StateOrder.READY);
        checkRetrieveOrder.giveOrder(order.getId());
         order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire2.getId().equals(order1.getClientId())).findFirst().get();

        assertSame(order.getState(), StateOrder.TAKEN);
        assertSame(clientRepository.findById(claire.getId()).isPresent(), false);


    }

    @Test
    public void canGiveOrderErrorState() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);;
        LocalDateTime localDateTime =LocalDateTime.of(2025,12,10,10,10);
        clientOrderChoices.addInCashier(claire.getId(),new Item(recipe,1));
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        clientOrderChoices.validateOrder(claire.getId());
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire.getId().equals(order1.getClientId())).findFirst().get();

        Assertions.assertThrows( BadStateException.class, () -> {
            checkRetrieveOrder.giveOrder(order.getId());
        });
    }

    @Test
    public void canGiveOrderErrorID() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);;
        LocalDateTime localDateTime =LocalDateTime.of(2025,12,10,10,10);
        clientOrderChoices.addInCashier(claire.getId(),new Item(recipe,1));
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        clientOrderChoices.validateOrder(claire.getId());
        Assertions.assertThrows( BadStateException.class, () -> {
            checkRetrieveOrder.giveOrder(UUID.randomUUID());
        });
    }

}
