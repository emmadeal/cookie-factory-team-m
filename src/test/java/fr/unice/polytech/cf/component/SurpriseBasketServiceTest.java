package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.SurpriseBasketController;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.SurpriseBasket;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.IngredientCreator;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.OrderRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import fr.unice.polytech.cf.repositories.SurpriseBasketRepository;
import fr.unice.polytech.cf.utils.Constant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest
public class SurpriseBasketServiceTest {

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    ShopRepository shopRepository;



    @Autowired
    OrderRepository orderRepository;


    @Autowired
    CookRepository cookRepository;

    @Autowired
    SurpriseBasketController surpriseBasketController;


    @Autowired
   SurpriseBasketRepository surpriseBasketRepository;

    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;

    Recipe recipe;

    Shop shop;

    Cook cook;

    Order order;

    Order order2;

    @BeforeEach
    void setUp() throws ResourceNotFoundException {
        orderRepository.deleteAll();
        cookRepository.deleteAll();
        recipeRepository.deleteAll();
        shopRepository.deleteAll();
        surpriseBasketRepository.deleteAll();
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
        shopRepository.save(shop,shop.getId());

        cook = new Cook("marie",shop.getId());
        cook.setEndHour(shop.getClosingHour());
        cook.setBeginHour(shop.getOpeningHour());


        cookRepository.save(cook,cook.getId());

         order = new Order();
         order.setState(StateOrder.OBSOLETE);
         order.setShop(shop);
        Set<Item> basket = new HashSet<>();
        basket.add(new Item(recipe,1));
        order.setBasket(basket);
        order.setClientId(UUID.randomUUID());


        order2 = new Order();
        order2.setState(StateOrder.OBSOLETE);
        basket = new HashSet<>();
        basket.add(new Item(recipe,7));
        order2.setBasket(basket);
        order2.setClientId(UUID.randomUUID());
        order2.setShop(shop);
        orderRepository.save(order,order.getId());
        orderRepository.save(order2,order2.getId());
    }

    @Test
    public void createSurpriseBasket() throws Exception {
        long number = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order -> StateOrder.OBSOLETE.equals(order.getState())).count();
        assertSame(number, (long)2);
        surpriseBasketController.createSurpriseBasket();
        number = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order -> StateOrder.TOOGOODTOGO.equals(order.getState())).count();
        assertSame(number, (long)2);

        number = StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(surpriseBasket -> StateOrder.READY.equals(surpriseBasket.getState())).count();
        assertSame(number, (long)2);
    }



    @Test
    public void createSurpriseBasketWithInfo() throws Exception {
        orderRepository.deleteById(order.getId());
        long number = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order -> StateOrder.OBSOLETE.equals(order.getState())).count();
        assertSame(number, (long)1);
        surpriseBasketController.createSurpriseBasket();
        number = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order -> StateOrder.TOOGOODTOGO.equals(order.getState())).count();
        assertSame(number, (long)1);

        number = StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(surpriseBasket -> StateOrder.READY.equals(surpriseBasket.getState())).count();
        assertSame(number, (long)1);
        SurpriseBasket surpriseBasket =StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(surpriseBasket2 -> StateOrder.READY.equals(surpriseBasket2.getState())).findFirst().get();

        assertSame((int)surpriseBasket.getPrice(), (int)order2.calculatePrice()/2);
    }


    @Test
    public void reserveSurpriseBasket() throws Exception {;
        orderRepository.deleteById(order.getId());
        surpriseBasketController.createSurpriseBasket();
        long number = StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(surpriseBasket -> StateOrder.READY.equals(surpriseBasket.getState())).count();
        assertSame(number, (long)1);
        SurpriseBasket surpriseBasket = StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(surpriseBasket2 -> StateOrder.READY.equals(surpriseBasket2.getState())).findFirst().get();
        assertSame(surpriseBasketController.reserveSurpriseBasket(surpriseBasket), true);

         surpriseBasket =StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(surpriseBasket2 -> StateOrder.READY.equals(surpriseBasket2.getState())).findFirst().get();

        assertSame(surpriseBasket.isReserve(), true);
        assertSame(surpriseBasket.getTooGoodToGoId(), surpriseBasket.getTooGoodToGoId());
    }


    @Test
    public void reserveSurpriseBasketAlreadyRserve() throws Exception {;
        orderRepository.deleteById(order.getId());
        surpriseBasketController.createSurpriseBasket();
        long number = StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(surpriseBasket -> StateOrder.READY.equals(surpriseBasket.getState())).count();
        assertSame(number, (long)1);
        SurpriseBasket surpriseBasket = StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(surpriseBasket2 -> StateOrder.READY.equals(surpriseBasket2.getState())).findFirst().get();
        assertSame(surpriseBasketController.reserveSurpriseBasket(surpriseBasket), true);

        surpriseBasket =StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(surpriseBasket2 -> StateOrder.READY.equals(surpriseBasket2.getState())).findFirst().get();

        assertSame(surpriseBasket.isReserve(), true);
        assertSame(surpriseBasket.getTooGoodToGoId(), surpriseBasket.getTooGoodToGoId());

        assertSame(surpriseBasketController.reserveSurpriseBasket(surpriseBasket), false);

        surpriseBasket =StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(surpriseBasket2 -> StateOrder.READY.equals(surpriseBasket2.getState())).findFirst().get();

        assertSame(surpriseBasket.isReserve(), true);
        assertSame(surpriseBasket.getTooGoodToGoId(),surpriseBasket.getTooGoodToGoId());
    }


    @Test
    public void reserveSurpriseBasketError() throws Exception {;
        Assertions.assertThrows( ResourceNotFoundException.class, () -> {
            surpriseBasketController.reserveSurpriseBasket(new SurpriseBasket());

        });
    }

    @Test
    public void giveSurpriseBasket() throws Exception {;
        orderRepository.deleteById(order.getId());
        surpriseBasketController.createSurpriseBasket();
        long number = StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(surpriseBasket -> StateOrder.READY.equals(surpriseBasket.getState())).count();
        assertSame(number, (long)1);
        SurpriseBasket surpriseBasket = StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(surpriseBasket2 -> StateOrder.READY.equals(surpriseBasket2.getState())).findFirst().get();
        assertSame(surpriseBasketController.reserveSurpriseBasket(surpriseBasket), true);

        SurpriseBasket surpriseBasket3;
        surpriseBasketController.giveSurpriseBasket(surpriseBasket.getTooGoodToGoId());
        surpriseBasket3 =StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(surpriseBasket2 -> surpriseBasket.getTooGoodToGoId().equals(surpriseBasket2.getTooGoodToGoId())).findFirst().get();
        assertSame(surpriseBasket3.getState(), StateOrder.TAKEN);
    }

    @Test
    public void giveSurpriseBasketError() throws Exception {;
        Assertions.assertThrows( ResourceNotFoundException.class, () -> {
            surpriseBasketController.giveSurpriseBasket(UUID.randomUUID());

        });
    }

}
