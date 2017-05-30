package lab2;

import lab2.Entities.Flight;

public class ReturnFlight {
	private Flight flight;

	public ReturnFlight(Flight _flight) {
		this.flight = _flight;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

}
