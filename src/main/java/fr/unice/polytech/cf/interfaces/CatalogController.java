package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.model.recipe.Ingredient;

public interface CatalogController {

    void addNewIngredient(Ingredient ingredient);

    void registerCatalog();
}
