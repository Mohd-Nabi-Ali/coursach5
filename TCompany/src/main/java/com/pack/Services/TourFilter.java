package com.pack.Services;

import com.pack.Entities.Tour;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Component
@Transactional
public class TourFilter {
    @PersistenceContext
    private EntityManager entity;
    @Autowired
    private TourService tourService;

    public List<Tour> filterTour(String start, String finish, String date, int count) {
        if(start.equals("") && finish.equals("") && date.equals("") && count<=0){
            return tourService.getAll();
        }
        CriteriaBuilder cb = entity.getCriteriaBuilder();
        CriteriaQuery<Tour> cq = cb.createQuery(Tour.class);
        Root<Tour> tour = cq.from(Tour.class);

        Predicate predicateForStart = cb.like(tour.get("start"), start);
        Predicate predicateForFinish = cb.like(tour.get("finish"), finish);
        Predicate predicateForDate = cb.like(tour.get("date"), date);
        Predicate predicateForCount = cb.ge(tour.get("count"), count);

        Predicate finalePredicate = null;
        if (!start.equals("")){
            finalePredicate = cb.and(predicateForStart);
        }
        if(!finish.equals("")){
            if (finalePredicate != null) finalePredicate = cb.and(finalePredicate,predicateForFinish);
            else finalePredicate = predicateForFinish;
        }
        if(!date.equals("")){
            if (finalePredicate != null) finalePredicate = cb.and(finalePredicate,predicateForDate);
            else finalePredicate = predicateForDate;
        }
        if(count!=0){
            if (finalePredicate != null) finalePredicate = cb.and(finalePredicate,predicateForCount);
            else finalePredicate = predicateForCount;
        }
        cq.where(finalePredicate);
        List<Tour> tours = entity.createQuery(cq).getResultList();
        tours.sort((o1, o2) -> {
            for (int i=0;i<o1.getDate().length();++i){
                if (o1.getDate().toCharArray()[i]<o2.getDate().toCharArray()[i]) return -1;
                else if (o1.getDate().toCharArray()[i]>o2.getDate().toCharArray()[i]) return 1;
            }
            return 0;
        });
        return tours;
    }
}