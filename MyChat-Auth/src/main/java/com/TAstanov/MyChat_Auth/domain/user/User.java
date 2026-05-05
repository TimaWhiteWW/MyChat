package com.TAstanov.MyChat_Auth.domain.user;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name="user_auth")
@Entity
public class User {

    @Id
    private String email;
    private String name;
    private String tag;
    private String password;

    @Override
    public String toString(){
        return this.email +" " + " " + this.name + " " + this.tag +" "+ this.password;
    }

}
