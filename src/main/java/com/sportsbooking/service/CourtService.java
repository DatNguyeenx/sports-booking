package com.sportsbooking.service;

import com.sportsbooking.dto.CourtListItem;
import com.sportsbooking.dto.CourtSearchCriteria;
import com.sportsbooking.dto.SportTypeOption;
import com.sportsbooking.entity.Court;
import com.sportsbooking.repository.CourtRepository;
import com.sportsbooking.repository.SportTypeRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CourtService {

    private final CourtRepository courtRepository;
    private final SportTypeRepository sportTypeRepository;

    public CourtService(
        CourtRepository courtRepository,
        SportTypeRepository sportTypeRepository
    ) {
        this.courtRepository = courtRepository;
        this.sportTypeRepository = sportTypeRepository;
    }

    @Transactional(readOnly = true)
    public Page<CourtListItem> searchActiveCourts(CourtSearchCriteria criteria) {
        Specification<Court> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.isTrue(root.get("active")));

            if (criteria.sportTypeId() != null) {
                predicates.add(builder.equal(
                    root.get("sportType").get("id"), criteria.sportTypeId()
                ));
            }

            if (!criteria.keyword().isEmpty()) {
                String pattern = "%" + escapeLike(
                    criteria.keyword().toLowerCase(Locale.ROOT)
                ) + "%";
                predicates.add(builder.like(
                    builder.lower(root.get("code")), pattern, '\\'
                ));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };

        Sort sort = resolveSort(criteria.sort()).and(Sort.by("id").ascending());
        Page<Court> result = courtRepository.findAll(
            specification, PageRequest.of(criteria.page(), criteria.size(), sort)
        );

        if (result.getNumber() > 0 && result.isEmpty()) {
            int lastPage = Math.max(0, result.getTotalPages() - 1);
            result = courtRepository.findAll(
                specification, PageRequest.of(lastPage, criteria.size(), sort)
            );
        }

        return result.map(court -> new CourtListItem(
            court.getId(),
            court.getCode(),
            court.getSportType().getName(),
            court.getPricePerSlot()
        ));
    }

    @Transactional(readOnly = true)
    public List<SportTypeOption> findSportTypes() {
        return sportTypeRepository.findAll(Sort.by("name").ascending()).stream()
            .map(sport -> new SportTypeOption(sport.getId(), sport.getName()))
            .toList();
    }

    private Sort resolveSort(String sort) {
        return switch (sort) {
            case "codeDesc" -> Sort.by("code").descending();
            case "priceAsc" -> Sort.by("pricePerSlot").ascending();
            case "priceDesc" -> Sort.by("pricePerSlot").descending();
            default -> Sort.by("code").ascending();
        };
    }

    private String escapeLike(String value) {
        return value.replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")
            .replace("[", "\\[");
    }
}
