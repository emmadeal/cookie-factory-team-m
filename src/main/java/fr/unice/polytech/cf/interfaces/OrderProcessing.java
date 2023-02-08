package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.client.Order;

public interface OrderProcessing {

    void process(Order order) throws Exception;

    void stopOrderProcessing(Order order) throws ResourceNotFoundException;

    void readyOrders() throws ResourceNotFoundException;
}