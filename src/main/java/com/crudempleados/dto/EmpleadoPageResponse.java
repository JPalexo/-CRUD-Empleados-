package com.crudempleados.dto;

import java.util.List;

public class EmpleadoPageResponse {

    private List<EmpleadoResponse> data;
    private PaginationMetadata pagination;

    public EmpleadoPageResponse() {
    }

    public EmpleadoPageResponse(List<EmpleadoResponse> data, PaginationMetadata pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    public List<EmpleadoResponse> getData() {
        return data;
    }

    public void setData(List<EmpleadoResponse> data) {
        this.data = data;
    }

    public PaginationMetadata getPagination() {
        return pagination;
    }

    public void setPagination(PaginationMetadata pagination) {
        this.pagination = pagination;
    }
}