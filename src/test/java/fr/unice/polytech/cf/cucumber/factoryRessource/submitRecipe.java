package fr.unice.polytech.cf.cucumber.factoryRessource;

import fr.unice.polytech.cf.component.CatalogCreation;
import fr.unice.polytech.cf.component.FactoryAdministration;
import fr.unice.polytech.cf.component.RecipeFactoryService;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.CatalogController;
import fr.unice.polytech.cf.interfaces.FactoryHumanRessources;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.factory.Chef;
import fr.unice.polytech.cf.model.factory.Factory;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.IngredientCreator;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.repositories.*;
import fr.unice.polytech.cf.utils.Constant;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class submitRecipe {
    @Autowired
    ChefRepository chefRepository ;
    @Autowired
    FactoryManagerRepository factoryManagerRepository;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    CatalogRepository catalogRepository ;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    CatalogCreation catalogCreation;

    @Autowired
    FactoryAdministration factoryAdministration;

    @Autowired
    RecipeFactoryService recipeFactoryService;

    Recipe recipe;

    @Before
    public void settingUpContext() {
        chefRepository.deleteAll();
        factoryManagerRepository.deleteAll();
        shopRepository.deleteAll();
        catalogRepository.deleteAll();
    }
    @Given("a chef who works in a factory with name {string} who has {int} recipe to be validated")
    public void aChefWhoWorksInAFactoryWithNameWhoHasRecipeToBeValidated(String name, int arg1) throws ResourceNotFoundException {
        Ingredient dough = IngredientCreator.createIngredient(1.5f,"dough", Constant.TYPE_DOUGH);
        Ingredient flavor = IngredientCreator.createIngredient(1.5f,"flavor", Constant.TYPE_FLAVOR);
        Ingredient topping = IngredientCreator.createIngredient(1.5f,"topping", Constant.TYPE_TOPPING);
        catalogRepository.save(dough, UUID.randomUUID());
        catalogRepository.save(flavor,UUID.randomUUID());
        catalogRepository.save(topping,UUID.randomUUID());
        factoryAdministration = new FactoryAdministration(chefRepository,factoryManagerRepository,shopRepository,catalogCreation);
        recipeFactoryService = new RecipeFactoryService(recipeRepository,catalogRepository);
        factoryAdministration.hireChef(name);
    }

    @When("the chef submit a recipe named {string}")
    public void theChefSubmitARecipeNamed(String name) throws Exception {
        recipe = recipeFactoryService.createRecipe(name);
    }

    @Then("the factory has {int} recipe to be validated")
    public void theFactoryHasRecipeToBeValidated(int nb) {
        Assertions.assertEquals(recipeFactoryService.getRecipesByState(StateRecipe.TO_BE_VALIDATED).size(),nb);
    }

    @Then("the name of the recipe to be validated is {string}")
    public void theNameOfTheRecipeToBeValidatedIs(String name) {
        Assertions.assertEquals(recipeFactoryService.getRecipesByState(StateRecipe.TO_BE_VALIDATED).get(0).getName(),name);
    }
}
