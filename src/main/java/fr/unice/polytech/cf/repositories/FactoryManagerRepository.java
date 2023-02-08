package fr.unice.polytech.cf.repositories;

import fr.unice.polytech.cf.model.factory.FactoryManager;
import fr.unice.polytech.cf.repository.BasicRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class FactoryManagerRepository extends BasicRepositoryImpl<FactoryManager, UUID> {
}
