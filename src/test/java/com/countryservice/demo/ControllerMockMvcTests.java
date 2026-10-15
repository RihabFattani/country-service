package com.countryservice.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.countryservice.demo.beans.Country;
import com.countryservice.demo.controllers.CountryController;
import com.countryservice.demo.services.CountryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = { ControllerMockMvcTests.class })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan(basePackages = "com.countryservice.demo")
@AutoConfigureMockMvc
public class ControllerMockMvcTests {

    @Autowired
    MockMvc mockMvc;

    @Mock
    CountryService countryService;

    @InjectMocks
    CountryController countryController;

    public List<Country> mycountries;
    Country country;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(countryController).build();
    }

    @Test
    @Order(1)
    public void test_getAllCountries() throws Exception {
        mycountries = new ArrayList<Country>();
        mycountries.add(new Country(1, "India", "Delhi"));
        mycountries.add(new Country(2, "USA", "Washington"));

        when(countryService.getAllCountries()).thenReturn(mycountries); // Mocking

        this.mockMvc.perform(get("/getcountries"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(print());
    }

    @Test
    @Order(2)
    public void test_getCountryByID() throws Exception {
        country = new Country(1, "India", "Delhi");
        int countryID = 1;

        when(countryService.getCountrybyID(countryID)).thenReturn(country); // Mocking

        this.mockMvc.perform(get("/getcountries/{id}", countryID))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.countryName").value("India"))
                .andExpect(jsonPath("$.capital").value("Delhi"))
                .andDo(print());
    }

    @Test
    @Order(3)
    public void test_getCountryByID_notFound() throws Exception {
        when(countryService.getCountrybyID(99)).thenThrow(new NoSuchElementException());

        this.mockMvc.perform(get("/getcountries/{id}", 99))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @Order(4)
    public void test_getCountryByName() throws Exception {
        country = new Country(2, "USA", "Washington");
        String countryName = "USA";

        when(countryService.getCountrybyName(countryName)).thenReturn(country); // Mocking

        this.mockMvc.perform(get("/getcountries/countryname").param("name", countryName))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.countryName").value("USA"))
                .andExpect(jsonPath("$.capital").value("Washington"))
                .andDo(print());
    }

    @Test
    @Order(5)
    public void test_getCountryByName_notFound() throws Exception {
        when(countryService.getCountrybyName("Atlantis")).thenReturn(null);

        this.mockMvc.perform(get("/getcountries/countryname").param("name", "Atlantis"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @Order(6)
    public void test_addCountry() throws Exception {
        country = new Country(3, "Germany", "Berlin");

        when(countryService.addCountry(any(Country.class))).thenReturn(country); // Mocking

        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(country);

        this.mockMvc.perform(post("/addcountry")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.countryName").value("Germany"))
                .andExpect(jsonPath("$.capital").value("Berlin"))
                .andDo(print());
    }

    @Test
    @Order(7)
    public void test_updateCountry() throws Exception {
        country = new Country(3, "Japan", "Tokyo");
        int countryID = 3;

        when(countryService.updateCountry(eq(countryID), any(Country.class))).thenReturn(country); // Mocking

        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(country);

        this.mockMvc.perform(put("/updatecountry/{id}", countryID)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryName").value("Japan"))
                .andExpect(jsonPath("$.capital").value("Tokyo"))
                .andDo(print());
    }

    @Test
    @Order(8)
    public void test_deleteCountry() throws Exception {
        int countryID = 3;

        this.mockMvc.perform(delete("/deletecountry/{id}", countryID))
                .andExpect(status().isOk())
                .andDo(print());
    }
}