package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.BasketModifier;
import fr.unice.polytech.cf.interfaces.BasketProcessor;
import fr.unice.polytech.cf.interfaces.LoginProcessor;
import fr.unice.polytech.cf.interfaces.Payment;
import fr.unice.polytech.cf.interfaces.SignInProcessor;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
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
import fr.unice.polytech.cf.repositories.UserRepository;
import fr.unice.polytech.cf.utils.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PaiementServiceTest {

    @Autowired
    private BasketProcessor basketProcessor;

    @Autowired
    private ClientRepository clientRepository;


    @Autowired
    private UserRepository userRepository;


    @Autowired
    ShopRepository shopRepository;


    @Autowired
    RecipeRepository recipeRepository;


    @Autowired
    OrderRepository orderRepository;


    @Autowired
    CookRepository cookRepository;


    @Autowired
    private LoginProcessor loginProcessor;


    @Autowired
    private SignInProcessor signInProcessor;

    Client claire;


    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;

    Recipe recipe;

    Shop shop;

    Cook cook;


    @BeforeEach
    void setUp() throws ResourceNotFoundException {
        clientRepository.deleteAll();
        orderRepository.deleteAll();
        cookRepository.deleteAll();
        recipeRepository.deleteAll();
        shopRepository.deleteAll();
        userRepository.deleteAll();
        claire = new Client();
        Order order = new Order();
        shop = new Shop("nice");
        order.setShop(shop);
        order.setBasket(new HashSet<>());
        order.setClientId(claire.getId());
        order.setLoginClient(false);
        claire.setActualOrder(order);

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
        shopRepository.save(shop,shop.getId());
        clientRepository.save(claire,claire.getId());

        cook = new Cook("marie",shop.getId());
        cookRepository.save(cook,cook.getId());
    }


    @Test
    public void validate() throws Exception {
        signInProcessor.signIn("claire","1234","0640347631","clairemarini@gmail.com",new Order());
        User user = loginProcessor.login("claire","1234",new Order());
        user.getActualOrder().setShop(shop);
        user.getActualOrder().setPickUpHour(LocalDateTime.now());
        Item item2 = new Item(recipe,2);
        user.getActualOrder().getBasket().add(item2);
        user.getActualOrder().setCookId(cook.getId());
        user.getActualOrder().setLoginClient(true);
        userRepository.save(user,user.getId());
        Order order = basketProcessor.validate(user);
        boolean isPresent = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .anyMatch(order1 -> user.getId().equals(order1.getClientId()));
        assertTrue(isPresent);

        Order orderRepo = orderRepository.findById(order.getId()).get();
        assertSame(orderRepo.getState(), StateOrder.PAID);

        cook = cookRepository.findById(cook.getId()).get();
        isPresent = cook.getOrdersInProgress().stream().anyMatch(orderInProgress -> orderInProgress==order);
        assertTrue(isPresent);

        recipe =recipeRepository.findById(recipe.getId()).get();
        assertEquals(2, recipe.getNumberOfOrders());

    }

}
