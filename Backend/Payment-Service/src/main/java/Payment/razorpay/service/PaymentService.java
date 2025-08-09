package Payment.razorpay.service;



import com.razorpay.*;

import java.util.UUID;

import org.json.*;
import org.springframework.stereotype.Service;


@Service
public class PaymentService {


    private static final String KEY_ID = "rzp_test_Krj1zUQFRHYumw";
    private static final String KEY_SECRET = "SbbmaXsyWpWEq0nBgH819V9A";

    public static Order createOrder(Integer amount) throws RazorpayException, JSONException {
        RazorpayClient razorpay = new RazorpayClient(KEY_ID, KEY_SECRET);
        	
        String orderId="Order_"+(UUID.randomUUID().toString().replace("-", ""));
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", orderId);
        
        Order order = razorpay.orders.create(orderRequest);

        return order;
    }
}
