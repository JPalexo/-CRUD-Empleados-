package com.crudempleados.dto;

import java.util.List;

public class DepartamentoPageResponse {

    private List<DepartamentoResponse> data;
    private PaginationMetadata pagination;

    public DepartamentoPageResponse() {
    }

    public DepartamentoPageResponse(List<DepartamentoResponse> data, PaginationMetadata pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    public List<DepartamentoResponse> getData() {
        return data;
    }

    public void setData(List<DepartamentoResponse> data) {
        this.data = data;
    }

    public PaginationMetadata getPagination() {
        return pagination;
    }

    public void setPagination(PaginationMetadata pagination) {
        this.pagination = pagination;
    }
}