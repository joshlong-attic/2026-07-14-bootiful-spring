package com.example.dop;

public class DopApplication {

    public static void main(String[] args) {

    }

    static String displayMessageFor(double d ,Loan loan) {
        return switch (loan) {
            case WhateverLoan wl -> "Whatever";
            case SecuredLoan sl -> "Secured";
            case InsecureLoan(var interest) -> "ouch! that " + interest + " is going to hurt!";
        };
    }

}


final class SecuredLoan implements Loan {
}

record InsecureLoan(float interest) implements Loan {
}

final class WhateverLoan implements Loan {
}

sealed interface Loan
        permits WhateverLoan, SecuredLoan, InsecureLoan {
}


// DATA ORIENTED PROGRAMMING
// pattern matching
// smart switch
// sealed types
// records

// auto type inference (var)
// multiline strings

// streams
// lambdas
// generics
// collections
//