package com.vbforge.practice.practice_2_override_annotation;

public class Child extends Parent {
    @Override
    public void display() {
        System.out.println("Child.class--display()");
    }
}

/*Применяя аннотацию @Override, мы даем задание компилятору проверять соответствие сигнатуры
* метода класса-наследника методу родителя. Если в имени метода Child допустить «опечатку»
* (например, displey()), то без @Override код скомпилируется и будет вызван метод Parent,
* а с @Override компилятор сразу сообщит об ошибке — так устраняются ошибки «по невнимательности».*/
