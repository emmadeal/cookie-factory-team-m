package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.AlreadyExistingCustomerException;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;

public interface SignInProcessor {

    User signIn(String name, String password, String phone, String mail, Order order) throws AlreadyExistingCustomerException;
}
