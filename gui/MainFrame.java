package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import logic.*;
import model.Airport;
import model.Flight;


public class MainFrame extends Frame {

    private static final long serialVersionUID = 1L;




    private final AirportManager manager = new AirportManager();
    private InactivityTimer inactivityTimer;

    // polja za unos aerodroma
    private TextField airportName, airportCode, airportX, airportY;

    private TextField flightFrom, flightTo, flightDeparture, flightDuration;

    private TableCanvas airportTable, flightTable;

    public MainFrame() {
        super("Simulacija avionskog saobracaja - Faza A");
        buildUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (inactivityTimer != null) inactivityTimer.stop();
                dispose();
                System.exit(0);
            }
        });

        setSize(900, 650);
        setLocationRelativeTo(null);

        inactivityTimer = new InactivityTimer(this);
        inactivityTimer.start();
    }

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(245, 245, 245));

        Panel forms = new Panel(new GridLayout(1, 2, 10, 0));
        forms.add(buildAirportForm());
        forms.add(buildFlightForm());
        add(forms, BorderLayout.NORTH);

        add(buildTables(), BorderLayout.CENTER);
        add(buildFileButtons(), BorderLayout.SOUTH);
    }
 

    private Panel buildAirportForm() {
        Panel p = new Panel(new BorderLayout(4, 4));
        p.add(new Label("Novi aerodrom", Label.CENTER), BorderLayout.NORTH);

        Panel fields = new Panel(new GridLayout(4, 2, 4, 4));
        airportName = new TextField();
        airportCode = new TextField();
        airportX = new TextField();
        airportY = new TextField();

        fields.add(new Label("Naziv:"));
        fields.add(airportName);
        fields.add(new Label("Kod (3 velika slova):"));
        fields.add(airportCode);
        fields.add(new Label("X (-180 do 180):"));
        fields.add(airportX);
        fields.add(new Label("Y (-90 do 90):"));
        fields.add(airportY);
        p.add(fields, BorderLayout.CENTER);

        Button add = new Button("Dodaj aerodrom");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddAirport();
            }
        });
        Panel wrap = new Panel(new FlowLayout(FlowLayout.CENTER));
        wrap.add(add);
        p.add(wrap, BorderLayout.SOUTH);
        return p;
    }

    private Panel buildFlightForm() {
        Panel p = new Panel(new BorderLayout(4, 4));
        p.add(new Label("Novi let", Label.CENTER), BorderLayout.NORTH);

        Panel fields = new Panel(new GridLayout(4, 2, 4, 4));
        flightFrom = new TextField();
        flightTo = new TextField();
        flightDeparture = new TextField();
        flightDuration = new TextField();

        fields.add(new Label("Polazni kod:"));
        fields.add(flightFrom);
        fields.add(new Label("Odredisni kod:"));
        fields.add(flightTo);
        fields.add(new Label("Poletanje (HH:MM):"));
        fields.add(flightDeparture);
        fields.add(new Label("Trajanje (minuti):"));
        fields.add(flightDuration);
        p.add(fields, BorderLayout.CENTER);

        Button add = new Button("Dodaj let");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddFlight();
            }
        });
        Panel wrap = new Panel(new FlowLayout(FlowLayout.CENTER));
        wrap.add(add);
        p.add(wrap, BorderLayout.SOUTH);
        return p;
    }

    //  tabele

    private Panel buildTables() {
        Panel p = new Panel(new GridLayout(1, 2, 10, 0));

        airportTable = new TableCanvas(
                new String[] { "KOD", "NAZIV", "X", "Y" },
                new int[] { 60, 200, 60, 60 });
        flightTable = new TableCanvas(
                new String[] { "OD", "DO", "POLETANJE", "TRAJANJE" },
                new int[] { 60, 60, 100, 90 });

        p.add(wrapInScroll("Aerodromi", airportTable));
        p.add(wrapInScroll("Letovi", flightTable));
        return p;
    }

    private Panel wrapInScroll(String title, TableCanvas table) {
        Panel p = new Panel(new BorderLayout());
        p.add(new Label(title, Label.CENTER), BorderLayout.NORTH);
        ScrollPane sp = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        sp.add(table);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    // dugmad za rad sa fajlovima

    private Panel buildFileButtons() {
        Panel p = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 8));

        Button loadCsv = new Button("Ucitaj CSV");
        Button saveCsv = new Button("Sacuvaj CSV");
        Button loadJson = new Button("Ucitaj JSON");
        Button saveJson = new Button("Sacuvaj JSON");

        loadCsv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLoad(true);
            }
        });
        saveCsv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSave(true);
            }
        });
        loadJson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLoad(false);
            }
        });
        saveJson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSave(false);
            }
        });
        
        Button mapBtn = new Button("Prikazi mapu");
        mapBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		new MapFrame(manager, inactivityTimer).setVisible(true);
        	}
        });
        
        
        p.add(loadCsv);
        p.add(saveCsv);
        p.add(loadJson);
        p.add(saveJson);
        p.add(mapBtn);
        return p;
    }

    //akcije korisnika

    private void onAddAirport() {
        try {
            String name = airportName.getText().trim();
            String code = airportCode.getText().trim();

            if (name.isEmpty())
                throw new IllegalArgumentException("Naziv aerodroma ne sme biti prazan.");
            if (!code.matches("[A-Z]{3}"))
                throw new IllegalArgumentException("Kod mora imati tacno 3 velika slova (npr. BEG).");

            int x = parseInt(airportX.getText(), "X koordinata");
            int y = parseInt(airportY.getText(), "Y koordinata");

            manager.addAirport(new Airport(name, code, x, y));
            clear(airportName, airportCode, airportX, airportY);
            refreshTables();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void onAddFlight() {
        try {
            String from = flightFrom.getText().trim();
            String to = flightTo.getText().trim();
            String departure = flightDeparture.getText().trim();

            if (from.isEmpty() || to.isEmpty())
                throw new IllegalArgumentException("Morate uneti kod polaznog i odredisnog aerodroma.");
            if (!departure.matches("\\d{1,2}:\\d{2}"))
                throw new IllegalArgumentException("Vreme poletanja mora biti u formatu HH:MM (npr. 08:30).");

            String[] t = departure.split(":");
            int h = Integer.parseInt(t[0]);
            int m = Integer.parseInt(t[1]);

            if (h < 0 || h > 23)
                throw new IllegalArgumentException("Sati moraju biti izmedju 0 i 23.");
            if (m < 0 || m > 59)
                throw new IllegalArgumentException("Minuti moraju biti izmedju 0 i 59.");

            int duration = parseInt(flightDuration.getText(), "Trajanje leta");
            if (duration <= 0)
                throw new IllegalArgumentException("Trajanje leta mora biti pozitivan broj minuta.");

            manager.addFlight(from, to, duration, h, m);
            clear(flightFrom, flightTo, flightDeparture, flightDuration);
            refreshTables();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void onLoad(boolean csv) {
        FileDialog fd = new FileDialog(this, csv ? "Ucitaj CSV" : "Ucitaj JSON", FileDialog.LOAD);
        fd.setVisible(true);
        if (fd.getFile() == null) return;
        String path = fd.getDirectory() + fd.getFile();

        try {
        	FileHandler h;
            if(csv)h = new CsvHandler(path);
            else h = new JsonHandler(path);
            h.load();
            manager.loadAirports(h.getAirports());
            manager.loadFlights(h.getFlights());
            refreshTables();
            showInfo("Podaci su uspesno ucitani.");
        }
        catch(ValidationException ex){
            showError("Fajl nije pronadjen:\n" + path);
        }
        catch (FileNotFoundException ex) {
            showError("Fajl nije pronadjen:\n" + path);
        } catch (Exception ex) {
            showError("Greska pri ucitavanju fajla:\n" + ex.getMessage());
        }
    }

    private void onSave(boolean csv) {
        FileDialog fd = new FileDialog(this, csv ? "Sacuvaj CSV" : "Sacuvaj JSON", FileDialog.SAVE);
        fd.setVisible(true);
        if (fd.getFile() == null) return;
        String path = fd.getDirectory() + fd.getFile();

        try {
            if (csv) {
                new CsvHandler(path).save(manager.getAirports(), manager.getFlights());
            } else {
                new JsonHandler(path).save(manager.getAirports(), manager.getFlights());
            }
            showInfo("Podaci su uspesno sacuvani.");
        } catch (Exception ex) {
            showError("Greska pri cuvanju fajla:\n" + ex.getMessage());
        }
    }

    // pomocne metode

    private int parseInt(String text, String fieldName) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " mora biti ceo broj.");
        }
    }

    private void clear(TextField... fields) {
        for (TextField f : fields) f.setText("");
    }

    /** Osvezava tabele */
    private void refreshTables() {
        List<String[]> airportRows = new ArrayList<String[]>();
        for (Airport a : manager.getAirports()) {
            airportRows.add(new String[] {
                    a.getCode(),
                    a.getName(),
                    String.valueOf(a.getX()),
                    String.valueOf(a.getY()) });
        }
        airportTable.setRows(airportRows);

        List<String[]> flightRows = new ArrayList<String[]>();
        for (Flight f : manager.getFlights()) {
            flightRows.add(new String[] {
                    f.getFrom().getCode(),
                    f.getTo().getCode(),
                    String.format("%02d:%02d", f.getDepartureH(), f.getDepartureM()),
                    f.getDuration() + " min" });
        }
        flightTable.setRows(flightRows);

        validate();
    }

    private void showError(String message) {
        showDialog("Greska", message);
    }

    private void showInfo(String message) {
        showDialog("Informacija", message);
    }

    // Dijalog sa porukom
    private void showDialog(String title, String message) {
        if (message == null) message = "Nepoznata greska.";

        final Dialog d = new Dialog(this, title, true);
        d.setLayout(new BorderLayout(10, 10));

        String[] lines = message.split("\n");
        Panel text = new Panel(new GridLayout(0, 1));
        for (String linija : lines) {
            text.add(new Label(linija, Label.CENTER));
        }
        d.add(text, BorderLayout.CENTER);

        Button ok = new Button("U redu");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                d.dispose();
            }
        });
        Panel wrap = new Panel(new FlowLayout(FlowLayout.CENTER));
        wrap.add(ok);
        d.add(wrap, BorderLayout.SOUTH);

        d.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                d.dispose();
            }
        });

        d.setSize(440, 110 + 22 * lines.length);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    public static void main(String[] args) {
        new MainFrame().setVisible(true);
    }
}
