package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.enumeration.Size;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.Recipe;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public interface PartyRecipeCreator {

    Set<Item> createPersonalizedPartyRecipe(Client client, Recipe recipe, Size size, HashMap<Ingredient, Integer> ingredientsPlus , HashMap<Ingredient, Integer> ingredientsWithout, UUID occasionId, UUID themeId) throws Exception;
}
