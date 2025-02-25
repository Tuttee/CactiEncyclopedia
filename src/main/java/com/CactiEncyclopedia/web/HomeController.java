package com.CactiEncyclopedia.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class HomeController extends BaseController {
    @GetMapping
    public ModelAndView getIndex(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user_id", session.getAttribute("user_id"));
        return super.view("index", modelAndView);
    }

    @GetMapping("/about")
    public ModelAndView about(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user_id", session.getAttribute("user_id"));
        return super.view("about", modelAndView);
    }
}
