package org.iuh.fit;

public class File implements FileSystemComponent {
    private String name;

    public File(String name) {
        this.name = name;
    }

    @Override
    public void showInfo() {
        System.out.println("File: " + name);
    }
}

