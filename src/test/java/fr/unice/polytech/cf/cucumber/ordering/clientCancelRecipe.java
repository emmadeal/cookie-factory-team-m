package fr.unice.polytech.cf.cucumber.ordering;

import fr.unice.polytech.cf.exception.CantCancelException;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

public class clientCancelRecipe {


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


    @Before
    public void settingUpContext() {
        clientRepository.deleteAll();
        shopRepository.deleteAll();
        orderRepository.deleteAll();
        recipeRepository.deleteAll();

    }

    @Given("an available recipe  with name {string}")
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
    @Given("a shop in city {string}  and tax {double} with a stock")
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
    @Given("a cook with name  {string}")
    public void a_cook_with_name(String name) {
        cook = new Cook(name,shop.getId());
        cook.setEndHour(shop.getClosingHour());
        cook.setBeginHour(shop.getOpeningHour());
        cook.getThemeIds().add(theme.getId());
        cookRepository.save(cook,cook.getId());
    }


    @When("a client validate an order and cancel his order")
    public void a_client_validate_an_order_and_cancel_his_order() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(claire.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2025,10,10,10,10);
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        clientOrderChoices.validateOrder(claire.getId());
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire.getId().equals(order1.getClientId())).findFirst().get();

        clientOrderChoices.cancelOrder(claire.getId(),order);


    }
    @Then("the order have state CANCEL and the cook have no this order in progress")
    public void the_order_have_state_cancel_and_the_cook_have_no_this_order_in_progress() {
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire.getId().equals(order1.getClientId())).findFirst().get();
        assertSame(order.getState(), StateOrder.CANCEL);
        cook = cookRepository.findById(cook.getId()).get();
        assertSame(cook.getOrdersInProgress().size(), 0);
    }

    @Then("the stock of the shop have always the number of ingredients")
    public void the_stock_of_the_shop_have_always_the_number_of_ingredients() {
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),10);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),10);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),10);
    }


    @When("a client can't cancel his order because the order is not paid")
    public void a_client_can_t_cancel_his_order_because_the_order_is_not_paid() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(claire.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2025,10,10,10,10);
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        Assertions.assertThrows(CantCancelException.class, () -> {
            clientOrderChoices.cancelOrder(claire.getId(),claire.getActualOrder());
        });
    }
    @Then("the order have state INPROGRESS and the cook have no this order in progress")
    public void the_order_have_state_inprogress_and_the_cook_have_no_this_order_in_progress() {
        boolean isPresent =StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .anyMatch(order1 -> claire.getId().equals(order1.getClientId()));
        assertFalse(isPresent);
        assertSame(claire.getActualOrder().getState(), StateOrder.IN_PROGRESS);
        cook = cookRepository.findById(cook.getId()).get();
        assertSame(cook.getOrdersInProgress().size(), 0);
    }
    @Then("the stock of the shop have lost ingredients")
    public void the_stock_of_the_shop_have_lost_ingredients() {
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),10-3);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),10-3);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),10-3);
    }


    @When("a client can't cancel his order because the order is preparing by the cook")
    public void a_client_can_t_cancel_his_order_because_the_order_is_preparing_by_the_cook() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(claire.getId(),new Item(availableRecipeInShopStock.get(0),3));
        localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        clientOrderChoices.validateOrder(claire.getId());
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire.getId().equals(order1.getClientId())).findFirst().get();
        Assertions.assertThrows(CantCancelException.class, () -> {
            clientOrderChoices.cancelOrder(claire.getId(),order);
        });

    }
    @Then("the order have state PAID and the cook have this order in progress")
    public void the_order_have_state_paid_and_the_cook_have_this_order_in_progress() {
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire.getId().equals(order1.getClientId())).findFirst().get();
        assertSame(order.getState(), StateOrder.PAID);
        cook = cookRepository.findById(cook.getId()).get();
        assertSame(cook.getOrdersInProgress().size(), 1);
    }


}
