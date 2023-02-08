package fr.unice.polytech.cf.cucumber.cookingTime;

import fr.unice.polytech.cf.component.Kitchen;
import fr.unice.polytech.cf.interfaces.ClientOrderChoices;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.StreamSupport;

@SpringBootTest
public class cookPrepareOrder {

    @Autowired
    CookRepository cookRepository;

    @Autowired
    ClientOrderChoices clientOrderChoices;

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    Kitchen kitchen;

    Shop shop;
    Client client;
    Recipe recipe;

    @Before
    public void settingUpContext(){
        cookRepository.deleteAll();
        shopRepository.deleteAll();
        clientRepository.deleteAll();
        recipeRepository.deleteAll();
        orderRepository.deleteAll();
    }


    @Given("a shop in city {string} with a cook named {string}")
    public void aShopInCityWithCookNamed(String city, String nameC){
        shop = new Shop(city);
        Cook cook = new Cook(nameC, shop.getId());
        cook.setBeginHour(LocalTime.of(9, 00));
        cook.setEndHour(LocalTime.of(17, 00));
        cookRepository.save(cook, cook.getId());
        shopRepository.save(shop, shop.getId());
    }

    @And("an available recipe")
    public void anAvailableRecipeNamed(){
        Dough dough = new Dough(1.5F,"dough");
        recipe = new Recipe();
        recipe.getIngredients().put(dough,1);
        recipe.setStateRecipe(StateRecipe.AVAILABLE);
        recipeRepository.save(recipe, recipe.getId());
        shop.getStock().put(dough,10);
    }

    @And("a client validate an order")
    public void aClientValidateAnOrder(){
        try{
            clientOrderChoices.chooseShop(null, shop);
            client = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
            clientOrderChoices.addInCashier(client.getId(), new Item(recipe, 1));
            clientOrderChoices.choosePickUpHour(client.getId(), LocalDateTime.of(2022, 12, 13, 11, 00));
            clientOrderChoices.validateOrder(client.getId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @When("the cook starts cooking order")
    public void theCookStartsCookingOrder() {
        try{
            kitchen.process(client.getActualOrder());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Then("the cook has {int} order in progress in his list")
    public void theCookHasOrderInProgressInHisList(int nb) {
        Cook cook = StreamSupport.stream(cookRepository.findAll().spliterator(), false)
                .findFirst().get();
        int check = cook.getOrdersInProgress().size();
        Assertions.assertEquals(nb, check);
    }

    @When("the cook finished cooking order")
    public void theCookFinishedCookingOrder() {
        try {
            kitchen.readyOrders();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Then("there is {int} order ready in the list")
    public void thereIsOrderReadyInTheList(int nb) {
        int check = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order -> order.getState().equals(StateOrder.READY)).toList().size();
        Assertions.assertEquals(nb, check);
    }
}
