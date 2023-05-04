package com.driver.controllers;


import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AirportRepository {

    TreeMap<String, Airport> airportDb = new TreeMap<>();
    HashMap<Integer, Flight> flightDb = new HashMap<>();
    HashMap<Integer, Passenger> passengerDb = new HashMap<>();
    HashMap<Integer, Set<Integer>> flightPassengerDb = new HashMap<>();
    HashMap<Integer, Integer> flightCollection = new HashMap<>();
    HashMap<Integer, Integer> passengerPayment = new HashMap<>();

    public void addAirport(Airport airport){
        String airportName = airport.getAirportName();
        airportDb.put(airportName, airport);

    }

    public void addFlight(Flight flight){
        int flightId = flight.getFlightId();
        flightDb.put(flightId, flight);
    }

    public void addPassenger(Passenger passenger){
        int passengerId = passenger.getPassengerId();
        passengerDb.put(passengerId, passenger);
    }

    public String getLargestAirportName(){

        int noOfTerminals = 0;
//        Airport largestAirport = null;
//        for(String airportName : airportDb.keySet()){
//            Airport airport = airportDb.get(airportName);
//            if(airport.getNoOfTerminals() > noOfTerminals){
//                largestAirport = airport;
//            }
//            else if(airport.getNoOfTerminals() == noOfTerminals){
//                if(airport.getAirportName().compareTo(largestAirport.getAirportName()) < 0){
//                    largestAirport = airport;
//                }
//            }
//        }
//        return largestAirport.getAirportName();
        String answer="";
        int ans=0;
        for(String name:airportDb.keySet()){
            int co=airportDb.get(name).getNoOfTerminals();
            if(co>ans){
                ans=co;
                answer=name;
            }
        }
        return answer;
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity){

        double shortestDuration = Double.MAX_VALUE;
        boolean foundFlight = false;

        for(int flightId : flightDb.keySet()){
            Flight flight = flightDb.get(flightId);
            if(flight.getFromCity().equals(fromCity) && flight.getToCity().equals(toCity)){
                foundFlight = true;
                if(flight.getDuration() < shortestDuration){
                    shortestDuration = flight.getDuration();
                }
            }
        }

        if(foundFlight == false){
            return -1;
        }
        return shortestDuration;
    }

    public String bookATicket(Integer flightId, Integer passengerId){
        Flight flight = flightDb.get(flightId);

        if(flightPassengerDb.containsKey(flightId)){
            if(flightPassengerDb.get(flightId).size() >= flight.getMaxCapacity()){
                return "FAILURE";
            }
            if(flightPassengerDb.get(flightId).contains(passengerId)){
                return "FAILURE";
            }
//            calculating fare and adding revenue
            int fare = calculateFlightFare(flightId);
            passengerPayment.put(passengerId, fare);
            int revenue = flightCollection.getOrDefault(flightId,0);
            revenue = revenue + fare;
            flightCollection.put(flightId,revenue);

            flightPassengerDb.get(flightId).add(passengerId);
            return "SUCCESS";
        }
        Set<Integer> set = new HashSet<>();
        set.add(passengerId);
        // calculating fare and adding revenue
        int fare = calculateFlightFare(flightId);
        passengerPayment.put(passengerId, fare);
        int revenue = flightCollection.getOrDefault(flightId,0);
        revenue = revenue + fare;
        flightCollection.put(flightId,revenue);

        flightPassengerDb.put(flightId, set);
        return "SUCCESS";
        //If the numberOfPassengers who have booked the flight is greater than : maxCapacity, in that case :
        //return a String "FAILURE"
        //Also if the passenger has already booked a flight then also return "FAILURE".
        //else if you are able to book a ticket then return "SUCCESS"
    }

    public String cancelATicket(Integer flightId, Integer passengerId){

        if(flightPassengerDb.containsKey(flightId)){
            if(flightPassengerDb.get(flightId).contains(passengerId)){

//              removing payment from flight revenue
                int fare = passengerPayment.get(passengerId);
                passengerPayment.remove(passengerId);
                int revenue = flightCollection.get(flightId);
                revenue = revenue - fare;
                flightCollection.put(flightId,revenue);

                flightPassengerDb.get(flightId).remove(passengerId);
                return "SUCCESS";
            }
        }
        return "FAILURE";

        //If the passenger has not booked a ticket for that flight or the flightId is invalid or in any other failure case
        // then return a "FAILURE" message
        // Otherwise return a "SUCCESS" message
        // and also cancel the ticket that passenger had booked earlier on the given flightId
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId){
        int ans = 0;
        for(int flightId : flightPassengerDb.keySet()){
            if(flightPassengerDb.get(flightId).contains(passengerId)){
                ans++;
            }
        }
        return ans;
    }

    public int getNumberOfPeopleOn(Date date, String airportName){
        int noOfPeople = 0;
        Airport airport = airportDb.get(airportName);
        if(airport!=null){
            City city = airport.getCity();

            for(int flightId : flightDb.keySet()){
                Flight flight = flightDb.get(flightId);
                if((flight.getFromCity().equals(city) || flight.getToCity().equals(city)) && flight.getFlightDate().equals(date)){
//                    noOfPeople = noOfPeople + flightPassengerDb.get(flightId).size();
                    Set<Integer> set = flightPassengerDb.get(flightId);
                    if (set != null) {
                        noOfPeople += set.size();
                    }
                }

            }
        }
        return noOfPeople;
    }

    public int calculateFlightFare(int flightId){
        int fare=3000;
        int alreadyBooked=0;
        if(flightPassengerDb.containsKey(flightId))
            alreadyBooked=flightPassengerDb.get(flightId).size();
        return (fare+(alreadyBooked*50));
//        int passengerCount = flightPassengerDb.get(flightId).size();
//        int price = 3000 + (passengerCount*50);
//        return price;
        //Calculation of flight prices is a function of number of people who have booked the flight already.
        //Price for any flight will be : 3000 + noOfPeopleWhoHaveAlreadyBooked*50
        //Suppose if 2 people have booked the flight already : the price of flight for the third person will be 3000 + 2*50 = 3100
        //This will not include the current person who is trying to book, he might also be just checking price
    }

    public int calculateRevenueOfAFlight(int flightId){
        int revenue = flightCollection.getOrDefault(flightId,0);
        return revenue;
    }

    public String getAirportNameFromFlightId(int flightId){
        if(!flightDb.containsKey(flightId)){
            return null;
        }
        Flight flight = flightDb.get(flightId);
        City city = flight.getFromCity();
        for(String airportName : airportDb.keySet()){
            Airport airport = airportDb.get(airportName);
            if(airport.getCity() == city){
                return airport.getAirportName();
            }
        }
        return null;
    }


}