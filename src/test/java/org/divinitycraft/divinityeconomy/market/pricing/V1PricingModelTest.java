package org.divinitycraft.divinityeconomy.market.pricing;

import org.divinitycraft.divinityeconomy.market.MarketableToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class V1PricingModelTest {

    private static final double DELTA = 0.0001;

    private V1PricingModel model;

    @BeforeEach
    void setUp() {
        model = new V1PricingModel(0.0, 100_000.0);
    }

    @Test
    void getPrice_atBaselineStockEqualsFifteen() {
        // baseQuantity == currentQuantity -> scale of 1 -> price of 15
        assertEquals(15.0, model.getPrice(100, 100, 1.0, 1.0), DELTA);
    }

    @Test
    void getPrice_risesAsStockFalls() {
        double priceAtFullStock = model.getPrice(100, 100, 1.0, 1.0);
        double priceAtHalfStock = model.getPrice(100, 50, 1.0, 1.0);

        assertTrue(priceAtHalfStock > priceAtFullStock);
        assertEquals(30.0, priceAtHalfStock, DELTA);
    }

    @Test
    void getPrice_fallsAsStockRises() {
        double priceAtFullStock = model.getPrice(100, 100, 1.0, 1.0);
        double priceAtDoubleStock = model.getPrice(100, 200, 1.0, 1.0);

        assertTrue(priceAtDoubleStock < priceAtFullStock);
        assertEquals(7.5, priceAtDoubleStock, DELTA);
    }

    @Test
    void getPrice_zeroStockIsTreatedAsOneToAvoidDivisionByZero() {
        assertEquals(model.getPrice(100, 1, 1.0, 1.0), model.getPrice(100, 0, 1.0, 1.0), DELTA);
    }

    @Test
    void getPrice_appliesScaleAndInflationMultiplicatively() {
        double base = model.getPrice(100, 100, 1.0, 1.0);
        assertEquals(base * 2, model.getPrice(100, 100, 2.0, 1.0), DELTA);
        assertEquals(base * 3, model.getPrice(100, 100, 1.0, 3.0), DELTA);
    }

    @Test
    void getPrice_isClampedToConfiguredBounds() {
        V1PricingModel constrained = new V1PricingModel(0.0, 10.0);
        assertEquals(10.0, constrained.getPrice(100, 1, 1.0, 1.0), DELTA);
    }

    @Test
    void calculatePrice_summesPerUnitPriceWhileDecreasingStockOnPurchase() {
        MarketableToken token = mock(MarketableToken.class);
        when(token.getQuantity()).thenReturn(100);

        double expected = model.getPrice(100, 100, 1.0, 1.0) + model.getPrice(100, 99, 1.0, 1.0);
        double actual = model.calculatePrice(token, 100, 1000, 1000, 2, 1.0, true, false);

        assertEquals(expected, actual, DELTA);
    }

    @Test
    void calculatePrice_summesPerUnitPriceWhileIncreasingStockOnSale() {
        MarketableToken token = mock(MarketableToken.class);
        when(token.getQuantity()).thenReturn(100);

        double expected = model.getPrice(100, 101, 1.0, 1.0) + model.getPrice(100, 102, 1.0, 1.0);
        double actual = model.calculatePrice(token, 100, 1000, 1000, 2, 1.0, false, false);

        assertEquals(expected, actual, DELTA);
    }

    @Test
    void calculateStock_isInverseOfGetPrice() {
        double price = model.getPrice(100, 50, 1.0, 1.0);
        int stock = model.calculateStock(100, price, 1.0, 1.0);

        assertEquals(50, stock);
    }

    @Test
    void getInflation_isLinearRatioOfMarketSize() {
        assertEquals(1.0, model.getInflation(1000, 1000), DELTA);
        assertEquals(2.0, model.getInflation(1000, 500), DELTA);
    }

    @Test
    void isDynamic_isTrue() {
        assertTrue(model.isDynamic());
    }

    @Test
    void updatesQuantityOnTransaction_defaultsToTrue() {
        assertTrue(model.updatesQuantityOnTransaction());
    }
}
