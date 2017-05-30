package lab2.DAO;

import org.springframework.data.repository.CrudRepository;

import lab2.Entities.Flight;

public interface FlightDao extends CrudRepository<Flight, Long> {
	/**
	 * @param number
	 * @return
	 */
	public Flight findByflightNumber(String number);
}
