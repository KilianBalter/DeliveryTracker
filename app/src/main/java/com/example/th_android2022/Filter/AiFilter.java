package com.example.th_android2022.Filter;

import com.example.th_android2022.Entities.Email;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class AiFilter {


    /**
     * every link in the email gets send to a server. There it gets evaluated by an ai model and the result gets returned.
     *
     * @param email {@link Email} to be filtered
     * @return likelihood of email being a tracking-email
     */
    public static double filter(Email email) {
        double result = 0.0;
        //TODO uncomment as soon as enough userdata has been collected on the server and an ai model has been trained
//        for(String link: LinkFilter.extractUrls(email.getContent())) {
//            try {
//                String ip = InetAddress.getLocalHost().getHostAddress().replace(".", "");
//
//                //upload data to be analysed by ai
//                URL url = new URL("http://172.16.146.7:5666/ai/data?email=test%40th-bingen.de&password=1234&dataName=" + ip);     //TODO replace with actual ip
//                URLConnection con = url.openConnection();
//                HttpURLConnection http = (HttpURLConnection) con;
//                http.setRequestMethod("POST"); // PUT is another valid option
//                http.setDoOutput(true);
//                http.setConnectTimeout(2000);
//                byte[] out = ("_," + getLinkContext(email, link)).getBytes(StandardCharsets.UTF_8);
//                System.out.println("evaluating  " + "_," + getLinkContext(email, email.getTrackingLink()));
//                int length = out.length;
//
//                http.setFixedLengthStreamingMode(length);
////            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                http.connect();
//                try (OutputStream os = http.getOutputStream()) {
//                    os.write(out);
//                }
//
//                //start ai
//                url = new URL("http://172.16.146.7:5666/ai/pipeline/Pipeline?email=test%40th-bingen.de&password=1234");     //TODO replace with actual ip
//                con = url.openConnection();
//                con.connect();
//
//                //get ai result
//                for (int i = 0; i < 5; i++) {
//                    try {
//                        url = new URL("http://172.16.146.7:5666/ai/data/" + ip + "_PipelineEval?email=test%40th-bingen.de&password=1234");     //TODO replace with actual ip
//                        con = url.openConnection();
//                        http = (HttpURLConnection) con;
//
//                        http.connect();
//
//                        if (!http.getResponseMessage().startsWith("true")) {
//                            email.setTrackingLink(link);
//                            return 0.75;
//                        }
//
//                    } catch (Exception e) {
//                        Thread.sleep(2000);
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return result;
    }

    /**
     * uploads the trackingLink of the email and the likelihood of isTrackingEmail to the ai server
     *
     * @param email           {@link Email} to be used as training data
     * @param isTrackingEmail likelihood of being tracking email
     */
    public static void train(Email email, boolean isTrackingEmail) {
        try {
            URL url = new URL("http://172.16.146.7:5666/ai/data?email=test%40th-bingen.de&password=1234&dataName=DeliveryTracker");     //TODO replace with actual ip
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);
            http.setConnectTimeout(2000);
            byte[] out = (isTrackingEmail + "," + getLinkContext(email, email.getTrackingLink())).getBytes(StandardCharsets.UTF_8);
            System.out.println("training ai with " + isTrackingEmail + "," + getLinkContext(email, email.getTrackingLink()));
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            }

            System.out.println(http.getResponseMessage());      //DON'T DELETE! Doesn't work without
            System.out.println(http.getResponseCode());

            System.out.println("done training");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * extracts important information from the email which can be used to train an ai.
     * extracted information are: 40 characters before the link and subject of the email
     * data gets cleaned of special characters
     *
     * @param email
     * @param link
     * @return special character free String
     */
    private static String getLinkContext(Email email, String link) {
        String text = email.getContent();
        int linkPosition = text.indexOf(link);
        String context = text.substring(linkPosition - 40, linkPosition);
        String data = email.getSubject() + " " + context;
        data = data.replaceAll("[^a-zA-Z]", "");
        System.out.println("link: " + link + "\n context: " + data);
        return data;
    }


    /**
     * Disables the SSL certificate checking for new instances of {@link HttpsURLConnection}
     */
    public static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
