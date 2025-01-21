package store.aurora.user.dto;

import store.aurora.user.domain.Rank;

import java.math.BigDecimal;

public record UserRank (
        long id,
        Rank rankName,
        int minAmount,
        int maxAmount,
        BigDecimal pointRate
){ }