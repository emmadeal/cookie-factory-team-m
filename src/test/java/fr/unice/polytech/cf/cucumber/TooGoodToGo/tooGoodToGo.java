package fr.unice.polytech.cf.cucumber.TooGoodToGo;

import fr.unice.polytech.cf.component.Phone;
import fr.unice.polytech.cf.component.SurpriseBasketService;
import fr.unice.polytech.cf.component.TooGoodToGoAPI;
import fr.unice.polytech.cf.component.UserActions;
import fr.unice.polytech.cf.exception.AlreadyExistingCustomerException;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.ClientOrderChoices;
import fr.unice.polytech.cf.interfaces.ShopInformationsGetter;
import fr.unice.polytech.cf.model.client.*;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.IngredientCreator;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.model.shop.ShopManager;
import fr.unice.polytech.cf.repositories.*;
import fr.unice.polytech.cf.utils.Constant;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

public class tooGoodToGo {

    @Autowired
    private ClientOrderChoices clientOrderChoices;
    @Autowired
    TooGoodToGoAPI tooGoodToGoAPI;
    @Autowired
    SurpriseBasketRepository surpriseBasketRepository;
    @Autowired
    UserActions userActions;
    @Autowired
    TooGoodToGoRepository tooGoodToGoRepository;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ShopManagerRepository shopManagerRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CookRepository cookRepository;
    @Autowired
    private ShopInformationsGetter shopInformationsGetter;

    @Autowired
    SurpriseBasketService surpriseBasketService;
    Shop shop;
    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;
    Recipe recipe;
    ShopManager shopManager;
    Cook cook;
    Client client;
    User user;
    SurpriseBasket surpriseBasket;


    Phone phone;


    @Before
    public void settingUpContext() {
        shopRepository.deleteAll();
        recipeRepository.deleteAll();
        clientRepository.deleteAll();
        shopManagerRepository.deleteAll();
        cookRepository.deleteAll();
        tooGoodToGoRepository.deleteAll();
        surpriseBasketRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Given("a recipe with name {string} belongs to the factory recipe list")
    public void aRecipeWithNameBelongsToTheFactoryRecipeList(String name) throws ResourceNotFoundException {
        dough = IngredientCreator.createIngredient(1.5f,"dough", Constant.TYPE_DOUGH);
        flavor = IngredientCreator.createIngredient(1.5f,"flavor", Constant.TYPE_FLAVOR);
        topping = IngredientCreator.createIngredient(1.5f,"topping", Constant.TYPE_TOPPING);
        recipe = new Recipe();
        recipe.setName(name);
        recipe.setId(UUID.randomUUID());
        recipe.getIngredients().put(dough,1);
        recipe.getIngredients().put(topping,1);
        recipe.getIngredients().put(flavor,1);
        recipe.setPreparingTime(10);
        recipe.setStateRecipe(StateRecipe.AVAILABLE);
        recipeRepository.save(recipe,recipe.getId());
    }

    @And("a shop with city {string} has {int} cookies in stock")
    public void aShopWithCityHasCookiesInStock(String city, int nb) {
        shop = new Shop(city);
        shop.getStock().put(dough,nb);
        shop.getStock().put(topping,nb);
        shop.getStock().put(flavor,nb);
        shop.setTax(0.20);
        shopRepository.save(shop,shop.getId());
    }

    @And("a shopManager")
    public void aShopManager() {
        shopManager = new ShopManager("Paul",UUID.randomUUID());
    }

    @And("a cook with named {string}")
    public void aCookWithNamed(String name) {
        cook = new Cook(name,shop.getId());
        cook.setEndHour(shop.getClosingHour());
        cook.setBeginHour(shop.getOpeningHour());
        cookRepository.save(cook,cook.getId());
    }

    @And("a client who pays for {int} cookies")
    public void aClientWhoPaysForCookies(int nb) throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        client = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        List<Recipe> availableRecipeInShopStock = shopInformationsGetter.getRecipeInStockByShops(shop);
        clientOrderChoices.addInCashier(client.getId(),new Item(availableRecipeInShopStock.get(0),nb));
    }

    @And("TooGoodToGo has no surprise baskets in his list")
    public void toogoodtogoHasNoSurpriseBasketsInHisList() {

    }

