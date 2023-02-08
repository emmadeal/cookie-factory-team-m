package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.ShopInformationsGetter;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.Occasion;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Theme;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.OccasionRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import fr.unice.polytech.cf.repositories.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class ShopShowcase implements ShopInformationsGetter {

    private final CookRepository cookRepository;

    private final OccasionRepository occasionRepository;

    private final ThemeRepository themeRepository;

    private final RecipeRepository recipeRepository;

    @Override
    public List<Theme> getThemeByShops(Shop shop) throws ResourceNotFoundException {
        List<Cook> cooks= StreamSupport.stream(cookRepository.findAll().spliterator(), false)
                .filter(cook -> shop.getId().equals(cook.getShopId())).toList();
        List<Theme> themes = new ArrayList<>();
        for (Cook cook : cooks) {
            LocalTime now = LocalTime.now();
            if(now.isAfter(cook.getBeginHour()) && now.isBefore(cook.getEndHour())){
                for (UUID themeId : cook.getThemeIds()) {
                    Theme theme = themeRepository.findById(themeId).orElseThrow(
                            () -> new ResourceNotFoundException(String.format("No theme found with given id %s", themeId.toString()))
                    );
                    if (!themes.contains(theme))
                        themes.add(theme);
                }
            }
        }
        return themes;
    }

    @Override
    public List<Occasion> getOccasionByShops(Shop shop) throws ResourceNotFoundException {
        List<Occasion> occasions = new ArrayList<>();
        for (UUID occasionId : shop.getOccasionIds()) {
            Occasion occasion = occasionRepository.findById(occasionId).orElseThrow(
                    () -> new ResourceNotFoundException(String.format("No occasion found with given id %s", occasionId.toString()))
            );
            occasions.add(occasion);
        }
        return occasions;
    }

    @Override
    public List<Recipe> getRecipeInStockByShops(Shop shop) {
       List<Recipe> recipeInStock = new ArrayList<>();
       List<Recipe> recipesAvailable = getRecipesByState(StateRecipe.AVAILABLE);
       boolean inStock;
       for(Recipe recipe : recipesAvailable){
           inStock =true;
           for(Ingredient ingredient : recipe.getIngredients().keySet()){
               int stockIngredientShop = shop.getStock().get(ingredient)!=null?shop.getStock().get(ingredient):0;
               if(stockIngredientShop < recipe.getIngredients().get(ingredient)){
                   inStock =false;
                   break;
               }
           }
           if(inStock)
               recipeInStock.add(recipe);
       }
    return recipeInStock;
    }

    @Override
    public List<Ingredient> getIngredientInStockByShops(Shop shop) {
        return new ArrayList<>(shop.getStock().keySet());
    }

    public List<Recipe> getRecipesByState(StateRecipe stateRecipe) {
        return StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe -> stateRecipe.equals(recipe.getStateRecipe())).collect(Collectors.toList());

    }
}
