package com.example.skytrace;

public class Airport {
    public String name;
    public double latitude;
    public double longitude;
    public String elevation;
    public String municipality;
    public String country;
    public String icao;
    public String iata;

    public Airport(String name, double latitude, double longitude, String elevation,
                   String municipality, String country, String icao, String iata) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.municipality = municipality;
        this.country = country;
        this.icao = icao;
        this.iata = iata;
    }
}