    @And("a TooGoodToGo client with no surprise basket reserved")
    public void aTooGoodToGoClientWithNoSurpriseBasketReserved() throws ResourceNotFoundException, AlreadyExistingCustomerException {
        userActions.signIn("momo","mdp","0101010101","eoh@gmail.com",new Order());
        user = StreamSupport.stream(userRepository.findAll().spliterator(), false).toList().get(0);
        userActions.subscribeToShopTooGoodToGONotifications(shop.getId(),user.getId());
    }

    @Given("a surprise basket reserved in the TooGoodToGo surprise baskets list")
    public void aSurpriseBasketReservedInTheTooGoodToGoSurpriseBasketsList() {
        surpriseBasket = new SurpriseBasket();
        surpriseBasket.setReserve(true);
        tooGoodToGoAPI = new TooGoodToGoAPI(tooGoodToGoRepository);
        phone = new Phone(userRepository);
       surpriseBasketRepository.save(surpriseBasket,surpriseBasket.getId());
    }

    @When("a client try to order the surprise basket")
    public void aClientTryToOrderTheSurpriseBasket() throws Exception {
        tooGoodToGoAPI = new TooGoodToGoAPI(tooGoodToGoRepository);
        phone = new Phone(userRepository);
       surpriseBasketService.reserveSurpriseBasket(surpriseBasket);
    }

    @Then("he has {int} surprise basket")
    public void hisBasketSurpriseIdIs(int nb) {
        user = StreamSupport.stream(userRepository.findAll().spliterator(), false).toList().get(0);
        if (user.getActualOrder().getBasket().stream().findFirst().isPresent()) Assertions.assertEquals(user.getActualOrder().getBasket().stream().findFirst().get().getQuantity(),nb);
    }

    @Given("a surprise basket available in the TooGoodToGo surprise baskets list")
    public void aSurpriseBasketAvailableInTheTooGoodToGoSurpriseBasketsList() {
        surpriseBasket = new SurpriseBasket();
        surpriseBasket.setReserve(false);
        surpriseBasketRepository.save(surpriseBasket,surpriseBasket.getId());
    }

    @Given("the shop have {int} surprise basket added to TooGoodToGo")
    public void theShopHaveSurpriseBasketAddedToTooGoodToGo(int arg0) {
        surpriseBasket = new SurpriseBasket();
        surpriseBasket.setTooGoodToGoId(UUID.randomUUID());
        surpriseBasket.setReserve(false);
        surpriseBasketRepository.save(surpriseBasket,surpriseBasket.getId());
    }

    @And("a TooGoodToGo client ordered the surprise basket")
    public void aTooGoodToGoClientOrderedTheSurpriseBasket() throws Exception {
        tooGoodToGoAPI = new TooGoodToGoAPI(tooGoodToGoRepository);
        phone = new Phone(userRepository);
        surpriseBasketService.reserveSurpriseBasket(surpriseBasket);
    }

    @When("the client takes his order")
    public void theClientTakesHisOrder() throws ResourceNotFoundException {
        surpriseBasketService.giveSurpriseBasket(surpriseBasket.getTooGoodToGoId());
    }

    @Then("there is {int} surprise basket in the shop's surprise baskets list")
    public void thereIsSurpriseBasketInTheShopSSurpriseBasketsList(int nb) {
        if (surpriseBasketRepository.storage!=null) Assertions.assertEquals(surpriseBasketRepository.storage.size(),nb);
    }

    @Then("there is {int} surprise basket in the TooGoodToGo surprise baskets list")
    public void thereIsSurpriseBasketInTheTooGoodToGoSurpriseBasketsList(int nb) {
        if (tooGoodToGoRepository.storage!=null) Assertions.assertEquals(tooGoodToGoRepository.storage.size(),nb);
    }

    @Then("the user recieve a new notification")
    public void theUserRecieveANewNotification() {
        Assertions.assertEquals(user.getNotifications().size(),1);
    }

    @When("a new surprise basket is created")
    public void aNewSurpriseBasketIsCreated() {
        Order order = new Order();
        order.setState(StateOrder.OBSOLETE);
        order.setShop(shop);
        orderRepository.save(order,UUID.randomUUID());
        tooGoodToGoAPI = new TooGoodToGoAPI(tooGoodToGoRepository);
        phone = new Phone(userRepository);
        surpriseBasketService.createSurpriseBasket();
    }
}
