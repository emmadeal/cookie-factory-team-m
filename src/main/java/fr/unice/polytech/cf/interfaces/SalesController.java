package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.model.client.Item;

import java.util.Set;

public interface SalesController {

    // pas facade
    void updatesSalesOfRecipe(Set<Item> basket);

    void elaborateNewRecipeMonthly() throws Exception;




}
