package fr.unice.polytech.cf.repositories;

import fr.unice.polytech.cf.model.factory.Chef;
import fr.unice.polytech.cf.repository.BasicRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ChefRepository extends BasicRepositoryImpl<Chef, UUID> {
}
