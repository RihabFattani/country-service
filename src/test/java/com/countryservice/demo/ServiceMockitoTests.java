package com.countryservice.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.countryservice.demo.beans.Country;
import com.countryservice.demo.repositories.CountryRepository;
import com.countryservice.demo.services.CountryService;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceMockitoTests {

    @Mock
    CountryRepository countryrep;

    @InjectMocks
    CountryService countryService;

    @Test
    @Order(1)
    public void test_getAllCountries() {
        List<Country> mycountries = new ArrayList<Country>();
        mycountries.add(new Country(1, "India", "Delhi"));
        mycountries.add(new Country(2, "USA", "Washington"));

        when(countryrep.findAll()).thenReturn(mycountries); // Mocking
        assertEquals(2, countryService.getAllCountries().size());
    }

    @Test
    @Order(2)
    public void test_getCountrybyID() {
        Country country = new Country(1, "India", "Delhi");
        int countryID = 1;

        when(countryrep.findById(countryID)).thenReturn(Optional.of(country)); // Mocking
        assertEquals(countryID, countryService.getCountrybyID(countryID).getId());
        assertEquals("India", countryService.getCountrybyID(countryID).getCountryName());
    }

    @Test
    @Order(3)
    public void test_getCountrybyID_notFound() {
        when(countryrep.findById(99)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> countryService.getCountrybyID(99));
    }

    @Test
    @Order(4)
    public void test_getCountrybyName() {
        Country country = new Country(2, "USA", "Washington");
        String countryName = "USA";

        when(countryrep.findByCountryName(countryName)).thenReturn(country); // Mocking
        assertEquals("Washington", countryService.getCountrybyName(countryName).getCapital());
    }

    @Test
    @Order(5)
    public void test_addCountry() {
        Country country = new Country(3, "Germany", "Berlin");

        when(countryrep.save(country)).thenReturn(country); // Mocking
        assertEquals(country, countryService.addCountry(country));
    }

    @Test
    @Order(6)
    public void test_updateCountry() {
        Country existing = new Country(3, "Germany", "Berlin");
        Country newData = new Country(3, "Japan", "Tokyo");

        when(countryrep.findById(3)).thenReturn(Optional.of(existing));
        when(countryrep.save(existing)).thenReturn(existing);

        Country updated = countryService.updateCountry(3, newData);

        assertEquals("Japan", updated.getCountryName());
        assertEquals("Tokyo", updated.getCapital());
    }

    @Test
    @Order(7)
    public void test_deleteCountry() {
        countryService.deleteCountry(3);
        verify(countryrep, times(1)).deleteById(3);
    }
}