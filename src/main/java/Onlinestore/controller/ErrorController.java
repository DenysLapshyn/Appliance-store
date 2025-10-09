package Onlinestore.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController
{
    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @GetMapping("/error")
    public String getErrorPage()
    {
        logger.error("Sending error page");
        return "error";
    }
}
