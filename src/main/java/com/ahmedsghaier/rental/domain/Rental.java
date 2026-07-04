package com.ahmedsghaier.rental.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * A rental record: a single {@link Product} rented by a {@link Customer} for a period.
 *
 * <p>The object carries a few denormalised display fields ({@code customerName},
 * {@code productLabel}, {@code dailyPrice}) so that list/table views and invoices can be
 * rendered from a single join without additional look-ups. The rental duration and total
 * price are derived on demand rather than stored.</p>
 */
public class Rental {

    private int id;

    private int customerId;
    private String customerName;

    private int productId;
    private String productLabel;
    private BigDecimal dailyPrice = BigDecimal.ZERO;

    private RentalStatus status = RentalStatus.RENTED;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public Rental() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductLabel() {
        return productLabel;
    }

    public void setProductLabel(String productLabel) {
        this.productLabel = productLabel;
    }

    public BigDecimal getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(BigDecimal dailyPrice) {
        this.dailyPrice = dailyPrice == null ? BigDecimal.ZERO : dailyPrice;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    /**
     * @return the number of rental days between {@code dateFrom} and {@code dateTo}
     *         (never negative), or {@code 0} if either date is missing.
     */
    public long getDays() {
        if (dateFrom == null || dateTo == null) {
            return 0;
        }
        return Math.max(0, ChronoUnit.DAYS.between(dateFrom, dateTo));
    }

    /** @return {@code dailyPrice * days}, rounded to the underlying price scale. */
    public BigDecimal getTotal() {
        return dailyPrice.multiply(BigDecimal.valueOf(getDays()));
    }

    /** Factory: build a fresh {@code RENTED} rental for a product over a period. */
    public static Rental of(int customerId, Product product, LocalDate from, LocalDate to) {
        Rental rental = new Rental();
        rental.customerId = customerId;
        rental.productId = product.getId();
        rental.productLabel = product.getLabel();
        rental.dailyPrice = product.getDailyPrice();
        rental.dateFrom = from;
        rental.dateTo = to;
        rental.status = RentalStatus.RENTED;
        return rental;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Rental rental)) {
            return false;
        }
        return id == rental.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
