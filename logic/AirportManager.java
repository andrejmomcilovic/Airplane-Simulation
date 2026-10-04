package logic;
import java.util.ArrayList;
import java.util.List;

import model.Airport;
import model.Flight;
public class AirportManager {
	public AirportManager() {
		airports = new ArrayList<>();
		flights = new ArrayList<>();
	}
	public void addAirport(Airport a) throws ValidationException {
		for(Airport e : airports) {
			if(a.getCode().equals(e.getCode())) throw new ValidationException("Airport with code " + a.getCode() + " already exists!");
		}
		airports.add(a);
	}
	
	public Airport findAirport(String code) throws ValidationException {
	    for (Airport a : airports) {
	        if (a.getCode().equals(code))
	            return a;
	    }
	    throw new ValidationException("Airport with code " + code + " does not exist!");
	}
	
	public void addFlight(String fromCode, String toCode, int duration, int depH, int depM) throws ValidationException, TimeException, InvalidFlightData {
	    Airport from = findAirport(fromCode);
	    Airport to = findAirport(toCode);
	    flights.add(new Flight(from, to, duration, depH, depM));
	}
	
	public void loadFlights(List<Flight> f) {
		flights = new ArrayList<>(f);
	}
	
	public void loadAirports(List<Airport> a) {
		airports = new ArrayList<>(a);
	}
	
	public List<Airport> getAirports() {
		return airports;
	}
	public List<Flight> getFlights() {
		return flights;
	}


	private List<Airport>airports;
	private List<Flight>flights;
}
