package br.com.acmattos.quota.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class QuotaServiceImpl implements QuotaService {
    private final ConcurrentMap<String, AtomicInteger> userQuotaMap;
    private final int maxRequestsPerUser;

    public QuotaServiceImpl(
        @Value("#{new Integer(${max.requests.per.user})}")
        Integer maxRequestsPerUser) {
        this.userQuotaMap = new ConcurrentHashMap<>();
        this.maxRequestsPerUser = maxRequestsPerUser;
    }

    public boolean isAllowedFor(final String userId) {
        AtomicInteger quota = userQuotaMap
            .computeIfAbsent(userId, key -> new AtomicInteger());
        boolean isAllowedForUserId =
            quota.incrementAndGet() <= maxRequestsPerUser;
        return isAllowedForUserId;
    }

    public Integer getUserQuota(final String userId) {
        return Optional.ofNullable(userQuotaMap.get(userId))
            .map(AtomicInteger::get)
            .orElse(null);
    }

    public ConcurrentMap<String, AtomicInteger> getUserQuotaMap() {
        return userQuotaMap;
    }
}
