package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.EmptyBasketException;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.shop.Cook;

public interface CookAvailabilityVerifier {

    Cook SearchCook(Order order) throws EmptyBasketException;
}
