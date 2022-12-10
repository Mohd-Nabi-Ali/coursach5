package com.pack.Controllers;

import com.pack.Repositories.UserRepository;
import com.pack.Entities.Tour;
import com.pack.Entities.User;
import com.pack.Requests.BuyRequest;
import com.pack.Requests.EditRequest;
import com.pack.Requests.TourFilterRequest;
import com.pack.Services.EmailService;
import com.pack.Services.TourFilter;
import com.pack.Services.TourService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Controller
public class ClientController {
    @Autowired
    TourService tourService;
    @Autowired
    TourFilter tourFilter;
    @Autowired
    EmailService emailService;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/booking")
    public String initBooking(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("FilteredTours",tourService.getAll());
        model.addAttribute("nickname",user.getUsername());
        return "booking";
    }

    @PostMapping("/booking")
    public String findTours(@Valid TourFilterRequest tourFilterForm,
                            Long tourId,
                            @RequestParam String action,
                            BindingResult bindingResult,
                            Model model){
        if(action.equals("filter")) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("filterError", "Ошибка");
                return "booking";
            }
            model.addAttribute("FilteredTours", tourFilter.filterTour(tourFilterForm.getStart(), tourFilterForm.getFinish(), tourFilterForm.getDate(), tourFilterForm.getCount()));
            return "booking";
        }
        else{
            return "redirect:/booking/view/"+tourId;
        }
    }

    @SneakyThrows
    @GetMapping("/booking/view/{tourId}")
    public String viewTour(@PathVariable Long tourId, Model model){
        Tour tourFromDb = tourService.findById(tourId);
        if (tourFromDb!=null) {
            model.addAttribute("image", tourFromDb.getDescription().getImg());
            URL text = new URL(tourFromDb.getDescription().getText());
            String inputLine, res = "";
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(text.openStream(), StandardCharsets.UTF_8));
                while ((inputLine = in.readLine()) != null) {
                    res += inputLine;
                }
                in.close();
            } catch (IOException e) {
                System.out.println("Произошёл сбой");
            }
            System.out.println(res);
            model.addAttribute("description", res);
            return "tour";
        }
        else return "redirect:/booking";
    }

    @SneakyThrows
    @PostMapping("/booking/view/{tourId}")
    public String buyTour(@PathVariable Long tourId,
                         @Valid BuyRequest buyRequest,
                         Model model,
                         @AuthenticationPrincipal User user){
        Tour tourFromDb = tourService.findById(tourId);
        if (tourFromDb!=null) {
            if (buyRequest.getSubC()<=tourFromDb.getCount() && buyRequest.getSubC()>0) {
                tourService.editTour(new EditRequest(tourFromDb.getId(), "", "", "", 0, tourFromDb.getCount() - buyRequest.getSubC(), "", ""));
                emailService.send(user.getEmail(),"      Вас приветствует тур.компания \"Voyage\" !"
                        + "\n\nБлагодарим Вас, что приобрели в нашей компании "
                        + buyRequest.getSubC() + " тура !"
                        +"\nМесто вылета: " + tourFromDb.getStart() + ", место прилёта: " + tourFromDb.getFinish()
                        + ".\nДата: " + tourFromDb.getDate() + ". К оплате: " + tourFromDb.getPrice()*buyRequest.getSubC() + " рублей."
                        + "\n\n      Удачного отдыха!");
                boolean exist = false;
                for (Tour t : userRepository.findById(user.getId()).get().getTours()) {
                    if (t.getId().equals(tourFromDb.getId())) exist = true;
                    System.out.println(t.getId());
                }
                if (!exist) {
                    Set<Tour> tourSet = user.getTours();
                    tourSet.add(tourFromDb);
                    for (Tour t : tourSet) {
                        System.out.println(t.getId());
                    }
                    user.setTours(tourSet);
                    userRepository.save(user);
                }
                return "redirect:/booking";
            }
            else {
                model.addAttribute("image",tourFromDb.getDescription().getImg());
                URL text = new URL(tourFromDb.getDescription().getText());
                String inputLine, res = "";
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(text.openStream()));
                    while ((inputLine = in.readLine()) != null) {
                        res += inputLine;
                    }
                    in.close();
                } catch (IOException e) {
                    System.out.println("Произошёл сбой");
                }
                model.addAttribute("description", res);
                model.addAttribute("BuyError", "Введено некорректное число путевок");
                return "tour";
            }
        }
        else return "redirect:/booking";
    }
}