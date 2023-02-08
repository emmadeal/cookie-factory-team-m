package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.CantOrderException;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.enumeration.Size;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.shop.Shop;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

public interface ClientOrderChoices {

    void chooseShop(UUID clientId, Shop shop) throws ResourceNotFoundException, CantOrderException;

    void choosePickUpHour(UUID clientId, LocalDateTime hour) throws Exception;

    void cancelOrder(UUID clientId, Order order) throws Exception;

    void validateOrder(UUID clientId) throws Exception;

    void addInCashier(UUID clientId, Item item) throws Exception;

    void deleteInCashier(UUID clientId, Item item) throws Exception;

    void createPersonalizedPartyRecipeAndAddInCashier(UUID clientId, Recipe recipe, Size size, HashMap<Ingredient,Integer> ingredientsPlus , HashMap<Ingredient, Integer> ingredientsWithout, UUID occasionId, UUID themeId) throws Exception;



}
