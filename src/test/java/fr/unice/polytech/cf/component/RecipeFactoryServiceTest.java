package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.CatalogController;
import fr.unice.polytech.cf.interfaces.RecipeFactoryManager;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest
public class RecipeFactoryServiceTest {

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private CatalogController catalogController;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    RecipeFactoryManager recipeFactoryManager;



    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;

    Recipe recipe;


    @BeforeEach
    void setUp() throws ResourceNotFoundException {
        catalogRepository.deleteAll();
        recipeRepository.deleteAll();
        catalogController.registerCatalog();

        dough = IngredientCreator.createIngredient(1.5f,"dough", Constant.TYPE_DOUGH);
        flavor = IngredientCreator.createIngredient(1.5f,"flavor", Constant.TYPE_FLAVOR);
        topping = IngredientCreator.createIngredient(1.5f,"topping", Constant.TYPE_TOPPING);

        recipe = new Recipe();
        recipe.setId(UUID.randomUUID());
        recipe.getIngredients().put(dough,1);
        recipe.getIngredients().put(topping,1);
        recipe.getIngredients().put(flavor,1);
        recipe.setPreparingTime(10);
        recipe.setName("recipe 0");
        recipe.setStateRecipe(StateRecipe.AVAILABLE);
        recipeRepository.save(recipe,recipe.getId());
        catalogController.registerCatalog();
    }

    @Test
    public void createRecipe() throws Exception {
        recipeFactoryManager.createRecipe("recipe 1");
        boolean isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, true);
        assertSame(StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe())).findFirst().get().getName(),"recipe 1" );

    }

    @Test
    public void ValidatedRecipes() throws Exception {
        recipeFactoryManager.createRecipe("recipe 1");
        boolean isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, true);
        assertSame(StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe())).findFirst().get().getName(),"recipe 1" );

        recipeFactoryManager.validatedRecipes();
        isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, false);

        isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, true);
        assertSame(StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe -> StateRecipe.VALIDATED.equals(recipe.getStateRecipe())).findFirst().get().getName(),"recipe 1" );
    }


    @Test
    public void ValidatedRecipesErrrorName() throws Exception {
        recipeFactoryManager.createRecipe("recipe 0");
        boolean isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, true);
        assertSame(StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe())).findFirst().get().getName(),"recipe 0" );

        recipeFactoryManager.validatedRecipes();
        isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, false);

        isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, false);
    }


    @Test
    public void ValidatedRecipesErrrorIngredient() throws Exception {
        recipeFactoryManager.createRecipe("recipe 1");
        boolean isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, true);
        assertSame(StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe())).findFirst().get().getName(),"recipe 1" );

        Recipe recipeToBeValidated =StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe())).findFirst().get();
        recipeToBeValidated.getIngredients().put(dough,1);
        recipeToBeValidated.getIngredients().put(topping,1);
        recipeToBeValidated.getIngredients().put(flavor,1);
        recipeRepository.save(recipeToBeValidated,recipeToBeValidated.getId());
        recipeFactoryManager.validatedRecipes();
        isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, false);

        isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, false);
    }


    @Test
    public void addRecipes() throws Exception {
        recipeFactoryManager.createRecipe("recipe 1");
        boolean isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, true);
        assertSame(StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe())).findFirst().get().getName(),"recipe 1" );

        recipeFactoryManager.validatedRecipes();
        isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.TO_BE_VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, false);

        isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, true);
        assertSame(StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe -> StateRecipe.VALIDATED.equals(recipe.getStateRecipe())).findFirst().get().getName(),"recipe 1" );

        recipeFactoryManager.addRecipes();
        isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.VALIDATED.equals(recipe.getStateRecipe()));
        assertSame(isPresent, false);

        isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.AVAILABLE.equals(recipe.getStateRecipe()));
        assertSame(isPresent, true);
        assertSame(StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe -> StateRecipe.AVAILABLE.equals(recipe.getStateRecipe()) && recipe.getName().equals("recipe 1")),true);

    }
}
