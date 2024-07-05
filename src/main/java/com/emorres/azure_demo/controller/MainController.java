package com.emorres.azure_demo.controller;

import com.emorres.azure_demo.dao.UserRepository;
import com.emorres.azure_demo.model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final UserRepository repo;
    private final String SEPARATOR = "    |    ";

    @GetMapping("/")
    public String newEntry(HttpServletRequest request) {
        var ip = request.getRemoteAddr();
        var newUser = false;
        try {
            repo.save(User.builder().ip(ip).time(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))).build());
            newUser = true;
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate IP pinging: {}", ip);
        }
        var allEntries = repo.findAll();
        var totalUsers = allEntries.size();
        var builder = new StringBuilder();

        builder.append(createGreeting(ip, totalUsers));

        for(var entry: allEntries) {
            builder.append(buildEntry(entry));
        }
        return builder.toString();
    }

    private String createGreeting(String ip, int numberOfUsers) {
        return String.format("Hi! <b>%s</b>. You are the user number : <b>%d</b> on this page <br>", ip, numberOfUsers);
    }

    private char[] buildEntry(User entry) {
        return (SEPARATOR + entry.getId() + SEPARATOR + entry.getIp() + SEPARATOR + entry.getTime().toString() + SEPARATOR + "<br>").toCharArray();
    }

}
