package com.example.demo

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/api/hello")
    fun hello(): String {
        return "Your Mom!"
    }
}
