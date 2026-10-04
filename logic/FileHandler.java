package logic;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Airport;
import model.Flight;
public abstract class FileHandler {
	public FileHandler(String filename) {
		file = new File(filename);
        airports = new ArrayList<>();
        flights = new ArrayList<>();
	}
	public abstract void load() throws IOException, ValidationException;
	public abstract void save(List<Airport> airports, List<Flight> flights) throws IOException;
	
	public List<Airport> getAirports() {
        return airports;
    }

    public List<Flight> getFlights() {
        return flights;
    }
	
	
	protected File file;
	protected List<Airport> airports;
    protected List<Flight> flights;
}
