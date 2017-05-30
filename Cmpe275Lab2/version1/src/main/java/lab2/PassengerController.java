package lab2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lab2.Entities.Passenger;
import lab2.Services.PassengerServices;
import lab2.Services.Success;
import lab2.Services.customException;

@RestController
public class PassengerController {
	@Autowired
	private PassengerServices passengerServices;

	// ************ Create a passenger*****************

	// create passenger

	// http://hostname/passenger?firstname=XX&lastname=YY&age=11&gender=famale&phone=123
	/**
	 * @param firstname
	 * @param lastname
	 * @param age
	 * @param gender
	 * @param phone
	 * @return
	 * @throws customException
	 */
	@RequestMapping(value = "/passenger/**", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ReturnPassenger createPassenger(@RequestParam("firstname") String firstname,
			@RequestParam("lastname") String lastname, @RequestParam("age") int age,
			@RequestParam("gender") String gender, @RequestParam("phone") String phone) throws customException {
		Passenger p = new Passenger();
		p.setFirstname(firstname);
		p.setLastname(lastname);
		p.setAge(age);
		p.setGender(gender);
		p.setPhone(phone);
		return new ReturnPassenger(passengerServices.createPassenger(p));
	}

	// ************ Get Passengers*********************
	// http://hostname/passenger/id?json=true
	/**
	 * @param passengerID
	 * @return
	 * @throws customException
	 */
	@RequestMapping(value = "/passenger/{id}/**", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ReturnPassenger GetPassengerJson(@PathVariable("id") int passengerId) throws customException {
		return new ReturnPassenger(passengerServices.getPassengerByNumber(passengerId));
	}

	// http://hostname/passenger/id?xml=true
	/**
	 * @param passengerID
	 * @return
	 * @throws customException
	 */
	@RequestMapping(value = "/passenger/{id}", params = "xml", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public Passenger GetPassengerXml(@PathVariable("id") int passengerId, @RequestParam("xml") String retTypeJson)
			throws customException {
		return passengerServices.getPassengerByNumber(passengerId);
	}

	// ***************update Passengers *****************

	// http://hostname/passenger/id?firstname=XX&lastname=YY&age=11&gender=famale&phone=123
	/**
	 * @param passengerId
	 * @param firstname
	 * @param lastname
	 * @param age
	 * @param gender
	 * @param phone
	 * @return
	 * @throws customException
	 */
	@RequestMapping(value = "/passenger/{id}/**", method = RequestMethod.PUT, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ReturnPassenger createPassenger(@PathVariable("id") String passengerId,
			@RequestParam("firstname") String firstname, @RequestParam("lastname") String lastname,
			@RequestParam("age") int age, @RequestParam("gender") String gender, @RequestParam("phone") String phone)
			throws customException {

		Passenger p = new Passenger();
		p.setId(Integer.parseInt(passengerId));
		p.setFirstname(firstname);
		p.setLastname(lastname);
		p.setAge(age);
		p.setGender(gender);
		p.setPhone(phone);
		return new ReturnPassenger(passengerServices.updatePassenger(p));
	}

	// **************** delete passenger ********************************
	// http://hostname/passenger/id
	/**
	 * @param passengerID
	 * @return
	 * @throws customException
	 */
	@RequestMapping(value = "/passenger/{id}", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public SuccessfulResponse DeletePassengerJson(@PathVariable("id") int passengerId) throws customException {
		passengerServices.deletePassenger(passengerId);
		Success succ = new Success();
		succ.setCode(200);
		succ.setMessage("Passenger with id " + passengerId + " is deleted successfully");
		return new SuccessfulResponse(succ);

	}

}
