package com.pack.Requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourCreateRequest {
    String start;
    String finish;
    int price;
    String date;
    int count;
    String img;
    String text;
}
