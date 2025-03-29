package com.hotel.HotelMgmt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Testing Server", description = "Testing Server")
public class HelloController {

	@GetMapping("/")
	public String index() {
		return "Server is Up and Running!";
	}

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return "Hello " + name;
    }

}