package org.example.subscription.service;

import org.example.subscription.dto.SubscriptionResponseDTO;
import org.example.subscription.entity.*;
import org.example.subscription.enums.SubscriptionStatus;
import org.example.subscription.exception.ResourceNotFoundException;
import org.example.subscription.repository.*;
import org.example.subscription.service.impl.SubscriptionServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.example.subscription.enums.SubscriptionStatus;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@DataJpaTest
//@ActiveProfiles("test")
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponUsageRepository couponUsageRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Mock
    private AddOnRepository addOnRepository;

    @Mock
    private SubscriptionAddOnRepository subscriptionAddOnRepository;


//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }

//    @Test
//    void createSubscription_success() {
//
//        // 1️⃣ Arrange (Given)
//
//        Long userId = 1L;
//        Long planId = 10L;
////        User user = new User();
////        user.setId(1L);
////        user.setName("Aman");
////        when(userRepository.findById(userId))
////                .thenReturn(Optional.of(user));
////
////
////        Plan plan = new Plan();
////        plan.setId(10L);
////        plan.setName("Basic");
////        plan.setPrice(1000.0);
////        plan.setDurationDays(30);
//        User user = new User();
//        user.setId(userId);
//        user.setName("Aman");
//
//        when(userRepository.findById(1L))
//                .thenReturn(Optional.of(user));
//
//
//        Plan plan = new Plan();
//        plan.setId(planId);
//        plan.setName("Basic");
//        plan.setPrice(1000.0);
//        plan.setDurationDays(30);
//
//        when(planRepository.findById(10L))
//                .thenReturn(Optional.of(plan));
//
////        when(planRepository.findById(planId))
////                .thenReturn(Optional.of(plan));
//
//        when(subscriptionRepository.save(any(Subscription.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        // 2️⃣ Act (When)
//
//        SubscriptionResponseDTO response =
//                subscriptionService.createSubscription(1L, 10L, null);
//
//        // 3️⃣ Assert (Then)
//
//        assertNotNull(response);
//        assertEquals("Aman", response.getUserName());
//        assertEquals("Basic", response.getPlanName());
//        assertEquals("PENDING", response.getStatus());
//
//        verify(subscriptionRepository, times(1))
//                .save(any(Subscription.class));
//    }

