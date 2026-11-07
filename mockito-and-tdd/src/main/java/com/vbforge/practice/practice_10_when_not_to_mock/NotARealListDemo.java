package com.vbforge.practice.practice_10_when_not_to_mock;

import java.util.ArrayList;
import java.util.List;

/** Don't mock what you can just use: a mocked List is not a list. */
public class NotARealListDemo {

    public static void main(String[] args) {
        MockList mock = new MockList();
        mock.add("x");
        System.out.println("mock : size = " + mock.size() + ", get(0) = " + mock.get(0)
                + ", add recorded = " + mock.recordedAdds());

        List<String> real = new ArrayList<String>();
        real.add("x");
        System.out.println("real : size = " + real.size() + ", get(0) = " + real.get(0));
    }
}
