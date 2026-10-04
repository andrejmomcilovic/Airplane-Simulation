package gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Image;

import logic.AirportManager;
import logic.Simulation;
import model.Airport;
import model.Plane;

public class MapCanvas extends Canvas {

	private int x1, y1, x2, y2;
	private boolean danger = false;

	private static final int SIZE = 14;
	
	private Simulation simulation;
	
	private Image buffer;
	
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}
	
	@Override
	public void update(Graphics g) {
		if(buffer == null || buffer.getWidth(this) != getWidth() || buffer.getHeight(this) != getHeight()) {
			buffer = createImage(getWidth(), getHeight());
		}
		Graphics bg = buffer.getGraphics();
		bg.setColor(getBackground());
		bg.fillRect(0, 0, getWidth(), getHeight());
		paint(bg);
		bg.dispose();
		g.drawImage(buffer, 0, 0, this);
	}
	
	private volatile boolean isBlinking = true;

	private AirportManager manager;
	private InactivityTimer inactivityTimer;
	private volatile Airport selected;

	public MapCanvas(AirportManager manager, InactivityTimer inactivityTimer) {
		this.manager = manager;
		this.inactivityTimer = inactivityTimer;
		setBackground(Color.WHITE);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				for(Airport a : manager.getAirports()) {
					if(!a.isVisible()) continue;
					int airportX = toScreenX(a.getX());
					int airportY = toScreenY(a.getY());
					if(((airportX - SIZE / 2) < x) && ((airportX + SIZE / 2) > x) && ((airportY - SIZE / 2) < y) && ((airportY + SIZE / 2) > y)) {
						if(a == selected) {
							selected = null;
							inactivityTimer.resume();
						}
						else {
							selected = a;
							inactivityTimer.pause();
						}
						repaint();
						break;
					}
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int x = e.getX(), y = e.getY();
				x1 = x;
				y1 = y;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				int x = e.getX(), y = e.getY();
				x2 = x;
				y2 = y;
				danger = true;
				repaint();
			}
		});
		startBlinking();
	}

	@Override
	public void paint(Graphics g) {
		if(danger){
			g.setColor(Color.ORANGE);
			g.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
		}
		for (Airport a : manager.getAirports()) {
			if(!a.isVisible()) continue;
			int x = toScreenX(a.getX());
			int y = toScreenY(a.getY());
			if(a != selected || !isBlinking) g.setColor(Color.GRAY);
			else g.setColor(Color.RED);
			g.fillRect(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE);
			g.setColor(Color.BLACK);
			g.drawString(a.getCode(), x - SIZE / 2, y - SIZE / 2 - 3);
		}
		if(simulation != null) {
			for(Plane p : simulation.getInTheAir()) {
				if(p.getFlight().changed){
					int fromX = toScreenX(p.getFlight().changedX);
					int fromY = toScreenY(p.getFlight().changedY);
					int toX = toScreenX(p.getFlight().getTo().getX());
					int toY = toScreenY(p.getFlight().getTo().getY());

					int x = (int)(fromX + p.getProgress(simulation.getSimTime()) * (toX - fromX));
					int y = (int)(fromY + p.getProgress(simulation.getSimTime()) * (toY - fromY));


					g.setColor(Color.BLUE);
					g.fillOval(x - 5, y - 5, 10, 10);

					continue;
				}  //brkanje koordinata
				int fromX = toScreenX(p.getFlight().getFrom().getX());
				int fromY = toScreenY(p.getFlight().getFrom().getY());
				int toX = toScreenX(p.getFlight().getTo().getX());
				int toY = toScreenY(p.getFlight().getTo().getY());
				int x = (int)(fromX + p.getProgress(simulation.getSimTime()) * (toX - fromX));
				int y = (int)(fromY + p.getProgress(simulation.getSimTime()) * (toY - fromY));
				if(danger && Math.min(x1, x2) <= x && x <= Math.max(x1, x2) && Math.min(y1, y2) <= y && y <= Math.max(y1, y2) && !p.getFlight().changed){
					int toWorldx = toWorldX(x);
					int toWorldy = toWorldY(y);
					p.getFlight().changed = true;
					double min = 100000;
					Airport curr = null;
					for(Airport air : manager.getAirports()){
						if(Math.sqrt((air.getX() - x) * (air.getX() - x) + (air.getY() - y) * (air.getY() - y)) < min){
							min = Math.sqrt((air.getX() - x) * (air.getX() - x) + (air.getY() - y) * (air.getY() - y));
							curr = air;
						}
					}
					p.getFlight().setChanged(toWorldx, toWorldy, simulation.getSimTime(), curr);
				}
				g.setColor(Color.BLUE);
				g.fillOval(x - 5, y - 5, 10, 10);
			}
		}
	}
	
	private void startBlinking() {
		Thread t = new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
					if(selected != null) {
						isBlinking = !isBlinking;
						repaint();
					}
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}
	
	private int toScreenX(int airportX) {
		return (int) ((airportX + 180) / 360.0 * getWidth());
	}

	private int toScreenY(int airportY) {
		return (int) (getHeight() - (airportY + 90) / 180.0 * getHeight());
	}

	private int toWorldX(int x){
		return (int) ((double)x / getWidth() * 360.0 - 180);
	}

	private int toWorldY(int y){
		return (int) (((double)(getHeight() - y) / getHeight() * 180.0) - 90);
	}
}