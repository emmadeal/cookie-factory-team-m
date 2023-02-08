package fr.unice.polytech.cf.cucumber.addingToFactory;

import fr.unice.polytech.cf.component.FactoryAdministration;
import fr.unice.polytech.cf.repositories.ShopRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.StreamSupport;

@SpringBootTest
public class addShop {

    @Autowired
    FactoryAdministration factoryAdministration;

    @Autowired
    ShopRepository shopRepository;

    @Before
    public void settingUpContext(){
        shopRepository.deleteAll();
    }


    @When("the factory add a shop in {string}")
    public void theFactoryAddAShopIn(String city) {
        factoryAdministration.addShop(city);
    }

    @Then("there is {int} shop in the factory")
    public void thereIsShopInTheFactory(int arg0) {
        int nbShop = StreamSupport.stream(shopRepository.findAll().spliterator(), false)
                .toList().size();
        Assertions.assertEquals(arg0, nbShop);
    }

    @When("the factory don't add a shop")
    public void theFactoryDonTAddAShop() {

    }
}
