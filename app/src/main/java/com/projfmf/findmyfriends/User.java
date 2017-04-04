package com.projfmf.findmyfriends;

import android.location.Location;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by traceys5 on 3/29/17.
 */

@IgnoreExtraProperties
public class User {

    String email;
    String username;
    String firstName;
    String lastName;
    boolean isSelected;
    Location location;


    public User() {}

    public User(String username, String email, boolean isSelected, String firstName, String lastName) {
            this.username = username;
            this.email = email;
            this.isSelected = isSelected;
            this.firstName = firstName;
            this.lastName = lastName;
        }

    public String getUsername() {
            return username;
        }

    public void setUsername(String username) { this.username = username; }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setLocation(Location location) { this.location = location; }

    public Location getLocation() { return location; }
}

