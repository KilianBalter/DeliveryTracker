package com.example.th_android2022.Filter;

import com.example.th_android2022.Entities.Email;

import java.util.regex.Pattern;

public class RegexFilter {
    private final static Pattern PACKAGE_PATTERN_EN = Pattern.compile("(package|deliver|delivered|track|ship|arrive|arriving|order|dispatched)", Pattern.CASE_INSENSITIVE);
    private final static Pattern PACKAGE_PATTERN_DE = Pattern.compile("(paket|lieferung|zustellung|zugestellt|geliefert|verfolge|verfolgung|unterwegs|auf dem weg|ankommen|angekommen|bestellung|bestellt|versand)", Pattern.CASE_INSENSITIVE);

    public static boolean filter(Email email) {
        String full = email.getSubject() + email.getContent();
        return PACKAGE_PATTERN_EN.matcher(full).matches() || PACKAGE_PATTERN_DE.matcher(full).matches();
    }
}
