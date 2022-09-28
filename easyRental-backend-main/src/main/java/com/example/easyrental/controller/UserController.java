package com.example.easyrental.controller;

import com.example.easyrental.dao.UserRepository;
import com.example.easyrental.model.User;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class UserController {
    @Autowired
    UserRepository userRepository;
    final Gson gson;

    public UserController() {
        gson = new Gson();
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.POST)
    public String login(@RequestBody Map<String, Object> payLoad) {
        String email = (String) payLoad.get(User.FIELD_EMAIL);
        String password = (String) payLoad.get(User.FIELD_PASSWORD);
        User currUser = userRepository.findByEmail(email);
        if (currUser != null) {
            System.out.println(email + "," + password);
            if (currUser.getPassword().equals(password)) {
                System.out.println("Login Successful");
                return "Successful";
            }
        }
        System.out.println("Login Failed.....");
        return "Invalid";

    }

    @RequestMapping(
            value = "/registerUser",
            method = RequestMethod.POST)
    public String registerUser(@RequestBody Map<String, Object> payLoad) {
        String firstName = (String) payLoad.get(User.FIELD_FIRST_NAME);
        String lastName = (String) payLoad.get(User.FIELD_LAST_NAME);
        String email = (String) payLoad.get(User.FIELD_EMAIL);
        String password = (String) payLoad.get(User.FIELD_PASSWORD);
        String mobile = (String) payLoad.get(User.FIELD_MOBILE);
        String country = (String) payLoad.get(User.FIELD_COUNTRY);
        String addressLine1 = (String) payLoad.get(User.FIELD_ADDRESS_LINE1);
        String addressLine2 = (String) payLoad.get(User.FIELD_ADDRESS_LINE2);
        String city = (String) payLoad.get(User.FIELD_CITY);
        String state = (String) payLoad.get(User.FIELD_STATE);
        String zipCode = (String) payLoad.get(User.FIELD_ZIPCODE);
        int zip = Integer.parseInt(zipCode);
        User currUser = userRepository.findByEmail(email);
        if (currUser != null) {
            System.out.println("Same user with same email id found");
            return "User Found with same email";
        }
        User user = new User(firstName, lastName, country, addressLine1, addressLine2, city, state, zip, email, password, mobile);
        userRepository.save(user);
        System.out.println("Save complete.....");
        return "Successful";
    }

    @RequestMapping(
            value = "/updateProfile",
            method = RequestMethod.PATCH)
    public String updateProfile(@RequestBody Map<String, Object> payLoad) {
        String firstName = (String) payLoad.get(User.FIELD_FIRST_NAME);
        String lastName = (String) payLoad.get(User.FIELD_LAST_NAME);
        String email = (String) payLoad.get(User.FIELD_EMAIL);
        String password = (String) payLoad.get(User.FIELD_PASSWORD);
        String mobile = String.valueOf(payLoad.get("number"));
        String country = (String) payLoad.get(User.FIELD_COUNTRY);
        String addressLine1 = (String) payLoad.get(User.FIELD_ADDRESS_LINE1);
        String addressLine2 = (String) payLoad.get(User.FIELD_ADDRESS_LINE2);
        String city = (String) payLoad.get(User.FIELD_CITY);
        String state = (String) payLoad.get(User.FIELD_STATE);
        String zipCode = String.valueOf(payLoad.get(User.FIELD_ZIPCODE));
        int zip = Integer.parseInt(zipCode);
        User currUser = userRepository.findByEmail(email);
        if (currUser == null) {
            System.out.println("Does not exists");
            return "User Not Found";
        }
        User user = new User(firstName, lastName, country, addressLine1, addressLine2, city, state, zip, email, password, mobile);
        user.setId(currUser.getId());
        userRepository.save(user);
        System.out.println("Save complete.....");
        return "Successful";
    }

    @RequestMapping(
            value = "/userID",
            method = RequestMethod.GET)
    public String getUserID(@RequestParam String email) {

        try {
            User currUser = userRepository.findByEmail(email);
            String id= String.valueOf(currUser.getId());
            return id;
        }
        catch (Exception e){
            e.printStackTrace();
            return "Failed to fetch user ID";
        }
    }

    @RequestMapping(
            value = "/getUserDetails",
            method = RequestMethod.GET)
    public String getProfile(@RequestParam String email) {
        User currUser = userRepository.findByEmail(email);
        if (currUser == null) {
            System.out.println("User does not exists");
            return "User Not Found";
        }
        return gson.toJson(currUser);
    }

}