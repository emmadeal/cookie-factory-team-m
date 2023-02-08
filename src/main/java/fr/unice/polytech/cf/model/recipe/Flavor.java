package fr.unice.polytech.cf.model.recipe;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Flavor extends Ingredient{

    public static int limitUp = 1;

    public static int limitDown = 0;

    public Flavor(@JsonProperty("price")float price,
                  @JsonProperty("name")String name) {
        super(price, name);
    }

    @Override
    public boolean checkIngredients(Recipe recipe) {
        if (recipe instanceof PartyRecipe) return this.checkLimit(recipe, 0, recipe.getNbF(), limitUp*3);
        else return this.checkLimit(recipe, 1, recipe.getNbF(), limitUp);
    }

}
