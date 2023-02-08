package fr.unice.polytech.cf.cucumber.obsoleting;


import fr.unice.polytech.cf.interfaces.ClientOrderChoices;
import fr.unice.polytech.cf.interfaces.LoginProcessor;
import fr.unice.polytech.cf.interfaces.ObsoleteManager;
import fr.unice.polytech.cf.interfaces.OrderProcessing;
import fr.unice.polytech.cf.interfaces.ShopInformationsGetter;
import fr.unice.polytech.cf.interfaces.SignInProcessor;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Occasion;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Theme;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.ClientRepository;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.OrderRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import fr.unice.polytech.cf.repositories.UserRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class userNoPickUpOrder {


    @Autowired
    private ClientOrderChoices clientOrderChoices;


    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ObsoleteManager obsoleteManager;


    @Autowired
    ShopRepository shopRepository;


    @Autowired
    RecipeRepository recipeRepository;


    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CookRepository cookRepository;


    @Autowired
    private ShopInformationsGetter shopInformationsGetter;


    @Autowired
    private OrderProcessing orderProcessing;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private LoginProcessor loginProcessor;



    @Autowired
    private SignInProcessor signInProcessor;



    Dough dough;
    Flavor flavor;
    Topping topping;

    Recipe recipe;

    Shop shop;

    Cook cook;

    Theme theme;
    Occasion occasion ;

    LocalDateTime localDateTime;

    User user;


    @Before
    public void settingUpContext() {
        clientRepository.deleteAll();
        shopRepository.deleteAll();
        orderRepository.deleteAll();
        recipeRepository.deleteAll();
        userRepository.deleteAll();

    }

    @Given("an  available recipe with  name {string}")
    public void an_available_recipe_with_name(String name) {
        dough = new Dough(1.5F,"dough");
        flavor = new Flavor(1.5F,"flavor");
        topping = new Topping(1.5F,"topping");

        recipe = new Recipe();
        recipe.setName(name);
        recipe.setId(UUID.randomUUID());
        recipe.getIngredients().put(dough,1);
        recipe.getIngredients().put(topping,1);
        recipe.getIngredients().put(flavor,1);
        recipe.setPreparingTime(10);
        recipe.setStateRecipe(StateRecipe.AVAILABLE);
        recipeRepository.save(recipe,recipe.getId());
    }
    @Given("a  shop  in city {string} and tax {double} with a stock")
    public void a_shop_in_city_and_tax_with_a_stock(String city, Double tax) {
        shop = new Shop(city);
        shop.getStock().put(dough,10);
        shop.getStock().put(topping,10);
        shop.getStock().put(flavor,10);;
        shop.setTax(tax);
        shopRepository.save(shop,shop.getId());
    }
    @Given("a  cook with name {string}")
    public void a_cook_with_name(String name) {
        cook = new Cook(name,shop.getId());
        cook.setEndHour(shop.getClosingHour());
        cook.setBeginHour(shop.getOpeningHour());
        cookRepository.save(cook,cook.getId());
    }

    @Given("user with name {string} and password {string} signin and login")
    public void user_with_name_and_password_signin_and_login(String name, String password) throws Exception {
        signInProcessor.signIn(name,password,"0640347631","clairemarini@gmail.com",new Order());
        user = loginProcessor.login(name,password,new Order());
    }

    @When("after {int} minute the user is notify")
    public void after_minute_the_user_is_notify(Integer int1) throws Exception {
        clientOrderChoices.chooseShop(user.getId(),shop);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(user.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.choosePickUpHour(user.getId(),localDateTime );
        clientOrderChoices.validateOrder(user.getId());
        orderProcessing.readyOrders();

        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId())).findFirst().get();
        localDateTime =LocalDateTime.now().minusMinutes(5);
        order.setReadyTime(localDateTime);
        orderRepository.save(order,order.getId());
        obsoleteManager.checkOrderSate();
    }
    @Then("the user have the notifications")
    public void the_user_have_the_notifications() {
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId())).findFirst().get();
        assertSame(order.getState(), StateOrder.READY);
        assertEquals(1, user.getNotifications().size());
    }

    @When("after 1h  the user is notify")
    public void after_1h_the_user_is_notify() throws Exception {
        clientOrderChoices.chooseShop(user.getId(),shop);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(user.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.choosePickUpHour(user.getId(),localDateTime );
        clientOrderChoices.validateOrder(user.getId());
        orderProcessing.readyOrders();

        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId())).findFirst().get();
        localDateTime =LocalDateTime.now().minusMinutes(60);
        order.setReadyTime(localDateTime);
        orderRepository.save(order,order.getId());
        obsoleteManager.checkOrderSate();
    }

    @When("after 2h  the order begin obsolete")
    public void after_2h_the_order_begin_obsolete() throws Exception {
        clientOrderChoices.chooseShop(user.getId(),shop);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(user.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.choosePickUpHour(user.getId(),localDateTime );
        clientOrderChoices.validateOrder(user.getId());
        orderProcessing.readyOrders();

        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId())).findFirst().get();
        localDateTime =LocalDateTime.now().minusMinutes(120);
        order.setReadyTime(localDateTime);
        orderRepository.save(order,order.getId());
        obsoleteManager.checkOrderSate();
    }
    @Then("the order is obsolete")
    public void the_order_is_obsolete() {
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId())).findFirst().get();
        assertSame(order.getState(), StateOrder.OBSOLETE);
    }


}
