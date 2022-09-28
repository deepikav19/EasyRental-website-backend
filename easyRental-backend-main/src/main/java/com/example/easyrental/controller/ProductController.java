package com.example.easyrental.controller;

import com.example.easyrental.dao.ProductRepository;
import com.example.easyrental.dao.UserRepository;
import com.example.easyrental.model.Product;
import com.example.easyrental.model.User;
import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class ProductController{
    final ProductRepository productRepository;
    final UserRepository userRepository;
    final Gson gson;

    public ProductController(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        gson = new Gson();
    }

    @RequestMapping(
            value = "/browseProducts",
            method = RequestMethod.GET)
    public String returnProductRepository() {
        try {
            List<Product> allProducts = productRepository.findAll();
            //logger statement
            return gson.toJson(allProducts);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "Failed";
        }

    }

    @RequestMapping(
            value = "/registerProduct",
            method = RequestMethod.POST)
    public String registerProduct(@RequestBody Map<String, Object> payLoad) {
        try {
            String title = (String) payLoad.get(Product.FIELD_TITLE);
            System.out.println(title);

            String description = (String) payLoad.get(Product.FIELD_DESCRIPTION);
            System.out.println(description);

            String price = (String) payLoad.get(Product.FIELD_PRICE);
            System.out.println(price);

            List<String> images = Stream.of(payLoad.get(Product.FIELD_IMAGES))
                    .map(object -> Objects.toString(object, null))
                    .collect(Collectors.toList());
            List<String> tags = Stream.of(payLoad.get(Product.FIELD_TAGS))
                    .map(object -> Objects.toString(object, null))
                    .collect(Collectors.toList());
            List<String>  img=new ArrayList<String>();
            img.add(images.get(0).substring(1,images.get(0).length()-1));
            System.out.println(img.get(0));
            for(String tag: images){
                System.out.println(tag);
            }

            String productMetaData = (String) payLoad.get(Product.FIELD_PRODUCT_METADATA);
            boolean availability = true;
            String email = (String) payLoad.get(User.FIELD_EMAIL);
            User currentUser = userRepository.findByEmail(email);
            Long userId = currentUser.getId();

            if (title == null) {
                System.out.println("Product with title doesn't exist");
                return "IncorrectDetails";
            }
            Product product = new Product(title, description, userId, price, tags, img, productMetaData, availability);
            productRepository.save(product);
            return "Successful";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Failed";
        }
    }



    @RequestMapping(
            value = "/updateProduct",
            method = RequestMethod.PATCH)
    public String updateProduct(@RequestBody Map<String, Object> payLoad) {

        //Get new details
        String title = (String) payLoad.get("name");
        String description = (String) payLoad.get(Product.FIELD_DESCRIPTION);
        String price = (String) payLoad.get(Product.FIELD_PRICE);
        List<String> tags = Stream.of(payLoad.get(Product.FIELD_TAGS))
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());
        List<String> images = Stream.of(payLoad.get(Product.FIELD_IMAGES))
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());
        String productMetaData = (String) payLoad.get(Product.FIELD_PRODUCT_METADATA);
        boolean availability = true;
        String email = (String) payLoad.get(User.FIELD_EMAIL);
        String prodID= String.valueOf(payLoad.get(User.FIELD_ID));

        User currentUser = userRepository.findByEmail(email);
        Long userId = currentUser.getId();

        //GET IMAGES AS STRINGS
        List<String>  img=new ArrayList<String>();
        img.add(images.get(0).substring(1,images.get(0).length()-1));
        System.out.println(img.get(0));
        for(String tag: images){
            System.out.println(tag);
        }

        //Check if it is there
        if (title == null) {
            System.out.println("Product with title doesn't exist");
            return "IncorrectDetails";
        }

        System.out.println(title);
        System.out.println(description);
        System.out.println(prodID);

        Product product = new Product(title, description, userId, price, tags, img, productMetaData, availability);
        product.setId(Long.valueOf(prodID));
        productRepository.save(product);
        return "Updated successfully";
    }


    @RequestMapping(
            value = "/getUsersProducts",
            method = RequestMethod.GET)
    public String returnUsersProducts(@RequestParam String email) {
        try {
            User u = userRepository.findByEmail(email);
            List<Product> allProducts = productRepository.findAll();
            List<Product> usersOwn = new ArrayList<>();
            for(Product p: allProducts){
                if(u.getId() == p.getUserId()){
                    usersOwn.add(p);
                }
            }
            return gson.toJson(usersOwn);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "Failed";
        }

    }

    @RequestMapping(
            value = "/makeProductUnavailable",
            method = RequestMethod.GET)
    public String makeUnavailable(@RequestParam String email, @RequestParam Long id, @RequestParam int op) {
        //op == 1 -> make avaiable
        //op == 0 -> make unavailable
        try {
            User u = userRepository.findByEmail(email);
            Product p = productRepository.getById(id);
            if(p == null){
                return "product not found";
            }
            if(p.getUserId() == u.getId()){
                int myInt = p.isAvailability() ? 1 : 0;
                if(myInt == op){
                    return "Can't perform same operation1";
                }
                productRepository.updateIsAvailability(!p.isAvailability(), id);
            } else{
                return "Not your own product to make unavailable";
            }
            return "Successful";
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "Failed";
        }

    }

    @RequestMapping(
            value = "/getProductDetails",
            method = RequestMethod.GET)
    public String getProductDetails(@RequestParam Long prodId,@RequestParam Long userId ) {
        Product p = productRepository.findByIdAndUserId(prodId,userId);
        if (p == null) {
            System.out.println("Product does not exists");
            return "Product Not Found";
        }
        return gson.toJson(p,Product.class);
    }

}