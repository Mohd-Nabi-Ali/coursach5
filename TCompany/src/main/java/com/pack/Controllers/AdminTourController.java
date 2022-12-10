package com.pack.Controllers;

import com.pack.Entities.Description;
import com.pack.Entities.Tour;
import com.pack.Entities.User;
import com.pack.Requests.DeleteRequest;
import com.pack.Requests.EditRequest;
import com.pack.Requests.TourCreateRequest;
import com.pack.Requests.TourFilterRequest;
import com.pack.Services.DescriptionService;
import com.pack.Services.TourFilter;
import com.pack.Services.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;

@Controller
public class AdminTourController {
    @Autowired
    TourService tourService;

    @Autowired
    DescriptionService tourDescriptionService;

    @Autowired
    TourFilter tourFilter;

    @GetMapping("/admin/add")
    public String initAdd(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("nickname",user.getUsername());
        return "create";
    }

    @PostMapping("/admin/add")
    public String addTour(@Valid TourCreateRequest tourForm, BindingResult bindingResult, Model model) throws ParseException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tourError", "Неверно введены данные");
            return "create";
        }

        if (tourForm.getStart().equals("") ||tourForm.getFinish().equals("")
                ||tourForm.getPrice()<=0||tourForm.getDate().equals("")
                ||tourForm.getCount()<=0||tourForm.getImg().length()<=32||tourForm.getText().length()<=32)
        {
            model.addAttribute("tourError", "Неверно введены данные");
            return "create";
        }

        Tour tour = new Tour();
        tour.setStart(tourForm.getStart());
        tour.setFinish(tourForm.getFinish());
        tour.setPrice(tourForm.getPrice());
        tour.setDate(tourForm.getDate());
        tour.setCount(tourForm.getCount());

        Description description = new Description();
        description.setImg(tourDescriptionService.parse(tourForm.getImg()));
        description.setText(tourDescriptionService.parse(tourForm.getText()));

        tourDescriptionService.add(description);
        tour.setDescription(description);

        if (!tourService.addTour(tour)){
            model.addAttribute("tourError", "Неверно введены данные");
            tourDescriptionService.delete(description);
            return "create";
        }
        else {
            description.setTour(tour);
            tourDescriptionService.add(description);
            return "redirect:/admin/add";
        }
    }

    @GetMapping("/admin/find")
    public String initFind(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("FilteredTours",tourService.getAll());
        model.addAttribute("nickname",user.getUsername());
        return "findTour";
    }

    @PostMapping("/admin/find")
    public String findTour(@Valid TourFilterRequest tourFilterForm,
                           Long tourId,
                           @RequestParam String action,
                           BindingResult bindingResult,
                           Model model){
        if(action.equals("filter")) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("filterError", "Неверно введены данные");
                return "findTour";
            }
            model.addAttribute("FilteredTours", tourFilter.filterTour(tourFilterForm.getStart(), tourFilterForm.getFinish(), tourFilterForm.getDate(), tourFilterForm.getCount()));
            return "findTour";
        }
        else{
            return "redirect:/admin/find/view/"+tourId;
        }
    }

    @GetMapping("/admin/find/view/{tourId}")
    public String viewTour(@AuthenticationPrincipal User user, @PathVariable Long tourId, Model model){
        model.addAttribute("nickname",user.getUsername());
        Tour tour = tourService.findById(tourId);
        if(tour!=null){
            model.addAttribute("users",tour.getUsers());
            return "users";
        }
        else{
            return "redirect:/admin/find";
        }
    }

    @GetMapping("/admin/edit")
    public String initEdit(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("nickname",user.getUsername());
        return "editTour";
    }

    @PostMapping("/admin/edit")
    public String editTour(@Valid EditRequest editForm, @Valid DeleteRequest deleteForm, @RequestParam String action, BindingResult bindingResult, Model model) {
        if(action.equals("edit")) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("editError", "Неверно введены данные");
                return "editTour";
            }
            if (!tourService.editTour(editForm)) {
                model.addAttribute("editError", "Неверно введены данные");
            } else {
                model.addAttribute("editError", "Тур изменен");
            }
            return "editTour";
        }
        else if(action.equals("delete")) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("deleteError", "Тур не найден");
                return "editTour";
            }
            Tour tour = tourService.deleteTour(deleteForm.id);
            if (tour != null) {
                model.addAttribute("deletedTour", "ID = " + tour.getId()
                        + " From = " + tour.getStart()
                        + " To = " + tour.getFinish()
                        + " Date = " + tour.getDate()
                        + " Price = " + tour.getPrice()
                        + " Count = " + tour.getCount());
            } else model.addAttribute("deletedTour", "Тур не найден");
            return "editTour";
        }
        else return "editTour";
    }
}