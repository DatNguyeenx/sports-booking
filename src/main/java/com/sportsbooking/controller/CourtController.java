package com.sportsbooking.controller;

import com.sportsbooking.dto.CourtListItem;
import com.sportsbooking.dto.CourtSearchCriteria;
import com.sportsbooking.service.CourtService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @GetMapping("/courts")
    public String list(
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(required = false) Long sportTypeId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "codeAsc") String sort,
        Model model
    ) {
        CourtSearchCriteria criteria = new CourtSearchCriteria(
            keyword, sportTypeId, page, size, sort
        );
        Page<CourtListItem> result = courtService.searchActiveCourts(criteria);
        model.addAttribute("criteria", criteria);
        model.addAttribute("courtPage", result);
        model.addAttribute("courts", result.getContent());
        model.addAttribute("sportTypes", courtService.findSportTypes());
        return "courts/list";
    }
}
