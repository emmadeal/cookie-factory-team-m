package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.model.recipe.Recipe;

import java.util.UUID;

public interface RecipeFactoryManager {

    Recipe createRecipe(String recipeName) throws Exception;
    void validatedRecipes();

    void addRecipes();

    void removeRecipe(UUID recipeId);
}
