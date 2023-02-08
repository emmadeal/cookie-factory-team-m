package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.EmptyBasketException;
import fr.unice.polytech.cf.exception.NegativeQuantityException;
import fr.unice.polytech.cf.exception.NoPickUpHourtException;
import fr.unice.polytech.cf.exception.NoShopException;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.BasketModifier;
import fr.unice.polytech.cf.interfaces.BasketProcessor;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
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
import fr.unice.polytech.cf.utils.Constant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CartHandlerTest {


    @Autowired
    private BasketProcessor basketProcessor;

    @Autowired
    private BasketModifier basketModifier;

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
    public void emptyCartByDefault() {
        assertEquals(0, claire.getActualOrder().getBasket().size());
    }

    @Test
    public void addItems() throws Exception {
        Item item = new Item(recipe,1);
        Set<Item> basket  = basketModifier.addInBasket(claire,item);
        assertEquals(1,basket.size());
        boolean isPresent = basket.stream().anyMatch(item1 ->
            item1.getRecipe() ==recipe && item1.getQuantity()==1
        );
        assertTrue(isPresent);
        assertEquals(shop.getStock().get(dough),9);
        assertEquals(shop.getStock().get(flavor),9);
        assertEquals(shop.getStock().get(topping),9);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),9);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),9);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),9);
    }


    @Test
    public void deleteItems() throws Exception {
        Item item2 = new Item(recipe,2);
        claire.getActualOrder().getBasket().add(item2);
        Item item1 = new Item(recipe,1);
        Set<Item> basket  = basketModifier.removeInBasket(claire,item1);
        assertEquals(1,basket.size());
        boolean isPresent = basket.stream().anyMatch(item ->
                item.getRecipe() ==recipe && item.getQuantity()==1
        );
        assertTrue(isPresent);
        assertEquals(shop.getStock().get(dough),11);
        assertEquals(shop.getStock().get(flavor),11);
        assertEquals(shop.getStock().get(topping),11);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),11);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),11);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),11);
    }

    @Test
    public void deleteTooItems() throws NegativeQuantityException {
        Item item2 = new Item(recipe,2);
        claire.getActualOrder().getBasket().add(item2);
        Item item1 = new Item(recipe,3);
        Assertions.assertThrows( NegativeQuantityException.class, () -> {
            basketModifier.removeInBasket(claire,item1);
        });
    }

    @Test
    public void validateException() throws Exception {
        claire.getActualOrder().setShop(shop);
        claire.getActualOrder().setPickUpHour(LocalDateTime.now());
        Assertions.assertThrows( EmptyBasketException.class, () -> {
            basketProcessor.validate(claire);
        });

        Item item2 = new Item(recipe,2);
        claire.getActualOrder().getBasket().add(item2);
        claire.getActualOrder().setShop(null);
        Assertions.assertThrows( NoShopException.class, () -> {
            basketProcessor.validate(claire);
        });

        claire.getActualOrder().setShop(shop);
        claire.getActualOrder().setPickUpHour(null);
        Assertions.assertThrows( NoPickUpHourtException.class, () -> {
            basketProcessor.validate(claire);
        });

    }

    @Test
    public void validate() throws Exception {
        claire.getActualOrder().setShop(shop);
        claire.getActualOrder().setPickUpHour(LocalDateTime.now());
        Item item2 = new Item(recipe,2);
        claire.getActualOrder().getBasket().add(item2);
        claire.getActualOrder().setCookId(cook.getId());

        Order order = basketProcessor.validate(claire);
        boolean isPresent = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .anyMatch(order1 -> claire.getId().equals(order1.getClientId()));
        assertTrue(isPresent);

        Order orderRepo = orderRepository.findById(order.getId()).get();
        assertSame(orderRepo.getState(), StateOrder.PAID);

        cook = cookRepository.findById(cook.getId()).get();
        isPresent = cook.getOrdersInProgress().stream().anyMatch(orderInProgress -> orderInProgress==order);
        assertTrue(isPresent);
        assertEquals(2, recipe.getNumberOfOrders());
    }



    @Test
    public void validateUser() throws Exception {
        claire.getActualOrder().setShop(shop);
        claire.getActualOrder().setPickUpHour(LocalDateTime.now());
        Item item2 = new Item(recipe,2);
        claire.getActualOrder().getBasket().add(item2);
        claire.getActualOrder().setCookId(cook.getId());

        Order order = basketProcessor.validate(claire);
        boolean isPresent = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .anyMatch(order1 -> claire.getId().equals(order1.getClientId()));
        assertTrue(isPresent);

        Order orderRepo = orderRepository.findById(order.getId()).get();
        assertSame(orderRepo.getState(), StateOrder.PAID);

        cook = cookRepository.findById(cook.getId()).get();
        isPresent = cook.getOrdersInProgress().stream().anyMatch(orderInProgress -> orderInProgress==order);
        assertTrue(isPresent);
        assertEquals(2, recipe.getNumberOfOrders());
    }




}