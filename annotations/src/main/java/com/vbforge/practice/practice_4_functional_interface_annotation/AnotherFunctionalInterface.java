package com.vbforge.practice.practice_4_functional_interface_annotation;

@FunctionalInterface
public interface AnotherFunctionalInterface extends MyFunctionalInterface {
    //void anotherAbstractMethod();
}

/*Если раскомментить: Ошибка вызвана тем, что мы, расширяя интерфейс MyFunctionalInterface,
наследуем абстрактный метод расширяемого интерфейса, и как результат, имеем два
абстрактных метода, что не совместимо с аннотацией @FunctionalInterface.

Таким образом, мы не сможем использовать интерфейс, помеченный аннотацией @FunctionalInterface
и включающей в себя явно или неявно два и более абстрактных метода*/
