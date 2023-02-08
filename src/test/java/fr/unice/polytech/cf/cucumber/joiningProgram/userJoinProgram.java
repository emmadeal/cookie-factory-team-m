package fr.unice.polytech.cf.cucumber.joiningProgram;

import fr.unice.polytech.cf.exception.BadStateException;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.*;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@SpringBootTest
public class userJoinProgram {

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
    private LoginProcessor loginProcessor;

    @Autowired
    private SignInProcessor signInProcessor;

    @Autowired
    private UserModifier userModifier;

    @Autowired
    ClientOrderChoices clientOrderChoices;

    @Autowired
    ShopInformationsGetter shopInformationsGetter;

    @Autowired
    OrderProcessing orderProcessing;

    @Autowired
    CheckRetrieveOrder checkRetrieveOrder;


    Client client;
    User user;
    Shop shop;
    Cook cook;
    Order order;
    Recipe recipe;


    @Before
    public void settingUpContext() {
        clientRepository.deleteAll();
        shopRepository.deleteAll();
        orderRepository.deleteAll();
        recipeRepository.deleteAll();
    }


    @Given("an available recipe in a shop with a cook")
    public void anAvailableRecipeInAShopWithACook() {
        Dough dough = new Dough(1.5F,"dough");
        Flavor flavor = new Flavor(1.5F,"flavor");
        Topping topping = new Topping(1.5F,"topping");

        recipe = new Recipe();
        recipe.setName("3 chocolats");
        recipe.setId(UUID.randomUUID());
        recipe.getIngredients().put(dough,1);
        recipe.getIngredients().put(topping,1);
        recipe.getIngredients().put(flavor,1);
        recipe.setPreparingTime(10);
        recipe.setStateRecipe(StateRecipe.AVAILABLE);
        recipeRepository.save(recipe,recipe.getId());

        shop = new Shop("Nice");
        shop.getStock().put(dough,35);
        shop.getStock().put(topping,35);
        shop.getStock().put(flavor,35);
        shop.setTax(0);
        shopRepository.save(shop,shop.getId());

        cook = new Cook("Bernard",shop.getId());
        cook.setEndHour(shop.getClosingHour());
        cook.setBeginHour(shop.getOpeningHour());
        cookRepository.save(cook,cook.getId());
    }

    @Given("a client with name Claire")
    public void client_with_name(){
        client = new Client();
        clientRepository.save(client, client.getId());
    }

    @Given("the client {string} sign in  with the  password {string} with the mail {string} with the phone {string}")
    public void client_signin_with_pwd_mail_and_phone(String name, String pwd, String mail, String phone){
        try {
            signInProcessor.signIn(name, pwd, phone, mail, new Order());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Given("the client with name {string} and password {string} login")
    public void client_with_name_and_pwd_login(String name, String pwd){
        try {
            user = loginProcessor.login(name, pwd, new Order());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    @When("user with name Claire join the loyalty program")
    public void userWithNameJoinTheLoyaltyProgram() {
        try {
            userModifier.joinLoyaltyProgram(user.getId());
        } catch (ResourceNotFoundException e) {
            System.out.println("Impossible de rejoindre le loyalty program");
        }
    }

    @Then("the user is membership")
    public void theUserIsMembership() {
        Assertions.assertTrue(user.isMembership());
    }


    @Given("user with name Claire begin, pays and take an order with {int} cookies")
    public void userWithNameBeginPaysAndTakeAnOrderWithCookies(int nbCookies) {
        try {
            userModifier.joinLoyaltyProgram(user.getId());
        } catch (ResourceNotFoundException e) {
            System.out.println("Impossible de rejoindre le loyalty program");
        }

        try {
            clientOrderChoices.chooseShop(user.getId(),shop);
            List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
            clientOrderChoices.addInCashier(user.getId(),new Item(availableRecipeInShopStock.get(0), nbCookies));
            LocalDateTime localDateTime = LocalDateTime.of(2022,10,10,17,10);
            clientOrderChoices.choosePickUpHour(user.getId(),localDateTime );
            order = user.getActualOrder();
            clientOrderChoices.validateOrder(user.getId());
            orderProcessing.readyOrders();
            Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                    .filter(order1 -> user.getId().equals(order1.getClientId())).findFirst().get();
            checkRetrieveOrder.giveOrder(order.getId());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @When("user with name Claire begin and pays an order with {int} cookies")
    public void userWithNameBeginAndPaysAnOrderWithCookies(int nbCookies) {
        try {
            userModifier.joinLoyaltyProgram(user.getId());
        } catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            clientOrderChoices.chooseShop(user.getId(),shop);
            List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
            clientOrderChoices.addInCashier(user.getId(),new Item(availableRecipeInShopStock.get(0), nbCookies));
            LocalDateTime localDateTime = LocalDateTime.of(2022,10,10,17,10);
            clientOrderChoices.choosePickUpHour(user.getId(),localDateTime );
            order = user.getActualOrder();
            clientOrderChoices.validateOrder(user.getId());
            orderProcessing.readyOrders();

        } catch(Exception e) {
            System.out.println(e.toString());
        }

    }

    @Then("the user don't have reduction on his order")
    public void theUserDonTHaveReductionOnHisOrder() {
        Order myOrder = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> order.getId().equals(order1.getId())).findFirst().get();
        Assertions.assertEquals(9F, myOrder.getPrice());
    }

    @Then("the user have reduction on his order")
    public void theUserHaveReductionOnHisOrder() {
        Order myOrder = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> order.getId().equals(order1.getId())).findFirst().get();

        Assertions.assertEquals(8.1F, myOrder.getPrice());
    }

}

