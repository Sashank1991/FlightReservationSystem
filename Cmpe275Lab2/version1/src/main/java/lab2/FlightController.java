package lab2;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lab2.Entities.Flight;
import lab2.Entities.Plane;
import lab2.Services.FlightServices;
import lab2.Services.Success;
import lab2.Services.customException;

@RestController
public class FlightController {
	@Autowired
	private FlightServices flightServices;

	// *************** create flight & update flight *************
	// https://hostname/flight/flightNumber?price=120&from=AA&to=BB&
	// departureTime=CC&arrivalTime=DD&description=EE&capacity=GG&model=HH&manufacturer=II&yearOfManufacture=1997
	/**
	 * @param flightNumber
	 * @param price
	 * @param from
	 * @param to
	 * @param departureTime
	 * @param arrivalTime
	 * @param capacity
	 * @param description
	 * @param model
	 * @param manufacturer
	 * @param yearOfManufacture
	 * @return
	 * @throws customException
	 */
	@RequestMapping(value = "/flight/{flightNumber}/**", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public Flight createFlight(@PathVariable("flightNumber") String flightNumber, @RequestParam("price") int price,
			@RequestParam("from") String from, @RequestParam("to") String to,
			@RequestParam("departureTime") @DateTimeFormat(pattern = "yyyy-MM-dd-HH") Date departureTime,
			@RequestParam("arrivalTime") @DateTimeFormat(pattern = "yyyy-MM-dd-HH") Date arrivalTime,
			@RequestParam("capacity") int capacity, @RequestParam("description") String description,
			@RequestParam("model") String model, @RequestParam("manufacturer") String manufacturer,
			@RequestParam("yearOfManufacture") int yearOfManufacture) throws customException {

		Flight flight = new Flight();
		Plane pl = new Plane();

		pl.setCapacity(capacity);
		pl.setManufacturer(manufacturer);
		pl.setModel(model);
		pl.setYearOfManufacture(yearOfManufacture);

		flight.setNumber(flightNumber);
		flight.setPrice(price);
		flight.setTo(to);
		flight.setFrom(from);
		flight.setArrivalTime(arrivalTime);
		flight.setDepartureTime(departureTime);
		flight.setDescription(description);
		flight.setPlane(pl);
		flight.setSeatsLeft(capacity);

		flightServices.createFlight(flight);

		return flight;
	}

	// ************ Get Flights*********************

	// https://hostname/flight/flightNumber
	/**
	 * @param flightNumber
	 * @return
	 * @throws customException
	 */
	@RequestMapping(value = "/flight/{flightNumber}/**", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ReturnFlight GetFlightJson(@PathVariable("flightNumber") String flightNumber) throws customException {
		return new ReturnFlight(flightServices.getFlightByNumber(flightNumber));
	}

	// https://hostname/flight/flightNumber?xml=true
	/**
	 * @param flightNumber
	 * @return
	 * @throws customException
	 */
	@RequestMapping(value = "/flight/{flightNumber}", params = "xml", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public Flight GetFlightXml(@PathVariable("flightNumber") String flightNumber,
			@RequestParam("xml") String retTypeJson) throws customException {
		return flightServices.getFlightByNumber(flightNumber);
	}

	// ******************* delete flight *******************

	// http://hostname/airline/flightNumber
	/**
	 * @param flightnumber
	 * @return
	 * @throws customException
	 */
	@RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public SuccessfulResponse DeleteFlight(@PathVariable("flightNumber") String flightNumber) throws customException {
		flightServices.deleteFlight(flightNumber);
		Success succ = new Success();
		succ.setCode(200);
		succ.setMessage("Flight with number " + flightNumber + " is deleted successfully");
		return new SuccessfulResponse(succ);
	}
}
