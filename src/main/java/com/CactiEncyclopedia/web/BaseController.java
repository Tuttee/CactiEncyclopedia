package com.CactiEncyclopedia.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public abstract class BaseController {
    public ModelAndView view(String viewName, ModelAndView modelAndView) {
        modelAndView.setViewName(viewName);
        return modelAndView;
    }

    public ModelAndView view(String viewName) {
        return this.view(viewName, new ModelAndView());
    }

    public ModelAndView redirect(String url) {
        return view("redirect:" + url);
    }

    public ModelAndView redirect(String url, ModelAndView modelAndView) {
        return view("redirect:" + url, modelAndView);
    }
}
