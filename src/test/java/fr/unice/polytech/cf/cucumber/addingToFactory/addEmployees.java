package fr.unice.polytech.cf.cucumber.addingToFactory;

import fr.unice.polytech.cf.interfaces.FactoryHumanRessources;
import fr.unice.polytech.cf.model.factory.Chef;
import fr.unice.polytech.cf.model.factory.FactoryManager;
import fr.unice.polytech.cf.repositories.ChefRepository;
import fr.unice.polytech.cf.repositories.FactoryManagerRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.StreamSupport;


@SpringBootTest
public class addEmployees {

    @Autowired
    FactoryHumanRessources factoryHumanRessources;

    @Autowired
    FactoryManagerRepository factoryManagerRepository;

    @Autowired
    ChefRepository chefRepository;


    @Before
    public void settingUpContext(){
        factoryManagerRepository.deleteAll();
        chefRepository.deleteAll();
    }


    @When("the factory add a factory manager named {string}")
    public void theFactoryAddAFactoryManager(String name) {
        factoryHumanRessources.hireFactoryManager(name);
    }

    @Then("there is {int} factory manager working for the factory")
    public void thereIsFactoryManagerWorkingForTheFactory(int arg0) {
        int nbFactoryManager = StreamSupport.stream(factoryManagerRepository.findAll().spliterator(), false)
                .toList().size();
        Assertions.assertEquals(arg0, nbFactoryManager);
    }


    @When("the factory add a chef named {string}")
    public void theFactoryAddAChef(String name) {
        factoryHumanRessources.hireChef(name);
    }

    @Then("there is {int} chef working for the factory")
    public void thereIsChefWorkingForTheFactory(int arg0) {
        int nbChef = StreamSupport.stream(chefRepository.findAll().spliterator(), false)
                .toList().size();
        Assertions.assertEquals(arg0, nbChef);
    }


    @When("the factory fire the factory manager named {string}")
    public void theFactoryDeleteAFactoryManager(String name) {
        try{
            FactoryManager factoryManager = StreamSupport.stream(factoryManagerRepository.findAll().spliterator(), false)
                    .filter(factoryManager1 -> factoryManager1.getName().equals(name)).findFirst().get();

            factoryHumanRessources.fireFactoryManager(factoryManager);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



    @When("the factory fire the chef named {string}")
    public void theFactoryDeleteAChef(String name) {
        try{
            Chef chef = StreamSupport.stream(chefRepository.findAll().spliterator(), false)
                    .filter(chef1 -> chef1.getName().equals(name)).findFirst().get();

            factoryHumanRessources.fireChef(chef);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
