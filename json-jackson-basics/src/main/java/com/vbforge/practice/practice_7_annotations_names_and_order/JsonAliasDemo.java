package com.vbforge.practice.practice_7_annotations_names_and_order;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** @JsonAlias: extra names accepted on INPUT only; output always uses the primary name. */
public class JsonAliasDemo {

    public static class Account {
        @JsonAlias({"user_name", "login"})
        public String username;

        @Override
        public String toString() { return "Account[username=" + username + "]"; }
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        for (String json : new String[] {"{\"username\":\"alice\"}", "{\"user_name\":\"alice\"}", "{\"login\":\"alice\"}"}) {
            System.out.println(json + " -> " + mapper.readValue(json, Account.class));
        }

        Account account = new Account();
        account.username = "alice";
        System.out.println("written as: " + mapper.writeValueAsString(account));
    }
}
