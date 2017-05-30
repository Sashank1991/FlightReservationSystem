package lab2.Entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Entity
@Table(name = "Reservations")
@JsonRootName("Reservation")
public class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ordernumber")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long orderNumber;

	private int price;

	@ManyToOne
	@JoinColumn(name = "passenger")
	private Passenger passenger;

	@ManyToMany
	@JoinTable(name = "reservation_flights", joinColumns = @JoinColumn(name = "orderNumber"), inverseJoinColumns = @JoinColumn(name = "flightNumber"))
	private List<Flight> flights = new ArrayList<Flight>();

	@JsonIgnoreProperties("passengers")
	public List<Flight> getFlights() {
		return flights;
	}

	public void setFlights(List<Flight> flight) {
		this.flights = flight;
	}

	@JsonIgnoreProperties("reservations")
	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

}
