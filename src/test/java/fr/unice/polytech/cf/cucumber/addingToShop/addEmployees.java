package fr.unice.polytech.cf.cucumber.addingToShop;

import fr.unice.polytech.cf.component.ShopRH;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.model.shop.ShopManager;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.ShopManagerRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.StreamSupport;

@SpringBootTest
public class addEmployees {

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    ShopManagerRepository shopManagerRepository;

    @Autowired
    CookRepository cookRepository;

    @Autowired
    ShopRH shopRH;


    @Before
    public void settingUpContext() {
        shopRepository.deleteAll();
    }

    @Given("a shop in {string}")
    public void aShopIn(String city) {
        Shop shop = new Shop(city);
        shopRepository.save(shop, shop.getId());
    }

    @When("the shop in {string} add a manager named {string}")
    public void theShopInAddAManager(String city, String name) {
        Shop shop = StreamSupport.stream(shopRepository.findAll().spliterator(), false)
                .filter(shop1 -> shop1.getCity().equals(city)).findFirst().get();
        shopRH.hireShopManager(name, shop);
    }

    @Then("there is {int} manager in the shop in {string}")
    public void thereIsManagerInTheShopIn(int nb, String city) {
        Shop shop = StreamSupport.stream(shopRepository.findAll().spliterator(), false)
                .filter(shop1 -> shop1.getCity().equals(city)).findFirst().get();

        int check = StreamSupport.stream(shopManagerRepository.findAll().spliterator(), false)
                .filter(shopManager1 -> shopManager1.getShopId().equals(shop.getId())).toList().size();

        Assertions.assertEquals(nb, check);
    }

    @When("the shop in {string} add a cook named {string}")
    public void theShopInAddACook(String city, String name) {
        Shop shop = StreamSupport.stream(shopRepository.findAll().spliterator(), false)
                .filter(shop1 -> shop1.getCity().equals(city)).findFirst().get();
        shopRH.hireCook(name, shop);
    }

    @Then("there is {int} cook in the shop in {string}")
    public void thereIsCookInTheShopIn(int nb, String city) {
        Shop shop = StreamSupport.stream(shopRepository.findAll().spliterator(), false)
                .filter(shop1 -> shop1.getCity().equals(city)).findFirst().get();

        int check = StreamSupport.stream(cookRepository.findAll().spliterator(), false)
                .filter(cook1 -> cook1.getShopId().equals(shop.getId())).toList().size();

        Assertions.assertEquals(nb, check);
    }

    @When("the shop in {string} delete a manager named {string}")
    public void theShopDeleteAManager(String city, String name) {
        ShopManager shopManager = StreamSupport.stream(shopManagerRepository.findAll().spliterator(), false)
                .filter(shopManager1 -> shopManager1.getName().equals(name)).findFirst().get();
        try {
            shopRH.fireShopManager(shopManager);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @When("the shop in {string} delete a cook named {string}")
    public void theShopDeleteACook(String city, String name) {
        Cook cook = StreamSupport.stream(cookRepository.findAll().spliterator(), false)
                .filter(cook1 -> cook1.getName().equals(name)).findFirst().get();
        try {
            shopRH.fireCook(cook);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}