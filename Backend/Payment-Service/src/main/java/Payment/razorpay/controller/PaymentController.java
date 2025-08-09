package Payment.razorpay.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Payment.razorpay.model.Payment;
import Payment.razorpay.repository.PaymentRepository;
import Payment.razorpay.service.PaymentService;

@CrossOrigin(origins = "http://localhost:2025")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

   
    @PostMapping("/orders")
    public ResponseEntity<Payment> createOrder(@RequestBody OrderRequest orderRequest) 
            throws RazorpayException, JSONException {
        
        Order razorpayOrder = PaymentService.createOrder(orderRequest.getAmount());
        
        Payment paymentOrder = new Payment();
        paymentOrder.setRazorpayOrderId(razorpayOrder.get("id").toString());
        paymentOrder.setAmount((Integer) razorpayOrder.get("amount"));
        paymentOrder.setCurrency(razorpayOrder.get("currency").toString());
        paymentOrder.setReceipt(razorpayOrder.get("receipt").toString());
        paymentOrder.setStatus(razorpayOrder.get("status").toString());
        
        Payment saved = paymentRepository.save(paymentOrder);
        return ResponseEntity.ok(saved);
    }

    public static class OrderRequest {
        private Integer amount;

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }
    }
}
