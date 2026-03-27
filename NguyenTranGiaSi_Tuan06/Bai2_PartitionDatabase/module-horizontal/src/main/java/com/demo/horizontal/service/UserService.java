package com.demo.horizontal.service;

import com.demo.horizontal.dto.UserRequest;
import com.demo.horizontal.entity.UserFemale;
import com.demo.horizontal.entity.UserMale;
import com.demo.horizontal.repository.UserFemaleRepository;
import com.demo.horizontal.repository.UserMaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMaleRepository   maleRepo;
    private final UserFemaleRepository femaleRepo;

    public Object save(UserRequest req) {
        if ("M".equalsIgnoreCase(req.getGender())) {
            UserMale u = new UserMale();
            u.setName(req.getName());
            u.setEmail(req.getEmail());
            return maleRepo.save(u);        // → ghi vào user_male
        } else {
            UserFemale u = new UserFemale();
            u.setName(req.getName());
            u.setEmail(req.getEmail());
            return femaleRepo.save(u);      // → ghi vào user_female
        }
    }

    public List<UserMale>   getAllMale()   { return maleRepo.findAll(); }
    public List<UserFemale> getAllFemale() { return femaleRepo.findAll(); }

    /**
     * So sánh: query partition nhỏ vs toàn bảng giả sử không partition.
     * Dù đây là 2 bảng riêng, cả 2 đều chỉ có ~10 000 rows thay vì 20 000.
     */
    public Map<String, Object> benchmark() {
        Map<String, Object> result = new HashMap<>();

        // Query user_male (~10 000 rows)
        long t1 = System.currentTimeMillis();
        long maleCount = maleRepo.count();
        long t2 = System.currentTimeMillis();

        // Query user_female (~10 000 rows)
        long femaleCount = femaleRepo.count();
        long t3 = System.currentTimeMillis();

        result.put("male_table_rows",   maleCount);
        result.put("male_query_ms",     t2 - t1);
        result.put("female_table_rows", femaleCount);
        result.put("female_query_ms",   t3 - t2);
        result.put("total_rows",        maleCount + femaleCount);
        result.put("note", "Mỗi query chỉ scan 1 partition nhỏ (~10k rows) thay vì toàn bảng (~20k rows)");

        log.info("Benchmark Horizontal: male={}ms, female={}ms", t2 - t1, t3 - t2);
        return result;
    }
}
