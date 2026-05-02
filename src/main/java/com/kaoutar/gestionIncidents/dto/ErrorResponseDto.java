package com.kaoutar.gestionIncidents.dto;

import lombok.Data;

@Data
public class ErrorResponseDto {
        private int status;
        private String message;
        private long timestamp;

        public ErrorResponseDto(int status, String message) {
            this.status = status;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

    }

