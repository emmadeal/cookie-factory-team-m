package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.CantCancelException;
import fr.unice.polytech.cf.exception.CantOrderException;
import fr.unice.polytech.cf.exception.CloserShopException;
import fr.unice.polytech.cf.exception.NoCookAvailableException;
import fr.unice.polytech.cf.exception.NoShopException;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.exception.ThemeOrOccasionIsNotPossibleException;
import fr.unice.polytech.cf.interfaces.BasketModifier;
import fr.unice.polytech.cf.interfaces.BasketProcessor;
import fr.unice.polytech.cf.interfaces.CancelationManager;
import fr.unice.polytech.cf.interfaces.ClientOrderChoices;
import fr.unice.polytech.cf.interfaces.CookAvailabilityVerifier;
import fr.unice.polytech.cf.interfaces.PartyRecipeCreator;
import fr.unice.polytech.cf.interfaces.ShopInformationsGetter;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.model.enumeration.Size;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.ClientRepository;
import fr.unice.polytech.cf.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClientActions implements ClientOrderChoices {

    private final CancelationManager cancelationManager;


    private final BasketProcessor basketProcessor;

    private final BasketModifier basketModifier;

    private final CookAvailabilityVerifier cookAvailabilityVerifier;

    private final PartyRecipeCreator partyRecipeCreator;

    private final ShopInformationsGetter shopInformationsGetter;

    private final UserRepository userRepository;

    private final ClientRepository clientRepository;



    @Override
    public void chooseShop(UUID userId,Shop shop) throws ResourceNotFoundException, CantOrderException {
        // si user connecté
        if(userId!=null){
            User user =(User) getClientOrUserById(userId);
            if(!cancelationManager.userCanOrder(user)){
                throw new CantOrderException(String.format("Faute de trop d'annulation vous ne pouvez plus commandez pendant %d minutes",cancelationManager.timeBeforeUserCanCancelOrder(user)));
            }
            user.getActualOrder().setShop(shop);
            user.getActualOrder().setLoginClient(true);
            userRepository.save(user,user.getId());
        }else {
            //sinon
            Client client = new Client();
            client.getActualOrder().setShop(shop);
            client.getActualOrder().setLoginClient(false);
            clientRepository.save(client,client.getId());
        }
    }

    @Override
    public void choosePickUpHour(UUID clientId, LocalDateTime hour) throws Exception {
        Client client = getClientOrUserById(clientId);
        Order orderActual = client.getActualOrder();
        Shop shop = orderActual.getShop();
        if(shop!=null){
            if (hour.minusMinutes(orderActual.getTotalPreparationMinutes()).isBefore(LocalDateTime.of(hour.getYear(), hour.getMonth(), hour.getDayOfMonth(), shop.getOpeningHour().getHour(), shop.getOpeningHour().getMinute())))
                throw  new CloserShopException("Vous ne pouvez pas commande quand le magasin est fermé");
            if (hour.isAfter(LocalDateTime.of(hour.getYear(), hour.getMonth(), hour.getDayOfMonth(),shop.getClosingHour().getHour(), shop.getClosingHour().getMinute())))
                throw  new CloserShopException("Vous ne pouvez pas commande quand le magasin est fermé");
            else {
                orderActual.setPickUpHour(hour);
                Cook cook =cookAvailabilityVerifier.SearchCook(orderActual);
                if(cook!=null){
                    orderActual.setCookId(cook.getId());
                    saveClient(client);
                    return;
                }
                throw new NoCookAvailableException("pas de cuisinier disponible a cette horaire");
            }
        }
        throw new NoShopException("Vous ne pouvez pas selectionner un heure de recuperation si aucun magasin n'est selectionné");
      }

    @Override
    public void cancelOrder(UUID clientId, Order order) throws Exception {
        StateOrder stateOrder = order.getState();
        LocalDateTime lo = order.getPreparationHour();
        if (stateOrder == StateOrder.PAID && !order.getPreparationHour().isBefore(LocalDateTime.now())) {
            cancelationManager.cancelOrder(order);
            return;
        }
        throw new CantCancelException("vous ne pouvez pas annulé une commande non payé ou deja préparé");
    }


    @Override
    public void validateOrder(UUID clientId) throws Exception {
        Client client = getClientOrUserById(clientId);
        basketProcessor.validate(client);
        client.setActualOrder(new Order());
        saveClient(client);
    }

    @Override
    public void addInCashier(UUID clientId, Item item) throws Exception {
        Client client = getClientOrUserById(clientId);
        Order order =client.getActualOrder();
        order.setBasket(basketModifier.addInBasket(client,item));
        saveClient(client);
    }

    @Override
    public void deleteInCashier(UUID clientId, Item item) throws Exception {
        Client client = getClientOrUserById(clientId);
        Order order =client.getActualOrder();
        order.setBasket(basketModifier.removeInBasket(client,item));
        saveClient(client);
    }


    @Override
    public void createPersonalizedPartyRecipeAndAddInCashier(UUID clientId, Recipe recipe, Size size, HashMap<Ingredient, Integer> ingredientsPlus , HashMap<Ingredient, Integer> ingredientsWithout, UUID occasionId, UUID themeId) throws Exception {
        Client client = getClientOrUserById(clientId);
        Order order =client.getActualOrder();
        if(verifyThemeAndOccasionIsPossible(client,themeId,occasionId)){
            order.setBasket(partyRecipeCreator.createPersonalizedPartyRecipe(client,recipe,size,ingredientsPlus,ingredientsWithout,themeId,occasionId));
            saveClient(client);
            return;
        }
        throw new ThemeOrOccasionIsNotPossibleException("pas possible");
    }

    public boolean verifyThemeAndOccasionIsPossible(Client client ,UUID themeId ,UUID occassionId) throws Exception {
        Shop shop= client.getActualOrder().getShop();
        boolean shopContainTheme = shopInformationsGetter.getThemeByShops(shop).stream()
                .anyMatch(theme -> theme.getId() == themeId);
        if (!shopContainTheme)
            return false ;
        return shop.getOccasionIds().contains(occassionId);
    }


    public Client getClientOrUserById(UUID clientId) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findById(clientId);
        if(user.isPresent()){
            return user.get();
        }
        return clientRepository.findById(clientId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("No client found with given id %s", clientId.toString()))
        );
    }

    public void saveClient(Client client){
        if(client.getActualOrder().isLoginClient()){
            userRepository.save((User)client,client.getId());
        }else{
            clientRepository.save(client,client.getId());
        }
    }
}
