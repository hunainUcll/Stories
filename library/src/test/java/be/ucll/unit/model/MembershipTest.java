package be.ucll.unit;

import be.ucll.model.Membership;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MembershipTest {

    @Test
    void bronzeMembershipAccepts0to5FreeLoans() {
        Membership m = new Membership(
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "BRONZE",
                4
        );
        assertEquals(4, m.getFreeLoans());
    }

    @Test
    void silverMembershipRejectsOutOfRangeFreeLoans() {
        assertThrows(RuntimeException.class, () ->
                new Membership(
                        LocalDate.now(),
                        LocalDate.now().plusYears(1),
                        "SILVER",
                        2              // < 6 is illegal for SILVER
                )
        );
    }

    @Test
    void redeemFreeLoanDecrementsCounter() {
        Membership m = new Membership(
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "GOLD",
                13
        );
        m.redeemFreeLoan();
        assertEquals(12, m.getFreeLoans());
    }

    @Test
    void redeemWhenNoLoansLeftThrows() {
        Membership m = new Membership(
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "BRONZE",
                0
        );
        assertThrows(RuntimeException.class, m::redeemFreeLoan);
    }
}
