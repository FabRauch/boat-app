package com.openwt.boatapp.cucumber;

import com.openwt.boatapp.BoatApp;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = BoatApp.class)
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
