package org.divinitycraft.divinityeconomy.market.pricing;

import org.divinitycraft.divinityeconomy.market.MarketableToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class V2PricingModelTest {

    private static final double DELTA = 0.0001;

    private V2PricingModel model;

    @BeforeEach
    void setUp() {
        model = new V2PricingModel(0.0, 100_000.0);
    }

    @Test
    void getPrice_atBaselineStockEqualsBasePrice() {
        // supplyRatio == 1 -> priceMultiplier == 1 -> raw price == BASE_PRICE (1.0)
        assertEquals(1.0, model.getPrice(100, 100, 1.0, 1.0), DELTA);
    }

    @Test
    void getPrice_risesAsStockFallsWithDefaultElasticity() {
        double priceAtFullStock = model.getPrice(100, 100, 1.0, 1.0);
        double priceAtHalfStock = model.getPrice(100, 50, 1.0, 1.0);

        assertTrue(priceAtHalfStock > priceAtFullStock);
        // (1 / 0.5)^0.7 == 2^0.7
        assertEquals(Math.pow(2, 0.7), priceAtHalfStock, DELTA);
    }

    @Test
    void getPrice_fallsAsStockRisesWithDefaultElasticity() {
        double priceAtFullStock = model.getPrice(100, 100, 1.0, 1.0);
        double priceAtDoubleStock = model.getPrice(100, 200, 1.0, 1.0);

        assertTrue(priceAtDoubleStock < priceAtFullStock);
        assertEquals(Math.pow(0.5, 0.7), priceAtDoubleStock, DELTA);
    }

    @Test
    void getPrice_zeroStockIsTreatedAsOneToAvoidDivisionByZero() {
        assertEquals(model.getPrice(100, 1, 1.0, 1.0), model.getPrice(100, 0, 1.0, 1.0), DELTA);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.3, 0.5, 0.7, 1.0, 1.2})
    void getPrice_higherElasticityMeansSteeperPriceReactionToScarcity(double elasticity) {
        double priceAtFullStock = model.getPrice(100, 100, 1.0, 1.0, elasticity);
        double priceAtScarcity = model.getPrice(100, 10, 1.0, 1.0, elasticity);

        assertEquals(1.0, priceAtFullStock, DELTA);
        assertTrue(priceAtScarcity > priceAtFullStock);
        assertEquals(Math.pow(10, elasticity), priceAtScarcity, DELTA);
    }

    @Test
    void getPrice_appliesScaleAndInflationMultiplicatively() {
        double base = model.getPrice(100, 50, 1.0, 1.0);
        assertEquals(base * 2, model.getPrice(100, 50, 2.0, 1.0), DELTA);
        assertEquals(base * 3, model.getPrice(100, 50, 1.0, 3.0), DELTA);
    }

    @Test
    void getPrice_isClampedToConfiguredBounds() {
        V2PricingModel constrained = new V2PricingModel(0.0, 5.0);
        assertEquals(5.0, constrained.getPrice(100, 1, 1.0, 1.0), DELTA);
    }

    @Test
    void calculatePrice_usesTokenElasticityAndDecreasesStockOnPurchase() {
        MarketableToken token = mock(MarketableToken.class);
        when(token.getQuantity()).thenReturn(100);
        when(token.getElasticity()).thenReturn(0.7);

        double expected = model.getPrice(100, 100, 1.0, 1.0, 0.7) + model.getPrice(100, 99, 1.0, 1.0, 0.7);
        double actual = model.calculatePrice(token, 100, 1000, 1000, 2, 1.0, true, false);

        assertEquals(expected, actual, DELTA);
    }

    @Test
    void calculateStock_isInverseOfGetPriceAtDefaultElasticity() {
        // calculateStock always assumes the default ITEM_ELASTICITY (0.7), matching the
        // no-arg getPrice() overload used when calculating stock for a price.
        double price = model.getPrice(100, 50, 1.0, 1.0);
        int stock = model.calculateStock(100, price, 1.0, 1.0);

        assertTrue(Math.abs(stock - 50) <= 1, "Expected stock close to 50 but was " + stock);
    }

    @Test
    void getInflation_isOneAtBaselineMarketSize() {
        assertEquals(1.0, model.getInflation(1000, 1000), DELTA);
    }

    @Test
    void getInflation_risesAsMarketSizeShrinks() {
        double baseline = model.getInflation(1000, 1000);
        double shrunk = model.getInflation(1000, 500);

        assertTrue(shrunk > baseline);
        assertEquals(Math.pow(2, 0.3), shrunk, DELTA);
    }

    @Test
    void getInflation_treatsNonPositiveMarketSizeAsOneToAvoidDivisionByZero() {
        assertEquals(model.getInflation(1000, 1), model.getInflation(1000, 0), DELTA);
        assertEquals(model.getInflation(1000, 1), model.getInflation(1000, -50), DELTA);
    }

    @Test
    void isDynamic_isTrue() {
        assertTrue(model.isDynamic());
    }
}
