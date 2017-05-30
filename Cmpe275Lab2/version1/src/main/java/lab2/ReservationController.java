package lab2;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lab2.Entities.Reservation;
import lab2.Services.ReservationServices;
import lab2.Services.Success;
import lab2.Services.customException;

@RestController
public class ReservationController {
	@Autowired
	private ReservationServices reservationServices;
	
	
	/** 
	 * Create Reservations for a passenger using the list of flights given.
	 * @param passengerId
	 * @param flightLists
	 * @return 
	 * @throws customException 
	 */
	@RequestMapping(value = "/reservation", method = RequestMethod.POST, produces = { "application/xml"})
	public Reservation createReservation(@RequestParam("passengerId") int passengerId,
											   @RequestParam("flightLists") List<String> flightLists) throws customException {
		
		//return new ReturnReservation(reservationServices.createReservation(passengerId, flightLists));
		return reservationServices.createReservation(passengerId, flightLists);
	}
	
	
	/**
	 * Update reservation based on reservation number. Add/Remove flights as supplied in the query parameters.
	 * @param orderNumber
	 * @param flightsAdded
	 * @param flightsRemoved
	 * @throws customException 
	 * @return Returns updated reservation object as a json
	 */
	@RequestMapping(value = "/reservation/{orderNumber}", method = RequestMethod.POST, produces = { "application/json" })
	public Reservation updateReservation(@PathVariable long orderNumber,
											   @RequestParam(value="flightsAdded", required=false) List<String> flightsAdded,
											   @RequestParam(value="flightsRemoved", required=false) List<String> flightsRemoved) throws customException {
	
		if(flightsAdded==null && flightsRemoved==null){
			customException cex = new customException();
			cex.setCode(404);
			cex.setMessage("Atleast one parameter flightsAdded or flightsRemoved should be supplied");
			throw cex;
		}
		return reservationServices.updateReservation(orderNumber, flightsAdded, flightsRemoved);
    }
	
	
	/**
	 * Method to get the reservation details based on the reservation number
	 * @param flightnumber
	 * @return Returns the reservation details as JSON if found, returns error otherwise
	 * @throws customException 
	 */
	@RequestMapping(value = "/reservation/{reservationNumber}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public Reservation getReservation(@PathVariable("reservationNumber") Long reservationNumber) throws customException {
		//return new ReturnReservation(reservationServices.getReservationByNumber(reservationNumber));
		return reservationServices.getReservationByNumber(reservationNumber);
	}

	@RequestMapping(value = "/reservation/{reservationNumber}", params = "xml", method = RequestMethod.GET, produces = {MediaType.APPLICATION_XML_VALUE })
	public Reservation getReservationXml(
			@PathVariable("reservationNumber") Long reservationNumber,
			@RequestParam("xml") String retTypeJson) throws customException {
		//return new ReturnReservation(reservationServices.getReservationByNumber(reservationNumber));
		return reservationServices.getReservationByNumber(reservationNumber);
	}

	
	/**
	 * Method to search for a reservation based on a criteria.
	 * @param passengerId
	 * @param from
	 * @param to
	 * @param flightnumber
	 * @return Returns List of Reservation objects as XML
	 * @throws customException 
	 */
	@RequestMapping(value = "/reservation", method = RequestMethod.GET, produces = {MediaType.APPLICATION_XML_VALUE })
	public Set<Reservation>  SearchReservation(
			@RequestParam(name = "passengerId", defaultValue = "0", required=false) int passengerId,
			@RequestParam(name = "from", defaultValue = "", required=false) String from,
			@RequestParam(name = "to", defaultValue = "", required=false) String to,
			@RequestParam(name = "flightNumber", defaultValue = "", required=false) String flightNumber) {
		
		return reservationServices.searchReservation(passengerId, from, to, flightNumber);
	}


	/**
	 * delete Reservation
	 * @throws customException 
	 * @param 
	 */
	// https://hostname/reservation/number
	@RequestMapping(value = "/reservation/{reservationNumber}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_XML_VALUE })
	public SuccessfulResponse deleteReservation(@PathVariable("reservationNumber") Long reservationNumber) throws customException {
		reservationServices.deleteReservation(reservationNumber);
		Success succ = new Success();
		succ.setCode(200);
		succ.setMessage("Reservation with number "+reservationNumber+" is canceled successfully is deleted successfully");
		return new SuccessfulResponse(succ);
	}
}
