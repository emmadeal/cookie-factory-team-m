package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.client.Order;

public interface NotificationSender {


    void notifyUser(Order order, int time) throws ResourceNotFoundException;

}
