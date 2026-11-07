package com.vbforge.practice.set.practice_19_hashset_object_contract;

import java.util.HashSet;

/**
 * Set:
 *   1) unique (no same items)
 *   2) no index access   // set.get(i) does not exist — use iterator or for-each
 *   3) no order guarantee
 *
 * Details on "unique":
 *   What does unique mean? It is defined by the items' equals() and hashCode().
 *   For numbers and Strings — that is the natural, built-in identity.
 *   For Dog — the Dog's own equals() and hashCode() define uniqueness.
 *
 * This demo focuses on the Dog case: three distinct dogs are added, then a
 * fourth dog with the *same* age and name as one of them is added. Because
 * Dog overrides equals() and hashCode(), the fourth dog is considered a
 * duplicate and is NOT inserted.
 *
 * For generic HashSet basics (add/remove/size/contains/iterator/for-each),
 * see {@code practice_54_hashset_basics}.
 */
public class HashSetEqualsHashCodeDemo {

    public static void main(String[] args) {

        HashSet<Dog> dogSet = new HashSet<>();

        Dog dogA = new Dog();
        dogA.age=1;
        dogA.name="DogA";
        dogSet.add(dogA);

        Dog dogB = new Dog();
        dogB.age=2;
        dogB.name="DogB";
        dogSet.add(dogB);

        Dog dogC = new Dog();
        dogC.age=3;
        dogC.name="DogC";
        dogSet.add(dogC);

        System.out.println("dogSet.size()="+dogSet.size());
        System.out.println(dogSet);

        String text = "[";
        for(Object item: dogSet){
            text+=item.toString();
        }
        text+="]";
        System.out.println("text="+text);
        System.out.println("--------add another Dog");
        Dog dogD = new Dog();
        dogD.age=3;
        dogD.name="DogC";
        dogSet.add(dogD);
        System.out.println(dogSet);

        //what is equal in this set ?
        //dogD is logically equal to dogC (same age + name) => equals() returns true
        //and hashCode() is identical => HashSet refuses to add it
    }

    /**
     * Dog with custom equals/hashCode — the identity is (age, name).
     */
    static class Dog {
        public int age;
        public String name = null;

        public String toString(){
            return "(age="+age+"; name="+name+")";
        }

        public int hashCode(){
            int nameHashCode = 0;
            if(name!=null){
                nameHashCode = name.hashCode();
            }
            return age+nameHashCode;
        }

        public boolean equals(Object other){
            if(other==null){
                return false;
            }
            if(other==this){
                return true;
            }
            if(other.getClass()==Dog.class){//other.getClass()==this.getClass()
                Dog otherDog = (Dog) other;
                if(otherDog.age!=age){
                    return false;
                }
                if(name==null && otherDog.name==null){
                    return true;
                }
                if(name!=null){
                    return name.equals(otherDog.name);
                }
                //if(otherDog.name!=null){
                  //  return otherDog.name.equals(name);
               // }
                return false;
            }
            return false;
        }
    }
}
