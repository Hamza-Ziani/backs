package com.veviosys.vdigit.classes;

import java.util.List;

import com.veviosys.vdigit.models.User;

import lombok.Data;

@Data
public class EditEtapeClass {

    private String comm;
    private String quality;
    private Long idSkiped;
    private List<UserClassEtape> users;
}
