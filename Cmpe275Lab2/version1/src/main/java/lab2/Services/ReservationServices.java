package lab2.Services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import lab2.DAO.FlightDao;
import lab2.DAO.PassengerDao;
import lab2.DAO.ReservationDao;
import lab2.Entities.Flight;
import lab2.Entities.Passenger;
import lab2.Entities.Reservation;


@Service
@Configurable
public class ReservationServices {
	@Autowired
	private ReservationDao reservationDao;
	@Autowired
	private PassengerDao passengerDao;
	@Autowired
	private FlightDao flightDao;

	private customException cex;

	/**
	 * @param ordernumber
	 * @return
	 * @throws customException
	 */
	@Transactional
	public Reservation getReservationByNumber(Long ordernumber) throws customException {
		Reservation reservation = reservationDao.findByorderNumber(ordernumber);
		if (reservation == null) {
			cex = new customException();
			cex.setCode(404);
			cex.setMessage("No reservation exists with the reservation number" + ordernumber);
			throw cex;
		}
		return reservation;
	}

	/**
	 * @param passengerId
	 * @param from
	 * @param to
	 * @param flightNumber
	 * @return
	 */
	@Transactional
	public Set<Reservation> searchReservation(int passengerId, String from, String to, String flightNumber) {
		Set<Reservation> abc = reservationDao.search(passengerId, from, to, flightNumber);
		return abc;
	}

	/**
	 * @param passengerId
	 * @param flightLists
	 * @return
	 * @throws customException
	 */
	@Transactional
	public Reservation createReservation(int passengerId, List<String> flightLists) throws customException {
		Reservation reservation = new Reservation();
		// Check if passenger exists
		Passenger passenger = passengerDao.findById(passengerId);

		if (passenger == null) {
			cex = new customException();
			cex.setCode(404);
			cex.setMessage("No passenger exists with id: " + passengerId);
			throw cex;
		}

		reservation.setPassenger(passenger);
		List<Flight> flights = new ArrayList<Flight>();
		int price = 0;

		// Check overlap between the flights for this reservation
		for (int i = 0; i < flightLists.size(); i++) {
			Flight flight1 = flightDao.findByflightNumber(flightLists.get(i));
			for (int j = i + 1; j < flightLists.size(); j++) {
				Flight flight2 = flightDao.findByflightNumber(flightLists.get(j));
				if (checkOverlap(flight1, flight2)) {
					// OverLap exists return error
					cex = new customException();
					cex.setCode(404);
					cex.setMessage("Flights " + flightLists.get(i) + " " + flightLists.get(j) + " overlap in timings");
					throw cex;
				}
			}
		}

		// Check for overlap with existing reservations of the passenger
		List<Reservation> existingReservations = passenger.getReservations();
		for (String flightnumber : flightLists) {
			Flight flight = flightDao.findByflightNumber(flightnumber);
			if (checkOverlapWithExistingReservation(existingReservations, flight)) {
				// OverLap exists return error
				cex = new customException();
				cex.setCode(404);
				cex.setMessage("Flight " + flightnumber + " overlaps with existing reservations");
				throw cex;
			}

			price = price + flight.getPrice();
			if (flight.getSeatsLeft() >= 1) {
				// Decrement the seats left by 1
				flight.setSeatsLeft(flight.getSeatsLeft() - 1);
				// Update the flight entry in database and add the saved flight
				// to list of flights
				flights.add(flightDao.save(flight));
			} else {
				cex = new customException();
				cex.setCode(404);
				cex.setMessage("No seats left on the flight " + flightnumber);
				throw cex;
			}
		}

		reservation.setFlights(flights);
		reservation.setPrice(price);

		reservationDao.save(reservation);
		return reservation;

	}

