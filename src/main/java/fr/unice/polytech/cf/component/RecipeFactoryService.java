package fr.unice.polytech.cf.component;


import fr.unice.polytech.cf.interfaces.RecipeFactoryManager;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.factory.Factory;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.repositories.CatalogRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class RecipeFactoryService implements RecipeFactoryManager {

    private final RecipeRepository recipeRepository;

    private final CatalogRepository catalogRepository;

    @Override
    public Recipe createRecipe(String recipeName) throws Exception {
        List<Ingredient> getAvailableIngredientOfTheFactory =
                StreamSupport.stream(catalogRepository.findAll().spliterator(), false).toList();
        Recipe newRecipe = new Recipe(UUID.randomUUID(),recipeName, StateRecipe.TO_BE_VALIDATED);
        Random random = new Random();
        int randomIndex;

        if(getAvailableIngredientOfTheFactory.size()>0){
            while(!newRecipe.isValid()){
                randomIndex = random.nextInt(getAvailableIngredientOfTheFactory.size());
                addIngredient(getAvailableIngredientOfTheFactory.get(randomIndex),newRecipe);
            }
        }else{
            throw  new Exception("pas d'ingredient dispo dans la facotry");
        }

        recipeRepository.save(newRecipe,newRecipe.getId());
        return newRecipe;
    }
    @Override
    public void validatedRecipes() {
        List<Recipe> recipesToBeValidated = getRecipesByState(StateRecipe.TO_BE_VALIDATED);
        List<Recipe> recipesAvailable = getRecipesByState(StateRecipe.AVAILABLE);
        for(Recipe recipeToBeValidated : recipesToBeValidated ){
            if (recipeToBeValidated.getPrice() > Factory.limitPrice) {
                recipeRepository.deleteById(recipeToBeValidated.getId());
                return;
            }
            for (Recipe availableRecipe : recipesAvailable) {
                if (availableRecipe.getName().equals(recipeToBeValidated.getName()) || sameIngredients(availableRecipe,recipeToBeValidated)) {
                    recipeRepository.deleteById(recipeToBeValidated.getId());
                    return;
                }
            }
            recipeToBeValidated.setStateRecipe(StateRecipe.VALIDATED);
            recipeRepository.save(recipeToBeValidated,recipeToBeValidated.getId());
        }
    }


    private boolean sameIngredients(Recipe availableRecipe,Recipe recipeToBeValidated ) {
        if(availableRecipe.getIngredients() == recipeToBeValidated.getIngredients())
            return true;
        return false;
    }

    @Override
    public void addRecipes() {
        List<Recipe> recipesValidated = getRecipesByState(StateRecipe.VALIDATED);
        for(Recipe recipeValidated : recipesValidated ){
            recipeValidated.setStateRecipe(StateRecipe.AVAILABLE);
            recipeRepository.save(recipeValidated,recipeValidated.getId());
        }
    }

    @Override
    public void removeRecipe(UUID recipeId) {
        recipeRepository.deleteById(recipeId);
    }


    public void addIngredient(Ingredient ingredient,Recipe recipe) {
        if (ingredient.checkIngredients(recipe)) {
            HashMap<Ingredient, Integer> ingredients = recipe.getIngredients();
            if (ingredients.containsKey(ingredient)){
                ingredients.put(ingredient, ingredients.get(ingredient) + 1);
            }
            else{
                ingredients.put(ingredient, 1);
            }
            recipe.getPrice();
        }
    }

    public List<Recipe> getRecipesByState(StateRecipe stateRecipe) {
        return StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe -> stateRecipe.equals(recipe.getStateRecipe())).collect(Collectors.toList());

    }
}
