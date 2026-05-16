package com.member.storage.service;

import com.member.storage.model.Gift;
import com.member.storage.repository.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GiftService {

    @Autowired
    private MemoryStore memoryStore;

    public Gift createGift(Gift gift) {
        memoryStore.saveGift(gift);
        return gift;
    }

    public Gift getGift(String giftId) {
        Gift gift = memoryStore.getGift(giftId);
        if (gift == null) {
            throw new RuntimeException("赠送金记录不存在: " + giftId);
        }
        return gift;
    }

    public List<Gift> getValidGifts(String userId) {
        return memoryStore.getValidGiftsByUserId(userId);
    }

    public List<Gift> getAllGifts(String userId) {
        return memoryStore.getGiftsByUserId(userId);
    }

    public void useGift(String giftId, BigDecimal amount) {
        Gift gift = getGift(giftId);
        if (!"ACTIVE".equals(gift.getStatus())) {
            throw new RuntimeException("赠送金状态不可用");
        }
        if (gift.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("赠送金已过期");
        }
        if (gift.getRemainAmount().compareTo(amount) < 0) {
            throw new RuntimeException("赠送金余额不足");
        }
        
        gift.setRemainAmount(gift.getRemainAmount().subtract(amount));
        if (gift.getRemainAmount().compareTo(BigDecimal.ZERO) == 0) {
            gift.setStatus("USED");
        }
        memoryStore.saveGift(gift);
    }

    public void expireGift(String giftId) {
        Gift gift = getGift(giftId);
        gift.setStatus("EXPIRED");
        memoryStore.saveGift(gift);
    }
}
