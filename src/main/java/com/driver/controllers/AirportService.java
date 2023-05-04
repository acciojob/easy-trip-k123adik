package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AirportService {

    AirportRepository airportRepository = new AirportRepository();

    public void addAirport(Airport airport){
        airportRepository.addAirport(airport);
    }

    public void addFlight(Flight flight){
        airportRepository.addFlight(flight);
    }

    public void addPassenger(Passenger passenger){
        airportRepository.addPassenger(passenger);
    }

    public String getLargestAirportName(){
        return airportRepository.getLargestAirportName();
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromcity, City toCity){
        double shortestDuration = airportRepository.getShortestDurationOfPossibleBetweenTwoCities(fromcity, toCity);
        return shortestDuration;
    }

    public int getNumberOfPeopleOn(Date date, String airportName){
        int people = airportRepository.getNumberOfPeopleOn(date, airportName);
        return people;
    }

    public String bookATicket(Integer flightId, Integer passengerId){
        String ans = airportRepository.bookATicket(flightId, passengerId);
        return ans;
    }

    public String cancelATicket(Integer flightId, Integer passengerId){
        String ans = airportRepository.cancelATicket(flightId, passengerId);
        return ans;
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId){
        int ans = airportRepository.countOfBookingsDoneByPassengerAllCombined(passengerId);
        return ans;
    }

    public int calculateFlightFare(int flightId){
        int ans = airportRepository.calculateFlightfare(flightId);
        return ans;
    }

    public int calculateRevenueOfAFlight(int flightId){
        int ans = airportRepository.calculateRevenueOfAFlight(flightId);
        return ans;
    }

    public String getAirportNameFromFlightId(int flightId){
        return airportRepository.getAirportNameFromFlightId(flightId);
    }
}
