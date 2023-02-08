package fr.unice.polytech.cf.cucumber.factoryRessource;

import fr.unice.polytech.cf.component.RecipeFactoryService;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.IngredientCreator;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.repositories.CatalogRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import fr.unice.polytech.cf.utils.Constant;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class addRecipe {

    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    CatalogRepository catalogRepository;

    @Autowired
    RecipeFactoryService recipeFactoryService;

    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;

    @Before
    public void settingUpContext() throws ResourceNotFoundException {
        recipeRepository.deleteAll();
        dough = IngredientCreator.createIngredient(1.5f,"dough", Constant.TYPE_DOUGH);
        flavor = IngredientCreator.createIngredient(1.5f,"flavor", Constant.TYPE_FLAVOR);
        topping = IngredientCreator.createIngredient(1.5f,"topping", Constant.TYPE_TOPPING);
    }

    @Given("a new recipe with name {string}")
    public void aNewRecipeWithName(String arg0) {
        catalogRepository.save(dough, UUID.randomUUID());
        catalogRepository.save(flavor,UUID.randomUUID());
        catalogRepository.save(topping,UUID.randomUUID());
        try {
            recipeFactoryService.createRecipe(arg0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Then("There is {int} available recipe in the factory")
    public void thereIsAvailableRecipeInTheFactory(int arg0) {
     //   Assertions.assertEquals(recipeFactoryService.getRecipesByState(StateRecipe.AVAILABLE).size(), arg0);
    }

    @And("There is {int} recipe to be validated by the factoryManager")
    public void thereIsRecipeToBeValidatedByTheFactoryManager(int arg0) {
       // Assertions.assertEquals(recipeFactoryService.getRecipesByState(StateRecipe.TO_BE_VALIDATED).size(), arg0);
    }

    @And("the factory manager validate it")
    public void theFactoryManagerValidateIt() {
        recipeFactoryService.validatedRecipes();
    }

    @Given("a non-validated recipe with name {string}")
    public void aNonValidatedRecipeWithName(String arg0) {
        try {
            recipeFactoryService.createRecipe(arg0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @And("the factory manager add it")
    public void theFactoryManagerAddIt() {
        recipeFactoryService.addRecipes();
    }

    @And("There is {int} validated recipe in the factory")
    public void thereIsValidatedRecipeInTheFactory(int arg0) {
       // Assertions.assertEquals(recipeFactoryService.getRecipesByState(StateRecipe.VALIDATED).size(), arg0);
    }
}
