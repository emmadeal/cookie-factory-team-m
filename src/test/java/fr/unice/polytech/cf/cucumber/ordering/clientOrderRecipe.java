package fr.unice.polytech.cf.cucumber.ordering;

import fr.unice.polytech.cf.exception.CantOrderException;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.CheckRetrieveOrder;
import fr.unice.polytech.cf.interfaces.ClientOrderChoices;
import fr.unice.polytech.cf.interfaces.OrderProcessing;
import fr.unice.polytech.cf.interfaces.ShopInformationsGetter;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
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
import fr.unice.polytech.cf.repositories.OccasionRepository;
import fr.unice.polytech.cf.repositories.OrderRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import fr.unice.polytech.cf.repositories.ThemeRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class clientOrderRecipe {

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
    private OccasionRepository occasionRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ShopInformationsGetter shopInformationsGetter;


    @Autowired
    private OrderProcessing orderProcessing;

    @Autowired
    private CheckRetrieveOrder checkRetrieveOrder;


    Dough dough;
    Flavor flavor;
    Topping topping;

    Recipe recipe;

    Shop shop;

    Cook cook;

    Theme theme;
    Occasion occasion ;

    Client claire;

    LocalDateTime localDateTime;


    @BeforeEach
    public void settingUpContext() {
        clientRepository.deleteAll();
        shopRepository.deleteAll();
        orderRepository.deleteAll();
        recipeRepository.deleteAll();

    }


    @Given("an available recipe with name {string}")
    public void an_available_recipe_with_id_and_with_name(String name) {
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
    @Given("a shop in city {string} and tax {double} with a stock")
    public void a_shop_in_city_and_tax_with_a_stock(String city, Double tax) {
        shop = new Shop(city);
        shop.getStock().put(dough,10);
        shop.getStock().put(topping,10);
        shop.getStock().put(flavor,10);
        theme = new Theme("fleur");
        themeRepository.save(theme,theme.getId());
        occasion = new Occasion("anniversaire");
        occasionRepository.save(occasion,occasion.getId());
        shop.getOccasionIds().add(occasion.getId());
        shop.setTax(tax);
        shopRepository.save(shop,shop.getId());
    }

    @Given("a cook with name {string}")
    public void a_cook_with_name(String name) {
        cook = new Cook(name,shop.getId());
        cook.setEndHour(shop.getClosingHour());
        cook.setBeginHour(shop.getOpeningHour());
        cook.getThemeIds().add(theme.getId());
        cookRepository.save(cook,cook.getId());
    }

    @When("the client choose shop")
    public void the_client_choose_shop() throws CantOrderException, ResourceNotFoundException {
        clientOrderChoices.chooseShop(null,shop);
    }
    @Then("the actual order of client have a shop")
    public void the_actual_order_of_client_have_a_shop() {
        assertEquals(1, StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().size());
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        assertEquals(claire.getActualOrder().getShop(), shop);
        assertFalse(claire.getActualOrder().isLoginClient());
    }



    @When("a client add {int} recipe in this basket")
    public void a_client_add_recipe_in_this_basket(Integer number) throws Exception {
        clientOrderChoices.chooseShop(null,shop);
         claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(claire.getId(),new Item(availableRecipeInShopStock.get(0),number));
    }
    @Then("the client have {int} recipe in this basket")
    public void the_client_have_recipe_in_this_basket(Integer number) {
        claire = clientRepository.findById(claire.getId()).get();
        assertSame(claire.getActualOrder().getBasket().size(), 1);

        boolean isPresent = claire.getActualOrder().getBasket().stream().anyMatch(item1 ->
                item1.getRecipe() ==recipe && item1.getQuantity()==number
        );
        assertTrue(isPresent);
    }
    @Then("the stock have lost ingredients of this {int} recipe")
    public void the_stock_have_lost_ingredients_of_this_recipe(Integer number) {
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),10-number);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),10-number);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),10-number);

    }

    @When("a client add {int} recipe and after delete {int} recipe in this basket")
    public void a_client_add_recipe_and_after_delete_recipe_in_this_basket(Integer numberAdd, Integer numberRemove) throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(claire.getId(),new Item(availableRecipeInShopStock.get(0),numberAdd));
        clientOrderChoices.deleteInCashier(claire.getId(),new Item(availableRecipeInShopStock.get(0),numberRemove));
    }


    @When("a client choose a pickup hour")
    public void a_client_choose_a_pickup_hour() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(claire.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
    }
    @Then("the actual order of client have a pickup hour and cook")
    public void the_actual_order_of_client_have_a_pickup_hour_and_cook() {
        claire = clientRepository.findById(claire.getId()).get();
        assertSame(claire.getActualOrder().getPickUpHour(),localDateTime);
        assertSame(claire.getActualOrder().getCookId(), cook.getId());
    }



    @When("after choose shop , choose recipe and pickup hour the client validate his order")
    public void after_choose_shop_choose_recipe_and_pickup_hour_the_client_validate_his_order() throws Exception {
         clientOrderChoices.chooseShop(null,shop);
        claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(claire.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        clientOrderChoices.validateOrder(claire.getId());

    }
    @Then("the order is in repository , the is PAID and the actual order of client is empty")
    public void the_order_is_in_repository_the_is_paid_and_the_actual_order_of_client_is_null() {
        claire = clientRepository.findById(claire.getId()).get();
        boolean isPresent = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .anyMatch(order1 -> claire.getId().equals(order1.getClientId()));
        assertTrue(isPresent);
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire.getId().equals(order1.getClientId())).findFirst().get();
        assertSame(order.getState(), StateOrder.PAID);
        assertSame(order.isLoginClient(), false);
        assertSame(claire.getActualOrder().getClientId(), null);
    }


    @Then("the order is in progress for a cook")
    public void the_order_is_in_progress_for_a_cook() {
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire.getId().equals(order1.getClientId())).findFirst().get();
        cook = cookRepository.findById(cook.getId()).get();
        boolean isPresent = cook.getOrdersInProgress().stream().anyMatch(orderInProgress -> orderInProgress==order);
        assertTrue(isPresent);
    }


    @When("after validate the order the order his ready,the client pick up the order")
    public void after_validate_the_order_the_order_his_ready_the_client_pick_up_the_order() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(claire.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        clientOrderChoices.validateOrder(claire.getId());
        orderProcessing.readyOrders();
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire.getId().equals(order1.getClientId())).findFirst().get();
        checkRetrieveOrder.giveOrder(order.getId());
    }
    @Then("the order have state TAKEN")
    public void the_order_have_state_taken() {
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire.getId().equals(order1.getClientId())).findFirst().get();
        assertSame(order.getState(), StateOrder.TAKEN);
        assertSame(clientRepository.findById(claire.getId()).isPresent(), false);
    }




}
