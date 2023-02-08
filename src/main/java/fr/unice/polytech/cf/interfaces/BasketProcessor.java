package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Order;

public interface BasketProcessor {

    Order validate(Client client) throws Exception;
}
