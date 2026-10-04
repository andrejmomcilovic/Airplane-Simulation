package gui;

import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import logic.Simulation;

public class ButtonsPanel extends Panel {
	private Simulation sim;
	private InactivityTimer timer;
	private MapCanvas canvas;
	public ButtonsPanel(Simulation sim, InactivityTimer timer, MapCanvas canvas) {
		this.sim = sim;
		this.timer = timer;
		this.canvas  = canvas;
		setLayout(new GridLayout(1, 0));
		Button b1 = new Button("Start");
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sim.start();
				timer.pause();
			}
		});
		Button b2 = new Button("Pauza");
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sim.pause();
				timer.resume();
			}
		});
		Button b3 = new Button("Reset");
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sim.reset();
				timer.resume();
				canvas.repaint();
			}
		});
		add(b1);
		add(b2);
		add(b3);
	}
}
