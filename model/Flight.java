package model;
import logic.InvalidFlightData;
import logic.TimeException;
public class Flight {
	
	public Flight(Airport from, Airport to, int duration, int departureH, int departureM) throws InvalidFlightData, TimeException {
		if(from == null) throw new InvalidFlightData("The departure airport is not setted!");
		if(to == null) throw new InvalidFlightData("The destination airport is not setted!");
		if(from.getCode().equals(to.getCode())) 
		    throw new InvalidFlightData("Departure and destination airport cannot be the same!");
		this.from = from;
		this.to = to;
		if(duration <= 0) throw new TimeException("Duration must be an positive integer");
		this.duration = duration;
		if(!(departureH < 24 && departureH >= 0)) throw new TimeException("Hours must be between 0 and 24!");
		this.departureH = departureH;
		if(!(departureM < 60 && departureM >= 0)) throw new TimeException("Minutes must be between 0 and 60!");
		this.departureM = departureM;
	}
	
	public Airport getFrom() {
		return from;
	}
	public void setFrom(Airport from) {
		this.from = from;
	}
	public Airport getTo() {
		return to;
	}
	public void setTo(Airport to) {
		this.to = to;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getDepartureH() {
		return departureH;
	}
	public void setDepartureH(int departureH) {
		this.departureH = departureH;
	}
	public int getDepartureM() {
		return departureM;
	}
	public void setDepartureM(int departureM) {
		this.departureM = departureM;
	}

	public void setChanged(int x, int y, int simTime, Airport dest){
		if(!changed){
			changedX = x;
			changedY = y;
			changed = true;
			startEmergency = simTime;
			prev = to;
			to = dest;
		}
	}



	/*public void changeAirport(Airport a){
		if(changed) to = a;
	}*/

	public boolean changed = false;
	public int changedX, changedY;
	public int startEmergency;
	private Airport from, to;
	public Airport prev;
	private int duration;
	private int departureH, departureM;
}
