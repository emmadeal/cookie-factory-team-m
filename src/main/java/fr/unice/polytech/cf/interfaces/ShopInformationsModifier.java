package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.recipe.Occasion;
import fr.unice.polytech.cf.model.recipe.Theme;
import fr.unice.polytech.cf.model.shop.Shop;

import java.time.LocalTime;
import java.util.UUID;

public interface ShopInformationsModifier {

    Shop fixTax(UUID shopId, double taxValue) throws ResourceNotFoundException;

    Occasion addOccasion(UUID shopId, String name) throws ResourceNotFoundException;

    void setUpOpeningHour(UUID shopId, LocalTime openingHour) throws ResourceNotFoundException;

    void setUpClosingHour(UUID shopId, LocalTime closingHour) throws ResourceNotFoundException;

    Theme addThemeToCook(UUID cookId, String name) throws ResourceNotFoundException;
}
