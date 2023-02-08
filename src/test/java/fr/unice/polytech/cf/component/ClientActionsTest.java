package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.CantCancelException;
import fr.unice.polytech.cf.exception.CantOrderException;
import fr.unice.polytech.cf.exception.CloserShopException;
import fr.unice.polytech.cf.exception.NoCookAvailableException;
import fr.unice.polytech.cf.exception.NoShopException;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.exception.ThemeOrOccasionIsNotPossibleException;
import fr.unice.polytech.cf.interfaces.CancelationManager;
import fr.unice.polytech.cf.interfaces.ClientOrderChoices;
import fr.unice.polytech.cf.interfaces.LoginProcessor;
import fr.unice.polytech.cf.interfaces.SignInProcessor;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.model.enumeration.Size;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.IngredientCreator;
import fr.unice.polytech.cf.model.recipe.Occasion;
import fr.unice.polytech.cf.model.recipe.PartyRecipe;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Theme;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.ClientRepository;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.OccasionRepository;
import fr.unice.polytech.cf.repositories.OrderRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import fr.unice.polytech.cf.repositories.ThemeRepository;
import fr.unice.polytech.cf.repositories.UserRepository;
import fr.unice.polytech.cf.utils.Constant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class ClientActionsTest {

    @Autowired
    private ClientOrderChoices clientOrderChoices;


    @Autowired
    private ClientRepository clientRepository;


    @Autowired
    private UserRepository userRepository;


    @Autowired
    ShopRepository shopRepository;


    @Autowired
    RecipeRepository recipeRepository;


    @Autowired
    OrderRepository orderRepository;


    @Autowired
    CookRepository cookRepository;

    @Autowired
    private LoginProcessor loginProcessor;


    @Autowired
    private SignInProcessor signInProcessor;

    @Autowired
    private  OccasionRepository occasionRepository;

    @Autowired
    private  ThemeRepository themeRepository;

    @Autowired
    private CancelationManager cancelationManager;

    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;

    Recipe recipe;

    Shop shop;

    Cook cook;

    Theme theme;
    Occasion occasion ;


    @BeforeEach
    void setUp() throws ResourceNotFoundException {
        userRepository.deleteAll();
        clientRepository.deleteAll();
        orderRepository.deleteAll();
        cookRepository.deleteAll();
        recipeRepository.deleteAll();
        shopRepository.deleteAll();
        shop = new Shop("nice");
        shop.setOpeningHour(LocalTime.of(0,0));
        shop.setClosingHour(LocalTime.of(23,59));


        dough = IngredientCreator.createIngredient(1.5f,"dough", Constant.TYPE_DOUGH);
        flavor = IngredientCreator.createIngredient(1.5f,"flavor", Constant.TYPE_FLAVOR);
        topping = IngredientCreator.createIngredient(1.5f,"topping", Constant.TYPE_TOPPING);


        recipe = new Recipe();
        recipe.setId(UUID.randomUUID());
        recipe.getIngredients().put(dough,1);
        recipe.getIngredients().put(topping,1);
        recipe.getIngredients().put(flavor,1);
        recipe.setPreparingTime(10);

        recipeRepository.save(recipe,recipe.getId());

        shop.getStock().put(dough,10);
        shop.getStock().put(topping,10);
        shop.getStock().put(flavor,10);

         theme = new Theme("fleur");
         themeRepository.save(theme,theme.getId());
         occasion = new Occasion("anniversaire");
        occasionRepository.save(occasion,occasion.getId());
         shop.getOccasionIds().add(occasion.getId());
        cook = new Cook("marie",shop.getId());
        cook.setEndHour(shop.getClosingHour());
        cook.setBeginHour(shop.getOpeningHour());
        cook.getThemeIds().add(theme.getId());

        shopRepository.save(shop,shop.getId());
        cookRepository.save(cook,cook.getId());
    }

    @Test
    public void ChooseShop() throws ResourceNotFoundException, CantOrderException {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        assertSame(claire.getActualOrder().getShop(), shop);
        assertSame(claire.getActualOrder().isLoginClient(), false);
    }

    @Test
    public void ChooseShopUserError() throws Exception {
        signInProcessor.signIn("claire","1234","0640347631","clairemarini@gmail.com",new Order());
        User user = loginProcessor.login("claire","1234",new Order());
        clientOrderChoices.chooseShop(user.getId(),shop);
        LocalDateTime localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.addInCashier(user.getId(),new Item(recipe,1));
        clientOrderChoices.choosePickUpHour(user.getId(),localDateTime );
        clientOrderChoices.validateOrder(user.getId());
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId())).findFirst().get();
        assertTrue(order.isLoginClient());
        cancelationManager.cancelOrder(order);

        clientOrderChoices.chooseShop(user.getId(),shop);
        clientOrderChoices.addInCashier(user.getId(),new Item(recipe,1));
        clientOrderChoices.choosePickUpHour(user.getId(),localDateTime );
        clientOrderChoices.validateOrder(user.getId());
        long number = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId())).count();
        assertEquals(2, number);
        order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> user.getId().equals(order1.getClientId()) && order1.getState()==StateOrder.PAID).findFirst().get();
        assertTrue(order.isLoginClient());
        cancelationManager.cancelOrder(order);

        Assertions.assertThrows(CantOrderException.class, () -> {
            clientOrderChoices.chooseShop(user.getId(),shop);
        });
    }

    @Test
    public void ChooseShopUser() throws Exception {
        signInProcessor.signIn("claire","1234","0640347631","clairemarini@gmail.com",new Order());
        User user = loginProcessor.login("claire","1234",new Order());
        clientOrderChoices.chooseShop(user.getId(),shop);
        Client claire = StreamSupport.stream(userRepository.findAll().spliterator(), false).toList().get(0);
        assertSame(claire.getActualOrder().getShop(), shop);
        assertSame(claire.getActualOrder().isLoginClient(), true);
    }

    @Test
    public void choosePickUpHour() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        assertSame(claire.getActualOrder().getShop(), shop);
        claire.getActualOrder().getBasket().add(new Item(recipe,2));

        LocalDateTime localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        claire = clientRepository.findById(claire.getId()).get();
        assertSame(claire.getActualOrder().getShop(), shop);
        assertSame(claire.getActualOrder().isLoginClient(), false);
        assertSame(claire.getActualOrder().getPickUpHour(),localDateTime);
        assertSame(claire.getActualOrder().getCookId(), cook.getId());
    }

    @Test
    public void choosePickUpHourErrorNoCook() throws Exception {
        cookRepository.deleteAll();
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        assertSame(claire.getActualOrder().getShop(), shop);
        claire.getActualOrder().getBasket().add(new Item(recipe,2));
        LocalDateTime localDateTime =LocalDateTime.of(2022,10,10,10,10);
        Assertions.assertThrows(NoCookAvailableException.class, () -> {
            clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        });
    }

    @Test
    public void choosePickUpHourError() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        claire.getActualOrder().setShop(null);
        claire.getActualOrder().getBasket().add(new Item(recipe,2));
        LocalDateTime localDateTime =LocalDateTime.of(2022,10,10,10,10);
        Assertions.assertThrows( NoShopException.class, () -> {
            clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        });
        shop.setOpeningHour(LocalTime.of(8,0 ));
        shop.setClosingHour(LocalTime.of(18,0));
        shopRepository.save(shop,shop.getId());
        claire.getActualOrder().setShop(shop);
        LocalDateTime localDateTime2 =LocalDateTime.of(2022,10,13,20,10);
        Assertions.assertThrows( CloserShopException.class, () -> {
            clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime2 );
        });
    }

    @Test
    public void choosePickUpHourErrorNoShop() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        claire.getActualOrder().setShop(null);
        claire.getActualOrder().getBasket().add(new Item(recipe,2));
        LocalDateTime localDateTime =LocalDateTime.of(2022,10,10,10,10);
        Assertions.assertThrows( NoShopException.class, () -> {
            clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        });

        claire.getActualOrder().setShop(null);
        LocalDateTime localDateTime2 =LocalDateTime.of(2022,10,13,20,10);
        Assertions.assertThrows( NoShopException.class, () -> {
            clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime2 );
        });
    }

    @Test
    public void addInCashier() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        clientOrderChoices.addInCashier(claire.getId(),new Item(recipe,1));
        claire = clientRepository.findById(claire.getId()).get();
        assertSame(claire.getActualOrder().getBasket().size(), 1);

        boolean isPresent = claire.getActualOrder().getBasket().stream().anyMatch(item1 ->
                item1.getRecipe() ==recipe && item1.getQuantity()==1
        );
        assertTrue(isPresent);
        assertEquals(shop.getStock().get(dough),9);
        assertEquals(shop.getStock().get(flavor),9);
        assertEquals(shop.getStock().get(topping),9);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),9);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),9);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),9);
    }

    @Test
    public void deleteInCashier() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        clientOrderChoices.addInCashier(claire.getId(),new Item(recipe,4));
        assertEquals(shop.getStock().get(dough),6);
        assertEquals(shop.getStock().get(flavor),6);
        assertEquals(shop.getStock().get(topping),6);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),6);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),6);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),6);

        claire = clientRepository.findById(claire.getId()).get();
        assertSame(claire.getActualOrder().getBasket().size(), 1);

        clientOrderChoices.deleteInCashier(claire.getId(),new Item(recipe,1));
        claire = clientRepository.findById(claire.getId()).get();
        assertSame(claire.getActualOrder().getBasket().size(), 1);

        boolean isPresent = claire.getActualOrder().getBasket().stream().anyMatch(item1 ->
                item1.getRecipe() ==recipe && item1.getQuantity()==3
        );
        assertTrue(isPresent);

        assertEquals(shop.getStock().get(dough),7);
        assertEquals(shop.getStock().get(flavor),7);
        assertEquals(shop.getStock().get(topping),7);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),7);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),7);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),7);

         }


    @Test
    public void validate() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);;
        LocalDateTime localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.addInCashier(claire.getId(),new Item(recipe,1));
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        Client claire2 = clientRepository.findById(claire.getId()).get();
        clientOrderChoices.validateOrder(claire.getId());
        boolean isPresent = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .anyMatch(order1 -> claire2.getId().equals(order1.getClientId()));
        assertTrue(isPresent);

         claire = clientRepository.findById(claire.getId()).get();
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire2.getId().equals(order1.getClientId())).findFirst().get();
        assertSame(order.getState(), StateOrder.PAID);
        assertSame(order.isLoginClient(), false);
        assertSame(claire.getActualOrder().getClientId(), null);

        cook = cookRepository.findById(cook.getId()).get();
        isPresent = cook.getOrdersInProgress().stream().anyMatch(orderInProgress -> orderInProgress==order);
        assertTrue(isPresent);
    }


    @Test
    public void cancelOrder() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);;
        LocalDateTime localDateTime =LocalDateTime.of(2025,12,25,10,10);
         clientOrderChoices.addInCashier(claire.getId(),new Item(recipe,1));
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        Client claire2 = clientRepository.findById(claire.getId()).get();
        clientOrderChoices.validateOrder(claire.getId());
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire2.getId().equals(order1.getClientId())).findFirst().get();

        clientOrderChoices.cancelOrder(claire2.getId(),order);
        order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire2.getId().equals(order1.getClientId())).findFirst().get();

        assertSame(order.getState(), StateOrder.CANCEL);
        cook = cookRepository.findById(cook.getId()).get();
        assertSame(cook.getOrdersInProgress().size(), 0);
    }


    @Test
    public void cancelOrderErrorState() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);;
        LocalDateTime localDateTime =LocalDateTime.of(2022,12,25,10,10);
        clientOrderChoices.addInCashier(claire.getId(),new Item(recipe,1));
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        Client claire2 = clientRepository.findById(claire.getId()).get();
        Assertions.assertThrows( CantCancelException.class, () -> {
            clientOrderChoices.cancelOrder(claire2.getId(),claire.getActualOrder());;
        });
    }


    @Test
    public void cancelOrderErrorTime() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);;
        LocalDateTime localDateTime =LocalDateTime.of(2022,5,25,10,10);
        clientOrderChoices.addInCashier(claire.getId(),new Item(recipe,1));
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        Client claire2 = clientRepository.findById(claire.getId()).get();
        clientOrderChoices.validateOrder(claire.getId());
        Order order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order1 -> claire2.getId().equals(order1.getClientId())).findFirst().get();

        Assertions.assertThrows( CantCancelException.class, () -> {
            clientOrderChoices.cancelOrder(claire2.getId(),order);;
        });
    }

    @Test
    public void createPersonalizedPartyRecipeAndAddInCashier() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        assertSame(claire.getActualOrder().getShop(), shop);
        clientOrderChoices.createPersonalizedPartyRecipeAndAddInCashier(claire.getId(), recipe, Size.L, new HashMap<>(), new HashMap<>(), occasion.getId(), theme.getId());
        LocalDateTime localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        claire = clientRepository.findById(claire.getId()).get();
        assertSame(claire.getActualOrder().getBasket().size(), 1);
        boolean isPresent = claire.getActualOrder().getBasket().stream().anyMatch(item1 ->
                 item1.getQuantity()==4
        );
        assertTrue(isPresent);
         isPresent = claire.getActualOrder().getBasket().stream().anyMatch(item1 ->
                item1.getRecipe() instanceof PartyRecipe
        );
        assertTrue(isPresent);

    }

    @Test
    public void createPersonalizedPartyRecipeAndAddInCashierError() throws Exception {
        clientOrderChoices.chooseShop(null,shop);
        Client claire = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList().get(0);
        assertSame(claire.getActualOrder().getShop(), shop);
        claire.getActualOrder().getBasket().add(new Item(recipe,2));
        LocalDateTime localDateTime =LocalDateTime.of(2022,10,10,10,10);
        clientOrderChoices.choosePickUpHour(claire.getId(),localDateTime );
        Theme newTheme = new Theme("noel");
        Assertions.assertThrows( ThemeOrOccasionIsNotPossibleException.class, () -> {
            clientOrderChoices.createPersonalizedPartyRecipeAndAddInCashier(claire.getId(), recipe, Size.L, new HashMap<>(), new HashMap<>(), occasion.getId(), newTheme.getId());

        });

    }

}
