package fr.unice.polytech.cf.model.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Topping extends Ingredient{

    public static int limitUp = 3;

    public static int limitDown = 0;

    public Topping(@JsonProperty("price") float price,
                   @JsonProperty("name") String name) {
        super(price, name);
    }

    @Override
    public boolean checkIngredients(Recipe recipe) {
        if (recipe instanceof PartyRecipe) return this.checkLimit(recipe, 0, recipe.getNbT(), limitUp*3);
        else return this.checkLimit(recipe,2, recipe.getNbT(), limitUp);
    }
}
