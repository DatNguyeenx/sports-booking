package com.sportsbooking.controller;

import com.sportsbooking.service.CourtService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @GetMapping("/courts")
    public String list(Model model) {
        model.addAttribute("courts", courtService.findActiveCourts());
        return "courts/list";
    }
}
