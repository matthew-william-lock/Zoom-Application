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

# All possible combinations of locations
locations_combinations=[]
for x in itertools.permutations(locations):
    locations_combinations.append(x)
print("\nNumber of different combinations {}\n".format(len(locations_combinations)))

# Pick the best route
now = datetime.now()
if len(locations_combinations)>0:

    end_location = locations_combinations[0][0]
    waypoints_order= locations_combinations[0][1::]
    waypoints_parameter=[]
    
    for i, values in enumerate(waypoints_order):
        waypoints_parameter.append(values)

    directions_result = gmaps.directions(current_location,end_location,waypoints=waypoints_parameter, mode="driving",departure_time=now)
    legs=directions_result[0]['legs']

    best_time_seconds=0
    for i, value in enumerate(legs):
        best_time_seconds+= value['duration']['value']
    best_time_index =0

    print("Time from {} to {} with waypoints {} is {} seconds.".format(address,end_location,waypoints_order,best_time_seconds))


    for i,list_of_locations in enumerate(locations_combinations,start=1):
        if i>len(locations_combinations)-1: break

        end_location = locations_combinations[i][0]
        waypoints_order= locations_combinations[i][1::]
        waypoints_parameter=[]

        for i, values in enumerate(waypoints_order):
            waypoints_parameter.append(values)    

        directions_result = gmaps.directions(current_location,end_location,waypoints=waypoints_parameter, mode="driving",departure_time=now)
        legs=directions_result[0]['legs']
        time_seconds=0
        for i, value in enumerate(legs):
            time_seconds+= value['duration']['value']

        print("Time from {} to {} with waypoints {} is {} seconds.".format(address,end_location,waypoints_order,time_seconds))

        if time_seconds<best_time_seconds:
            best_time_seconds=time_seconds
            best_time_index=i

    best_end_location= locations_combinations[best_time_index][0]
    best_waypoints= locations_combinations[best_time_index][1::]
    print("\nThe best route to take is from {} to {} with waypoints {}, taking {} seconds.\n".format(address,best_end_location,best_waypoints,best_time_seconds))

    directions_result = gmaps.directions(current_location,end_location,mode="driving",departure_time=now)
    json_formatted_str = json.dumps(directions_result, indent=2)
    print(json_formatted_str)