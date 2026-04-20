package com.demo.pubsub.service;

import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class MessageStore {

    private static final int MAX_SIZE = 100;
    private final ConcurrentLinkedDeque<Map<String, Object>> messages = new ConcurrentLinkedDeque<>();

    public void add(Map<String, Object> message) {
        messages.addFirst(message);
        while (messages.size() > MAX_SIZE) {
            messages.pollLast();
        }
    }

    public List<Map<String, Object>> getAll() {
        return new ArrayList<>(messages);
    }

    public int size() {
        return messages.size();
    }

    public void clear() {
        messages.clear();
    }
}
