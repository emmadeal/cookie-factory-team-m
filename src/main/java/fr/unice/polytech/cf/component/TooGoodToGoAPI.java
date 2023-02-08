package fr.unice.polytech.cf.component;


import fr.unice.polytech.cf.model.client.SurpriseBasket;
import fr.unice.polytech.cf.repositories.TooGoodToGoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TooGoodToGoAPI {

    private final TooGoodToGoRepository tooGoodToGoRepository;


    public UUID addSurpriseBasket(SurpriseBasket surpriseBasket){
        UUID surpriseBasketTooGoodToGoId = UUID.randomUUID();
        surpriseBasket.setTooGoodToGoId(surpriseBasketTooGoodToGoId);
        tooGoodToGoRepository.save(surpriseBasket,surpriseBasket.getId());
        return surpriseBasketTooGoodToGoId;
    }


    public double paySurpriseBasket(UUID surpriseBasketTooGoodToGoId){
        double price=0 ;
        for(SurpriseBasket surpriseBasket :tooGoodToGoRepository.findAll()){
            if(surpriseBasket.getTooGoodToGoId() == surpriseBasketTooGoodToGoId){
                price = surpriseBasket.getPrice();
                tooGoodToGoRepository.deleteById(surpriseBasket.getId());
                break;
            }
        }
        return price;
    }
}