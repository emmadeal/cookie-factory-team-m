package fr.unice.polytech.cf.cucumber.managingShop;

import fr.unice.polytech.cf.exception.CantOrderException;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.BasketModifier;
import fr.unice.polytech.cf.interfaces.ClientOrderChoices;
import fr.unice.polytech.cf.interfaces.ShopInformationsGetter;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.IngredientCreator;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.*;
import fr.unice.polytech.cf.utils.Constant;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

public class manageStock {
    @Autowired
    private ClientOrderChoices clientOrderChoices;
    @Autowired
    BasketModifier basketModifier;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    private ShopInformationsGetter shopInformationsGetter;
    Shop shop;
    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;
    Recipe recipe;
    Client thomas;
    Client nicolas;

    @Before
    public void settingUpContext() {
        shopRepository.deleteAll();
        recipeRepository.deleteAll();
        clientRepository.deleteAll();
    }
    @Given("an available recipe named {string}")
    public void anAvailableRecipeNamed(String name) throws ResourceNotFoundException {
        dough = IngredientCreator.createIngredient(1.5f,"dough", Constant.TYPE_DOUGH);
        flavor = IngredientCreator.createIngredient(1.5f,"flavor", Constant.TYPE_FLAVOR);
        topping = IngredientCreator.createIngredient(1.5f,"topping", Constant.TYPE_TOPPING);
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

    @And("a shop with {int} cookies")
    public void aShopWithCookies(int nb) {
        shop = new Shop("Paris");
        shop.getStock().put(dough,nb);
        shop.getStock().put(topping,nb);
        shop.getStock().put(flavor,nb);
        shop.setTax(0.20);
        shopRepository.save(shop,shop.getId());
    }

    @And("a client with name {string} start an order")
    public void aClientWithNameStartAnOrder(String arg0) throws CantOrderException, ResourceNotFoundException {
        clientOrderChoices.chooseShop(null,shop);
    }
    @And("the client with name {string} start an order")
    public void theClientWithNameStartAnOrder(String arg0) throws CantOrderException, ResourceNotFoundException {
        clientOrderChoices.chooseShop(null,shop);
    }

    @When("we add {int} cookies to the shop's stock")
    public void weAddCookiesToTheShopSStock(int nb) {
        shop = StreamSupport.stream(shopRepository.findAll().spliterator(), false).toList().get(0);
        shop.getStock().put(dough,shop.getStock().get(dough)+nb);
        shop.getStock().put(topping,shop.getStock().get(topping)+nb);
        shop.getStock().put(flavor,shop.getStock().get(flavor)+nb);
    }

    @Then("the shop has the recipe in stock")
    public void theShopHasTheRecipeInStock() {
        shop = StreamSupport.stream(shopRepository.findAll().spliterator(), false).toList().get(0);
        Assertions.assertFalse(shop.getStock().isEmpty());
    }

    @Then("the shop has {int} cookies in stock")
    public void theShopHasCookiesInStock(int nb) {
        Assertions.assertEquals(shop.getStock().get(dough),nb);
        Assertions.assertEquals(shop.getStock().get(topping),nb);
        Assertions.assertEquals(shop.getStock().get(flavor),nb);
    }

    @When("{string} choose {int} cookie")
    public void chooseCookie(String name, int nb) throws Exception {
        thomas = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        nicolas = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(1);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        if (name.equals("thomas") && availableRecipeInShopStock.size()>0) clientOrderChoices.addInCashier(thomas.getId(),new Item(availableRecipeInShopStock.get(0),nb));
        if (name.equals("nicolas") && availableRecipeInShopStock.size()>0) clientOrderChoices.addInCashier(nicolas.getId(),new Item(availableRecipeInShopStock.get(0),nb));
    }

    @Then("{string} has {int} cookies in his basket")
    public void hasCookiesInHisBasket(String name, int nb) {
        if (name.equals("thomas") && thomas.getActualOrder().getBasket().stream().findFirst().isPresent()) Assertions.assertEquals(thomas.getActualOrder().getBasket().stream().findFirst().get().getQuantity(),nb);
        if (name.equals("nicolas") && nicolas.getActualOrder().getBasket().stream().findFirst().isPresent()) Assertions.assertEquals(nicolas.getActualOrder().getBasket().stream().findFirst().get().getQuantity(),nb);
    }

    @Then("the shop doesn't have this cookie anymore")
    public void theShopDoesnTHaveThisCookieAnymore() {
        shop = StreamSupport.stream(shopRepository.findAll().spliterator(), false).toList().get(0);
        Assertions.assertTrue(shop.getStock().isEmpty());
    }

    @Given("{string} choose {int} cookie, then removes {int}")
    public void chooseCookieThenRemoves(String name, int nb, int remo) throws Exception {
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        thomas = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        nicolas = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(1);
        if (name.equals("thomas")) {
            clientOrderChoices.addInCashier(thomas.getId(),new Item(availableRecipeInShopStock.get(0),nb));
            for (int i=0; i<remo; i++)
                basketModifier.removeInBasket(thomas,new Item(availableRecipeInShopStock.get(0),remo));
        }
        if (name.equals("nicolas")) {
            clientOrderChoices.addInCashier(nicolas.getId(),new Item(availableRecipeInShopStock.get(0),nb));
            for (int i=0; i<remo; i++)
                basketModifier.removeInBasket(nicolas,new Item(availableRecipeInShopStock.get(0),remo));

        }
    }
}
