package model;

public class Plane {
	private Flight flight;
	private int actualDeparture; //in minutes
	public Plane(Flight flight, int actualDeparture) {
		this.flight = flight;
		this.actualDeparture = actualDeparture;
	}
	public boolean isInAir(int simTime) {
		if(simTime >= actualDeparture && simTime <= flight.getDuration() + actualDeparture) {
			return true;
		}
		return false;
	}
	public double getProgress(int simTime) {
			if (flight.changed) {
				double odx = flight.getFrom().getX() - flight.prev.getX();
				double ody = flight.getFrom().getY() - flight.prev.getY();
				double originalDistance = Math.sqrt(odx * odx + ody * ody);
				double speed = originalDistance / flight.getDuration(); // svetske jedinice po minutu

				double ndx = flight.getTo().getX() - flight.changedX;
				double ndy = flight.getTo().getY() - flight.changedY;
				double newLegDistance = Math.sqrt(ndx * ndx + ndy * ndy);

				if (speed <= 0 || newLegDistance <= 0) return 1.0;

				double newLegDuration = newLegDistance / speed;
				double t = (simTime - flight.startEmergency) / newLegDuration;

				if (t < 0) t = 0;
				if (t > 1) t = 1;
				return t;
			}
			return (simTime - actualDeparture) * 1.0 / flight.getDuration();
	}
	public Flight getFlight() {
		return flight;
	}
	public int getActualDeparture() {
		return actualDeparture;
	}
}
