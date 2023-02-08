package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;

public interface CancelationManager {

    void cancelOrder(Order order) throws ResourceNotFoundException;

    boolean userCanOrder(User user);

    long timeBeforeUserCanCancelOrder(User user);

}
