package com.vbforge.practice.practice_3_deprecated_and_suppresswarnings;

public class DeprecatedAndSuppressWarningsDemo {

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        Child child = new Child();
        child.display();
    }
}

/*С @SuppressWarnings("deprecation") код проще читать: название метода display() не перечеркивается
и компилятор не выдает предупреждение об использовании устаревшего API.
Чтобы отключить несколько предупреждений, необходимо через запятую перечислить их
список, например, в следующем виде:

@SuppressWarnings({"unused", "deprecation"})*/
