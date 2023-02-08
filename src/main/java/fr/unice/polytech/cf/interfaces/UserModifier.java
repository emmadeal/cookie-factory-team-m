package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.client.User;

import java.util.UUID;

public interface UserModifier {

    User joinLoyaltyProgram(UUID userId) throws ResourceNotFoundException;

    User unsubscribeToShopTooGoodToGONotifications(UUID shopId,UUID userId) throws ResourceNotFoundException;

    User subscribeToShopTooGoodToGONotifications(UUID shopId,UUID userId) throws ResourceNotFoundException;


}
