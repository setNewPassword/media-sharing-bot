package com.btard.dto;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MailParams {
    private String id;
    private String emailTo;
}