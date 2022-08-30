package com.vslc.model;

public class Matrix {

    private String imgBase64;

    private int width;

    private int height;

    public Matrix() {

    }

    public Matrix(String imgBase64, int width, int height) {
        this.imgBase64 = imgBase64;
        this.width = width;
        this.height = height;
    }

    public String getImgBase64() {
        return imgBase64;
    }

    public void setImgBase64(String imgBase64) {
        this.imgBase64 = imgBase64;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
