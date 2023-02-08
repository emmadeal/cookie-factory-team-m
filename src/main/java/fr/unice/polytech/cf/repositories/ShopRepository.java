package fr.unice.polytech.cf.repositories;

import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repository.BasicRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ShopRepository  extends BasicRepositoryImpl<Shop, UUID> {
}
