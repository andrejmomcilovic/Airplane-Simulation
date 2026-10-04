package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import model.Airport;
import model.Flight;

public class CsvHandler extends FileHandler {

    public CsvHandler(String filename) {
        super(filename);
    }
    public void load() throws IOException, ValidationException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        String mode = "";
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            if (line.equals("# AIRPORTS")) {
                mode = "AIRPORTS";
                continue;
            }
            if (line.equals("# FLIGHTS")) {
                mode = "FLIGHTS";
                continue;
            }
            if (line.startsWith("CODE") || line.startsWith("FROM")) continue;
            if (mode.equals("AIRPORTS")) {
                String[] parts = line.split(",");
                Airport a = new Airport(parts[1].trim(), parts[0].trim(), Integer.parseInt(parts[2].trim()), Integer.parseInt(parts[3].trim()));
                airports.add(a);
            } else if (mode.equals("FLIGHTS")) {
                String[] parts = line.split(",");
                String[] t = parts[2].trim().split(":");
                Airport from = null, to = null;
                for (Airport a : airports) {
                    if (parts[0].trim().equals(a.getCode())) from = a;
                    if (parts[1].trim().equals(a.getCode())) to = a;
                }
                if (from == null) throw new InvalidFlightData("Airport " + parts[0].trim() + " not found!");
                if (to == null) throw new InvalidFlightData("Airport " + parts[1].trim() + " not found!");
                Flight f = new Flight(from, to, Integer.parseInt(parts[3].trim()), Integer.parseInt(t[0].trim()), Integer.parseInt(t[1].trim()));
                flights.add(f);
            }
        }
        reader.close();
    }

    public void save(List<Airport> airports, List<Flight> flights) throws IOException {
        PrintWriter writer = new PrintWriter(file);
        writer.println("# AIRPORTS");
        writer.println("CODE,NAME,X,Y");
        for (Airport a : airports) {
            writer.println(a.getCode() + "," + a.getName() + "," + a.getX() + "," + a.getY());
        }
        writer.println();
        writer.println("# FLIGHTS");
        writer.println("FROM,TO,DEPARTURE,DURATION");
        for (Flight f : flights) {
            writer.println(f.getFrom().getCode() + "," + f.getTo().getCode() + "," +
                    String.format("%02d:%02d", f.getDepartureH(), f.getDepartureM()) + "," +
                    f.getDuration());
        }
        writer.close();
    }

    public List<Airport> getAirports() {
        return airports;
    }

    public List<Flight> getFlights() {
        return flights;
    }
}