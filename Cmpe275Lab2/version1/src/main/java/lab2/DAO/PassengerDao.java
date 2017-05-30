package lab2.DAO;

import org.springframework.data.repository.CrudRepository;

import lab2.Entities.Passenger;

public interface PassengerDao extends CrudRepository<Passenger, Integer> {
	public Passenger findById(int id);
	public Passenger findByPhone(String phone);
}