//@Test
//void createSubscription_success() {
//
//    Long userId = 1L;
//    Long planId = 10L;
//
//    User user = new User();
//    user.setId(userId);
//    user.setName("Aman");
//
//    Plan plan = new Plan();
//    plan.setId(planId);
//    plan.setName("Basic");
//    plan.setPrice(1000.0);
//    plan.setDurationDays(30);
//
//    when(userRepository.findById(userId))
//            .thenReturn(Optional.of(user));
//
//    when(planRepository.findById(planId))
//            .thenReturn(Optional.of(plan));
//
//    when(subscriptionRepository.save(any(Subscription.class)))
//            .thenAnswer(invocation -> invocation.getArgument(0));
//
//    SubscriptionResponseDTO response =
//            subscriptionService.createSubscription(userId, planId, null);
//
//    assertNotNull(response);
//    assertEquals("PENDING", response.getStatus());
//}


    @Test
    void createSubscription_invalidUser() {

        // 1️⃣ Arrange (Given)

        Long userId = 99L;
        Long planId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // 2️⃣ Act + Assert (When + Then)

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class, () ->
                        subscriptionService.createSubscription(userId, planId, null)
                );

        assertEquals("User not found", exception.getMessage());

        // 3️⃣ Verify repository not called further
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void createSubscription_invalidPlan() {

        // 1️⃣ Arrange (Given)

        Long userId = 1L;
        Long planId = 999L;

        User user = new User();
        user.setId(userId);
        user.setName("Aman");

        // User mil gaya
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Plan nahi mila
        when(planRepository.findById(planId))
                .thenReturn(Optional.empty());

        // 2️⃣ Act + Assert

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class, () ->
                        subscriptionService.createSubscription(userId, planId, null)
                );

        assertEquals("Plan not found", exception.getMessage());


        verify(subscriptionRepository, never()).save(any());
    }
    @Test
    void createSubscription_whenUserAlreadyActive_shouldThrowException() {

        Long userId = 1L;
        Long planId = 10L;

        User user = new User();
        user.setId(userId);

        Plan plan = new Plan();
        plan.setId(planId);
        plan.setPrice(1000.0);
        plan.setDurationDays(30);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(planRepository.findById(planId))
                .thenReturn(Optional.of(plan));

        when(subscriptionRepository.existsByUserIdAndStatus(
                userId, SubscriptionStatus.ACTIVE))
                .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                subscriptionService.createSubscription(userId, planId, null)
        );

        assertEquals("User already has an active subscription", exception.getMessage());
    }

    @Test
    void cancelSubscription_success() {

        // 1️⃣ Arrange

        Long subscriptionId = 1L;
        User user = new User();
        user.setId(1L);
        user.setName("Aman");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Plan plan = new Plan();
        plan.setId(10L);
        plan.setName("Basic");

        when(planRepository.findById(10L))
                .thenReturn(Optional.of(plan));

        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);
        subscription.setUserId(1L);
        subscription.setPlanId(10L);
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        when(subscriptionRepository.findById(subscriptionId))
                .thenReturn(Optional.of(subscription));

        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 2️⃣ Act

        SubscriptionResponseDTO response =
                subscriptionService.cancelSubscription(subscriptionId);

        // 3️⃣ Assert

        assertNotNull(response);
        assertEquals("CANCELLED", response.getStatus());

        verify(subscriptionRepository, times(1))
                .save(subscription);
    }
    @Test
    void changePlan_subscriptionNotFound() {

        when(subscriptionRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> subscriptionService.changePlan(1L, 2L)
        );

        assertEquals("Subscription not found", exception.getMessage());
    }
    @Test
    void changePlan_notActive() {

        Subscription subscription = new Subscription();
        subscription.setId(1L);
        subscription.setStatus(SubscriptionStatus.CANCELLED);

        when(subscriptionRepository.findById(1L))
                .thenReturn(Optional.of(subscription));

        assertThrows(RuntimeException.class,
                () -> subscriptionService.changePlan(1L, 20L));
    }
    @Test
    void changePlan_oldPlanNotFound() {

        Subscription subscription = new Subscription();
        subscription.setId(1L);
        subscription.setPlanId(10L);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setEndDate(new Date());

        when(subscriptionRepository.findById(1L))
                .thenReturn(Optional.of(subscription));

        when(planRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> subscriptionService.changePlan(1L, 20L));
    }
    @Test
    void changePlan_newPlanNotFound() {

        Subscription subscription = new Subscription();
        subscription.setId(1L);
        subscription.setPlanId(10L);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setEndDate(new Date());

        Plan oldPlan = new Plan();
        oldPlan.setId(10L);
        oldPlan.setPrice(1000.0);
        oldPlan.setDurationDays(30);

        when(subscriptionRepository.findById(1L))
                .thenReturn(Optional.of(subscription));

        when(planRepository.findById(10L))
                .thenReturn(Optional.of(oldPlan));

        when(planRepository.findById(20L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> subscriptionService.changePlan(1L, 20L));
    }




    @Test
    void changePlan_success() {

        // 1️⃣ Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Aman");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Plan plan = new Plan();
        plan.setId(10L);
        plan.setName("Basic");

        when(planRepository.findById(10L))
                .thenReturn(Optional.of(plan));

        Long subscriptionId = 1L;
        Long oldPlanId = 10L;
        Long newPlanId = 20L;

        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);
        subscription.setUserId(1L);
        subscription.setPlanId(oldPlanId);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(new Date());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 10);
        subscription.setEndDate(cal.getTime());

        Plan oldPlan = new Plan();
        oldPlan.setId(oldPlanId);
        oldPlan.setPrice(1000.0);
        oldPlan.setDurationDays(30);

        Plan newPlan = new Plan();
        newPlan.setId(newPlanId);
        newPlan.setPrice(2000.0);
        newPlan.setDurationDays(30);

        when(subscriptionRepository.findById(subscriptionId))
                .thenReturn(Optional.of(subscription));
        when(planRepository.findById(oldPlanId))
                .thenReturn(Optional.of(oldPlan));


        when(planRepository.findById(newPlanId))
                .thenReturn(Optional.of(newPlan));

        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 2️⃣ Act

        SubscriptionResponseDTO response =
                subscriptionService.changePlan(subscriptionId, newPlanId);

        // 3️⃣ Assert

        assertNotNull(response);
        assertEquals(newPlanId, subscription.getPlanId());
        assertEquals("ACTIVE", response.getStatus());

        verify(paymentRepository, times(1))
                .save(any(Payment.class));

        verify(subscriptionRepository, times(1))
                .save(subscription);
    }


    @Test
    void cancelSubscription_notFound() {

        Long subscriptionId = 100L;

        when(subscriptionRepository.findById(subscriptionId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class,
                        () -> subscriptionService.cancelSubscription(subscriptionId));

        assertEquals("Subscription not found", exception.getMessage());
    }


    @Test
    void createSubscription_withValidCoupon_success() {

        Long userId = 1L;
        Long planId = 10L;

        User user = new User();
        user.setId(userId);
        user.setName("Aman");

        Plan plan = new Plan();
        plan.setId(planId);
        plan.setName("Pro");
        plan.setPrice(1000.0);
        plan.setDurationDays(30);

        Coupon coupon = new Coupon();
        coupon.setId(100L);
        coupon.setCode("NEW10");
        coupon.setActive(true);
        coupon.setUsageLimit(5);
        coupon.setUsedCount(0);
        coupon.setDiscountPercentage(10.0);
        coupon.setExpiryDate(new Date(System.currentTimeMillis() + 1000000));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(planRepository.findById(planId))
                .thenReturn(Optional.of(plan));

        when(couponRepository.findByCode("NEW10"))
                .thenReturn(Optional.of(coupon));

        when(couponUsageRepository.findByUserIdAndCouponId(userId, 100L))
                .thenReturn(Optional.empty());

        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(couponUsageRepository.save(any(CouponUsage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SubscriptionResponseDTO response =
                subscriptionService.createSubscription(userId, planId, "NEW10");

        assertNotNull(response);
        assertEquals("Pro", response.getPlanName());
        assertEquals("Aman", response.getUserName());

        verify(couponUsageRepository, times(1))
                .save(any(CouponUsage.class));

        verify(couponRepository, times(1))
                .save(coupon);
    }
    @Test
    void createSubscription_couponAlreadyUsed_shouldThrowException() {

        Long userId = 1L;
        Long planId = 10L;

        User user = new User();
        user.setId(userId);

        Plan plan = new Plan();
        plan.setId(planId);
        plan.setPrice(1000.0);
        plan.setDurationDays(30);

        Coupon coupon = new Coupon();
        coupon.setId(100L);
        coupon.setCode("NEW10");
        coupon.setActive(true);
        coupon.setUsageLimit(5);
        coupon.setUsedCount(1);
        coupon.setExpiryDate(new Date(System.currentTimeMillis() + 1000000));

        CouponUsage usage = new CouponUsage();
        usage.setUserId(userId);
        usage.setCouponId(100L);
        usage.setUsageCount(1); // already used

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(planRepository.findById(planId))
                .thenReturn(Optional.of(plan));

        when(couponRepository.findByCode("NEW10"))
                .thenReturn(Optional.of(coupon));

        when(couponUsageRepository.findByUserIdAndCouponId(userId, 100L))
                .thenReturn(Optional.of(usage));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> subscriptionService.createSubscription(userId, planId, "NEW10")
        );

        assertEquals("User already used this coupon", exception.getMessage());
    }

    @Test
    void createSubscription_couponExpired_shouldThrowException() {

        Long userId = 1L;
        Long planId = 10L;

        // --------- Arrange ---------

        User user = new User();
        user.setId(userId);

        Plan plan = new Plan();
        plan.setId(planId);
        plan.setPrice(1000.0);
        plan.setDurationDays(30);

        Coupon coupon = new Coupon();
        coupon.setId(100L);
        coupon.setCode("NEW10");
        coupon.setActive(true);
        coupon.setUsageLimit(5);
        coupon.setUsedCount(0);

        // 👇 Expiry date past me set kar rahe hain
        coupon.setExpiryDate(new Date(System.currentTimeMillis() - 100000));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(planRepository.findById(planId))
                .thenReturn(Optional.of(plan));

        when(couponRepository.findByCode("NEW10"))
                .thenReturn(Optional.of(coupon));

        // --------- Act + Assert ---------

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> subscriptionService.createSubscription(userId, planId, "NEW10")
        );

        assertEquals("Coupon expired", exception.getMessage());

        // 👇 Verify nothing saved
        verify(subscriptionRepository, never()).save(any());
        verify(couponUsageRepository, never()).save(any());
    }

    @Test
    void createSubscription_couponInactive_shouldThrowException() {

        Long userId = 1L;
        Long planId = 10L;

        // -------- Arrange --------

        User user = new User();
        user.setId(userId);

        Plan plan = new Plan();
        plan.setId(planId);
        plan.setPrice(1000.0);
        plan.setDurationDays(30);

        Coupon coupon = new Coupon();
        coupon.setId(100L);
        coupon.setCode("NEW10");
        coupon.setActive(false);   // 👈 Inactive coupon
        coupon.setUsageLimit(5);
        coupon.setUsedCount(0);
        coupon.setExpiryDate(new Date(System.currentTimeMillis() + 100000)); // future date

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(planRepository.findById(planId))
                .thenReturn(Optional.of(plan));

        when(couponRepository.findByCode("NEW10"))
                .thenReturn(Optional.of(coupon));

        // -------- Act + Assert --------

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> subscriptionService.createSubscription(userId, planId, "NEW10")
        );

        assertEquals("Coupon inactive", exception.getMessage());

        // 👇 Verify nothing saved
        verify(subscriptionRepository, never()).save(any());
        verify(couponUsageRepository, never()).save(any());
    }

    @Test
    void createSubscription_couponUsageLimitExceeded_shouldThrowException() {

        Long userId = 1L;
        Long planId = 10L;

        // -------- Arrange --------

        User user = new User();
        user.setId(userId);

        Plan plan = new Plan();
        plan.setId(planId);
        plan.setPrice(1000.0);
        plan.setDurationDays(30);

        Coupon coupon = new Coupon();
        coupon.setId(100L);
        coupon.setCode("NEW10");
        coupon.setActive(true);
        coupon.setUsageLimit(5);
        coupon.setUsedCount(5);   // Limit reached
        coupon.setExpiryDate(new Date(System.currentTimeMillis() + 100000)); // future date

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(planRepository.findById(planId))
                .thenReturn(Optional.of(plan));

        when(couponRepository.findByCode("NEW10"))
                .thenReturn(Optional.of(coupon));

        // -------- Act + Assert --------

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> subscriptionService.createSubscription(userId, planId, "NEW10")
        );

        assertEquals("Coupon usage exceeded", exception.getMessage());

        // 👇 Verify nothing saved
        verify(subscriptionRepository, never()).save(any());
        verify(couponUsageRepository, never()).save(any());
    }
    @Test
    void changePlan_paymentFailed_shouldThrowException() {

        // 1️⃣ Arrange

        Long subscriptionId = 1L;
        Long oldPlanId = 10L;
        Long newPlanId = 20L;

        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);
        subscription.setPlanId(oldPlanId);
        subscription.setUserId(1L);
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        // Set future end date
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        subscription.setEndDate(cal.getTime());

        Plan oldPlan = new Plan();
        oldPlan.setId(oldPlanId);
        oldPlan.setPrice(1000.0);
        oldPlan.setDurationDays(30);

        Plan newPlan = new Plan();
        newPlan.setId(newPlanId);
        newPlan.setPrice(10000.0);
        newPlan.setDurationDays(30);

        when(subscriptionRepository.findById(subscriptionId))
                .thenReturn(Optional.of(subscription));

        when(planRepository.findById(oldPlanId))
                .thenReturn(Optional.of(oldPlan));

        when(planRepository.findById(newPlanId))
                .thenReturn(Optional.of(newPlan));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 2️⃣ Act + Assert

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> subscriptionService.changePlan(subscriptionId, newPlanId)
        );

        assertEquals("Payment failed", exception.getMessage());

        // 3️⃣ Verify subscription was NOT saved
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }



    @Test
    void createSubscription_withCoupon_success() {

        // ---------- Arrange ----------

        Long userId = 1L;
        Long planId = 10L;

        User user = new User();
        user.setId(userId);
        user.setName("Aman");

        Plan plan = new Plan();
        plan.setId(planId);
        plan.setName("Basic");
        plan.setPrice(1000.0);
        plan.setDurationDays(30);

        Coupon coupon = new Coupon();
        coupon.setId(100L);
        coupon.setCode("NEW10");
        coupon.setActive(true);
        coupon.setUsageLimit(5);
        coupon.setUsedCount(0);
        coupon.setDiscountPercentage(10.0);
        coupon.setExpiryDate(new Date(System.currentTimeMillis() + 100000));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(planRepository.findById(planId))
                .thenReturn(Optional.of(plan));

        when(couponRepository.findByCode("NEW10"))
                .thenReturn(Optional.of(coupon));

        when(couponUsageRepository.findByUserIdAndCouponId(userId, 100L))
                .thenReturn(Optional.empty());

        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(couponUsageRepository.save(any(CouponUsage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(planRepository.findById(planId))
                .thenReturn(Optional.of(plan));

        // ---------- Act ----------

        SubscriptionResponseDTO response =
                subscriptionService.createSubscription(userId, planId, "NEW10");

        // ---------- Assert ----------

        assertNotNull(response);
        assertEquals("Aman", response.getUserName());
        assertEquals("Basic", response.getPlanName());
        assertEquals("ACTIVE", response.getStatus());

        // verify calls
        verify(subscriptionRepository, atLeastOnce())
                .save(any(Subscription.class));

        verify(couponUsageRepository, times(1))
                .save(any(CouponUsage.class));

        verify(couponRepository, times(1))
                .save(coupon);
    }

    @Test
    void handleSubscriptions_activeToGrace() {

        Subscription sub = new Subscription();
        sub.setId(1L);
        sub.setPlanId(10L);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setAutoRenew(false);
        sub.setGraceDays(3);


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        sub.setEndDate(cal.getTime());

        when(subscriptionRepository.findAll())
                .thenReturn(List.of(sub));

        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        subscriptionService.handleSubscriptions();

        // Assert
        assertEquals(SubscriptionStatus.GRACE, sub.getStatus());
    }
    @Test
    void handleSubscriptions_autoRenew_success() {

        Subscription sub = new Subscription();
        sub.setId(1L);
        sub.setPlanId(10L);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setAutoRenew(true);
        sub.setRenewalAttempts(0);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        sub.setEndDate(cal.getTime());

        Plan plan = new Plan();
        plan.setId(10L);
        plan.setPrice(1000.0);
        plan.setDurationDays(30);

        when(subscriptionRepository.findAll())
                .thenReturn(List.of(sub));

        when(planRepository.findById(10L))
                .thenReturn(Optional.of(plan));

        when(subscriptionRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(paymentRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        subscriptionService.handleSubscriptions();

        // Assert
        assertEquals(SubscriptionStatus.ACTIVE, sub.getStatus());
        assertEquals(0, sub.getRenewalAttempts());
    }
    @Test
    void handleSubscriptions_graceToExpired() {

        Subscription sub = new Subscription();
        sub.setId(1L);
        sub.setStatus(SubscriptionStatus.GRACE);
        sub.setGraceDays(3);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -5);
        sub.setEndDate(cal.getTime());

        when(subscriptionRepository.findAll())
                .thenReturn(List.of(sub));

        when(subscriptionRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        subscriptionService.handleSubscriptions();

        // Assert
        assertEquals(SubscriptionStatus.EXPIRED, sub.getStatus());
    }


    @Test
    void handleSubscriptions_autoRenew_withRecurringAddOn_success() {

        // -------- Arrange --------

        Long subscriptionId = 1L;
        Long planId = 10L;
        Long addOnId = 100L;

        // Subscription (expired yesterday)
        Subscription sub = new Subscription();
        sub.setId(subscriptionId);
        sub.setPlanId(planId);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setAutoRenew(true);
        sub.setRenewalAttempts(0);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        sub.setEndDate(cal.getTime());

        // Plan
        Plan plan = new Plan();
        plan.setId(planId);
        plan.setPrice(1000.0);
        plan.setDurationDays(30);

        // AddOn
        AddOn addOn = new AddOn();
        addOn.setId(addOnId);
        addOn.setPrice(200.0);
        addOn.setRecurring(true);

        // SubscriptionAddOn
        SubscriptionAddOn sa = new SubscriptionAddOn();
        sa.setSubscriptionId(subscriptionId);
        sa.setAddOnId(addOnId);
        sa.setActive(true);

        // Mocking
        when(subscriptionRepository.findAll())
                .thenReturn(List.of(sub));

        when(planRepository.findById(planId))
                .thenReturn(Optional.of(plan));

        when(subscriptionAddOnRepository
                .findBySubscriptionIdAndActiveTrue(subscriptionId))
                .thenReturn(List.of(sa));

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.of(addOn));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // -------- Act --------

        subscriptionService.handleSubscriptions();

        // -------- Assert --------

        assertEquals(SubscriptionStatus.ACTIVE, sub.getStatus());
        assertEquals(0, sub.getRenewalAttempts());

        // Verify payment amount = 1000 + 200
        verify(paymentRepository).save(
                argThat(payment ->
                        payment.getAmount().equals(1200.0)
                )
        );
    }





}
