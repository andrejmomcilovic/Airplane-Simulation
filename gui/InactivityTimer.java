package gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class InactivityTimer {

    private static final int TIMEOUT = 60;   // sekundi do gasenja
    private static final int WARNING = 5;    // sekundi pre gasenja kada se javlja dijalog

    private final Frame owner;
    private Thread worker;

    private volatile long lastAction = System.currentTimeMillis();
    private volatile boolean paused = false;
    private volatile boolean running = true;
    private volatile boolean dialogOpen = false;

    private Dialog warningDialog;
    private Label countdownLabel;

    public InactivityTimer(Frame owner) {
        this.owner = owner;
        installGlobalListener();
    }

    private void installGlobalListener() {
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (!dialogOpen) reset();
            }
        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.ACTION_EVENT_MASK);
    }

    
    public void start() {
        worker = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        return;
                    }
                    if (paused || dialogOpen) continue;

                    long elapsed = (System.currentTimeMillis() - lastAction) / 1000;
                    long remaining = TIMEOUT - elapsed;

                    if (remaining <= WARNING) {
                        showWarning();
                    }
                }
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    // Resetuje odbrojavanje - poziva se na svaku akciju korisnika.
    public void reset() {
        lastAction = System.currentTimeMillis();
    }

    // Pauzira odbrojavanje (npr. dok je aerodrom selektovan ili traje simulacija).
    public void pause() {
        paused = true;
    }

    // Nastavlja odbrojavanje od pocetka. 
    public void resume() {
        reset();
        paused = false;
    }

    public void stop() {
        running = false;
        if (worker != null) worker.interrupt();
    }


    private void showWarning() {
        dialogOpen = true;

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                buildAndShowDialog();
            }
        });
    }

    private void buildAndShowDialog() {
        warningDialog = new Dialog(owner, "Upozorenje o neaktivnosti", true);
        warningDialog.setLayout(new BorderLayout(10, 10));

        countdownLabel = new Label("Program se zatvara za " + WARNING + " s zbog neaktivnosti.", Label.CENTER);
        warningDialog.add(countdownLabel, BorderLayout.CENTER);

        Panel buttons = new Panel();
        Button continueBtn = new Button("Nastavi rad");
        Button exitBtn = new Button("Zatvori program");
        buttons.add(continueBtn);
        buttons.add(exitBtn);
        warningDialog.add(buttons, BorderLayout.SOUTH);

        // nit koja odbrojava i gasi program ako korisnik ne reaguje
        final Thread countdown = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = WARNING; i > 0; i--) {
                    final int sec = i;
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (countdownLabel != null) {
                                countdownLabel.setText("Program se zatvara za " + sec + " s zbog neaktivnosti.");
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return; // korisnik je kliknuo "Nastavi rad"
                    }
                }
                System.exit(0);
            }
        });
        countdown.setDaemon(true);

        continueBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                countdown.interrupt();
                closeDialog();
            }
        });

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        warningDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                countdown.interrupt();
                closeDialog();
            }
        });

        warningDialog.setSize(340, 130);
        warningDialog.setLocationRelativeTo(owner);
        countdown.start();
        warningDialog.setVisible(true); // blokira dok se dijalog ne zatvori
    }

    private void closeDialog() {
        if (warningDialog != null) {
            warningDialog.dispose();
            warningDialog = null;
        }
        reset();
        dialogOpen = false;
    }
}
