package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;

public interface LoginProcessor {

    User login(String name , String password, Order order) throws Exception;
}
