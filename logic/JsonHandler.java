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

/**
 * Ucitavanje i cuvanje podataka u JSON formatu.
 * Ocekuje se da je svaki objekat (aerodrom ili let) u svom redu.
 */
public class JsonHandler extends FileHandler{

    public JsonHandler(String filename) {
        super(filename);
    }

    public void load() throws IOException, ValidationException {

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        String mode = "";

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.isEmpty()) continue;
            if (line.equals("{") || line.equals("}")) continue;
            if (line.startsWith("]")) continue;

            if (line.startsWith("\"airports\"")) {
                mode = "airports";
                continue;
            }
            if (line.startsWith("\"flights\"")) {
                mode = "flights";
                continue;
            }
            if (!line.startsWith("{")) continue;

            String[] t = stripObject(line).split(",");
            if (t.length != 4)
                throw new InvalidFlightData("Red nema ocekivana 4 polja: " + line);

            if (mode.equals("airports")) {
                String code = value(t[0]);
                String name = value(t[1]);
                int x = Integer.parseInt(value(t[2]));
                int y = Integer.parseInt(value(t[3]));
                airports.add(new Airport(name, code, x, y));

            } else if (mode.equals("flights")) {
                String fromCode = value(t[0]);
                String toCode = value(t[1]);
                String[] time = value(t[2]).split(":");
                int duration = Integer.parseInt(value(t[3]));

                Airport from = null, to = null;
                for (Airport a : airports) {
                    if (fromCode.equals(a.getCode())) from = a;
                    if (toCode.equals(a.getCode())) to = a;
                }
                if (from == null) throw new InvalidFlightData("Airport " + fromCode + " not found!");
                if (to == null) throw new InvalidFlightData("Airport " + toCode + " not found!");

                flights.add(new Flight(from, to, duration,
                        Integer.parseInt(time[0].trim()),
                        Integer.parseInt(time[1].trim())));
            }
        }
        reader.close();
    }

    /** Uklanja zarez na kraju reda (ako postoji) i spoljne viticaste zagrade. */
    private String stripObject(String line) {
        line = line.trim();
        if (line.endsWith(",")) line = line.substring(0, line.length() - 1);
        return line.substring(1, line.length() - 1);
    }

    /**
     * Vraca vrednost iz para "kljuc":vrednost.
     * Uklanja navodnike ako je vrednost string, ostavlja broj kakav jeste.
     */
    private String value(String part) {
        int colon = part.indexOf(':');
        if (colon < 0) throw new NumberFormatException("Neispravan par kljuc-vrednost: " + part);

        String v = part.substring(colon + 1).trim();
        if (v.length() >= 2 && v.startsWith("\"") && v.endsWith("\"")) {
            v = v.substring(1, v.length() - 1);
        }
        return v;
    }

    public void save(List<Airport> airports, List<Flight> flights) throws IOException {
        PrintWriter writer = new PrintWriter(file);
        writer.println("{");

        writer.println("\"airports\":[");
        for (int i = 0; i < airports.size(); i++) {
            Airport a = airports.get(i);
            writer.print("{\"code\":\"" + a.getCode() + "\",\"name\":\"" + a.getName()
                    + "\",\"x\":" + a.getX() + ",\"y\":" + a.getY() + "}");
            writer.println(i < airports.size() - 1 ? "," : "");
        }
        writer.println("],");

        writer.println("\"flights\":[");
        for (int i = 0; i < flights.size(); i++) {
            Flight f = flights.get(i);
            writer.print("{\"from\":\"" + f.getFrom().getCode()
                    + "\",\"to\":\"" + f.getTo().getCode()
                    + "\",\"departure\":\"" + String.format("%02d:%02d", f.getDepartureH(), f.getDepartureM())
                    + "\",\"duration\":" + f.getDuration() + "}");
            writer.println(i < flights.size() - 1 ? "," : "");
        }
        writer.println("]");

        writer.println("}");
        writer.close();
    }

}