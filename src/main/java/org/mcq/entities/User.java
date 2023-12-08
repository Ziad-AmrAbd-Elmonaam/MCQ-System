package org.mcq.entities;

public class User {
    private String email; // Email address of the user


    // Constructor
    public User(String email) {
        this.email = email;
    }

    // Getters and Setters



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    // You can add more getters and setters for additional fields

    @Override
    public String toString() {
        return "User{" +
                ", email='" + email + '\'' +
                '}';
    }
}
