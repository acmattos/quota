package br.com.acmattos.quota.service;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public interface QuotaService {
    boolean isAllowedFor(final String userId);

    Integer getUserQuota(final String userId);

    ConcurrentMap<String, AtomicInteger> getUserQuotaMap();
}
