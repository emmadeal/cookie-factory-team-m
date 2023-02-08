package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.model.client.Client;
import org.springframework.stereotype.Component;


@Component
public class BankAPI  {

    public boolean pay(Client client, double value) {
        return (value>0);
    }
}
