package fr.unice.polytech.cf.repositories;

import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.repository.BasicRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserRepository extends BasicRepositoryImpl<User, UUID> {

}

