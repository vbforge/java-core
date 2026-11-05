package com.vbforge.practice.practice_10_repeatable;

/**
 * @Repeatable — the same annotation applied several times to one element.
 *
 * когда необходимо повторно применить одну и ту же аннотацию к какому-то элементу
 * (объявлению класса, интерфейсу, полю или к используемому типу)
 *
 * Scenes:
 *   1. explicit container  - @Games({ @Game, @Game, @Game }) read via getAnnotation(Games.class)
 *   2. repeated @Game      - read via the container, getAnnotation(Games.class)
 *   3. repeated @Game      - read via getAnnotationsByType(Game.class)
 */
public class RepeatableAnnotationDemo {

    // scene 1 - container written out by hand
    @Games({
            @Game(name = "Match Day", day = "Sunday"),
            @Game(day = "Thursday"),
            @Game(name = "Rest Day", day = "Monday")
    })
    static class Ex1ExplicitContainer { }

    // scenes 2 / 3 - annotation simply repeated, compiler wraps it into @Games
    @Game(name = "Match Day", day = "Sunday")
    @Game(day = "Thursday")
    @Game(name = "Rest Day", day = "Monday")
    static class Ex2RepeatedAnnotations { }

    public static void main(String[] args) {

        System.out.println("=== Scene 1: explicit container ===");
        Games games1 = Ex1ExplicitContainer.class.getAnnotation(Games.class);
        for (Game game : games1.value()) {
            System.out.println(game.name() + " on " + game.day());
        }

        System.out.println("\n=== Scene 2: repeated annotation, read via container ===");
        Games games2 = Ex2RepeatedAnnotations.class.getAnnotation(Games.class);
        for (Game game : games2.value()) {
            System.out.println(game.name() + " on " + game.day());
        }

        System.out.println("\n=== Scene 3: getAnnotationsByType ===");
        Game[] arrayGames = Ex2RepeatedAnnotations.class.getAnnotationsByType(Game.class);
        for (Game game : arrayGames) {
            System.out.println(game.name() + " on " + game.day());
        }
    }
}
