package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.model.client.SurpriseBasket;
import fr.unice.polytech.cf.repositories.TooGoodToGoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TooGoodToGoAPITest {

    @Autowired
    TooGoodToGoRepository tooGoodToGoRepository;

    @Autowired
    TooGoodToGoAPI tooGoodToGoAPI;


    @BeforeEach
    void setUp()  {
        tooGoodToGoRepository.deleteAll();
    }

    @Test
    public void addSurpriseBasket() throws Exception {
        SurpriseBasket surpriseBasket = new SurpriseBasket();
        surpriseBasket.setPrice(10);
        tooGoodToGoAPI.addSurpriseBasket(surpriseBasket);
        assertSame(tooGoodToGoRepository.existsById(surpriseBasket.getId()), true);


    }

    @Test
    public void paySurpriseBasket() throws Exception {
        SurpriseBasket surpriseBasket = new SurpriseBasket();
        surpriseBasket.setPrice(10);
        tooGoodToGoAPI.addSurpriseBasket(surpriseBasket);
        assertSame(tooGoodToGoRepository.existsById(surpriseBasket.getId()), true);
        surpriseBasket =  tooGoodToGoRepository.findById(surpriseBasket.getId()).get();
        assertTrue(tooGoodToGoAPI.paySurpriseBasket(surpriseBasket.getTooGoodToGoId())> 0);
        assertSame(tooGoodToGoRepository.existsById(surpriseBasket.getId()), false);
    }

    @Test
    public void paySurpriseBasketError() throws Exception {
    assertSame((int)tooGoodToGoAPI.paySurpriseBasket(UUID.randomUUID()), 0);
    }
}
