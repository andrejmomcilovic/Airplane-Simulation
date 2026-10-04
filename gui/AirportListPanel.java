package gui;

import java.awt.Checkbox;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import logic.AirportManager;
import model.Airport;

public class AirportListPanel extends Panel {
	private AirportManager manager;
	private MapCanvas mapCanvas;
	
	public AirportListPanel(AirportManager manager, MapCanvas mapCanvas) {
		this.manager = manager;
		this.mapCanvas = mapCanvas;
		setLayout(new GridLayout(0, 1));
		for(Airport a : manager.getAirports()) {
			String text = a.getCode() + " - " + a.getName() + "(" + a.getX() + ", " + a.getY() + ")";
			Checkbox cb = new Checkbox(text, a.isVisible());
			cb.addItemListener(new ItemListener() {
		        @Override
		        public void itemStateChanged(ItemEvent e) {
		        	boolean checked = (e.getStateChange() == ItemEvent.SELECTED);
		        	if(checked)a.setVisible();
		        	else a.setInvisible();
		        	mapCanvas.repaint();
		        }
		    });
			add(cb);
		}
	}
}
