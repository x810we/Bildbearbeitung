package Test;
/*
 * Beispielanwendung logische Operatoren in Java.
 */

public class LogischeOperatoren{
    public static void main(String[] args) {

        boolean a = true, b = true, c = false;
        boolean ergebnis;

        System.out.println("\na = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        ergebnis = !a;
        System.out.println("\nNicht a:   !a = " + ergebnis);

        ergebnis = a ^ b;
        System.out.println("\nExklusiv Oder:   a ^ b = " + ergebnis);

        ergebnis = a ^ c;
        System.out.println("Exklusiv Oder:   a ^ c = " + ergebnis);

        ergebnis = a & b;
        System.out.println("\nUnd:   a & b = " + ergebnis);

        ergebnis = a & c;
        System.out.println("Und:   a & c = " + ergebnis);

        ergebnis = a | b;
        System.out.println("\nOder:      a | b = " + ergebnis);

        ergebnis = a | c;
        System.out.println("Oder:      a | c = " + ergebnis);

        ergebnis = (!a) | c;
        System.out.println("Oder:   (!a) | c = " + ergebnis);

        ergebnis = a && b;
        System.out.println("\nUnd (S-C-E):   a && b = " + ergebnis);

        ergebnis = a && c;
        System.out.println("Und (S-C-E):   a && c = " + ergebnis);

        ergebnis = a || b;
        System.out.println("\nOder (S-C-E):      a || b = " + ergebnis);

        ergebnis = a || c;
        System.out.println("Oder (S-C-E):      a || c = " + ergebnis);

        ergebnis = (!a) || c;
        System.out.println("Oder (S-C-E):   (!a) || c = " + ergebnis);

    }
}