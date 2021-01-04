## Sliding Log Algorithm

![sliding-log-image](https://miro.medium.com/max/1400/1*Vbu8IGdjfW2GYfZiTubhLw.png "SLIDING LOG PICTORIAL REPRESENTATION")

Sliding Log rate limiting involves tracking a time stamped log for each consumer request. These logs are usually stored in a hash set or table that is sorted by time. Logs with timestamps beyond a threshold are discarded. When a new request comes in, we calculate the sum of logs to determine the request rate. If the request would exceed the threshold rate, then it is held.

The advantage of this algorithm is that it does not suffer from the boundary conditions of fixed windows. The rate limit will be enforced precisely and because the sliding log is tracked for each consumer, you don't have the rush effect that challenges fixed windows. However, it can be very expensive to store an unlimited number of logs for every request. It's also expensive to compute because each request requires calculating a summation over the consumers prior requests, potentially across a cluster of servers. As a result, it does not scale well to handle large bursts of traffic or denial of service attacks.

Please refer to the [Understanding Rate Limiting Algorithms](https://nataraj-srikantaiah.medium.com/understanding-rate-limiting-algorithms-2244c302025a) blog where the Sliding Log and other algorithms have been explained in detail.

## Sliding Log Algorithm - RateLimiter Spring Boot Project

Refer to the [Rate Limiter Implementation - Sliding Log Algorithm](https://nataraj-srikantaiah.medium.com/rate-limiter-implementation-sliding-log-algorithm-55299fae2a5c) for step by step implementation details.
