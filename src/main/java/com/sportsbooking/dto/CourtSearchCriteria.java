package com.sportsbooking.dto;

public record CourtSearchCriteria(
    String keyword,
    Long sportTypeId,
    int page,
    int size,
    String sort
) {
    public CourtSearchCriteria {
        keyword = keyword == null ? "" : keyword.strip();
        if (keyword.length() > 50) {
            keyword = keyword.substring(0, 50);
        }
        if (sportTypeId != null && sportTypeId <= 0) {
            sportTypeId = null;
        }
        page = Math.max(0, page);
        size = switch (size) {
            case 5, 10, 20 -> size;
            default -> 5;
        };
        sort = switch (sort == null ? "" : sort) {
            case "codeDesc", "priceAsc", "priceDesc" -> sort;
            default -> "codeAsc";
        };
    }
}
