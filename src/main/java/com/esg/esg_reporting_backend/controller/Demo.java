package com.esg.esg_reporting_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Demo {
	
	@GetMapping("/hi")
	public String hi() {
		return "hi this is springboot";
	}
}
