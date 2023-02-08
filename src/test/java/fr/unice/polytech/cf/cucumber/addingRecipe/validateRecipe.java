package fr.unice.polytech.cf.cucumber.addingRecipe;

import fr.unice.polytech.cf.component.CatalogCreation;
import fr.unice.polytech.cf.component.RecipeFactoryService;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.repositories.CatalogRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.StreamSupport;

@SpringBootTest
public class validateRecipe {

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    RecipeFactoryService recipeFactoryService;

    @Autowired
    CatalogRepository catalogRepository;

    @Autowired
    CatalogCreation catalogCreation;

    Dough dough1;
    Dough dough2;
    Flavor flavor;
    Topping topping;

    Recipe recipe;

    @Before
    public void settingUpContext(){
        recipeRepository.deleteAll();
    }

    @Given("{int} dough1, {int} dough2, {int} flavor, {int} topping")
    public void there_are_doughs_flavors_and_topping(int nbD1, int nbD2, int nbF, int nbT){
        int i;
        dough1 = new Dough(1, "dough1");
        dough2 = new Dough(1, "dough2");
        flavor = new Flavor(1, "flavor");
        topping = new Topping(1, "topping");
        
        for(i=0; i<nbD1; i++)
            catalogCreation.addNewIngredient(dough1);
        for(i=0; i<nbD2; i++)
            catalogCreation.addNewIngredient(dough2);
        for(i=0; i<nbF; i++)
            catalogCreation.addNewIngredient(flavor);
        for(i=0; i<nbT; i++)
            catalogCreation.addNewIngredient(topping);
    }

    @Given("a recipe1 named {string} with {int} dough1, {int} flavor, {int} topping to be validated")
    public void aRecipeNamedWithDoughFlavorToBeValidated(String name, int nbD1, int nbF, int nbT) {
        recipe = new Recipe();
        recipe.setName(name);
        recipe.getIngredients().put(dough1, nbD1);
        recipe.getIngredients().put(flavor, nbF);
        recipe.setStateRecipe(StateRecipe.TO_BE_VALIDATED);
        recipeRepository.save(recipe, recipe.getId());
    }

    @When("a factoryManager validate the recipe1")
    public void aFactoryManagerValidateTheRecipe() {
        recipeFactoryService.validatedRecipes();
    }

    @Then("There is {int} recipe validated in the factory")
    public void thereIsRecipeValidatedInTheFactory(int arg0) {
        int nbR = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe1 -> recipe1.getStateRecipe().equals(StateRecipe.VALIDATED)).toList().size();
        Assertions.assertEquals(arg0, nbR);
    }

    @And("There is {int} recipe to be validated in the factory")
    public void thereIsRecipeToBeValidatedInTheFactory(int arg0) {
        int nbR = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe1 -> recipe1.getStateRecipe().equals(StateRecipe.TO_BE_VALIDATED)).toList().size();
        Assertions.assertEquals(arg0, nbR);
    }

    @Given("a recipe2 named {string} with {int} dough{int}, {int} flavor, {int} topping belongs to the factory recipe list")
    public void aRecipeNamedWithDoughFlavorToppingBelongsToTheFactoryRecipeList(String name, int nbD, int typeD, int nbF, int nbT) {

        recipe = new Recipe();
        recipe.setName(name);
        switch(typeD){
            case 1:
                recipe.getIngredients().put(dough1, nbD);
                break;
            case 2:
                recipe.getIngredients().put(dough2, nbD);
        }
        recipe.getIngredients().put(flavor, nbF);
        recipe.getIngredients().put(topping, nbT);
        recipe.setStateRecipe(StateRecipe.AVAILABLE);
        recipeRepository.save(recipe, recipe.getId());
    }
}
