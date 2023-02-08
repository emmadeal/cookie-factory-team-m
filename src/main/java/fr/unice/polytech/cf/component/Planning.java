package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.EmptyBasketException;
import fr.unice.polytech.cf.interfaces.CookAvailabilityVerifier;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.recipe.PartyRecipe;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.repositories.CookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class Planning implements CookAvailabilityVerifier {

    private final CookRepository cookRepository;

    @Override
    public Cook SearchCook(Order order) throws EmptyBasketException {
        List<UUID> themeIds = new ArrayList<>();
        order.getBasket().forEach(item -> {
            Recipe recipe = item.getRecipe();
            if(recipe instanceof PartyRecipe){
                themeIds.add(((PartyRecipe) recipe).getThemeId());
            }
        });
        for (Cook cook : getCookByShopId(order.getShop().getId())) {
            if (cookIsAvailable(cook,order) && new HashSet<>(cook.getThemeIds()).containsAll(themeIds)) {
                return cook;
            }
        }
        return null;
    }


    public boolean cookIsAvailable(Cook cook, Order order) throws EmptyBasketException {
        LocalDateTime pickHourOrder = order.getPickUpHour();
        LocalDateTime beginPreparationTimeOrder = pickHourOrder.minusMinutes(order.getTotalPreparationMinutes());
        // si c'est apres le heure de fin de cook c'est mort
        if(cook.getEndHour().compareTo(LocalTime.of(pickHourOrder.getHour(),pickHourOrder.getMinute()))<0){
            return false;
        }
        // si c'est avant le heure de debut de cook c'est mort
        if(cook.getBeginHour().compareTo(LocalTime.of(beginPreparationTimeOrder.getHour(),beginPreparationTimeOrder.getMinute()))>0){
            return false;
        }
        for (Order orderInProgress : cook.getOrdersInProgress()) {
            LocalDateTime pickHourOrderInProgress = orderInProgress.getPickUpHour();
            LocalDateTime beginPreparationTimeOrderInProgress =pickHourOrderInProgress.minusMinutes(order.getTotalPreparationMinutes());

            // si le debut de la prepa est entre le debut et la fin d'une autre prepa pas possible
            if(beginPreparationTimeOrder.compareTo(beginPreparationTimeOrderInProgress)>=0 && beginPreparationTimeOrder.compareTo(pickHourOrderInProgress)<=0){
                return false;
            }
            //idem pour la fin de la prepa
            if(pickHourOrder.compareTo(beginPreparationTimeOrderInProgress)>=0 && pickHourOrder.compareTo(pickHourOrderInProgress)<=0){
                return false;
            }
        }
        return true;
    }

    public List<Cook> getCookByShopId(UUID ShopId) {
        return StreamSupport.stream(cookRepository.findAll().spliterator(), false)
                .filter(cook -> ShopId.equals(cook.getShopId())).collect(Collectors.toList());
    }
}
