package com.vslc.model;

public class Page {

    private Integer page;

    private Integer rows;

    private Integer firstPage;

    public Page(Integer page, Integer rows) {
        this.page = page;
        this.rows = rows;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getFirstPage() {
        firstPage = (page - 1) * rows;
        return firstPage;
    }

    @Override
    public String toString() {
        return "Page{" +
                "page=" + page +
                ", rows=" + rows +
                ", firstPage=" + firstPage +
                '}';
    }
}
