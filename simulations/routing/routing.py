import googlemaps

from datetime import datetime
from getmac import get_mac_address as gma

import itertools
import itertools
import json

# Connect to API
from APIKey import Key
gmaps = googlemaps.Client(key=Key)

# Get co-ordinates of current location
results = gmaps.geolocate() # Return json object containing current location

current_location = results['location']
longitudinal = results['location']['lng']
lateral = results['location']['lat']
accuracy = results['accuracy']

# Get address of current location
results = gmaps.reverse_geocode(current_location,location_type="ROOFTOP")
address = results[0]['formatted_address']
print("Your current location is {} {} with an accuracy of {}m.".format(lateral,longitudinal,accuracy))
print("Your current location is {} with an accuracy of {}m.".format(address,accuracy))

# Routing Location Information
locations=[]

# Get locations from user
input_string=str(raw_input("\nEnter delivery locations or type 'done':\n")).rstrip()
while input_string!="done":
    if input_string: locations.append(input_string)
    input_string=str(raw_input("\nEnter delivery locations or type 'done':\n")).rstrip()

now = datetime.now()

# Fastest time
best_time_seconds=float("inf")
best_route_index=0

# select best route
for i in range(len(locations)):
    end_location=locations[i]
    waypoints=locations[:i]
    waypoints.extend(locations[i+1:])

    # API Request
    directions_result = gmaps.directions(current_location,end_location,waypoints=waypoints,optimize_waypoints =True, mode="driving",departure_time=now)
    legs=directions_result[0]['legs']

    # Get time for route
    time_seconds=0
    for i, value in enumerate(legs):
        time_seconds+= value['duration']['value']
    
    if time_seconds<best_time_seconds:
        best_time_seconds=time_seconds
        best_route_index=i

    # Print route results
    print("Time from {} to {} with waypoints (out of order) {} is {} seconds.".format(address,end_location,waypoints,time_seconds))

# Print best route
best_end_location= locations[best_route_index]
best_waypoints= locations[:best_route_index]
best_waypoints.extend(locations[i+best_route_index:])
print("\nThe best route to take is from {} to {} with waypoints {}, taking {} seconds.\n".format(address,best_end_location,best_waypoints,best_time_seconds))