package org.divinitycraft.divinityeconomy.market.pricing;

import org.divinitycraft.divinityeconomy.market.MarketableToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StaticBottomlessPricingModelTest {

    private StaticBottomlessPricingModel model;

    @BeforeEach
    void setUp() {
        model = new StaticBottomlessPricingModel(0.0, 1000.0);
    }

    @Test
    void calculatePrice_usesTokenPriceLikeStaticModel() {
        MarketableToken token = mock(MarketableToken.class);
        when(token.getPrice()).thenReturn(20.0);

        double price = model.calculatePrice(token, 100, 1000, 1000, 3, 1.0, true, false);

        assertEquals(60.0, price);
    }

    @Test
    void doesNotUpdateQuantityOnTransaction_unlikeStandardStaticModel() {
        assertFalse(model.updatesQuantityOnTransaction());
    }

    @Test
    void isDynamic_isFalse() {
        assertFalse(model.isDynamic());
    }

    @Test
    void calculateStock_isNotApplicableAndReturnsZero() {
        assertEquals(0, model.calculateStock(100, 50, 1.0, 1.0));
    }

    @Test
    void getInflation_isAlwaysOne() {
        assertEquals(1.0, model.getInflation(1000, 1));
    }
}
