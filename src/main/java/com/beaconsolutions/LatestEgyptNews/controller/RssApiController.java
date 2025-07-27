package com.beaconsolutions.LatestEgyptNews.controller;

import com.beaconsolutions.LatestEgyptNews.service.RssPublisherService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/rss")
public class RssApiController {

    private final RssPublisherService rssService;

    public RssApiController(RssPublisherService rssService) {
        this.rssService = rssService;
    }

    @GetMapping("/published")
    public Set<String> getPublishedTitles() {
        return rssService.getPublishedTitles();
    }
}
