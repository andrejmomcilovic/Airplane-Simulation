package model;

import logic.AirportCodeException;
import logic.InvalidCoordinatesException;

public class Airport {
	public Airport(String name, String code, int x, int y) throws AirportCodeException, InvalidCoordinatesException {
		if(code.length() != 3 || !code.equals(code.toUpperCase())) throw new AirportCodeException("Invalid airport code!");
		if(!(((-180 <= x) && (x <= 180)) && ((-90 <= y) && (y <= 90)))) throw new InvalidCoordinatesException("Invalid airport coordinates!");
		this.name = name;
		this.code = code;
		this.x = x;
		this.y = y;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setInvisible() {
		visible = false;
	}
	public void setVisible() {
		visible = true;
	}
	public boolean isVisible() {
		return visible;
	}
	private String name;
	private String code;
	private int x, y;
	private boolean visible = true;
}
