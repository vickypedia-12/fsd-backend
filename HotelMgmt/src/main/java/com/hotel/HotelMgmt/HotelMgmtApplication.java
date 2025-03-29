package com.hotel.HotelMgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class HotelMgmtApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelMgmtApplication.class, args);
	}

}
