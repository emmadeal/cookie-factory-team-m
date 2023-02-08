package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.CatalogController;
import fr.unice.polytech.cf.interfaces.SalesController;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.IngredientCreator;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import fr.unice.polytech.cf.utils.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SalesManagerTest {

    @Autowired
    private  RecipeRepository recipeRepository;

    @Autowired
    private SalesController salesController;

    @Autowired
    private CatalogController catalogController;


    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;

    Recipe recipe;



    @BeforeEach
    void setUp() throws ResourceNotFoundException {
        recipeRepository.deleteAll();
        dough = IngredientCreator.createIngredient(1.5f,"dough", Constant.TYPE_DOUGH);
        flavor = IngredientCreator.createIngredient(1.5f,"flavor", Constant.TYPE_FLAVOR);
        topping = IngredientCreator.createIngredient(1.5f,"topping", Constant.TYPE_TOPPING);

        recipe = new Recipe();
        recipe.setId(UUID.randomUUID());
        recipe.getIngredients().put(dough,1);
        recipe.getIngredients().put(topping,1);
        recipe.getIngredients().put(flavor,1);
        recipe.setPreparingTime(10);
        recipe.setStateRecipe(StateRecipe.AVAILABLE);
        recipeRepository.save(recipe,recipe.getId());

        catalogController.registerCatalog();
    }

    @Test
    public void updatesSalesOfRecipe() {
        Set<Item> basket = new HashSet<>();
        basket.add(new Item(recipe,3));
        assertEquals(recipeRepository.findById(recipe.getId()).get().getNumberOfOrders(),0);
        salesController.updatesSalesOfRecipe(basket);
        assertEquals(recipeRepository.findById(recipe.getId()).get().getNumberOfOrders(),3);
    }


    @Test
    public void elaborateNewRecipeMonthly() throws Exception {
        Set<Item> basket = new HashSet<>();
        basket.add(new Item(recipe,3));
        assertTrue(recipeRepository.existsById(recipe.getId()));
        salesController.elaborateNewRecipeMonthly();
        assertFalse(recipeRepository.existsById(recipe.getId()));

        boolean isPresent = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .anyMatch(recipe1 -> StateRecipe.TO_BE_VALIDATED.equals(recipe1.getStateRecipe()));
        assertTrue(isPresent);
    }

}



