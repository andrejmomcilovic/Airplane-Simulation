# Airline Traffic Simulator

A graphical Java application for simulating and visualizing airline traffic, developed as a project assignment for the Object-Oriented Programming 2 (OOP2) course at the Faculty of Electrical Engineering, University of Belgrade (Academic Year 2025/2026).

---

## 📋 Table of Contents
- [Overview](#overview)
- [Key Features](#key-features)
  - [Phase A: Data Management & Input Validation](#phase-a-data-management--input-validation)
  - [Phase B: Interactive Map Visualization](#phase-b-interactive-map-visualization)
  - [Phase C: Flight Simulation & Concurrency](#phase-c-flight-simulation--concurrency)
- [Software Architecture](#software-architecture)
- [Data Formats](#data-formats)
  - [CSV Format](#csv-format)
  - [JSON Format](#json-format)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Compilation & Execution](#compilation--execution)
- [Inactivity & Safety Features](#inactivity--safety-features)

---

## ✈️ Overview

The **Airline Traffic Simulator** is an object-oriented desktop application that simulates real-time commercial flight operations across global airport networks. Built with modular design principles, clean separation of concerns, and robust error handling, the project provides an intuitive GUI to model, visualize, and execute synchronized flight schedules.

---

## ✨ Key Features

### Phase A: Data Management & Input Validation
* **Airport Entry:** Input airports with unique 3-letter uppercase codes (e.g., `BEG`, `LHR`), names, and Cartesian coordinates ($x \in [-180, 180]$, $y \in [-90, 90]$).
* **Flight Entry:** Define flight routes with departure airport, destination airport, departure time (`HH:mm`), and duration (in minutes).
* **Tabular Display:** Data is organized and presented using interactive table components.
* **File I/O:** Import and export system configurations via **CSV** and **JSON** files.
* **Granular Error Handling:** Clear, user-friendly exception messages for missing files, invalid coordinate ranges, duplicate airport codes, or incorrect time formats.
* **Auto-Termination:** Automatic program shutdown after 60 seconds of user inactivity, featuring a 5-second countdown warning dialog with an option to extend the session.

### Phase B: Interactive Map Visualization
* **Graphical Map Rendering:** Visual 2D map displaying airports as grey squares labeled with their 3-letter codes.
* **Selection & Animation:** Click on an airport to select it, triggering a red flashing animation. Re-clicking deselects the airport.
* **Inactivity Pause:** User inactivity timers automatically pause while an airport is selected.
* **Visibility Filtering:** Sidebar check-list allowing users to toggle individual airport visibility on the map.

### Phase C: Flight Simulation & Concurrency
* **Time Scale:** Real-time simulation starting at `00:00`, where **1 second of real time equals 10 minutes of simulated time**.
* **Departure Queuing Rule:** Maximum of **1 departure per airport every 10 simulated minutes**. Flights scheduled at the same time are queued sequentially.
* **Animated Flight Tracking:** Active flights render as blue circles moving along linear paths between origin and destination.
* **High-Precision Rendering:** Smooth visual updates refreshed every **200 milliseconds**.
* **Simulation Controls:** Full state control including **Start**, **Pause**, and **Reset**.
* **Thread Safety:** Fully synchronized multi-threaded architecture with inactivity timers paused during execution.

---

## 🏗️ Software Architecture

The application strictly adheres to the **Model-View-Controller (MVC)** design pattern and Object-Oriented Principles:

* **Model Layer:** Domain entities representing `Airport`, `Flight`, `Coordinate`, and `SimulationClock`.
* **View Layer:** Custom GUI components, canvas maps, tabular displays, and interaction dialogs.
* **Logic / Controller Layer:** Event handling, scheduling queues, movement interpolation, and simulation orchestration.
* **Utility Layer:** File parsing engines (CSV/JSON), exception hierarchies, and thread timer managers.

---

## 📁 Data Formats

The application natively supports reading and writing configuration files in both CSV and JSON formats.

### CSV Format
```csv
# AIRPORTS
CODE,NAME,X,Y
LHR,London Heathrow,0,51
BEG,Belgrade Nikola Tesla,10,45
JFK,John F Kennedy International,-37,41

# FLIGHTS
FROM,TO,DEPARTURE,DURATION
BEG,LHR,17:10,170
LHR,JFK,08:30,420
