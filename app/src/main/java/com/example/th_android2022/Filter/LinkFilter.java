package com.example.th_android2022.Filter;

import com.example.th_android2022.Entities.Email;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkFilter {

    /**
     * Returns a list with all links contained in the input
     */
    public static List<String> extractUrls(String text)
    {
        List<String> containedUrls = new ArrayList<>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    boolean checkForTrackingLink(String link){
        Pattern pattern = Pattern.compile(
                ".*www\\.amazon\\..*ship-track?.*|" +
                        ".*www\\.amazon\\..*shiptrack.*|" +
                        ".*www\\.dhl\\..*|" +
                        ".*nolp\\.dhl\\..*|" +
                        ".*mailing4\\.dhl\\..*|" +
                        ".*www\\.ups\\..*|" +
                        ".*www\\.dpd\\..*|" +
                        ".*tracking\\.dpd\\..*|" +
                        ".*www\\.myhermes\\..*|" +
                        ".*parcel-api\\.delivery.*", Pattern.CASE_INSENSITIVE);

        Matcher urlMatcher = pattern.matcher(link);

        return urlMatcher.find();
    }

    boolean checkForPasswordProtection(String document){
        Pattern pattern = Pattern.compile(
                "password|" +
                        "login|" +
                        "register", Pattern.CASE_INSENSITIVE);

        Matcher urlMatcher = pattern.matcher(document);

        return urlMatcher.find();
    }

    public double filter(Email email){
        String content = email.getContent();
        double isTrackingEmail = 0.0;

        List<String> links = extractUrls(content);

        for (String link: links) {
            System.out.println("checking link: " + link);

            if(checkForTrackingLink(link)){
                System.out.println("email contains tracking link");
                isTrackingEmail = 1;
                email.setTrackingLink(link);
            }
            else {
                try {
                    Connection conn = Jsoup.connect(link).userAgent("Opera");

                    Document doc = conn.get();

                    String redirectedLink = conn.followRedirects(true).execute().url().toString();
                    if (checkForTrackingLink(redirectedLink)) {
                        System.out.println("email contains indirect tracking link");
                        isTrackingEmail = 1;
                        email.setTrackingLink(redirectedLink);
                    }
//                else if (checkForPasswordProtection(doc.toString())){
//                    System.out.println("email link is password protected");
//                    isTrackingEmail = Math.max(isTrackingEmail, 0.5);
//                }
                    else {
                        Elements result = doc.select("a[href]");
                        List<String> linksOnWebsite = extractUrls(result.toString());
                        for (String websiteLink : linksOnWebsite) {
                            if (checkForTrackingLink(websiteLink)) {
                                isTrackingEmail = 1;
                                System.out.println("email contains link which redirects to website with tracking link");
                                email.setTrackingLink(websiteLink);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(isTrackingEmail == 1.0){
                return isTrackingEmail;
            }

        }

        return isTrackingEmail;
    }
}
