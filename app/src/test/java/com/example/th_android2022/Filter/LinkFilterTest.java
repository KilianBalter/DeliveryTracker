package com.example.th_android2022.Filter;

import static org.junit.Assert.*;

import com.example.th_android2022.Entities.Email;

import org.junit.Test;

public class LinkFilterTest {

    @Test
    public void test(){
        System.out.println("test");
        String test =  " I  Meine Bestellungen\n" +
                "2023-02-04 12:58:43.092 17718-17802 System.out              com.example.th_android2022           I  <https://www.amazon.de/gp/r.html?C=3KS82OZ9FHPVW&K=2NRCOE7PV6VGD&M=urn:rtn:msg:20220805120743354596efe7274814864538f30b40p0eu&R=2JSNZCXTAOVB7&T=C&U=https%3A%2F%2Fwww.amazon.de%2Fgp%2Fcss%2Fyour-orders-access%2Fref%3Dpe_27091401_487027711_TE_SCE_oh_tn&H=VRALPTZN76EMSSIGMTX0C823NVAA&ref_=pe_27091401_487027711_TE_SCE_oh_tn>\n" +
                "2023-02-04 12:58:43.095 17718-17802 System.out              com.example.th_android2022           I    |   Mein Konto";

        LinkFilter f = new LinkFilter();
        Email e = new Email(null, null, test, null, null);
        f.filter(e);

    }

}