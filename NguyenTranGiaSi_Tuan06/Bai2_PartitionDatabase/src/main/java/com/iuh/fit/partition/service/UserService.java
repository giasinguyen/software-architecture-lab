
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

    public Map<String, Object> benchmark() {
        Map<String, Object> result = new HashMap<>();

        // query user_male (chỉ ~10000 rows)
        long t1 = System.currentTimeMillis();
        long maleCount = maleRepo.count();
        long t2 = System.currentTimeMillis();

        // query user_female (chỉ ~10000 rows)
        long femaleCount = femaleRepo.count();
        long t3 = System.currentTimeMillis();

        result.put("male_table_rows",    maleCount);
        result.put("male_query_ms",      t2 - t1);
        result.put("female_table_rows",  femaleCount);
        result.put("female_query_ms",    t3 - t2);
        result.put("total_rows",         maleCount + femaleCount);
        result.put("note", "Mỗi query chỉ scan 1 partition nhỏ hơn → nhanh hơn scan toàn bảng");

        log.info("Benchmark Horizontal: male={}ms, female={}ms", t2 - t1, t3 - t2);
        return result;
    }
}
