package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.model.client.SurpriseBasket;

import java.util.UUID;

public interface NotificationObserverController {

    public void update(SurpriseBasket surpriseBasket, UUID userId) throws Exception;
}
