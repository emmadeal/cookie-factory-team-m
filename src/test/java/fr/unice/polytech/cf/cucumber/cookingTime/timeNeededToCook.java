package fr.unice.polytech.cf.cucumber.cookingTime;

import fr.unice.polytech.cf.interfaces.ClientOrderChoices;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.ClientRepository;
import fr.unice.polytech.cf.repositories.OrderRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.stream.StreamSupport;

@SpringBootTest
public class timeNeededToCook {

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ClientOrderChoices clientOrderChoices;

    Recipe recipe;
    Client client;
    Order order;
    Shop shop;

    @Before
    public void settingUpContext(){
        recipeRepository.deleteAll();
        clientRepository.deleteAll();
        orderRepository.deleteAll();
    }


    @Given("an available recipe named {string} and with cooking time {long} minutes and preparing time {long} minutes")
    public void availableRecipeNamedWithCookingTimeAndPreparingTime(String name, long cTime, long pTime){
        recipe = new Recipe();
        recipe.setName(name);
        recipe.setCookingTime(cTime);
        recipe.setPreparingTime(pTime);
        recipe.setStateRecipe(StateRecipe.AVAILABLE);
        recipeRepository.save(recipe, recipe.getId());
    }

    @And("client starting an order")
    public void clientWithNameStartingAnOrderForH() {
        try {
            clientOrderChoices.chooseShop(null, new Shop("Nice"));
            client = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @And("the client add {int} recipes to his basket and order for {int}h{int}")
    public void theClientAddRecipesToHisBasket(int nb, int hour, int min) {
        Item item = new Item(recipe, nb);
        try {
            clientOrderChoices.addInCashier(client.getId(), item);
            clientOrderChoices.choosePickUpHour(client.getId(), LocalDateTime.of(2022, 12, 17, hour, min));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Then("the order take {int} minutes to prepare")
    public void theOrderTakeMinutesToPrepare(int prepTime) {
        long check = 0;
        try {
            check = client.getActualOrder().getTotalPreparationMinutes();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Assertions.assertEquals(prepTime, check);

    }

    @Then("the cook start to cook at {int}h{int}")
    public void theCookStartToCookAtH(int hour, int min) {
        LocalDateTime check = null;
        try {
            check = client.getActualOrder().getPreparationHour();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Assertions.assertEquals(check.getHour(), hour);
        Assertions.assertEquals(check.getMinute(), min);
    }
}
