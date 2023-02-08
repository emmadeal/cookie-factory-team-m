package fr.unice.polytech.cf.cucumber.managingShop;

import fr.unice.polytech.cf.model.factory.Factory;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.model.shop.ShopManager;
import fr.unice.polytech.cf.repositories.ShopManagerRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class shopConfig {
    Shop shop;
    ShopManager shopManager;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    ShopManagerRepository shopManagerRepository;

    @Before
    public void settingUpContext() {
        shopRepository.deleteAll();
    }

    @Given("a shop with a shop manager")
    public void aShopWithAShopManager() {
        shop = new Shop("Paris");
        shopRepository.save(shop,shop.getId());
        shopManager=new ShopManager("Momo", UUID.randomUUID());
        shopManager.setShopId(shop.getId());
        shopManagerRepository.save(shopManager,shopManager.getId());
    }

    @When("the shopManager set closing hour to {int}:{int}")
    public void theShopManagerSetClosingHourTo(int h, int m) {
        shop.setClosingHour(LocalTime.of(h,m));
    }

    @Then("the shop has closing hour set to {int}:{int}")
    public void theShopHasClosingHourSetTo(int h, int m) {
        assertEquals(shop.getClosingHour(),LocalTime.of(h,m));
    }

    @When("the shopManager set opening hour to {int}:{int}")
    public void theShopManagerSetOpeningHourTo(int h, int m) {
        shop.setOpeningHour(LocalTime.of(h,m));
    }

    @Then("the shop has opening hour set to {int}:{int}")
    public void theShopHasOpeningHourSetTo(int h, int m) {
        assertEquals(shop.getOpeningHour(),LocalTime.of(h,m));
    }

    @When("the shopManager set tax to {float}%")
    public void theShopManagerSetTaxTo(float tax) {
        shop.setTax(tax);
    }

    @Then("the shop tax is at {float}%")
    public void theShopTaxIsAt(float tax) {
        assertEquals(shop.getTax(),tax);
    }
}
