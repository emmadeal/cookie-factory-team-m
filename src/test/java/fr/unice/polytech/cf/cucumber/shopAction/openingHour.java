package fr.unice.polytech.cf.cucumber.shopAction;

import fr.unice.polytech.cf.exception.CantOrderException;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.*;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.*;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.*;
import fr.unice.polytech.cf.utils.Constant;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class openingHour {


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
    Shop shop;
    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;
    Recipe recipe;
    Client client;

    @Before
    public void settingUpContext() {
        clientRepository.deleteAll();
        shopRepository.deleteAll();
        orderRepository.deleteAll();
        recipeRepository.deleteAll();
    }

    @Given("the shop with city {string} and openingHour {int}h and closingHour {int}h")
    public void theShopOfIdAndNameAndTaxAndOpeningHourHAndClosingHourH(String city, int op, int clo) {
        shop = new Shop(city);
        shop.getStock().put(dough,10);
        shop.getStock().put(topping,10);
        shop.getStock().put(flavor,10);
        shop.setTax(0.20);
        shop.setOpeningHour(LocalTime.of(op,0));
        shop.setClosingHour(LocalTime.of(clo,0));
        shopRepository.save(shop,shop.getId());
    }

    @Given("the available recipe with name {string} and preparing time {int} minutes")
    public void theAvailableRecipeWithIdAndWithNameAndWithPriceCookingTimeMinutesAndPreparingTimeMinutes(String name, int time) throws ResourceNotFoundException {
        dough = IngredientCreator.createIngredient(1.5f,"dough", Constant.TYPE_DOUGH);
        flavor = IngredientCreator.createIngredient(1.5f,"flavor", Constant.TYPE_FLAVOR);
        topping = IngredientCreator.createIngredient(1.5f,"topping", Constant.TYPE_TOPPING);
        recipe = new Recipe();
        recipe.setName(name);
        recipe.setId(UUID.randomUUID());
        recipe.getIngredients().put(dough,1);
        recipe.getIngredients().put(topping,1);
        recipe.getIngredients().put(flavor,1);
        recipe.setPreparingTime(time);
        recipe.setStateRecipe(StateRecipe.AVAILABLE);
        recipeRepository.save(recipe,recipe.getId());
    }

    @Given("a client")
    public void aClient() throws CantOrderException, ResourceNotFoundException {
        clientOrderChoices.chooseShop(null,shop);
    }

    @When("the client is starting an order with a shop and {int} {string} recipes in the basket")
    public void theClientWithNameStartingAnOrderWithAShopAndRecipesInTheBasket(int nb, String rec) throws Exception {
        client = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(client.getId(),new Item(availableRecipeInShopStock.get(0),nb));
    }

    @When("the client choose {int}h{int} as a pickup hour for his order")
    public void theClientChooseHAsAPickupHourForHisOrder(int h, int m) throws Exception {
        try {
            client = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
            clientOrderChoices.choosePickUpHour(client.getId(), LocalDateTime.of(2023, 5, 12, h, m));
            clientOrderChoices.validateOrder(client.getId());
        }catch (Exception e){}
    }

    @Then("the client can order")
    public void theClientCanOrder() {
        client = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        Assertions.assertNotNull(client.getActualOrder().getPickUpHour());
    }

    @Then("the client cant order")
    public void theClientCantOrder() {
        client = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        Assertions.assertNull(client.getActualOrder().getPickUpHour());
    }
}
