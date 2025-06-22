package Payment.razorpay.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import Payment.razorpay.model.PaymentModel;

public interface MyOrderRepo extends JpaRepository<PaymentModel, Long>{
	
 PaymentModel findByMyOrderId(Long id); 
 
 PaymentModel findByOrderId(String orderId);
 
 PaymentModel findByPassengerBookingId(Integer passengerBookingId);

}
