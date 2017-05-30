package lab2.Services;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import lab2.DAO.FlightDao;
import lab2.Entities.Flight;
import lab2.Entities.Passenger;
import lab2.Entities.Reservation;

/**
 * @author sasha
 *
 */
@Service
@Configurable
public class FlightServices {
	@Autowired
	private FlightDao flightDao;
	private customException cex;

	/**
	 * @param number
	 * @return
	 * @throws customException
	 */
	@Transactional
	public Flight getFlightByNumber(String number) throws customException {
		Flight currentFlight = flightDao.findByflightNumber(number);
		if (currentFlight == null) {
			cex = new customException();
			cex.setCode(404);
			cex.setMessage("Sorry, the requested flight with number " + number + " does not exist");
			throw cex;
		}
		return currentFlight;
	}

	/**
	 * this creates or updates flights based on the Flight object
	 * 
	 * @param flight
	 * @return
	 * @throws customException
	 */
	@Transactional
	public Flight createFlight(Flight flight) throws customException {

		Flight currentFlight = flightDao.findByflightNumber(flight.getNumber());

		boolean error = false;

		if (currentFlight != null) {

			int capacityPrev = currentFlight.getPlane().getCapacity();

			int capacityCurrent = flight.getPlane().getCapacity();

			int numOfReservations = capacityPrev - currentFlight.getSeatsLeft();

			if (capacityCurrent >= capacityPrev || numOfReservations <= capacityCurrent) {
				flight.setSeatsLeft(flight.getSeatsLeft() + (capacityCurrent - capacityPrev));
				error = false;
			} else {
				// throw seats error 400
				cex = new customException();
				cex.setCode(400);
				cex.setMessage("Reservation count will be higher than changed capacity.");
				throw cex;

			}
			List<Reservation> reservations = currentFlight.getReservations();
			for (Reservation _reservation : reservations) {
				List<Flight> flights = _reservation.getFlights();
				for (Flight _flight : flights) {
					if (currentFlight.getNumber() != _flight.getNumber()) {
						if (checkOverlap(_flight, flight)) {
							error = true;
							cex = new customException();
							cex.setCode(400);
							cex.setMessage("overlaps other flight timing.");
							throw cex;
						}
					}
				}
			}
		}
		if (!error) {
			flightDao.save(flight);
		}

		return getFlightByNumber(flight.getNumber());

	}

	/**
	 * delete passenger based on provided flight number
	 * 
	 * @param number
	 * @throws customException
	 */
	@Transactional
	public void deleteFlight(String number) throws customException {
		Flight p = flightDao.findByflightNumber(number);

		if (p != null) {

			if (p.getReservations().size() > 0) {
				int a = p.getReservations().size();
				// reservations exists
				// You can not delete a flight that has one or more reservation,
				// in
				// which case, the deletion should fail with error code 400.
				cex = new customException();
				cex.setCode(400);
				cex.setMessage("There are existing reservations for this flight.");
				throw cex;

			} else {

				flightDao.delete(p);
			}
		} else {

			cex = new customException();
			cex.setCode(404);
			cex.setMessage("Flight with number " + number + " does not exist");
			throw cex;

		}

	}

	/**
	 * checks overlap between two flights
	 * 
	 * @param flightA
	 * @param flightB
	 * @return
	 */
	private boolean checkOverlap(Flight flightA, Flight flightB) {

		Date startDate1 = flightA.getDepartureTime();
		Date endDate1 = flightA.getArrivalTime();
		Date startDate2 = flightB.getDepartureTime();
		Date endDate2 = flightB.getArrivalTime();

		if ((startDate1.before(endDate2) || startDate1.equals(endDate2))
				&& (endDate1.after(startDate2) || endDate1.equals(startDate2))) {
			return true;
		}
		return false;
	}

}
