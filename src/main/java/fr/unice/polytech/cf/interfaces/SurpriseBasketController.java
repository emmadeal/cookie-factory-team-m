package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.client.SurpriseBasket;

import java.util.UUID;

public interface SurpriseBasketController {

    void createSurpriseBasket();

    boolean reserveSurpriseBasket(SurpriseBasket surpriseBasket) throws Exception;

    void giveSurpriseBasket(UUID surpriseBasketTooGoodToGoId) throws ResourceNotFoundException;
}
