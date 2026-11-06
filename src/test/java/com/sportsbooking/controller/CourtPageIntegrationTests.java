package com.sportsbooking.controller;

import com.sportsbooking.dto.CourtListItem;
import com.sportsbooking.dto.CourtSearchCriteria;
import com.sportsbooking.entity.Court;
import com.sportsbooking.entity.SportType;
import com.sportsbooking.repository.CourtRepository;
import com.sportsbooking.repository.SportTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.jpa.open-in-view=false")
@AutoConfigureMockMvc
@Transactional
class CourtPageIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private SportTypeRepository sportTypeRepository;

    @Test
    @WithMockUser
    void displaysActiveCourtsWithSportAndFormattedPriceButHidesInactiveCourts()
            throws Exception {
        String suffix = uniqueSuffix();
        SportType sport = createSport(suffix);
        Court active = createCourt(sport, "TESTA" + suffix, "55000.00", true);
        Court inactive = createCourt(sport, "TESTI" + suffix, "55000.00", false);
        courtRepository.flush();

        mockMvc.perform(get("/courts").param("keyword", suffix).accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(view().name("courts/list"))
            .andExpect(content().string(containsString(active.getCode())))
            .andExpect(content().string(containsString(sport.getName())))
            .andExpect(content().string(containsString("55.000 đ")))
            .andExpect(content().string(not(containsString(inactive.getCode()))));
    }

    @Test
    @WithMockUser
    void combinesCaseInsensitiveKeywordAndSportFilterWithPriceSorting()
            throws Exception {
        String suffix = uniqueSuffix();
        SportType chosenSport = createSport("A" + suffix);
        SportType otherSport = createSport("B" + suffix);
        Court lowPrice = createCourt(chosenSport, "ABC" + suffix, "10000.00", true);
        Court highPrice = createCourt(chosenSport, "XYZ" + suffix, "90000.00", true);
        createCourt(otherSport, "OUT" + suffix, "150000.00", true);
        createCourt(chosenSport, "OFF" + suffix, "200000.00", false);
        courtRepository.flush();

        MvcResult result = mockMvc.perform(get("/courts")
                .param("keyword", "  " + suffix.toLowerCase() + "  ")
                .param("sportTypeId", chosenSport.getId().toString())
                .param("sort", "priceDesc")
                .accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andReturn();

        Page<CourtListItem> page = getPage(result);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(CourtListItem::code)
            .containsExactly(highPrice.getCode(), lowPrice.getCode());

        mockMvc.perform(get("/courts")
                .param("keyword", "abc" + suffix.toLowerCase())
                .param("sportTypeId", chosenSport.getId().toString())
                .accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(resultByCode -> assertThat(getPage(resultByCode).getContent())
                .extracting(CourtListItem::code).containsExactly(lowPrice.getCode()));
    }

    @Test
    @WithMockUser
    void paginatesResultsAndPreservesFiltersInNavigation() throws Exception {
        String prefix = "P" + uniqueSuffix();
        SportType sport = createSport(prefix);
        for (int i = 0; i < 7; i++) {
            createCourt(sport, prefix + i, "50000.00", true);
        }
        courtRepository.flush();

        MvcResult first = mockMvc.perform(get("/courts")
                .param("keyword", prefix)
                .param("sportTypeId", sport.getId().toString())
                .param("sort", "codeAsc")
                .param("size", "5")
                .accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("keyword=" + prefix)))
            .andExpect(content().string(containsString("sportTypeId=" + sport.getId())))
            .andExpect(content().string(containsString("sort=codeAsc")))
            .andExpect(content().string(containsString("size=5")))
            .andExpect(content().string(containsString("page=1")))
            .andReturn();

        assertThat(getPage(first).getTotalElements()).isEqualTo(7);
        assertThat(getPage(first).getContent()).hasSize(5);

        MvcResult last = mockMvc.perform(get("/courts")
                .param("keyword", prefix)
                .param("sportTypeId", sport.getId().toString())
                .param("page", "1")
                .param("size", "5")
                .accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andReturn();

        assertThat(getPage(last).getContent()).extracting(CourtListItem::code)
            .containsExactly(prefix + "5", prefix + "6");

        MvcResult beyondLast = mockMvc.perform(get("/courts")
                .param("keyword", prefix)
                .param("sportTypeId", sport.getId().toString())
                .param("page", "999")
                .accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andReturn();

        assertThat(getPage(beyondLast).getNumber()).isEqualTo(1);
        assertThat(getPage(beyondLast).getContent()).hasSize(2);
    }

    @Test
    @WithMockUser
    void treatsSqlLikeSpecialCharactersAsLiteralCodeCharacters() throws Exception {
        String suffix = uniqueSuffix();
        SportType sport = createSport(suffix);
        Court special = createCourt(sport, "W%_[" + suffix, "50000.00", true);
        createCourt(sport, "WABC" + suffix, "50000.00", true);
        courtRepository.flush();

        MvcResult result = mockMvc.perform(get("/courts")
                .param("keyword", "%_[")
                .param("sportTypeId", sport.getId().toString())
                .accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andReturn();

        assertThat(getPage(result).getContent()).extracting(CourtListItem::code)
            .containsExactly(special.getCode());
    }

    @Test
    @WithMockUser
    void displaysEmptyResultsAndNormalizesUnsupportedPagingAndSort()
            throws Exception {
        MvcResult result = mockMvc.perform(get("/courts")
                .param("keyword", "NONE" + uniqueSuffix())
                .param("page", "-2")
                .param("size", "99999")
                .param("sort", "notAProperty")
                .accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Không tìm thấy sân phù hợp")))
            .andReturn();

        assertThat(getPage(result).getTotalElements()).isZero();
        assertThat(getPage(result).getNumber()).isZero();
        assertThat(getPage(result).getSize()).isEqualTo(5);
        CourtSearchCriteria criteria = (CourtSearchCriteria)
            result.getModelAndView().getModel().get("criteria");
        assertThat(criteria.sort()).isEqualTo("codeAsc");
    }

    @Test
    void requiresLoginWithCurrentSecurityConfiguration() throws Exception {
        mockMvc.perform(get("/courts").accept(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
    }

    @SuppressWarnings("unchecked")
    private Page<CourtListItem> getPage(MvcResult result) {
        return (Page<CourtListItem>) result.getModelAndView().getModel().get("courtPage");
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private SportType createSport(String suffix) {
        return sportTypeRepository.save(new SportType("Môn kiểm thử " + suffix));
    }

    private Court createCourt(SportType sport, String code, String price, boolean active) {
        Court court = new Court();
        court.setSportType(sport);
        court.setCode(code);
        court.setPricePerSlot(new BigDecimal(price));
        court.setActive(active);
        return courtRepository.save(court);
    }
}
