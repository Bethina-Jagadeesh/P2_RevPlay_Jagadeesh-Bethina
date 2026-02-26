package com.rev.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/music")
public class MusicController {

    @GetMapping
    public String musicPlayer(Model model) {
        model.addAttribute("title", "RevPlay - Music Player");
        return "music";
    }

    @GetMapping("/library")
    public String musicLibrary(Model model) {
        model.addAttribute("title", "RevPlay - Library");
        return "library";
    }
}
