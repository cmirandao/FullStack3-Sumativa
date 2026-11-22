package com.duoc.foro.dto;

import java.time.LocalDateTime;

public class ApiResponse<T> {
    private boolean exito;
    private String mensaje;
    private T datos;
    private LocalDateTime timestamp;

    private ApiResponse(Builder<T> builder) {
        this.exito = builder.exito;
        this.mensaje = builder.mensaje;
        this.datos = builder.datos;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isExito() { return exito; }
    public String getMensaje() { return mensaje; }
    public T getDatos() { return datos; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public static class Builder<T> {
        private boolean exito;
        private String mensaje;
        private T datos;

        public Builder<T> exito(boolean exito) {
            this.exito = exito;
            return this;
        }

        public Builder<T> mensaje(String mensaje) {
            this.mensaje = mensaje;
            return this;
        }

        public Builder<T> datos(T datos) {
            this.datos = datos;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(this);
        }
    }
}