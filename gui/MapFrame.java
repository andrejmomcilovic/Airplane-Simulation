package gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import logic.AirportManager;
import logic.Simulation;

//klasa Frame koja oznacava prozor u Javi

public class MapFrame extends Frame {
	private AirportManager manager;
	private MapCanvas mapCanvas;
	private AirportListPanel airportListPanel;
	private volatile Simulation simulation;
	private Thread simThread;
	
	public void setSimulation(Simulation s) {
		this.simulation = s;
		mapCanvas.repaint();
	}
	
	public MapFrame(AirportManager manager, InactivityTimer inactivityTimer) {
		super("Mapa aerodroma");
		mapCanvas = new MapCanvas(manager, inactivityTimer);
		this.manager = manager;
		setLayout(new BorderLayout());
		add(mapCanvas, BorderLayout.CENTER);
		airportListPanel = new AirportListPanel(manager, mapCanvas);
		add(airportListPanel, BorderLayout.EAST);
		simulation = new Simulation(manager);
		mapCanvas.setSimulation(simulation);
		ButtonsPanel buttonsPanel = new ButtonsPanel(simulation, inactivityTimer, mapCanvas);
		add(buttonsPanel, BorderLayout.SOUTH);
		setSize(1000, 500);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				simThread.interrupt();
				dispose();
			}
		});
		simThread = new Thread() {
			@Override
			public void run() {
				while(!Thread.interrupted()) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {}
					simulation.tick();
					mapCanvas.repaint();
				}
			}
		};
		simThread.setDaemon(true);
		simThread.start();
	}
}