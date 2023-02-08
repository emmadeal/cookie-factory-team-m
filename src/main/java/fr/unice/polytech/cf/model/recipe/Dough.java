package fr.unice.polytech.cf.model.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dough extends Ingredient{

    public static int limit = 1;

    public Dough(@JsonProperty("price")float price,
                 @JsonProperty("name")String name) {
        super(price, name);
    }

    @Override
    public boolean checkIngredients(Recipe recipe) {
        if (recipe instanceof PartyRecipe) return this.checkLimit(recipe, 0, recipe.getNbD(), limit*3);
        else return this.checkLimit(recipe, 0, recipe.getNbD(), limit);
    }
}
