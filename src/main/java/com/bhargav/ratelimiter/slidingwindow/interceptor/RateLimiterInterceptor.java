package com.bhargav.ratelimiter.slidingwindow.interceptor;

import com.bhargav.ratelimiter.slidingwindow.service.SubscriptionService;
import com.bhargav.ratelimiter.slidingwindow.service.UserRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RateLimiterInterceptor implements HandlerInterceptor {

    private static final String HEADER_SUBSCRIPTION_KEY = "X-Subscription-Key";
    private static final String HEADER_LIMIT_REMAINING = "X-Rate-Limit-Remaining";
    private static final String HEADER_RETRY_AFTER = "X-Rate-Limit-Retry-After-Seconds";
    private static final String SUBSCRIPTION_QUOTA_EXHAUSTED =
            "You've exhausted your API Request Quota. Please upgrade your subscription plan.";

    @Autowired
    private SubscriptionService subscriptionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String subscriptionKey = request.getHeader(HEADER_SUBSCRIPTION_KEY);
        if (StringUtils.isEmpty(subscriptionKey)) {
            response.sendError(HttpStatus.BAD_REQUEST.value(),
                    "Missing Request Header: " + HEADER_SUBSCRIPTION_KEY);
            return false;
        }

        UserRequestData userRequestData =
                subscriptionService.resolveSubscribedUserData(subscriptionKey);
        if (!userRequestData.isServiceCallAllowed()) {
            int waitTime = userRequestData.getRequestWaitTime();
            response.addHeader(HEADER_RETRY_AFTER, String.valueOf(waitTime));

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), SUBSCRIPTION_QUOTA_EXHAUSTED);
            return false;
        }

        response.addHeader(HEADER_LIMIT_REMAINING,
                String.valueOf(userRequestData.getRemainingRequests()));
        return true;
    }
}
