package com.vbforge.practice.practice_7_query_and_form_encoding;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * URLEncoder.encode(value, UTF_8) is FORM encoding: space becomes '+' (not %20), reserved
 * characters such as '&', '=' and '+' are percent-encoded, non-ASCII becomes UTF-8 bytes.
 */
public class UrlEncoderDemo {

    public static void main(String[] args) {
        String[] samples = {"java 11", "a&b=c", "c++", "café", "plain"};
        for (String s : samples) {
            System.out.println(String.format("%-8s -> %s", s, URLEncoder.encode(s, StandardCharsets.UTF_8)));
        }

        String encoded = URLEncoder.encode("a b", StandardCharsets.UTF_8);
        System.out.println("\"a b\" form-encoded   -> " + encoded);
        System.out.println("\"a b\" with %20 form  -> " + encoded.replace("+", "%20") + "  (when a URI path needs it)");
    }
}
