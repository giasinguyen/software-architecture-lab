package com.demo.vertical.service;

import com.demo.vertical.entity.*;
import com.demo.vertical.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerticalService {

    private final UserFullRepository     fullRepo;
    private final UserBasicRepository    basicRepo;
    private final UserProfileRepository  profileRepo;
    private final UserActivityRepository activityRepo;

    public List<UserFull>     getAllFull()     { return fullRepo.findAll(); }
    public List<UserBasic>    getAllBasic()    { return basicRepo.findAll(); }
    public List<UserProfile>  getAllProfile()  { return profileRepo.findAll(); }
    public List<UserActivity> getAllActivity() { return activityRepo.findAll(); }

    /**
     * Benchmark: so sánh query user_full (8 cột, có TEXT + JSON)
     * với query user_basic (4 cột "hot" nhỏ gọn hơn).
     * Vertical partition giúp query "hot path" load ít data hơn → nhanh hơn.
     */
    public Map<String, Object> benchmark() {
        Map<String, Object> result = new HashMap<>();

        // Query user_full – phải đọc tất cả 8 cột (bao gồm TEXT, JSON)
        long t1 = System.currentTimeMillis();
        long fullCount = fullRepo.count();
        long t2 = System.currentTimeMillis();

        // Query user_basic – chỉ đọc 4 cột "hot" (id, name, email, phone)
        long basicCount = basicRepo.count();
        long t3 = System.currentTimeMillis();

        result.put("full_table_cols",      8);
        result.put("full_table_rows",      fullCount);
        result.put("full_query_ms",        t2 - t1);
        result.put("basic_table_cols",     4);
        result.put("basic_table_rows",     basicCount);
        result.put("basic_query_ms",       t3 - t2);
        result.put("note", "Vertical partition: user_basic (4 cột hot) → I/O ít hơn → nhanh hơn user_full (8 cột gồm TEXT+JSON)");

        log.info("Benchmark Vertical: full={}ms, basic={}ms", t2 - t1, t3 - t2);
        return result;
    }
}
