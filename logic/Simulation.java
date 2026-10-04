package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Flight;
import model.Plane;

public class Simulation {
	private AirportManager manager;
	private List<Plane> planes;  //ubacujemo avione
	private volatile int simTime;
	private volatile boolean running; // da li teče
	
	public Simulation(AirportManager manager) {
		this.manager = manager;
		this.planes = new ArrayList<>();
		simTime = 0;
		running = false;
		schedulePlanes();
	}
	
	public synchronized void tick() {
		if(!running) return;
		simTime += 2;
	}
	
	
	public void start() {running = true;}
	public void pause() {running = false;}
	public void reset() {running = false; simTime = 0;}
	public int getSimTime() {return simTime;}
	
	//to get Plains in the Air
	
	public synchronized List<Plane> getInTheAir(){
		List<Plane> result = new ArrayList<>();
		for(Plane p : planes) {
			if(p.isInAir(simTime)) result.add(p);
		}
		return result;
	}
	
	private void schedulePlanes() {
		List<Flight> sorted = new ArrayList<>(manager.getFlights());
		Collections.sort(sorted, new Comparator<Flight>() {
		    @Override
		    public int compare(Flight a, Flight b) {
		        int ta = a.getDepartureH() * 60 + a.getDepartureM();
		        int tb = b.getDepartureH() * 60 + b.getDepartureM();
		        return ta - tb;
		    }
		});
		Map <String, Integer> lastDeparture = new HashMap<>();  //heš mapa poslednjeg odlaska
		for (Flight f : sorted) {
		    String code = f.getFrom().getCode();
		    int planned = f.getDepartureH() * 60 + f.getDepartureM();
		    int actual = planned;
		    if (lastDeparture.containsKey(code)) {
		        int last = lastDeparture.get(code);
		        if (planned < last + 10) {
		            actual = last + 10;
		        }
		    }
		    
		    lastDeparture.put(code, actual);
		    planes.add(new Plane(f, actual));

	}
	}
}
