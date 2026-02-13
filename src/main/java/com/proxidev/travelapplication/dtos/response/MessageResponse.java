package com.proxidev.travelapplication.dtos.response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private String message;
    private Object data;

    public MessageResponse(String message) {
        this.message = message;
    }
}