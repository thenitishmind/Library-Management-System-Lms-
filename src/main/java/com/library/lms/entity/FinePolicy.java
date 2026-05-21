package com.library.lms.entity;

import com.library.lms.entity.enums.MembershipType;
import javax.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "fine_policies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FinePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type", nullable = false, unique = true)
    private MembershipType membershipType;

    @Column(name = "daily_rate", nullable = false, precision = 6, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "max_fine", nullable = false, precision = 8, scale = 2)
    private BigDecimal maxFine;

    @Column(name = "grace_period_days")
    private Short gracePeriodDays;

    @Column(name = "max_loan_days")
    private Short maxLoanDays;

    @Column(name = "max_renewals")
    private Short maxRenewals;

    @Column(name = "max_active_loans")
    private Short maxActiveLoans;
}
