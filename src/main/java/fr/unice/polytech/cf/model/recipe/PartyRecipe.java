package fr.unice.polytech.cf.model.recipe;

import fr.unice.polytech.cf.model.enumeration.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PartyRecipe extends Recipe {

    private Size size;

    private UUID occasionId;

    private UUID themeId;

    public PartyRecipe(Recipe recipe,HashMap<Ingredient, Integer> ingredients, Size size,UUID occasionId, UUID themeId) {
        super(recipe.getId(),ingredients,recipe.getName()+"Party ",recipe.getPrice(),recipe.isMixed(),recipe.isWellDone(),recipe.getCookingTime(),recipe.getPreparingTime()*size.getQuantity(),recipe.getCheckNb(),recipe.getNumberOfOrders(),recipe.getStateRecipe());
        this.size = size;
        this.themeId=themeId;
        this.occasionId=occasionId;
    }
}
