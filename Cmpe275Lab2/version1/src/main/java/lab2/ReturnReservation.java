package lab2;

import lab2.Entities.Reservation;

public class ReturnReservation {
	private Reservation reservation;

	public ReturnReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

}
