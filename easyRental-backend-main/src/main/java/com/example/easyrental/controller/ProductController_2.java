/* package com.example.easyrental.controller;

import com.example.easyrental.dao.ProductRepository;
import com.example.easyrental.dao.UserRepository;
import com.example.easyrental.model.Product;
import com.example.easyrental.model.User;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class ProductController_2 {
    final ProductRepository productRepository;
    final UserRepository userRepository;
    final Gson gson;


    public ProductController_2(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        gson = new Gson();
    }

    @RequestMapping(
            value = "/registerProduct",
            method = RequestMethod.POST)
    public String registerProduct(@RequestBody Map<String, Object> payLoad) {
        try{
            //Incoming payload map has product field that has product details

            for(String key: payLoad.keySet()){
                System.out.println(key);
                System.out.println(payLoad.get(key).getClass());
            }
            String title = (String) payLoad.get(Product.FIELD_TITLE);
            String description = (String) payLoad.get(Product.FIELD_DESCRIPTION);
            String price = (String) payLoad.get(Product.FIELD_PRICE);

            //
            List<String> tags = Stream.of(payLoad.get(Product.FIELD_TAGS))
                    .map(object -> Objects.toString(object, null))
                    .collect(Collectors.toList());

//            ArrayList<MultipartFile> images = (ArrayList<MultipartFile>) payLoad.get("image");
//            //payLoad.get(Product.FIELD_IMAGES);
////            List<Object> ip = Stream.of(payLoad.get("image")).toList();
////            for(int i=0;i<.size();i++){
////                System.out.println();
////
////            }
//            System.out.println(images.getClass());
//            for(MultipartFile img:images)
//            {
//                System.out.println(img.getName());
//
//            }

            String productMetaData = (String) payLoad.get(Product.FIELD_PRODUCT_METADATA);
            boolean availability = true;

            //User who wants to register their product on the site
            String email = (String) payLoad.get(User.FIELD_EMAIL);
            User currentUser = userRepository.findByEmail(email);
            Long userId = currentUser.getId();
            //If product does not exist
            if (title == null) {
                System.out.println("Product with title doesn't exist");
                return "IncorrectDetails";
            }
            //Combine user and product info and save
           Product product = new Product(title, description, userId, price, tags, null, productMetaData, availability);
           productRepository.save(product);
            return String.valueOf(product.getId());
        }catch(Exception ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }

    }


    @RequestMapping(
            value = "/browseProducts",
            method = RequestMethod.GET)
    public String returnProductRepository() {
        try{
            List<Product> allProducts = productRepository.findAll();
            //logger statement
            return gson.toJson(allProducts);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
            return "Failed";
        }

    }


   @PostMapping(path="/imageupload/")
    public String uploadFiles(@RequestBody MultipartFile[] files,@RequestParam("id") Long id) {
        try {


            List<String> fileNames = new ArrayList<>();
            Product product = productRepository.getById(id);

            // read and write the file to the local folder
            List<String> imgs = new ArrayList<>();
            Arrays.asList(files).stream().forEach(file -> {
                byte[] bytes = new byte[0];
                try {
                    bytes = file.getBytes();
                    imgs.add(String.valueOf(bytes));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            product.setImages(imgs);

            return "Done done done!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Didn't work";
        }
    }




}
 */
