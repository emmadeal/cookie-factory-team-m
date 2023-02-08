package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.NegativeQuantityException;
import fr.unice.polytech.cf.interfaces.BasketModifier;
import fr.unice.polytech.cf.interfaces.PartyRecipeCreator;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.enumeration.Size;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.PartyRecipe;
import fr.unice.polytech.cf.model.recipe.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PartyRecipePlant implements PartyRecipeCreator {

    private final BasketModifier basketModifier;

    @Override
    public Set<Item> createPersonalizedPartyRecipe(Client client, Recipe recipe, Size size,HashMap<Ingredient, Integer> ingredientsPlus ,HashMap<Ingredient, Integer> ingredientsWithout,UUID occasionId, UUID themeId) throws Exception {
        HashMap<Ingredient, Integer> ingredients  = recipe.getIngredients();
        for (Ingredient ingredientPlus : ingredientsPlus.keySet()) {
            int quantityOfIngredient = recipe.getIngredients().get(ingredientPlus)!=null?recipe.getIngredients().get(ingredientPlus):0;
            int quantityPlus = ingredientsPlus.get(ingredientPlus);
                ingredients.put(ingredientPlus,quantityOfIngredient+quantityPlus);
        }

        for (Ingredient ingredientWithout : ingredientsWithout.keySet()) {
            int quantityOfIngredient = recipe.getIngredients().get(ingredientWithout)!=null?recipe.getIngredients().get(ingredientWithout):0;
            int quantityPlus = ingredientsWithout.get(ingredientWithout);
            int newQuantity = quantityOfIngredient-quantityPlus;
            if(newQuantity<0)
                throw new NegativeQuantityException("vous ne pouvez pas supprimer des ingredient qui ne sont pas dans la recette originale");
            ingredients.put(ingredientWithout,newQuantity);
        }
        PartyRecipe partyRecipe = new PartyRecipe(recipe,ingredients, size,themeId,occasionId);
        Item itemPartyRecipe = new Item(partyRecipe,size.getQuantity());
        Set<Item> basket = basketModifier.addInBasket(client,itemPartyRecipe);
        return basket;
    }
}
