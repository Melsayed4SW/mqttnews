package com.beaconsolutions.LatestEgyptNews.service;

import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.*;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

@Service
public class RssPublisherService {

    @Value("${rss.url}")
    private String rssFeedUrl;

    @Value("${mqtt.broker}")
    private String mqttBroker;

    @Value("${mqtt.topic}")
    private String mqttTopic;
//    private final String broker = "tcp://broker.hivemq.com:1883";
//    private final String topic = "rss/news/bbc";
//    private final String rssFeedUrl = "https://www.almasryalyoum.com/rss/rssfeed";
    private final Set<String> publishedTitles = new HashSet<>();

    private final Path filePath = Paths.get("published_titles.txt");

    @PostConstruct
    public void loadPublished() throws IOException {
        if (Files.exists(filePath)) {
            publishedTitles.addAll(Files.readAllLines(filePath));
        }
    }

    private void savePublishedTitle(String title) throws IOException {
        Files.write(filePath, (title + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }


    @Scheduled(fixedRate = 1 * 60 * 1000) // every 10 minutes
    public void fetchAndPublishRSS() {
        try {
            MqttClient client = new MqttClient(mqttBroker, MqttClient.generateClientId());
            client.connect();

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new URL(rssFeedUrl)));

//            URL feedSource = new URL(rssFeedUrl);
//            URLConnection connection = feedSource.openConnection();
//            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
//            try (InputStream is = connection.getInputStream()) {
//                String xml = new String(is.readAllBytes());
//                System.out.println("Received XML: \n" + xml);
//                SyndFeedInput input = new SyndFeedInput();
//                SyndFeed feed = input.build(new XmlReader(new ByteArrayInputStream(xml.getBytes())));
//            }


//            URL feedSource = new URL(rssFeedUrl);
//            try (InputStream is = feedSource.openStream()) {
//                String xml = new String(is.readAllBytes());
//                System.out.println("Received XML: \n" + xml); // Debug output
//                SyndFeedInput input = new SyndFeedInput();
//                SyndFeed feed = input.build(new XmlReader(new ByteArrayInputStream(xml.getBytes())));
//
//                // ... your loop logic
//
//
                for (SyndEntry entry : feed.getEntries()) {
                    String title = entry.getTitle();
                    String link = entry.getLink();
                    if (!publishedTitles.contains(title)) {
                        String message = title + " - " + link;
                        client.publish(mqttTopic, new MqttMessage(message.getBytes()));
                        System.out.println("Published: " + message);
                        publishedTitles.add(title);
                        savePublishedTitle(title);
                    }
                }
//            }

            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<String> getPublishedTitles() {
        return publishedTitles;
    }
}

