package com.countryservice.demo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.countryservice.demo.beans.Country;
import com.countryservice.demo.controllers.CountryController;
import com.countryservice.demo.services.CountryService;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerMockitoTests {

    @Mock
    CountryService countryService;

    @InjectMocks
    CountryController countryController;

    public List<Country> mycountries;
    Country country;

    @Test
    @Order(1)
    public void test_getAllCountries() {
        mycountries = new ArrayList<Country>();
        mycountries.add(new Country(1, "India", "Delhi"));
        mycountries.add(new Country(2, "USA", "Washington"));

        when(countryService.getAllCountries()).thenReturn(mycountries); // Mocking
        ResponseEntity<List<Country>> res = countryController.getCountries();

        assertEquals(HttpStatus.FOUND, res.getStatusCode());
        assertEquals(2, res.getBody().size());
    }

    @Test
    @Order(2)
    public void test_getCountryById() {
        country = new Country(1, "India", "Delhi");
        int countryID = 1;

        when(countryService.getCountrybyID(countryID)).thenReturn(country);
        ResponseEntity<Country> res = countryController.getCountryById(countryID);

        assertEquals(HttpStatus.FOUND, res.getStatusCode());
        assertEquals(countryID, res.getBody().getId());
    }

    @Test
    @Order(3)
    public void test_getCountryById_notFound() {
        when(countryService.getCountrybyID(99)).thenThrow(new NoSuchElementException());
        ResponseEntity<Country> res = countryController.getCountryById(99);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    @Order(4)
    public void test_getCountryByName() {
        country = new Country(2, "USA", "Washington");
        String countryName = "USA";

        when(countryService.getCountrybyName(countryName)).thenReturn(country);
        ResponseEntity<Country> res = countryController.getCountryByName(countryName);

        assertEquals(HttpStatus.FOUND, res.getStatusCode());
        assertEquals(countryName, res.getBody().getCountryName());
    }

    @Test
    @Order(5)
    public void test_getCountryByName_notFound() {
        when(countryService.getCountrybyName("Atlantis")).thenReturn(null);
        ResponseEntity<Country> res = countryController.getCountryByName("Atlantis");

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    @Order(6)
    public void test_addCountry() {
        country = new Country(3, "Germany", "Berlin");

        when(countryService.addCountry(country)).thenReturn(country);
        ResponseEntity<Country> res = countryController.addCountry(country);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals(country, res.getBody());
    }

    @Test
    @Order(7)
    public void test_updateCountry() {
        country = new Country(3, "Japan", "Tokyo");
        int countryID = 3;

        when(countryService.updateCountry(countryID, country)).thenReturn(country);
        ResponseEntity<Country> res = countryController.updateCountry(countryID, country);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("Tokyo", res.getBody().getCapital());
    }

    @Test
    @Order(8)
    public void test_updateCountry_notFound() {
        country = new Country(99, "X", "Y");

        when(countryService.updateCountry(99, country)).thenThrow(new NoSuchElementException());
        ResponseEntity<Country> res = countryController.updateCountry(99, country);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    @Order(9)
    public void test_deleteCountry() {
        ResponseEntity<Void> res = countryController.deleteCountry(3);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        verify(countryService, times(1)).deleteCountry(3);
    }

    @Test
    @Order(10)
    public void test_deleteCountry_notFound() {
        doThrow(new RuntimeException()).when(countryService).deleteCountry(99);
        ResponseEntity<Void> res = countryController.deleteCountry(99);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }
}