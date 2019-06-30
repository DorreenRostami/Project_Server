package model;

import java.util.Arrays;

public class FileInfo {
    private byte[] fileBytes;
    private String fileName;

    public FileInfo(byte[] fileBytes, String fileName) {
        this.fileBytes = fileBytes;
        this.fileName = fileName;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo file = (FileInfo) o;
        return Arrays.equals(fileBytes, file.getFileBytes());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(fileBytes);
    }
}
