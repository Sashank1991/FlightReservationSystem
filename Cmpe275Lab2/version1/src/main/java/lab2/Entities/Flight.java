package lab2.Entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Entity
@Table(name = "Flights")
public class Flight {
	@Id
	@Column(name = "flightNumber")
	private String flightNumber; // Each flight has a unique flight number.
	@JsonSerialize(using = ToStringSerializer.class)
	private int price;
	@Column(name = "fromCity")
	private String from;
	@Column(name = "toCity")
	private String to;
	/*
	 * Date format: yy-mm-dd-hh, do not include minutes and sceonds. Example:
	 * 2017-03-22-19 The system only needs to supports PST. You can ignore other
	 * time zones.
	 */
	@JsonFormat(pattern = "yyyy-MM-dd-HH")
	private Date departureTime;
	@JsonFormat(pattern = "yyyy-MM-dd-HH")
	private Date arrivalTime;
	@JsonSerialize(using = ToStringSerializer.class)
	private int seatsLeft;
	private String description;
	@Embedded
	private Plane plane; // Embedded

	@ManyToMany(cascade = CascadeType.ALL, mappedBy = "flights")
	private List<Reservation> reservations = new ArrayList<Reservation>();

	@Transient
	@JsonSerialize
	private List<Passenger> passengers = new ArrayList<Passenger>();

	@JsonIgnore()
	public List<Reservation> getReservations() {
		return this.reservations;
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}

	@JsonIgnoreProperties({ "reservations" })
	public List<Passenger> getPassengers() {
		passengers.clear();
		for (Reservation pass : this.reservations) {
			Passenger _pass = new Passenger();
			_pass.setAge(pass.getPassenger().getAge());
			_pass.setId(pass.getPassenger().getId());
			_pass.setFirstname(pass.getPassenger().getFirstname());
			_pass.setGender(pass.getPassenger().getGender());
			_pass.setLastname(pass.getPassenger().getLastname());
			_pass.setPhone(pass.getPassenger().getPhone());
			passengers.add(_pass);
		}
		return this.passengers;
	}

	public String getNumber() {
		return flightNumber;
	}

	public void setNumber(String number) {
		this.flightNumber = number;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public Date getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getSeatsLeft() {
		return seatsLeft;
	}

	public void setSeatsLeft(int seatsLeft) {
		this.seatsLeft = seatsLeft;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Plane getPlane() {
		return plane;
	}

	public void setPlane(Plane plane) {
		this.plane = plane;
	}

}
