package fr.unice.polytech.cf.repositories;

import fr.unice.polytech.cf.model.recipe.Occasion;
import fr.unice.polytech.cf.repository.BasicRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class OccasionRepository extends BasicRepositoryImpl<Occasion, UUID> {
}
