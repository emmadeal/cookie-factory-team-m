package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.BadStateException;

import java.util.UUID;

public interface CheckRetrieveOrder {

    void giveOrder(UUID orderID) throws BadStateException;
}
