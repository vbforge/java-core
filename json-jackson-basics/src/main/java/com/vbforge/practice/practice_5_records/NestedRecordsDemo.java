package com.vbforge.practice.practice_5_records;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/** Records inside records inside a List: Jackson follows the structure without any extra configuration. */
public class NestedRecordsDemo {

    public record Address(String city, String street) { }

    public record Member(String name, Address address) { }

    public record Team(String title, List<Member> members) { }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {"title":"Backend",
                 "members":[
                   {"name":"Alice","address":{"city":"Kyiv","street":"Main 1"}},
                   {"name":"Bob","address":{"city":"Lviv","street":"Side 2"}}
                 ]}""";

        Team team = mapper.readValue(json, Team.class);

        System.out.println("title            : " + team.title());
        System.out.println("members          : " + team.members().size());
        System.out.println("second city      : " + team.members().get(1).address().city());
        System.out.println("round trip equal : " + mapper.readTree(json).equals(mapper.readTree(mapper.writeValueAsString(team))));
    }
}
