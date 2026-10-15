package com.countryservice.demo.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.countryservice.demo.beans.Country;
import com.countryservice.demo.repositories.CountryRepository;

@Component
public class CountryService {

    @Autowired
    CountryRepository countryRep;

    public List<Country> getAllCountries() {
        List<Country> countries = countryRep.findAll();
        return countries;
    }

    public Country getCountrybyID(int id) {
        return countryRep.findById(id).get();
    }

    public Country getCountrybyName(String countryName) {
        return countryRep.findByCountryName(countryName);
    }

    public Country addCountry(Country country) {
        return countryRep.save(country);
    }

    public Country updateCountry(int id, Country country) {
        Country existing = countryRep.findById(id).get();
        existing.setCountryName(country.getCountryName());
        existing.setCapital(country.getCapital());
        return countryRep.save(existing);
    }

    public void deleteCountry(int id) {
        countryRep.deleteById(id);
    }
}