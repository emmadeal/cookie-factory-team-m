package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.factory.Chef;
import fr.unice.polytech.cf.model.factory.FactoryManager;

public interface FactoryHumanRessources {

    Chef hireChef (String name);

    void fireChef (Chef chef) throws ResourceNotFoundException;

    FactoryManager hireFactoryManager (String name);

    void fireFactoryManager (FactoryManager factoryManager) throws ResourceNotFoundException;
}