	/**
	 * @param orderNumber
	 * @param flightsAddedList
	 * @param flightsRemovedList
	 * @return
	 * @throws customException
	 */
	@Transactional
	public Reservation updateReservation(long orderNumber, List<String> flightsAddedList,
			List<String> flightsRemovedList) throws customException {

		Reservation storedReservation = reservationDao.findByorderNumber(orderNumber);
		if (storedReservation == null) {
			cex = new customException();
			cex.setCode(404);
			cex.setMessage("No reservation exists with the reservation number" + orderNumber);
			throw cex;
		}
		List<Flight> currentFlights = storedReservation.getFlights();
		int price = 0;

		List<Flight> removedFlights = new ArrayList<Flight>();
		List<Flight> addedFlights = new ArrayList<Flight>();

		if (flightsRemovedList != null) {
			for (String flightnumber : flightsRemovedList) {
				Flight flight = flightDao.findByflightNumber(flightnumber);
				if (flight != null) {
					removedFlights.add(flight);
				}
			}
		}

		if (flightsAddedList != null) {
			for (String flightnumber : flightsAddedList) {
				Flight flight = flightDao.findByflightNumber(flightnumber);
				if (flight != null) {
					if (flight.getSeatsLeft() >= 1) {
						if (!currentFlights.contains(flight)) {
							addedFlights.add(flight);
						}
					} else {
						cex = new customException();
						cex.setCode(404);
						cex.setMessage("Flight " + flightnumber + " is full");
						throw cex;
					}
				} else {
					cex = new customException();
					cex.setCode(404);
					cex.setMessage("Flight " + flightnumber + " does not exist");
					throw cex;
				}
			}
		}

		if (flightsRemovedList != null) {
			// Remove flights from the list
			for (Flight flight : removedFlights) {
				if (currentFlights.remove(flight)) {
					// Increment the seats left by 1 also check it does not go
					// beyond the plane capacity
					flight.setSeatsLeft(Math.min(flight.getSeatsLeft() + 1, flight.getPlane().getCapacity()));
					// Update the flight entry in database and removed the
					// flight from the list of current flights in the
					// reservation
					flightDao.save(flight);
				}
			}
		}

		if (flightsAddedList != null) {
			// check for time overlap within the newly added flights
			for (int i = 0; i < addedFlights.size(); i++) {
				for (int j = i + 1; j < addedFlights.size(); j++) {
					if (checkOverlap(addedFlights.get(i), addedFlights.get(j))) {
						cex = new customException();
						cex.setCode(404);
						cex.setMessage("Flights " + addedFlights.get(i).getNumber() + " and "
								+ addedFlights.get(j).getNumber() + "overlap in timings");
						throw cex;
					}
				}
			}

			// Check for time overlap with currentFlights
			for (Flight flightToAdd : addedFlights) {
				for (Flight existingFlight : currentFlights) {
					if (checkOverlap(flightToAdd, existingFlight)) {
						cex = new customException();
						cex.setCode(404);
						cex.setMessage("Flight " + flightToAdd.getNumber()
								+ " overlaps with an existing flight in the reservation");
						throw cex;
					}
				}
			}

			// Update seats left and add the new flights to reservation object
			for (Flight flightToAdd : addedFlights) {
				flightToAdd.setSeatsLeft(flightToAdd.getSeatsLeft() - 1);
				currentFlights.add(flightDao.save(flightToAdd));
			}

		}

		// Calculate new price.
		for (Flight flight : currentFlights) {
			price += flight.getPrice();
		}

		storedReservation.setFlights(currentFlights);
		storedReservation.setPrice(price);
		// Return the updated reservation
		return reservationDao.save(storedReservation);

	}

	/**
	 * @param ordernumber
	 * @throws customException
	 */
	/**
	 * @param ordernumber
	 * @throws customException
	 */
	@Transactional
	public void deleteReservation(Long ordernumber) throws customException {
		Reservation reservation = reservationDao.findByorderNumber(ordernumber);
		if (reservation == null) {
			cex = new customException();
			cex.setCode(404);
			cex.setMessage("Reservation with order number " + ordernumber + " does not exist.");
			throw cex;
		}
		List<Flight> flights = reservation.getFlights();
		// Increment the seats left count by 1
		for (Flight flight : flights) {
			flight.setSeatsLeft(flight.getSeatsLeft() + 1);
			flightDao.save(flight);
		}
		reservationDao.delete(reservation);
	}

	/**
	 * @param flightA
	 * @param flightB
	 * @return
	 */
	private boolean checkOverlap(Flight flightA, Flight flightB) {

		Date startDate1 = flightA.getDepartureTime();
		Date endDate1 = flightA.getArrivalTime();
		Date startDate2 = flightB.getDepartureTime();
		Date endDate2 = flightB.getArrivalTime();

		if (startDate1.before(endDate2) && endDate1.after(startDate2)) {
			return true;
		}
		return false;
	}

	/**
	 * @param reservations
	 *            List of reservations to check for overlap with the
	 *            currentFlight
	 * @param currentFlight
	 *            A flight object to check for overlap with a list of
	 *            reservations
	 * @return
	 */
	public boolean checkOverlapWithExistingReservation(List<Reservation> reservations, Flight currentFlight) {

		for (Reservation reservation : reservations) {
			for (Flight reservedFlight : reservation.getFlights()) {
				if (this.checkOverlap(currentFlight, reservedFlight)) {
					return true;
				}
			}
		}
		return false;
	}

}
