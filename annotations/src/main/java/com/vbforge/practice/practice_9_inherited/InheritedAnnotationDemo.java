package com.vbforge.practice.practice_9_inherited;

import java.lang.annotation.Annotation;

/**
 * @Inherited in action (merged from the old Inherited/Main and MetaAnnotationsDemo scene 3).
 *   1. Parent / Child  - @InheritedAnnotation is visible on Child, @NonInheritedAnnotation is not
 *   2. BaseService / SubService - @Role is @Inherited, so the subclass sees it too
 */
public class InheritedAnnotationDemo {

    public static void main(String[] args) {
        scene1ParentAndChild();
        scene2RoleInheritance();
    }

    // -----------------------------------------------------------------------
    // Scene 1 - which annotations does Child see?
    // -----------------------------------------------------------------------
    static void scene1ParentAndChild() {
        System.out.println("=== Scene 1: Parent / Child ===");
        Parent parent = new Parent();
        Child child = new Child();

        //Parent.class
        if (parent.getClass().getAnnotations().length>0){
            System.out.println("For Parent.class available this annotations:");
            for (Annotation annotation: parent.getClass().getAnnotations()){
                System.out.println(annotation);
            }
        }else {
            System.out.println("Parent.class has no annotations!");
        }
        //Child.class
        if (child.getClass().getAnnotations().length>0){
            System.out.println("\nFor Child.class available this annotations:");
            for (Annotation annotation: child.getClass().getAnnotations()){
                System.out.println(annotation);
            }
        }else {
            System.out.println("\nChild.class has no annotations!");
        }
    }

    // -----------------------------------------------------------------------
    // Scene 2 - @Role on BaseService is inherited by SubService
    // -----------------------------------------------------------------------
    static void scene2RoleInheritance() {
        System.out.println("\n=== Scene 2: @Inherited — annotation inheritance ===\n");

        // @Role is @Inherited — SubService should inherit it from BaseService
        Role baseRole = BaseService.class.getAnnotation(Role.class);
        Role subRole  = SubService.class.getAnnotation(Role.class);

        System.out.println("@Role on BaseService : " + (baseRole != null ? baseRole.value() : "absent"));
        System.out.println("@Role on SubService  : " + (subRole  != null ? subRole.value()  : "absent"));
        System.out.println("SubService has no @Role of its own, but inherits it: " +
                           (subRole != null && subRole.equals(baseRole)));

        System.out.println();
    }
}
