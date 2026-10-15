package com.countryservice.demo.beans;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String countryName;
    private String capital;

    public Country() {
    }

    public Country(int id, String countryName, String capital) {
        this.id = id;
        this.countryName = countryName;
        this.capital = capital;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }

    public String getCapital() { return capital; }
    public void setCapital(String capital) { this.capital = capital; }
}