package lab2.Services;

import java.util.ArrayList;
import java.util.List;

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
public class PassengerServices {
	@Autowired
	private PassengerDao passengerDao;
	@Autowired
	private ReservationDao reservationDao;
	@Autowired
	private FlightDao flightDao;

	private customException cex;

	/*
	 * Fetch passenger using passenger Id
	 */
	/**
	 * @param id
	 * @return
	 * @throws customException
	 */
	@Transactional
	public Passenger getPassengerByNumber(int id) throws customException {
		Passenger currentPassenger = passengerDao.findById(id);
		if (currentPassenger == null) {
			cex = new customException();
			cex.setCode(404);
			cex.setMessage("Sorry, the requested passenger with id " + id + " does not exist");
			throw cex;
		}
		return currentPassenger;
	}

	/*
	 * Create new passenger based on the passenger object
	 */
	/**
	 * @param p
	 * @return
	 * @throws customException
	 */
	@Transactional
	public Passenger createPassenger(Passenger p) throws customException {
		if (passengerDao.findByPhone(p.getPhone()) == null) {
			passengerDao.save(p);
		} else {
			cex = new customException();
			cex.setCode(404);
			cex.setMessage("Another passenger with the same number already exists.");
			throw cex;

		}
		return getPassengerByNumber(p.getId());
	}

	/*
	 * Update existing passenger and check for phone number repetition if phone
	 * number repeats the exception raises
	 */

	/**
	 * @param p
	 * @return
	 * @throws customException
	 */
	@Transactional
	public Passenger updatePassenger(Passenger p) throws customException {
		Passenger currentPassenger = passengerDao.findByPhone(p.getPhone());

		if (currentPassenger == null || currentPassenger.getId() == p.getId()) {
			passengerDao.save(p);
		} else {
			cex = new customException();
			cex.setCode(404);
			cex.setMessage("Another passenger with the same number already exists.");
			throw cex;

		}
		return getPassengerByNumber(p.getId());
	}

	/*
	 * Delete passenger and his reservations correspondingly
	 */
	/**
	 * @param id
	 * @throws customException
	 */
	@Transactional
	public void deletePassenger(int id) throws customException {
		Passenger p = passengerDao.findById(id);
		if (p != null) {
			// delete the reservation made by him
			// update number of seats in the flight
			List<Reservation> reservations = p.getReservations();
			for (Reservation _reservations : reservations) {
				List<Flight> currentFlights = _reservations.getFlights();
				List<String> removeFlights = new ArrayList<>();
				for (Flight flightnumber : currentFlights) {
					removeFlights.add(flightnumber.getNumber());
				}
				_reservations.getFlights().clear();
				_reservations.setPassenger(null);
				reservationDao.delete(_reservations.getOrderNumber());
				for (String flightnumber : removeFlights) {
					if (flightnumber != null) {
						Flight flight = flightDao.findByflightNumber(flightnumber);
						if (flight != null) {
							// Increment the seats left by 1 also check it does
							// not
							// go beyond the plane capacity
							flight.setSeatsLeft(Math.min(flight.getSeatsLeft() + 1, flight.getPlane().getCapacity()));
							flightDao.save(flight);
						}
					}
				}
			}
			reservations.clear();
			passengerDao.delete(p);

		} else {

			cex = new customException();
			cex.setCode(404);
			cex.setMessage("Passenger with id " + id + " does not exist");
			throw cex;

		}
	}
}
