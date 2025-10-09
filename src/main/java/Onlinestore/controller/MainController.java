package Onlinestore.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController
{
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/")
    public String getMainPage()
    {
        logger.info("Sending main page");
        return "redirect:/about";
    }
    
    @GetMapping("/about")
    public String getAboutPage()
    {
        logger.info("Sending about us page page");
        return "about";
    }
}
