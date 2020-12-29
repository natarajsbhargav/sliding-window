package com.bhargav.ratelimiter.slidingwindow.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SubscriptionService {

    private final Map<String, UserRequestData> subscriptionCacheMap = new ConcurrentHashMap<>();

    public UserRequestData resolveSubscribedUserData(String subscriptionKey) {
        return subscriptionCacheMap.computeIfAbsent(subscriptionKey, this::resolveUser);
    }

    private UserRequestData resolveUser(String subscriptionKey) {
        if (subscriptionCacheMap.containsKey(subscriptionKey)) {
            return subscriptionCacheMap.get(subscriptionKey);
        }
        return buildUserLog(resolveSubscriptionPlanByKey(subscriptionKey));
    }

    private UserRequestData buildUserLog(SubscriptionPlan subscriptionPlan) {
        return new UserRequestData(
                subscriptionPlan.getRequestLimit(), subscriptionPlan.getWindowTime());
    }

    private SubscriptionPlan resolveSubscriptionPlanByKey(String subscriptionKey) {
        if (subscriptionKey.startsWith("PS1129-")) {
            return SubscriptionPlan.SUBSCRIPTION_PROFESSIONAL;
        } else if (subscriptionKey.startsWith("BS1129-")) {
            return SubscriptionPlan.SUBSCRIPTION_BASIC;
        }

        return SubscriptionPlan.SUBSCRIPTION_FREE;
    }

}
