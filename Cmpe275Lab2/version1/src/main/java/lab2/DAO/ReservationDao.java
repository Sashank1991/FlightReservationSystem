package lab2.DAO;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.fasterxml.jackson.core.sym.Name;

import lab2.Entities.Passenger;
import lab2.Entities.Reservation;

public interface ReservationDao extends CrudRepository<Reservation, Long> {
	public Reservation findByorderNumber(Long ordernumber);

	public List<Reservation> findByPassenger(Passenger passenger);

	@Query(value = "Select * from reservations res " + "left join passengers pass on pass.id=res.passenger "
			+ "left join reservation_flights flightresmapping on flightresmapping.order_number=res.ordernumber "
			+ "left join flights flights on flights.flight_number=flightresmapping.flight_number "
			+ "where (:passengerId ='' or res.passenger= :passengerId) and "
			+ "(:from_city ='' or flights.from_city= :from_city) and "
			+ "(:to_city ='' or flights.to_city= :to_city) and "
			+ "(:flightNumber ='' or flightresmapping.flight_number= :flightNumber)", nativeQuery = true)
	public Set<Reservation> search(@Param("passengerId") int passengerId, @Param("from_city") String from_city,
			@Param("to_city") String to_city, @Param("flightNumber") String flightNumber);

}
