package fr.unice.polytech.cf.repositories;

import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.repository.BasicRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class OrderRepository extends BasicRepositoryImpl<Order, UUID> {
}
