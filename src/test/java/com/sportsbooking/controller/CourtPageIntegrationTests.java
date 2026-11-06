package com.sportsbooking.controller;

import com.sportsbooking.entity.Court;
import com.sportsbooking.entity.SportType;
import com.sportsbooking.repository.CourtRepository;
import com.sportsbooking.repository.SportTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.jpa.open-in-view=false")
@AutoConfigureMockMvc
class CourtPageIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private SportTypeRepository sportTypeRepository;

    @Test
    @WithMockUser
    @Transactional
    void displaysActiveCourtsWithSportAndFormattedPriceButHidesInactiveCourts()
            throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        SportType sport = sportTypeRepository.save(
            new SportType("Môn kiểm thử " + suffix)
        );

        Court active = createCourt(sport, "TESTA" + suffix, true);
        Court inactive = createCourt(sport, "TESTI" + suffix, false);
        courtRepository.flush();

        mockMvc.perform(get("/courts").accept(org.springframework.http.MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(view().name("courts/list"))
            .andExpect(content().string(containsString(active.getCode())))
            .andExpect(content().string(containsString(sport.getName())))
            .andExpect(content().string(containsString("55.000 đ")))
            .andExpect(content().string(not(containsString(inactive.getCode()))));
    }

    @Test
    void requiresLoginWithCurrentSecurityConfiguration() throws Exception {
        mockMvc.perform(get("/courts").accept(org.springframework.http.MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
    }

    private Court createCourt(SportType sport, String code, boolean active) {
        Court court = new Court();
        court.setSportType(sport);
        court.setCode(code);
        court.setPricePerSlot(new BigDecimal("55000.00"));
        court.setActive(active);
        return courtRepository.save(court);
    }
}
