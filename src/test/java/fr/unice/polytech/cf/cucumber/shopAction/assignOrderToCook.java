package fr.unice.polytech.cf.cucumber.shopAction;

import fr.unice.polytech.cf.exception.CantOrderException;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.CatalogController;
import fr.unice.polytech.cf.interfaces.ClientOrderChoices;
import fr.unice.polytech.cf.interfaces.ShopInformationsGetter;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.enumeration.Size;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.factory.Factory;
import fr.unice.polytech.cf.model.factory.FactoryManager;
import fr.unice.polytech.cf.model.recipe.*;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class assignOrderToCook {

    @Autowired
    CatalogController catalogController;
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
    FactoryManagerRepository factoryManagerRepository;
    @Autowired
    ThemeRepository themeRepository;
    @Autowired
    OccasionRepository occasionRepository;
    FactoryManager factoryManager;
    Cook emma;
    Cook jules;
    Shop shop;
    Dough dough;
    Flavor flavor;
    Topping topping;
    Recipe recipe;
    Client client;
    Theme cat;
    Occasion occasion;

    @Before
    public void settingUpContext() {
        clientRepository.deleteAll();
        shopRepository.deleteAll();
        orderRepository.deleteAll();
        recipeRepository.deleteAll();
        cookRepository.deleteAll();
        themeRepository.deleteAll();
        occasionRepository.deleteAll();
    }

    @Given("a shop of name {string}")
    public void aShopOfName(String city) {
        shop = new Shop(city);
        shop.getStock().put(dough,10);
        shop.getStock().put(topping,10);
        shop.getStock().put(flavor,10);
        shop.setTax(0.20);
        occasion = new Occasion("anniversaire");
        occasionRepository.save(occasion,occasion.getId());
        shop.getOccasionIds().add(occasion.getId());
        shopRepository.save(shop,shop.getId());

    }

    @And("a factory manager with name {string}")
    public void aFactoryManagerWithName(String name) {
        factoryManager = new FactoryManager(name);
        factoryManagerRepository.save(factoryManager, UUID.randomUUID());
    }

    @And("a cook with name {string} and a cook with name {string}")
    public void aCookWithNameAndACookWithName(String amme, String seluj) {
        emma = new Cook(amme,shop.getId());
        emma.setEndHour(shop.getClosingHour());
        emma.setBeginHour(shop.getOpeningHour());
        cookRepository.save(emma,emma.getId());
        jules = new Cook(seluj,shop.getId());
        jules.setEndHour(shop.getClosingHour());
        jules.setBeginHour(shop.getOpeningHour());
        cookRepository.save(jules,jules.getId());
    }

    @And("an available recipe with name {string}, cooking time {int} minutes and preparing time {int} minutes")
    public void anAvailableRecipeWithNameCookingTimeMinutesAndPreparingTimeMinutes(String name, int co, int pr) {
        dough = new Dough(1.5F,"dough");
        flavor = new Flavor(1.5F,"flavor");
        topping = new Topping(1.5F,"topping");
        recipe = new Recipe();
        recipe.setName(name);
        recipe.setId(UUID.randomUUID());
        recipe.getIngredients().put(dough,1);
        recipe.getIngredients().put(topping,1);
        recipe.getIngredients().put(flavor,1);
        recipe.setCookingTime(co);
        recipe.setPreparingTime(pr);
        recipe.setStateRecipe(StateRecipe.AVAILABLE);
        recipeRepository.save(recipe,recipe.getId());
    }

    @And("a client with name {string}")
    public void aClientWithName(String arg0) throws CantOrderException, ResourceNotFoundException {
        clientOrderChoices.chooseShop(null,shop);
    }

    @When("the client choose {int}h{int} as a pickup hour")
    public void theClientChooseHAsAPickupHour(int h, int m) {
        try {
            client = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
            clientOrderChoices.choosePickUpHour(client.getId(), LocalDateTime.of(2023, 5, 12, h, m));
            clientOrderChoices.validateOrder(client.getId());
        }catch (Exception e){}
    }

    @And("the cook {string} has an order to deliver at {int}h{int}")
    public void theCookHasAnOrderToDeliverAtH(String name, int h, int m) {
        Order order = new Order();
        order.setPickUpHour(LocalDateTime.of(2023,5,12,h,m));
        if (name.equals("Emma")) cookRepository.findById(emma.getId()).get().getOrdersInProgress().add(order);
        if (name.equals("Jules")) cookRepository.findById(jules.getId()).get().getOrdersInProgress().add(order);
    }

    @And("the cook {string} has an order to deliver at {int}h{int} with {int} minutes of preparation")
    public void theCookHasAnOrderToDeliverAtHWithMinutesOfPreparation(String name, int h, int m, int t) {
        Order order = new Order();
        order.setTotalPreparationMinutes(t);
        order.setPickUpHour(LocalDateTime.of(2023,5,12,h,m));
        if (name.equals("Emma")) cookRepository.findById(emma.getId()).get().getOrdersInProgress().add(order);
        if (name.equals("Jules")) cookRepository.findById(jules.getId()).get().getOrdersInProgress().add(order);
    }

    @And("the cook {string} know how to do the {string} theme")
    public void theCookKnowHowToDoTheTheme(String name, String th) {
        if (name.equals("Emma")) cookRepository.findById(emma.getId()).get().getThemeIds().add(cat.getId());
        if (name.equals("Jules")) cookRepository.findById(jules.getId()).get().getThemeIds().add(cat.getId());
    }

    @And("the cook {string} doesnt know how to do the {string} theme")
    public void theCookDoesntKnowHowToDoTheTheme(String name, String th) {
        cat = new Theme(th);
        themeRepository.save(cat,cat.getId());
    }

    @And("the client choose {string} as a theme")
    public void theClientChooseAsATheme(String th) {
        client = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
    }

    @When("the client add {int} recipe")
    public void theClientAddRecipe(int nb) throws Exception {
        client = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(client.getId(),new Item(availableRecipeInShopStock.get(0),nb));
    }

    @When("the client add {int} party recipe")
    public void theClientAddPartyRecipe(int nb) throws Exception {
        java.util.HashMap<Ingredient,Integer> ingredientsPlus = new HashMap<>();
        HashMap<Ingredient, Integer> ingredientsWithout = new HashMap<>();
        client = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        try {
            clientOrderChoices.createPersonalizedPartyRecipeAndAddInCashier(client.getId(), recipe, Size.L, ingredientsPlus, ingredientsWithout, occasion.getId(), cat.getId());
        }catch (Exception e){}
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(client.getId(),new Item(availableRecipeInShopStock.get(0),nb));
    }

    @Then("the shop manager assign the order to the cook {string}")
    public void theShopManagerAssignTheOrderToTheCook(String name) {
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> client.getId().equals(order1.getClientId())).findFirst().get();
        if (name.equals("Jules")) assertSame(order.getCookId(), cookRepository.findById(jules.getId()).get().getId());
    }

    @Then("the shop manager assign the order to one of the available cooks")
    public void theShopManagerAssignTheOrderToOneOfTheAvailableCooks() {
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> client.getId().equals(order1.getClientId())).findFirst().get();
        assertNotNull(order.getCookId());
    }

    @Then("the shop manager does not assign a cook")
    public void theShopManagerDoesNotAssignACook() {
        client = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        assertNull(client.getActualOrder().getCookId());
    }
}
