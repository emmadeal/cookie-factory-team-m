package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.client.Order;

public interface ObsoleteManager {

    void obsoleteOrder(Order order) throws ResourceNotFoundException;

    void checkOrderSate() throws ResourceNotFoundException;
}
