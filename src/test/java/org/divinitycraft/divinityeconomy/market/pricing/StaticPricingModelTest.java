package org.divinitycraft.divinityeconomy.market.pricing;

import org.divinitycraft.divinityeconomy.market.MarketableToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StaticPricingModelTest {

    private StaticPricingModel model;

    @BeforeEach
    void setUp() {
        model = new StaticPricingModel(0.0, 1000.0);
    }

    @Test
    void calculatePrice_ignoresStockAndUsesTokenPrice() {
        MarketableToken token = mock(MarketableToken.class);
        when(token.getPrice()).thenReturn(10.0);

        double price = model.calculatePrice(token, 100, 1000, 1000, 5, 1.0, true, false);

        assertEquals(50.0, price);
    }

    @Test
    void calculatePrice_isIdenticalForBuyAndSell() {
        MarketableToken token = mock(MarketableToken.class);
        when(token.getPrice()).thenReturn(10.0);

        double buyPrice = model.calculatePrice(token, 100, 1000, 1000, 5, 1.0, true, false);
        double sellPrice = model.calculatePrice(token, 100, 1000, 1000, 5, 1.0, false, false);

        assertEquals(buyPrice, sellPrice);
    }

    @Test
    void calculatePrice_isConstrainedToMaxItemValue() {
        MarketableToken token = mock(MarketableToken.class);
        when(token.getPrice()).thenReturn(10_000.0);

        double price = model.calculatePrice(token, 100, 1000, 1000, 1, 1.0, true, false);

        assertEquals(1000.0, price);
    }

    @Test
    void getPrice_appliesScaleOnlyNoInflation() {
        // baseQuantity is repurposed as the item's configured price for the static model
        assertEquals(30.0, model.getPrice(15.0, 999 /* ignored */, 2.0, 5.0 /* ignored */));
    }

    @Test
    void calculateStock_isNotApplicableAndReturnsZero() {
        assertEquals(0, model.calculateStock(100, 50, 1.0, 1.0));
    }

    @Test
    void getInflation_isAlwaysOne() {
        assertEquals(1.0, model.getInflation(1000, 1));
        assertEquals(1.0, model.getInflation(1000, 5000));
    }

    @Test
    void isDynamic_isFalse() {
        assertEquals(false, model.isDynamic());
    }

    @Test
    void updatesQuantityOnTransaction_defaultsToTrue() {
        assertEquals(true, model.updatesQuantityOnTransaction());
    }
}
