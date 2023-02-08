package fr.unice.polytech.cf.repositories;

import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.repository.BasicRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class CookRepository extends BasicRepositoryImpl<Cook, UUID> {
}
