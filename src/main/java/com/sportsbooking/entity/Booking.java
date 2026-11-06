package com.sportsbooking.entity;

import com.sportsbooking.enums.BookingStatus;
import com.sportsbooking.enums.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "bookings", schema = "dbo")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sport_type_id", nullable = false)
    private SportType sportType;

    @Column(name = "play_date", nullable = false)
    private LocalDate playDate;

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 18,
            scale = 2
    )
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false,
            columnDefinition = "DATETIME2(0)"
    )
    private LocalDateTime createdAt;

    @Column(name = "paid_at", columnDefinition = "DATETIME2(0)")
    private LocalDateTime paidAt;

    @OneToMany(
            mappedBy = "booking",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private List<BookingDetail> details = new ArrayList<>();

    public Booking() {
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now().withNano(0);
        }
    }

    public BookingDetail addDetail(
            CourtSchedule schedule,
            BigDecimal unitPrice
    ) {
        BookingDetail detail = new BookingDetail();
        detail.setBooking(this);
        detail.setSchedule(schedule);
        detail.setUnitPrice(unitPrice);

        details.add(detail);
        return detail;
    }

    public BigDecimal calculateTotal() {
        return details.stream()
                .map(BookingDetail::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public SportType getSportType() {
        return sportType;
    }

    public void setSportType(SportType sportType) {
        this.sportType = sportType;
    }

    public LocalDate getPlayDate() {
        return playDate;
    }

    public void setPlayDate(LocalDate playDate) {
        this.playDate = playDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public List<BookingDetail> getDetails() {
        return Collections.unmodifiableList(details);
    }
}