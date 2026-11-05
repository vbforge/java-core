package com.vbforge.practice.practice_8_documented;

public class Tester {

    @TestDocumented(doTestDocument = "Document with annotation")
    public void doSomeTestDocument(){
        System.out.println("Test for annotation with create Document");
    }

    @TestNotDocumented(doTestNoDocument = "Document without annotation")
    public void doSomeTestNoDocument(){
        System.out.println("Test for annotation with no create Document");
    }
}

/*если запустить команду javadoc (или использовать IntellijIdea: Tools -> Generate JavaDoc…) и
просмотреть сгенерированный файл Tester.html, то для метода doSomeTestNoDocument() отсутствует
информация о типе аннотации, но эта информация предоставляется для метода doSomeTestDocument().
Причина этому @Documented, прикрепленная к аннотации @TestDocumented.
@TestNotDocumented не использует эту аннотацию.*/
