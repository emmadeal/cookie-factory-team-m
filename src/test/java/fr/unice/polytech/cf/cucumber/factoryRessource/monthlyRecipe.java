package fr.unice.polytech.cf.cucumber.factoryRessource;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.CatalogController;
import fr.unice.polytech.cf.interfaces.CheckRetrieveOrder;
import fr.unice.polytech.cf.interfaces.ClientOrderChoices;
import fr.unice.polytech.cf.interfaces.OrderProcessing;
import fr.unice.polytech.cf.interfaces.SalesController;
import fr.unice.polytech.cf.interfaces.ShopInformationsGetter;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.IngredientCreator;
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
import fr.unice.polytech.cf.utils.Constant;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class monthlyRecipe {

    @Autowired
    private SalesController salesController;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    CatalogController catalogController;

    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;

    Recipe recipe;

    Recipe recipe2;


    @Before
    public void settingUpContext() {
        recipeRepository.deleteAll();
    }


    @Given("an available recipe with name {string} buy {int} time")
    public void an_available_recipe_with_name_buy_time(String name, Integer times) throws ResourceNotFoundException {
        catalogController.registerCatalog();
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
        recipe.setNumberOfOrders(times);
        recipeRepository.save(recipe,recipe.getId());
    }

    @Given("an available recipe2 with name {string} buy {int} time")
    public void an_available_recipe2_with_name_buy_time(String name, Integer times) {
        dough = new Dough(1.5F,"dough");
        flavor = new Flavor(1.5F,"flavor");
        topping = new Topping(1.5F,"topping");

        recipe2 = new Recipe();
        recipe2.setName(name);
        recipe2.setId(UUID.randomUUID());
        recipe2.getIngredients().put(dough,4);
        recipe2.getIngredients().put(topping,5);
        recipe2.getIngredients().put(flavor,2);
        recipe2.setPreparingTime(10);
        recipe2.setStateRecipe(StateRecipe.AVAILABLE);
        recipe2.setNumberOfOrders(times);
        recipeRepository.save(recipe2,recipe2.getId());
    }
    @When("development of a new monthly recipe and deletion of the lowest selling recipe")
    public void development_of_a_new_monthly_recipe_and_deletion_of_the_lowest_selling_recipe() throws Exception {
        salesController.elaborateNewRecipeMonthly();
    }
    @Then("the new recipe is added to the list of the factory and the lowwest selling recipe is delete")
    public void the_new_recipe_is_added_to_the_list_of_the_factory_and_the_lowwest_selling_recipe_is_delete() {
        assertFalse(recipeRepository.existsById(recipe2.getId()));
        boolean isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe1 -> StateRecipe.TO_BE_VALIDATED.equals(recipe1.getStateRecipe()));
        assertTrue(isPresent);

        List<Recipe> recipeList = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe1 -> StateRecipe.AVAILABLE.equals(recipe1.getStateRecipe())).toList();
        assertEquals(1, recipeList.size());
    }
}
