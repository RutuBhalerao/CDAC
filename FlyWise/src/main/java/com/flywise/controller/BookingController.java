package com.flywise.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flywise.dto.FlightDto;
import com.flywise.exception.FlightException;
import com.flywise.exception.ResourceNotFoundException;
import com.flywise.pojos.AppUser;
import com.flywise.pojos.Booking;
import com.flywise.pojos.Classes;
import com.flywise.pojos.Flight;
import com.flywise.pojos.Passenger;
import com.flywise.repository.AppUserRepository;
import com.flywise.repository.ClassesRepository;
import com.flywise.service.IBookingService;
import com.flywise.service.IFlightService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/book")
public class BookingController {

	@Autowired
	private IBookingService bookingService;
	
	@Autowired
	private IFlightService flightService;
	
	@Autowired
	private ClassesRepository classesRepo;
	
	@Autowired
	private AppUserRepository userRepo;

//--------------------------------------------------------------------------------------------------------------------------

	@GetMapping(value = "/details", produces = "application/json")
	public ResponseEntity<?> bookingDetails(@RequestParam("fid") String fid) {
		
		Flight flight = bookingService.getFlightDetails(Integer.parseInt(fid));
		
		if (flight != null)
			return new ResponseEntity<Flight>(flight, HttpStatus.OK);
		
		else
			return new ResponseEntity<String>("Flight you asked for is fully booked, book another flight", HttpStatus.NOT_FOUND);
	}
	
//--------------------------------------------------------------------------------------------------------------------------
	
	// Post request for adding passengers for booking id
	
		@PostMapping(value = "/passengers", consumes = "application/json")
		public ResponseEntity<?> addPassengers(@RequestBody List<Passenger> passengersList, @RequestParam("bid") int bookingId) {
			
			String mesg = bookingService.addPassengers(passengersList, bookingId);
			
			return new ResponseEntity<String>(mesg, HttpStatus.OK);
		}

//--------------------------------------------------------------------------------------------------------------------------
		
		@PostMapping("/pay")
	    public ResponseEntity<?> processPayment(@RequestParam("bid") int bookingId) {
			
			String mesg = bookingService.processPayment(bookingId);
			
			return new ResponseEntity<String>(mesg,HttpStatus.OK);

	    }
		
//--------------------------------------------------------------------------------------------------------------------------
		
	@GetMapping(value = "/seats", produces = "application/json")
	public ResponseEntity<?> addBooking(@RequestParam("nos") int numberOfSeats,
										@RequestParam("fid") int flightId,
										@RequestParam("uid") int userId,
										@RequestParam("class") String className) {
		
		Booking booking = new Booking();
		
		booking.setNumberOfSeatsToBook(numberOfSeats);
		
		Flight selectedFlight = flightService.getFlightById(flightId);
		
		AppUser selectedUser = userRepo.findById(userId).orElseThrow(()->new ResourceNotFoundException("Invalid user Id"));
		
		if (selectedFlight.getAvailableSeats() <= 0)
			return new ResponseEntity<String>("Seats are not available.", HttpStatus.BAD_REQUEST);
		
		
		else if (booking.getNumberOfSeatsToBook() > selectedFlight.getAvailableSeats())
			return new ResponseEntity<String>("Only " + selectedFlight.getAvailableSeats() + " are available.", HttpStatus.BAD_REQUEST);
		
		
		else {
			
			int newSeatsRemaining = selectedFlight.getAvailableSeats() - booking.getNumberOfSeatsToBook();
			
			selectedFlight.setAvailableSeats(newSeatsRemaining);
			
			FlightDto flightDto = new FlightDto(selectedFlight.getFlightId(), selectedFlight.getSource(),
					selectedFlight.getDestination(), selectedFlight.getTravelDate(), selectedFlight.getArrivalTime(),
					selectedFlight.getDepartureTime(), selectedFlight.getEconomyFare(),
					selectedFlight.getFirstClassFare(), selectedFlight.getBusinessFare(),
					selectedFlight.getAvailableSeats());
			try {
				
				flightService.updateFlight(flightDto);
				
			} catch (FlightException e) {

			}
			
			int cid;
			
			if (className.toLowerCase().equals("business"))
				cid = 1;
			
			else if (className.toLowerCase().equals("firstclass"))
				cid = 2;
			
			else
				cid = 3;
			
			Classes classes=classesRepo.findById(cid).get();
			
			booking.setClasses(classes);
			
			booking.setFlight(selectedFlight);
			
			booking.setAppUser(selectedUser);
			
			booking.setBookingDate(LocalDate.now());
			
			bookingService.addBooking(booking);
			
			return new ResponseEntity<String>("" + booking.getBookingId(), HttpStatus.OK);

		}
	}
	
}
