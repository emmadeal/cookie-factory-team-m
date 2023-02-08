package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.Occasion;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Theme;
import fr.unice.polytech.cf.model.shop.Shop;

import java.util.List;

public interface ShopInformationsGetter {

    List<Theme> getThemeByShops(Shop shop) throws ResourceNotFoundException;

    List<Occasion> getOccasionByShops(Shop shop) throws ResourceNotFoundException;

    List<Recipe> getRecipeInStockByShops(Shop shop) ;

    List<Ingredient> getIngredientInStockByShops(Shop shop) ;

}
