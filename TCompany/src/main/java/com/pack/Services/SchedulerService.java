package com.pack.Services;

import com.pack.Entities.Tour;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Service
@RequiredArgsConstructor
@ManagedResource
public class SchedulerService {
    @Autowired
    TourService tourService;
    @Autowired
    DescriptionService descriptionService;

    @Scheduled(cron = "0 0/30 * * * *")
    @ManagedOperation
    public void deleteTour() throws ParseException {
        List<Tour> tours = tourService.getAll();
        Date currentDate = new Date();
        Date sDate;
        for (Tour t : tours) {
            sDate = new SimpleDateFormat("yyyy-MM-dd").parse(t.getDate());
            System.out.println(currentDate+"\n"+sDate);
            if (sDate.before(currentDate)){
                if(t.getUsers()!=null){
                    t.setCount(0);
                    tourService.save(t);
                }
                else {
                    descriptionService.delete(t.getDescription());
                    tourService.deleteTour(t.getId());
                }
            }
        }
    }
}