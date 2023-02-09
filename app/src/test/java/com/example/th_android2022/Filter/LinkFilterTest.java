package com.example.th_android2022.Filter;

import com.example.th_android2022.Entities.Email;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


public class LinkFilterTest {

    LinkFilter filter;

    @BeforeAll
    @Tag("setup")
    @DisplayName("setup")
    public void setup(){
        this.filter = new LinkFilter();
    }

    @ParameterizedTest(name="{index} input: {0}")
    @ValueSource(strings = {
            "2023-02-04 12:58:43.092 17718-17802 <https://www.amazon.de/gp/r.html?C=3KS82OZ9FHPVW&K=2NRCOE7PV6VGD&M=urn:rtn:msg:20220805120743354596efe7274814864538f30b40p0eu&R=2JSNZCXTAOVB7&T=C&U=https%3A%2F%2Fwww.amazon.de%2Fgp%2Fcss%2Fyour-orders-access%2Fref%3Dpe_27091401_487027711_TE_SCE_oh_tn&H=VRALPTZN76EMSSIGMTX0C823NVAA&ref_=pe_27091401_487027711_TE_SCE_oh_tn>",
            "2023-02-04 12:58:43.095 17718-17802 com.example.th_android2022 Mein Konto",
            "eider konnten wir Ihr pedag International Paket nicht persönlich übergeben. Es wird für Sie in eine Filiale geliefert. https://mailing4.dhl.de/go/pdzs1pyjb2khcl84daktj448gqbsntpzffiso4ssc1vo/7?t_id=1026383736"
    })
    public void stringTest(String s){
        Email e = new Email(null, null, s, null, null);
        Assertions.assertEquals(1.0, filter.filter(e), "String contains tracking link");
    }

    @Test
    @DisplayName("repeated Test")
    @RepeatedTest(3)
    public void textTest(){
        System.out.println("test");
        String test =  " I  Meine Bestellungen\n" +
                "2023-02-04 12:58:43.092 17718-17802 <https://www.amazon.de/gp/r.html?C=3KS82OZ9FHPVW&K=2NRCOE7PV6VGD&M=urn:rtn:msg:20220805120743354596efe7274814864538f30b40p0eu&R=2JSNZCXTAOVB7&T=C&U=https%3A%2F%2Fwww.amazon.de%2Fgp%2Fcss%2Fyour-orders-access%2Fref%3Dpe_27091401_487027711_TE_SCE_oh_tn&H=VRALPTZN76EMSSIGMTX0C823NVAA&ref_=pe_27091401_487027711_TE_SCE_oh_tn>\n" +
                "2023-02-04 12:58:43.095 17718-17802 com.example.th_android2022 Mein Konto";

        Email e = new Email(null, null, test, null, null);
        Assertions.assertEquals(1.0, filter.filter(e), "Text contains tracking link");
    }
}