package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.model.shop.Shop;

public interface FactoryRessources {
    Shop addShop(String city);

    void addIngredientsToCatalog();
}
