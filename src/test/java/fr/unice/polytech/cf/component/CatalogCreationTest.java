package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.interfaces.CatalogController;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.repositories.CatalogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CatalogCreationTest{

    @Autowired
    private  CatalogRepository catalogRepository;

    @Autowired
    private CatalogController catalogController;


    @BeforeEach
    void setUp()  {
        catalogRepository.deleteAll();
    }

    @Test
    public void registerCatalog() {
        catalogController.registerCatalog();
        List<Ingredient> ingredientList = StreamSupport.stream(catalogRepository.findAll().spliterator(), false).toList();
        assertTrue(ingredientList.size()>0);
    }

}
