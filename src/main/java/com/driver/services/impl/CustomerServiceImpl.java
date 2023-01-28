package com.driver.services.impl;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.model.TripBooking;
import com.driver.model.TripStatus;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
        Customer customer = customerRepository2.findById(customerId).get();
		customerRepository2.delete(customer);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL queryt
//		TripBooking bookedTrip = new TripBooking();
//		Customer customer = customerRepository2.findById(customerId).get();
//		boolean available = false;
//
//
//			bookedTrip.setCustomer(customer);
//			bookedTrip.setFromLocation(fromLocation);
//			bookedTrip.setToLocation(toLocation);
//			bookedTrip.setDistanceInKm(distanceInKm);
//
//			for(Driver driver : driverRepository2.findAll()){
//				Cab cab = driver.getCab();
//				if(cab.getAvailable()){
//					bookedTrip.setStatus(TripStatus.CONFIRMED);
//					cab.setAvailable(false);
//					bookedTrip.setBill(cab.getPerKmRate()*distanceInKm);
//					List<TripBooking> tripBookingList = customer.getTripBookingList();
//					if(tripBookingList==null) tripBookingList = new ArrayList<>();
//					tripBookingList.add(bookedTrip);
//					customer.setTripBookingList(tripBookingList);
//
//
//					List<TripBooking> tripBookingList1 = driver.getTripBookingList();
//					if(tripBookingList1==null) tripBookingList1 = new ArrayList<>();
//					tripBookingList1.add(bookedTrip);
//					driver.setTripBookingList(tripBookingList1);
//
//					driverRepository2.save(driver);
//					customerRepository2.save(customer);
//					available = true;
//                   break;
//				}
//			}
//
//		if(available==false) throw new Exception("No cab available!");
//
//
//		return bookedTrip;
		List<Driver> driverList = driverRepository2.findAll();
//		Collections.sort(driverList, new Comparator<Driver>() {
//			@Override
//			public int compare(Driver o1, Driver o2) {
//				return o1.getDriverId()-o2.getDriverId();
//			}
//		});

		TripBooking tripBooking=null;

		boolean isdriveravailable=false;

		for(Driver driver: driverList){
			if(driver.getCab().getAvailable()){
				isdriveravailable=true;

				int bill = driver.getCab().getPerKmRate()*distanceInKm;
				tripBooking = new TripBooking(fromLocation,toLocation,distanceInKm,TripStatus.CONFIRMED,bill);

				Customer customer = customerRepository2.findById(customerId).get();
				tripBooking.setCustomer(customer);
				tripBooking.setDriver(driver);

				driver.getCab().setAvailable(false);

				List<TripBooking> tripBookingList = customer.getTripBookingList();
				if(tripBookingList==null) tripBookingList = new ArrayList<>();
				tripBookingList.add(tripBooking);
				customer.setTripBookingList(tripBookingList);


				List<TripBooking> tripBookingList1 = driver.getTripBookingList();
				if(tripBookingList1==null) tripBookingList1 = new ArrayList<>();
				tripBookingList1.add(tripBooking);
				driver.setTripBookingList(tripBookingList1);


				driverRepository2.save(driver);
				customerRepository2.save(customer);

//				tripBookingRepository2.save(tripBooking);


				break;
			}
		}

		if(isdriveravailable==false){
			throw new Exception("No cab available!");
		}


		return tripBooking;


	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking trip = tripBookingRepository2.findById(tripId).get();
		trip.setStatus(TripStatus.CANCELED);
		trip.getDriver().getCab().setAvailable(true);
		trip.setBill(0);
		tripBookingRepository2.save(trip);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking trip = tripBookingRepository2.findById(tripId).get();
		trip.setStatus(TripStatus.COMPLETED);
		trip.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(trip);
	}
}
