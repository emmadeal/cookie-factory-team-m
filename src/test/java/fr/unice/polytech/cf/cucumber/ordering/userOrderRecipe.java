package fr.unice.polytech.cf.cucumber.ordering;

import fr.unice.polytech.cf.exception.CantOrderException;
import fr.unice.polytech.cf.interfaces.CheckRetrieveOrder;
import fr.unice.polytech.cf.interfaces.ClientOrderChoices;
import fr.unice.polytech.cf.interfaces.LoginProcessor;
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
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class userOrderRecipe {

    @Autowired
    private ClientOrderChoices clientOrderChoices;


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
    private ShopInformationsGetter shopInformationsGetter;


    @Autowired
    private OrderProcessing orderProcessing;

    @Autowired
    private CheckRetrieveOrder checkRetrieveOrder;


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

    @Given("an  available recipe with name {string}")
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
    
    @Given("a shop  in city {string} and tax {double} with a stock")
    public void a_shop_in_city_and_tax_with_a_stock(String city, Double tax) {
        shop = new Shop(city);
        shop.getStock().put(dough,10);
        shop.getStock().put(topping,10);
        shop.getStock().put(flavor,10);;
        shop.setTax(tax);
        shopRepository.save(shop,shop.getId());
    }
    @Given("a cook  with name {string}")
    public void a_cook_with_name(String name) {
        cook = new Cook(name,shop.getId());
        cook.setEndHour(shop.getClosingHour());
        cook.setBeginHour(shop.getOpeningHour());
        cookRepository.save(cook,cook.getId());
    }
    @Given("user with name {string} and password {string} signin")
    public void user_with_name_and_password_signin(String name, String password) throws Exception {
        signInProcessor.signIn(name,password,"0640347631","clairemarini@gmail.com",new Order());
        boolean isPresent = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .anyMatch(user -> user.getUserName().equals("claire") && user.getPassword().equals("1234"));
        assertTrue(isPresent);
        user = loginProcessor.login(name,password,new Order());
    }
    @When("the user with name {string} and password {string} choose shop")
    public void the_user_with_name_and_password_choose_shop(String name, String password) throws Exception {
        clientOrderChoices.chooseShop(user.getId(),shop);
        user = StreamSupport.stream(userRepository.findAll().spliterator(), false).toList().get(0);
    }
    @Then("the actual order of user have a shop")
    public void the_actual_order_of_user_have_a_shop() {
        assertEquals(1, StreamSupport.stream(userRepository.findAll().spliterator(), false).toList().size());
        assertEquals(user.getActualOrder().getShop(), shop);
        assertTrue(user.getActualOrder().isLoginClient());
    }

    

    @When("a user with name {string} and password {string} add {int} recipe in this basket")
    public void a_user_with_name_and_password_add_recipe_in_this_basket(String name, String password, Integer number) throws Exception {
        clientOrderChoices.chooseShop(user.getId(),shop);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(user.getId(),new Item(availableRecipeInShopStock.get(0),number));
        user = StreamSupport.stream(userRepository.findAll().spliterator(), false).toList().get(0);
    }
    @Then("the user have {int} recipe in this basket")
    public void the_user_have_recipe_in_this_basket(Integer number) {
        assertSame(user.getActualOrder().getBasket().size(), 1);
        boolean isPresent = user.getActualOrder().getBasket().stream().anyMatch(item1 ->
                item1.getRecipe() ==recipe && item1.getQuantity()==number
        );
        assertTrue(isPresent);
    }
    @Then("the stock  have lost ingredients of this {int} recipe")
    public void the_stock_have_lost_ingredients_of_this_recipe(Integer number) {
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),10-number);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),10-number);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),10-number);
    }

    

    @When("a user with name {string} and password {string} add {int} recipe and after delete {int} recipe in this basket")
    public void a_user_with_name_and_password_add_recipe_and_after_delete_recipe_in_this_basket(String name, String password, Integer numberAdd, Integer numberRemove) throws Exception {
        clientOrderChoices.chooseShop(user.getId(),shop);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(user.getId(),new Item(availableRecipeInShopStock.get(0),numberAdd));
        clientOrderChoices.deleteInCashier(user.getId(),new Item(availableRecipeInShopStock.get(0),numberRemove));
        user = StreamSupport.stream(userRepository.findAll().spliterator(), false).toList().get(0);
    }


    @When("a user with name {string} and password {string} choose a pickup hour")
    public void a_user_with_name_and_password_choose_a_pickup_hour(String name, String password) throws Exception {
        clientOrderChoices.chooseShop(user.getId(),shop);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(user.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.choosePickUpHour(user.getId(),localDateTime );
        user = StreamSupport.stream(userRepository.findAll().spliterator(), false).toList().get(0);
    }
    @Then("the actual order of user have a pickup hour and cook")
    public void the_actual_order_of_user_have_a_pickup_hour_and_cook() {
        assertSame(user.getActualOrder().getPickUpHour(),localDateTime);
        assertSame(user.getActualOrder().getCookId(), cook.getId());
    }

    
    @When("after choose shop , choose recipe and pickup hour the user with name {string} and password {string} validate his order")
    public void after_choose_shop_choose_recipe_and_pickup_hour_the_user_with_name_and_password_validate_his_order(String name, String password) throws Exception {
        clientOrderChoices.chooseShop(user.getId(),shop);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(user.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.choosePickUpHour(user.getId(),localDateTime );
        clientOrderChoices.validateOrder(user.getId());
        user = StreamSupport.stream(userRepository.findAll().spliterator(), false).toList().get(0);

    }
    @Then("the order is in repository , the is PAID and the actual order of user is empty")
    public void the_order_is_in_repository_the_is_paid_and_the_actual_order_of_user_is_empty() {
        boolean isPresent = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .anyMatch(order1 -> user.getId().equals(order1.getClientId()));
        assertTrue(isPresent);
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId())).findFirst().get();
        assertSame(order.getState(), StateOrder.PAID);
        assertSame(order.isLoginClient(), true);
        assertSame(user.getActualOrder().getClientId(), null);

        cook = cookRepository.findById(cook.getId()).get();
        isPresent = cook.getOrdersInProgress().stream().anyMatch(orderInProgress -> orderInProgress==order);
        assertTrue(isPresent);
    }
    @Then("the order  is in progress for a cook")
    public void the_order_is_in_progress_for_a_cook() {
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId())).findFirst().get();
        cook = cookRepository.findById(cook.getId()).get();
        boolean isPresent = cook.getOrdersInProgress().stream().anyMatch(orderInProgress -> orderInProgress==order);
        assertTrue(isPresent);
    }

    
    @When("after validate the order the order his ready,the user with name {string} and password {string} pick up the order")
    public void after_validate_the_order_the_order_his_ready_the_user_with_name_and_password_pick_up_the_order(String string, String string2) throws Exception {
        clientOrderChoices.chooseShop(user.getId(),shop);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(user.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.choosePickUpHour(user.getId(),localDateTime );
        clientOrderChoices.validateOrder(user.getId());
        orderProcessing.readyOrders();
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId())).findFirst().get();
        checkRetrieveOrder.giveOrder(order.getId());
        user = StreamSupport.stream(userRepository.findAll().spliterator(), false).toList().get(0);

    }
    @Then("the order  have state TAKEN")
    public void the_order_have_state_taken() {
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId())).findFirst().get();
        assertSame(order.getState(), StateOrder.TAKEN);
        assertSame(userRepository.findById(user.getId()).isPresent(), true);
    }

    

    @When("a user with name {string} and password {string} cancel {int} order in {int} minutes")
    public void a_user_with_name_and_password_cancel_order_in_minutes(String string, String string2, Integer int1, Integer int2) throws Exception {
        clientOrderChoices.chooseShop(user.getId(),shop);
         List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(user.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2025,10,10,10,10);
        clientOrderChoices.choosePickUpHour(user.getId(),localDateTime );
        clientOrderChoices.validateOrder(user.getId());
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId())).findFirst().get();
        clientOrderChoices.cancelOrder(user.getId(),order);


        clientOrderChoices.chooseShop(user.getId(),shop);
        availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(user.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2025,10,10,10,10);
        clientOrderChoices.choosePickUpHour(user.getId(),localDateTime );
        clientOrderChoices.validateOrder(user.getId());
        order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId() )  && order1.getState()==StateOrder.PAID).findFirst().get();
        clientOrderChoices.cancelOrder(user.getId(),order);
        user = StreamSupport.stream(userRepository.findAll().spliterator(), false).toList().get(0);

    }
    @Then("the user can't order")
    public void the_user_can_t_order() {
        Assertions.assertThrows(CantOrderException.class, () -> {
            clientOrderChoices.chooseShop(user.getId(),shop);
        });
    }


}
