package com.demo.p2p.service;

import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * In-memory store lưu lại các message đã được consumer xử lý.
 * Dùng để quan sát qua GET /api/p2p/received thay vì phải nhìn console log.
 */
@Service
public class MessageStore {

    private final Deque<Map<String, Object>> received = new ConcurrentLinkedDeque<>();
    private static final int MAX = 100;

    public void add(Map<String, Object> entry) {
        received.addFirst(entry);
        while (received.size() > MAX) received.pollLast();
    }

    public List<Map<String, Object>> getAll() {
        return List.copyOf(received);
    }

    public int size() {
        return received.size();
    }

    public void clear() {
        received.clear();
    }
}
