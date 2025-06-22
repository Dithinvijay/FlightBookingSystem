package Payment.razorpay.controller;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.razorpay.*;

import Payment.razorpay.Repo.MyOrderRepo;
import Payment.razorpay.model.PaymentModel;

@RestController
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private MyOrderRepo myOrderRepo;
    
    @GetMapping("/fetch/{passengerBookingId}")
    public ResponseEntity<String> fetchPayment(@PathVariable Integer passengerBookingId){
    	PaymentModel pay = myOrderRepo.findByPassengerBookingId(passengerBookingId);
    	if(pay.getPaymentId() == null) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed");
    	}
    	return ResponseEntity.ok("Completed");
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Integer> requestBody) throws Exception {
        logger.info("Received request to create order with body: {}", requestBody);
        int amount = requestBody.get("amount");
         int passengerBookingId = requestBody.get("bookingId");

        logger.info("Extracted amount: {}", amount);
        System.out.println("Hey order function executed");
        var client =new RazorpayClient("rzp_test_Krj1zUQFRHYumw", "SbbmaXsyWpWEq0nBgH819V9A");

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100);
        orderRequest.put("currency", "INR");
       orderRequest.put("receipt", passengerBookingId+"");
       // orderRequest.put("receipt", "ABCDE"); // orderRequest.put("passengerBookingId",passengerBookingId);

        logger.info("Creating Razorpay order with request: {}", orderRequest.toString(2));
        Order order = client.orders.create(orderRequest);
        logger.info("Razorpay order created successfully: {}", order);

        Map<String, Object> response = new HashMap<>();
        response.put("order_id", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("currency", order.get("currency"));
        response.put("passengerBookingId", passengerBookingId);


        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setAmount(order.get("amount")+" ");
        paymentModel.setOrderId(order.get("id"));
        paymentModel.setPaymentId(null);
        paymentModel.setStatus("created");
        paymentModel.setPassengerBookingId(passengerBookingId);
        paymentModel.setReceipt(order.get("receipt"));

        logger.info("Saving payment model to database: {}", paymentModel);
        PaymentModel pay = myOrderRepo.save(paymentModel);
        if(pay != null) {
            logger.info("Payment model saved successfully with ID: {}", pay.getOrderId());
            System.out.println("Saved successfully");
        }else {
            logger.error("Failed to save payment model.");
            System.out.println("Not saved");
        }


        logger.info("Returning create order response: {}", response);
        return ResponseEntity.ok(response);


    }


    @PostMapping("/capture-payment")
    public ResponseEntity<String> capturePayment(@RequestBody Map<String, Object> requestBody) {
        logger.info("Received request to capture payment with body: {}", requestBody);
        try {
            String paymentId = requestBody.get("paymentId").toString();
            int amount = Integer.parseInt(requestBody.get("amount").toString());
            String orderId = requestBody.get("orderId").toString();

            logger.info("Attempting to capture payment with paymentId: {}, amount: {}, orderId: {}", paymentId, amount, orderId);
            RazorpayClient razorpay = new RazorpayClient("rzp_test_Krj1zUQFRHYumw", "SbbmaXsyWpWEq0nBgH819V9A");

            // Fetch payment details from Razorpay
            logger.info("Fetching payment details from Razorpay for paymentId: {}", paymentId);
            Payment payment = razorpay.payments.fetch(paymentId);
            logger.info("Payment details fetched: {}", payment);
            String paymentStatus = payment.get("status"); 


            if ("captured".equals(paymentStatus)) {
                logger.info("Payment already captured for paymentId: {}, updating database.", paymentId);
                updatePaymentInDB(orderId, paymentId, "completed");
                return ResponseEntity.ok("Payment already captured. Updated database.");
            }

            JSONObject captureRequest = new JSONObject();
            captureRequest.put("amount", amount * 100);
            captureRequest.put("currency", "INR");

            logger.info("Initiating manual capture for paymentId: {} with request: {}", paymentId, captureRequest.toString(2));
            Payment capturedPayment = razorpay.payments.capture(paymentId, captureRequest);
            logger.info("Payment captured successfully: {}", capturedPayment);

            updatePaymentInDB(orderId, paymentId, "completed");

            return ResponseEntity.ok("Payment Captured Successfully! Payment ID: " + capturedPayment.get("id"));
        } catch (Exception e) {
            logger.error("Error capturing payment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error capturing payment: " + e.getMessage());
        }
    }

   @PutMapping("/updateOrder/{orderId}/{paymentId}/{status}")
   private void updatePaymentInDB(@PathVariable String orderId,@PathVariable String paymentId,@PathVariable String status) {
        logger.info("Updating payment details in database for orderId: {}, paymentId: {}, status: {}", orderId, paymentId, status);
        PaymentModel existingPayment = myOrderRepo.findByOrderId(orderId);

        if (existingPayment != null) {
            logger.info("Found existing payment record: {}", existingPayment);
            existingPayment.setPaymentId(paymentId);
            existingPayment.setStatus(status);
            myOrderRepo.save(existingPayment);
            logger.info("Payment details updated successfully: {}", existingPayment);
        } else {
            logger.warn("No existing payment record found for orderId: {}", orderId);
        }
    }
}