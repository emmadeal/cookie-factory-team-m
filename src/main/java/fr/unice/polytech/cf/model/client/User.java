package fr.unice.polytech.cf.model.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class User extends Client {
    private String userName;
    private String mail;

    private String password;

    private String phone;

    private boolean membership = false;

    private List<Notification> notifications = new ArrayList<>();

    public User(String name, String mail, String password, String phone) {
        super();
        this.userName = name;
        this.mail = mail;
        this.password = password;
        this.phone = phone;
    }
}
