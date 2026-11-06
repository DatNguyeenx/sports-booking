package com.sportsbooking.service;

import com.sportsbooking.dto.CourtListItem;
import com.sportsbooking.repository.CourtRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourtService {

    private final CourtRepository courtRepository;

    public CourtService(CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    @Transactional(readOnly = true)
    public List<CourtListItem> findActiveCourts() {
        return courtRepository.findByActiveTrueOrderByCodeAsc().stream()
            .map(court -> new CourtListItem(
                court.getId(),
                court.getCode(),
                court.getSportType().getName(),
                court.getPricePerSlot()
            ))
            .toList();
    }
}
