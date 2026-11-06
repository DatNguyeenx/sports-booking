package com.sportsbooking.dto;

import java.math.BigDecimal;

public record CourtListItem(
    Long id,
    String code,
    String sportName,
    BigDecimal pricePerSlot
) {
}
