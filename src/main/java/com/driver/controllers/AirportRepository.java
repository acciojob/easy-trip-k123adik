package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Repository
public class AirportRepository {

    HashMap<String, Airport> airportDb = new HashMap<>();
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

        String answer = "";
        int ans = 0;
        for(String name : airportDb.keySet()){
            int terminal = airportDb.get(name).getNoOfTerminals();
            if(terminal > ans){
                ans = terminal;
                answer = name;
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
            if(flightPassengerDb.get(flight).size() >= flight.getMaxCapacity()){
                return "FAILURE";
            }
            if(flightPassengerDb.get(flightId).contains(passengerId)){
                return "FAILURE";
            }
            //calculate fare and adding revenue;
            int fare = calculateFlightfare(flightId);
            passengerPayment.put(passengerId, fare);
            int revenue = flightCollection.getOrDefault(flightId, 0);
            revenue = revenue + fare;
            flightCollection.put(flightId, revenue);

            flightPassengerDb.get(flightId).add(passengerId);
            return "SUCCESS";
        }
        Set<Integer> set = new HashSet<>();
        set.add(passengerId);

        int fare = calculateFlightfare(flightId);
        passengerPayment.put(passengerId, fare);
        int revenue = flightCollection.getOrDefault(flightId, 0);
        revenue = revenue + fare;
        flightCollection.put(flightId, revenue);

        flightPassengerDb.put(flightId, set);
        return "SUCCESS";

    }

    public String cancelATicket(Integer flightId, Integer passengerId){

        if(flightPassengerDb.containsKey(flightId)){
            if(flightPassengerDb.get(flightId).contains(passengerId)){

                //remove payment from flight revenue
                int fare = passengerPayment.get(passengerId);
                passengerPayment.remove(passengerId);
                int revenue = flightCollection.get(flightId);
                revenue = revenue - fare;
                flightCollection.put(flightId, revenue);

                flightPassengerDb.get(flightId).remove(passengerId);
                return "SUCCESS";
            }
        }
        return "FAILURE";
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
        int people = 0;
        Airport airport = airportDb.get(airportName);
        if(airport != null){
            City city = airport.getCity();

            for(int flightId : flightDb.keySet()){
                Flight flight = flightDb.get(flightId);
                if((flight.getFromCity().equals(city) || flight.getToCity().equals(city)) && flight.getFlightDate().equals(date)){
                    Set<Integer> set = flightPassengerDb.get(flightId);
                    if(set != null){
                        people += set.size();
                    }
                }
            }
        }
        return people;
    }
    public int calculateFlightfare(int flightId){
        int fare = 3000;
        int alreadyBooked = 0;

        if(flightPassengerDb.containsKey(flightId)){
            alreadyBooked = flightPassengerDb.get(flightId).size();
        }
        return fare + alreadyBooked*50;
    }

    public int calculateRevenueOfAFlight(int flightId){
        int revenue = flightCollection.getOrDefault(flightId, 0);
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
