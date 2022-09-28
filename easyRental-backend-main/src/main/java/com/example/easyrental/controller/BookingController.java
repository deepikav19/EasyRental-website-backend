package com.example.easyrental.controller;

import com.example.easyrental.dao.BookingRepository;
import com.example.easyrental.dao.ProductRepository;
import com.example.easyrental.dao.UserRepository;
import com.example.easyrental.model.Booking;
import com.example.easyrental.model.BookingStatus;
import com.example.easyrental.model.Product;
import com.example.easyrental.model.User;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class BookingController {

    final ProductRepository productRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final Gson gson;

    public BookingController(ProductRepository productRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        gson = new Gson();
    }

    @RequestMapping(
            value = "/checkout",
            method = RequestMethod.POST)
    public String checkout(@RequestBody Map<String, Object> payLoad) {
        try {
            //Gets the borrower and product ID
            System.out.println(payLoad.get(Booking.FIELD_BORROWER_USER_ID));
            System.out.println(payLoad.get(Booking.FIELD_BORROWER_USER_ID).getClass());
            Long borrowerUserId = Long.valueOf(String.valueOf(payLoad.get(Booking.FIELD_BORROWER_USER_ID)));
            Long productId = Long.valueOf(String.valueOf(payLoad.get(Booking.FIELD_PRODUCT_ID)));
            Product product = productRepository.findById(productId).orElse(null);
            //Checks for product's existence
            if (product == null)
                return "CheckoutFailed";
            //If product exists- gets other details required for borrowing so it can be booked
            Long ownerUserId = product.getUserId();
            String productName = product.getTitle();
            BookingStatus status = BookingStatus.RESERVED;
            System.out.println(payLoad.get(Booking.FIELD_START_TIME));

            long startTime = Long.parseLong(String.valueOf(payLoad.get(Booking.FIELD_START_TIME)));
            System.out.println(payLoad.get(Booking.FIELD_END_TIME));
            long endTime = Long.parseLong(String.valueOf(payLoad.get(Booking.FIELD_END_TIME)));
            long total_price = Long.parseLong((String) payLoad.get(Booking.FIELD_TOTAL_PRICE));
            Booking booking = new Booking(productName, ownerUserId, borrowerUserId, productId, status, startTime, endTime, total_price);

            //Now need to update TWO repository to update the booking of a product
            //productRepository.updateIsAvailability(Boolean.FALSE, booking.getProductId());
            bookingRepository.save(booking);
            System.out.println("Saved booking");
            return "CheckoutSuccessful";
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "Failed";
        }
    }

    @RequestMapping(
            value = "/fetchAllProducts",
            method = RequestMethod.GET)
    public String fetchAllProducts(@RequestParam String email) {
        //String email = (String) email.get(User.FIELD_EMAIL);
        User currUser = userRepository.findByEmail(email);
        List<Booking> ownerBookings = bookingRepository.findBookingByOwnerUserId(currUser.getId());
//        List<Booking> pendingBooks = ownerBookings.stream()
//                //.filter(x -> x.getStatus().equals(BookingStatus.RESERVED))
//                .sorted(BookingStatus::compareTo)
//                .collect(Collectors.toList());
        Collections.sort(ownerBookings, new Comparator<Booking>() {
            @Override
            public int compare(Booking o1, Booking o2) {
                return o1.getStatus().compareTo(o2.getStatus());
            }
        });

        return gson.toJson(ownerBookings);
//        Map<Long, String> bookings = new HashMap<>();
//        for (Booking booking : ownerBookings) {
//            bookings.put(booking.getId(), booking.getName());
//        }
//        return bookings;
    }

//    @RequestMapping(
//            value = "/fetchRequestsForme",
//            method = RequestMethod.GET)
//    public Map<Long, String> fetchRequestsToMe(@RequestBody Map<String, Object> payLoad) {
//        String email = (String) payLoad.get(User.FIELD_EMAIL);
//        User currUser = userRepository.findByEmail(email);
//        List<Booking> ownerBookings = bookingRepository.findBookingByOwnerUserId(currUser.getId());
//        List<Booking> pendingBooks = ownerBookings.stream()
//                .filter(x -> x.getStatus().equals(BookingStatus.BOOKED))
//                .collect(Collectors.toList());
//        Map<Long, String> bookings = new HashMap<>();
//        for (Booking booking : pendingBooks) {
//            bookings.put(booking.getId(), booking.getName());
//        }
//        return bookings;
//    }

    @RequestMapping(
            value = "/approveTransaction",
            method = RequestMethod.GET)
    public String approveTransaction(@RequestParam("id") Long id) {
        try {
            Booking booking = bookingRepository.getById(id);
            bookingRepository.updateBookingStatus(BookingStatus.BOOKED, booking.getId());
            return "Successful";
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "Failed";
        }
    }

    @RequestMapping(
            value = "/rejectTransaction",
            method = RequestMethod.GET)
    public String rejectTransaction(@RequestParam("id") Long id) {
        try {
            Booking booking = bookingRepository.getById(id);
            bookingRepository.deleteById(id);
            productRepository.updateIsAvailability(Boolean.TRUE, booking.getProductId());
            return "Successful";
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "Failed";
        }
    }

    @RequestMapping(
            value = "/getMyBookings",
            method = RequestMethod.GET)
    public String getMyBookings(@RequestParam("email") String email) {
        try {
            User u = userRepository.findByEmail(email);
            List<Booking> booking = bookingRepository.findAll();
            List<Booking> myBookings = new ArrayList<>();
            for(Booking b: booking){
                if(b.getBorrowerUserId() == u.getId()){
                    myBookings.add(b);
                }
            }

            return gson.toJson(myBookings);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "Failed";
        }
    }

    @RequestMapping(
            value = "/getApprovedBoooking",
            method = RequestMethod.GET)
    public String getMyBookings(@RequestParam("id") long productID) {
        try {
            List<Booking> bookings=bookingRepository.findAll();
            //List<Booking> booking = bookingRepository.findAll();
            List<Booking> myBookings = new ArrayList<>();
            for(Booking b: bookings){
                if(b.getProductId() == productID && b.getStatus().compareTo(BookingStatus.BOOKED) == 0){
                    myBookings.add(b);
                }
            }

            return gson.toJson(myBookings);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "Failed to fetch the approved bookings";
        }
    }

    @RequestMapping(
            value = "/cancelBooking",
            method = RequestMethod.GET)
    public String cancelBooking(@RequestParam("id") Long id) {
        try {
            Booking booking = bookingRepository.getById(id);
            bookingRepository.updateBookingStatus(BookingStatus.CANCELLED, booking.getId());
            return "Successfully cancelled booking";
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "Failed to cancel the booking";
        }
    }
}