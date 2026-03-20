package com.iuh.fit;

import com.iuh.fit.entity.Food;
import com.iuh.fit.entity.User;
import com.iuh.fit.entity.Voucher;
import com.iuh.fit.enums.UserRole;
import com.iuh.fit.enums.VoucherType;
import com.iuh.fit.repository.FoodRepository;
import com.iuh.fit.repository.UserRepository;
import com.iuh.fit.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final VoucherRepository voucherRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) seedUsers();
        if (foodRepository.count() == 0) seedFoods();
        if (voucherRepository.count() == 0) seedVouchers();
    }

    private void seedUsers() {
        userRepository.saveAll(List.of(
                User.builder().name("Nguyễn Văn An").email("user@gmail.com")
                        .phone("0901234567").address("123 Lê Lợi, Q1, TP.HCM").role(UserRole.USER).build(),
                User.builder().name("Admin Restaurant").email("admin@gmail.com")
                        .phone("0987654321").address("456 Nguyễn Huệ, Q1, TP.HCM").role(UserRole.ADMIN).build()
        ));
    }

    private void seedFoods() {
        foodRepository.saveAll(List.of(
                Food.builder().name("Phở Bò Đặc Biệt").description("Phở bò truyền thống với nước dùng đậm đà, thịt bò tái chín mềm").price(BigDecimal.valueOf(65000)).category("Phở").imageUrl("https://images.unsplash.com/photo-1569050467447-ce54b3bbc37d?w=500&q=80").available(true).orderCount(0).build(),
                Food.builder().name("Bún Bò Huế").description("Bún bò Huế cay nồng đặc trưng, sả thơm, chả lụa").price(BigDecimal.valueOf(55000)).category("Bún").imageUrl("https://images.unsplash.com/photo-1555126634-323283e090fa?w=500&q=80").available(true).orderCount(0).build(),
                Food.builder().name("Cơm Tấm Sườn Bì").description("Cơm tấm sườn bì chả trứng, đầy đủ topping").price(BigDecimal.valueOf(45000)).category("Cơm").imageUrl("https://images.unsplash.com/photo-1512058564366-18510be2db19?w=500&q=80").available(true).orderCount(0).build(),
                Food.builder().name("Bánh Mì Thịt Nguội").description("Bánh mì giòn với pate, thịt nguội, rau thơm tươi").price(BigDecimal.valueOf(25000)).category("Bánh Mì").imageUrl("https://images.unsplash.com/photo-1509722747041-616f39b57569?w=500&q=80").available(true).orderCount(0).build(),
                Food.builder().name("Gà Chiên Nước Mắm").description("Gà chiên vàng giòn, sốt nước mắm tỏi ớt đặc biệt").price(BigDecimal.valueOf(75000)).category("Gà").imageUrl("https://images.unsplash.com/photo-1598103442097-8b74394b95c3?w=500&q=80").available(true).orderCount(0).build(),
                Food.builder().name("Pizza Hải Sản").description("Pizza đế mỏng với hải sản tươi, phô mai kéo sợi").price(BigDecimal.valueOf(120000)).category("Pizza").imageUrl("https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=500&q=80").available(true).orderCount(0).build(),
                Food.builder().name("Burger Bò Nướng").description("Burger bò Angus nướng, rau tươi và sốt đặc biệt của nhà").price(BigDecimal.valueOf(85000)).category("Burger").imageUrl("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500&q=80").available(true).orderCount(0).build(),
                Food.builder().name("Mì Xào Hải Sản").description("Mì xào giòn với tôm, mực, ngao và rau củ tươi").price(BigDecimal.valueOf(70000)).category("Mì").imageUrl("https://images.unsplash.com/photo-1552611052-33e04de081de?w=500&q=80").available(true).orderCount(0).build(),
                Food.builder().name("Trà Sữa Trân Châu").description("Trà sữa hoàng kim với trân châu đen dẻo, béo ngậy").price(BigDecimal.valueOf(35000)).category("Đồ Uống").imageUrl("https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=500&q=80").available(true).orderCount(0).build(),
                Food.builder().name("Chè Khúc Bạch").description("Chè khúc bạch mát lạnh với nhãn, hạnh nhân, lychee").price(BigDecimal.valueOf(30000)).category("Chè").imageUrl("https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=500&q=80").available(true).orderCount(0).build()
        ));
    }

    private void seedVouchers() {
        LocalDateTime expiry = LocalDateTime.now().plusDays(30);
        voucherRepository.saveAll(List.of(
                Voucher.builder().code("WELCOME20").type(VoucherType.PERCENTAGE).value(BigDecimal.valueOf(20)).minOrderAmount(BigDecimal.valueOf(50000)).maxUsage(100).usedCount(0).expiresAt(expiry).active(true).build(),
                Voucher.builder().code("FREESHIP").type(VoucherType.FIXED).value(BigDecimal.valueOf(15000)).minOrderAmount(BigDecimal.valueOf(30000)).maxUsage(50).usedCount(0).expiresAt(expiry).active(true).build(),
                Voucher.builder().code("SALE50K").type(VoucherType.FIXED).value(BigDecimal.valueOf(50000)).minOrderAmount(BigDecimal.valueOf(200000)).maxUsage(20).usedCount(0).expiresAt(expiry).active(true).build()
        ));
    }
}
