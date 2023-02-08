package fr.unice.polytech.cf.cucumber.authentication;

import fr.unice.polytech.cf.exception.AlreadyExistingCustomerException;
import fr.unice.polytech.cf.interfaces.LoginProcessor;
import fr.unice.polytech.cf.interfaces.SignInProcessor;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.repositories.UserRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class userAuth {

    @Autowired
    LoginProcessor loginProcessor;
    @Autowired
    SignInProcessor signInProcessor;
    @Autowired
    UserRepository userRepository;

    User signedUser;
    User loggedUser;

    @Before
    public void settingUpContext() {
        userRepository.deleteAll();
    }

    @Given("a user signing in with username {string}, password {string}, phone {string} and mail {string}")
    public void aUserSigningInWithUsernamePasswordPhoneAndMail(String arg0, String arg1, String arg2, String arg3) throws AlreadyExistingCustomerException {
        signedUser = signInProcessor.signIn(arg0, arg1, arg2, arg3, new Order());
    }

    @Then("the user with username {string} and password {string} exists")
    public void theUserWithUsernameAndPasswordExists(String arg0, String arg1) {
            boolean isPresent = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                    .anyMatch(user -> user.getUserName().equals(arg0) && user.getPassword().equals(arg1));
            assertTrue(isPresent);

    }

    @Given("a user login with username {string} and password {string}")
    public void aUserLoginWithUsernameAndPassword(String arg0, String arg1) throws Exception {
        loggedUser = loginProcessor.login(arg0,arg1,new Order());
    }

    @Then("the user corresponds to a signed user")
    public void theUserCorrespondsToASignedUser() {
        loggedUser = userRepository.findById(loggedUser.getId()).get();
        assertEquals(signedUser.getMail(), loggedUser.getMail());
    }
}
