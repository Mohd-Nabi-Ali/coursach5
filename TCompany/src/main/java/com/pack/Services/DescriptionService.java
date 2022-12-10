package com.pack.Services;

import com.pack.Repositories.DescriptionRepository;
import com.pack.Entities.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DescriptionService {
    @Autowired
    DescriptionRepository descriptionRepository;

    public void add(Description description){
        descriptionRepository.save(description);
    }

    public String parse(String url){
        return "https://drive.google.com/uc?export=view&id="+url.substring(("https://drive.google.com/file/d/").length(),url.length()-"/view?usp=sharing".length());
    }

    public void delete(Description description){
        descriptionRepository.delete(description);
    }
}
