package fr.unice.polytech.cf.cucumber.addingRecipe;

import fr.unice.polytech.cf.component.CatalogCreation;
import fr.unice.polytech.cf.component.RecipeFactoryService;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.repositories.CatalogRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.StreamSupport;

@SpringBootTest
public class createRecipe {

    @Autowired
    RecipeFactoryService recipeFactoryService;

    @Autowired
    CatalogCreation catalogCreation;

    @Autowired
    CatalogRepository catalogRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Before
    public void settingUpContext(){
        catalogRepository.deleteAll();
        recipeRepository.deleteAll();
    }

    @Given("a catalog with different ingredients")
    public void aCatalogWithDifferentIngredients(){
        catalogCreation.registerCatalog();
    }


    @When("a chef create a recipe named {string}")
    public void aChefCreateARecipeNamed(String name) {
        try {
            recipeFactoryService.createRecipe(name);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Then("the recipe has {int} dough")
    public void theRecipeHasDough(int arg0) {
        Recipe recipe = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe1 -> recipe1.getStateRecipe().equals(StateRecipe.TO_BE_VALIDATED)).findFirst().get();
        Assertions.assertEquals(arg0, recipe.getNbD());
    }

    @Then("the recipe has less than {int} flavors")
    public void theRecipeHasLessThanFlavors(int arg0) {
        Recipe recipe = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe1 -> recipe1.getStateRecipe().equals(StateRecipe.TO_BE_VALIDATED)).findFirst().get();
        boolean check = recipe.getNbF() < arg0;
        Assertions.assertTrue(check);
    }

    @Then("the recipe has less than {int} topping")
    public void theRecipeHasLessThanTopping(int arg0) {
        Recipe recipe = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe1 -> recipe1.getStateRecipe().equals(StateRecipe.TO_BE_VALIDATED)).findFirst().get();
        boolean check = recipe.getNbT() < arg0;
        Assertions.assertTrue(check);
    }

    @When("a chef create a recipe named {string} and a recipe named {string}")
    public void aChefCreateARecipeNamedAndARecipeNamed(String name1, String name2) {
        try {
            recipeFactoryService.createRecipe(name1);
            recipeFactoryService.createRecipe(name2);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Then("the two recipes are different")
    public void theTwoRecipesAreDifferent() {
        List<Recipe> recipes = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe1 -> recipe1.getStateRecipe().equals(StateRecipe.TO_BE_VALIDATED)).toList();
        Recipe recipe1 = recipes.get(0);
        Recipe recipe2 =  recipes.get(1);

        Assertions.assertNotEquals(recipe1, recipe2);
    }
}
