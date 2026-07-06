package vn.edu.xyz.olms.service;

import vn.edu.xyz.olms.entity.LoanRecord;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class FineCalculator {

    private static final long FINE_PER_DAY_VND = 2000L;

    private FineCalculator() {}

    public static long calculate(LoanRecord loan) {
        LocalDate returnDate = loan.getReturnDate() != null ? loan.getReturnDate() : LocalDate.now();
        if (!returnDate.isAfter(loan.getDueDate())) {
            return 0L;
        }
        long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), returnDate);
        return overdueDays * FINE_PER_DAY_VND;
    }
}
