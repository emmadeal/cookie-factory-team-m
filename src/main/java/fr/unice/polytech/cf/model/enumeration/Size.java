package fr.unice.polytech.cf.model.enumeration;


import lombok.Getter;

@Getter
public enum Size {
    L(4), XL(5), XXL(6);

    private int quantity;

    private Size(int quantity){
        this.quantity=quantity;
    }
}
