# Telemetry System Simulator for speeding

This is an application that is used for issuing speeding tickets for vehicles that are in range of a certain radar.


## Technologies

<div class="flex">
<img src="https://i.pinimg.com/736x/e9/94/61/e99461fdd5b3db8bdb3081d8acf5e524.jpg" height="50" style="margin-right: 20px;">
<img src="https://www.payara.fish/payara-site/themes/payara/nav/logo-social.png" height="50" style="margin-right: 20px;">
<img src="https://banner2.cleanpng.com/20180802/ipp/kisspng-docker-logo-kubernetes-microservices-cloud-computi-r-amp-d-solutions-custom-clould-solutions-5b63385043d6b1.3283562915332291362779.jpg" height="50" style="margin-right: 20px;">
<img src="https://www.itqlick.com/Graphics/Modules/Products/Logos/4716.jpg" height="50" style="margin-right: 20px;">
<img src="https://dbdb.io/media/logos/h2-logo.svg" height="50">
</div>

<ul>
    <li><strong>Java:</strong></li>   
    <li><strong>Maven:</strong></li>
    <li><strong>Payara server:</strong></li>
    <li><strong>Docker:</strong>
    <li><strong>H2 database:</strong>
    <li><strong>HSQL database:</strong> 
</ul>

User can choose which database (H2 or HSQL) he wants to use.

## Project description
The system is designed to simulate driving electric vehicles in a city using real data stored in CSV files. It starts by specifying a file name and then reads and processes all the data. 
This information is sent to radar systems located throughout the city, which are positioned based on GPS coordinates. When a vehicle enters the range of an active radar, the system checks if it is speeding. 
If the vehicle exceeds the speed limit defined by the radar (e.g., driving faster than 40 km/h for more than 7 seconds without any abnormal behavior), the radar issues a speeding ticket and stores the details in a database.
Zhis project was made for the needs of advanced web technology and services courses at the college (NWTiS).

### Features:
<ul>
  <li>
    <strong>Reading driving data (concurrency and multithreading):</strong>
    <ul>
      <li>Data is recorded at very short intervals (less than 1 second).</li>
      <li>Each row contains GPS coordinates, speed, time, and other vehicle parameters.</li>
    </ul>
  </li>
  <li>
    <strong>Simulating data transmission:</strong>
    <ul>
      <li>Data from the CSV file is treated as "live" data.</li>
      <li>Each row is sent one by one to the telemetry and speed control system.</li>
      <li>The time difference between rows is multiplied by a time correction factor.</li>
    </ul>
  </li>
  <li>
    <strong>Speed measurement radars:</strong>
    <ul>
      <li>Placed at different locations with specified GPS coordinates.</li>
      <li>Radars process data if the vehicle is within their range.</li>
      <li>They check if the speed exceeds the allowed limit and for how long.</li>
    </ul>
  </li>
  <li>
    <strong>Recording violations:</strong>
    <ul>
      <li>If the vehicle exceeds the speed limit, the radar records the data.</li>
      <li>If the overspeeding lasts longer than the allowed time, new data is recorded with a "false" status.</li>
      <li>If the overspeeding persists, a speeding ticket is generated.</li>
    </ul>
  </li>
  <li>
    <strong>Communication between servers:</strong>
    <ul>
      <li><strong>RadarServer:</strong>
        <ul>
          <li>Registers with the RegistrationServer.</li>
          <li>Opens a network socket and operates in a multithreaded mode.</li>
          <li>Creates virtual threads for each client and processes commands.</li>
        </ul>
      </li>
      <li><strong>PenaltyServer:</strong>
        <ul>
          <li>Opens a network socket and operates in a single-threaded mode.</li>
          <li>Stores penalty data and retrieves penalties/statistics as needed.</li>
        </ul>
      </li>
      <li><strong>VehicleServer:</strong>
        <ul>
          <li>Opens a network socket and operates in a multithreaded mode.</li>
          <li>Creates virtual threads for each client and checks if the vehicle is within a radar's range.</li>
          <li>Sends commands to the RadarServer.</li>
        </ul>
      </li>
    </ul>
  </li>
  <li>
    <strong>VehicleSimulator:</strong>
    <ul>
      <li>Connects to the VehicleServer.</li>
      <li>Reads the data file row by row.</li>
      <li>Prepares data for commands and sends them asynchronously without waiting for a response.</li>
    </ul>
  </li>
  <li>
    <strong>Docker Containerization:</strong>
    <ul>
      <li>Everything is containerized using Docker.</li>
      <li>Deployment is streamlined with Docker Compose (`docker-compose up` command).</li>
    </ul>
  </li>
  <li>
    <strong>Database Usage:</strong>
    <ul>
      <li>Utilizes two databases for storing various data.</li>
    </ul>
  </li>
  <li>
    <strong>Multithreading:</strong>
    <ul>
      <li>Uses multithreading extensively for efficient data processing and communication.</li>
    </ul>
  </li>
  <li>
    <strong>User Interface:</strong>
    <ul>
      <li>Powered by Payara.</li>
      <li>Allows visualization of simulation data stored in database, active radars, vehicle statuses, data filtering and much more.</li>
    </ul>
  </li>
</ul>


## Architecture

System scheme:

![image](https://github.com/CroAnna/telemetry-tickets-radar/assets/90924342/75840191-7547-4873-90bc-a82a61490e60)
![image](https://github.com/CroAnna/telemetry-tickets-radar/assets/90924342/ce102bbe-ae19-4a1e-8617-4cc1952ab2e2)

System installation architecture and methods of RESTful web services:

![image](https://github.com/CroAnna/telemetry-tickets-radar/assets/90924342/45db8b75-7f15-45c7-9e20-e774580c2a1a)

