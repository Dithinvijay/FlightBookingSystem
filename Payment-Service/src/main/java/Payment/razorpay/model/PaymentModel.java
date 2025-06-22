package Payment.razorpay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class PaymentModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long myOrderId;

	private String orderId;

	private String amount;

	private String receipt;

	private String status;

	private Integer passengerBookingId;

	private String paymentId;

	public PaymentModel() {
		super();
	}

	public PaymentModel(String orderId, String amount, String receipt, String status, Integer passengerBookingId,
			String paymentId) {
		super();
		this.orderId = orderId;
		this.amount = amount;
		this.receipt = receipt;
		this.status = status;
		this.passengerBookingId = passengerBookingId;
		this.paymentId = paymentId;
	}

	public Long getMyOrderId() {
		return myOrderId;
	}

	public void setMyOrderId(Long myOrderId) {
		this.myOrderId = myOrderId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getPassengerBookingId() {
		return passengerBookingId;
	}

	public void setPassengerBookingId(Integer passengerBookingId) {
		this.passengerBookingId = passengerBookingId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

}
