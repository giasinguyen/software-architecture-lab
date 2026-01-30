package org.iuh.fit;

public class Main {
    public static void main(String[] args) {
        File f1 = new File("a.txt");
        File f2 = new File("b.txt");

        Folder root = new Folder("root");
        Folder sub = new Folder("sub");

        sub.add(f1);
        root.add(sub);
        root.add(f2);

        root.showInfo();
    }
}
