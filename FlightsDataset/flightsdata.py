import csv
from datetime import datetime, timedelta

cities = [
    "Hyderabad", "New Delhi", "Bangalore", "Goa",
    "Rajahmundry", "Mumbai", "Chennai", "Kolkata"
]

airlines = [
    ("Air India", "AIR", 100),
    ("Indigo", "IND", 200),
    ("Vistara", "VIS", 300)
]

routes = [(dep, arr) for dep in cities for arr in cities if dep != arr]
base_time = datetime(2026, 4, 1, 6, 0)

# Open both CSV files
with open("flights.csv", mode="w", newline="") as flights_file, \
     open("seats.csv", mode="w", newline="") as seats_file:

    flights_writer = csv.writer(flights_file)
    seats_writer = csv.writer(seats_file)

    # Write headers
    flights_writer.writerow([
        "id", "airline", "arrival_airport", "arrival_time",
        "departure_airport", "departure_time", "flight_number", "status"
    ])
    seats_writer.writerow([
        "seat_id", "available_seats", "no_of_seats",
        "price", "seat_class", "id"
    ])

    flight_id = 1
    seat_id = 1

    for airline_name, prefix, start_num in airlines:
        for i, (dep, arr) in enumerate(routes):
            flight_number = f"{prefix}-{start_num + i}"
            departure_time = base_time + timedelta(minutes=15 * flight_id)
            arrival_time = departure_time + timedelta(hours=2)
            status = "Scheduled"

            # Write flight entry with full datetime
            flights_writer.writerow([
                flight_id, airline_name, arr,
                arrival_time.strftime("%Y-%m-%d %H:%M:%S"),
                dep, departure_time.strftime("%Y-%m-%d %H:%M:%S"),
                flight_number, status
            ])

            # Business seat
            seats_writer.writerow([
                seat_id, 40, 40, 6000 + flight_id * 10, "Business", flight_id
            ])
            seat_id += 1

            # Economy seat
            seats_writer.writerow([
                seat_id, 100, 100, 4000 + flight_id * 5, "Economy", flight_id
            ])
            seat_id += 1

            flight_id += 1