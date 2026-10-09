package org.divinitycraft.divinityeconomy.market.exp;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpManagerTest {

    @ParameterizedTest
    @CsvSource({
            "1, 9",
            "15, 37",
            "16, 42",   // crosses into the 16-30 piecewise branch
            "30, 112",
            "31, 121",  // crosses into the 31+ piecewise branch
            "32, 130"
    })
    void getExpToLevelUp_matchesPiecewiseFormula(int level, int expected) {
        assertEquals(expected, ExpManager.getExpToLevelUp(level));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "16, 352",
            "17, 394",  // crosses into the 17-31 piecewise branch
            "31, 1507",
            "32, 1628"  // crosses into the 32+ piecewise branch
    })
    void getExpAtLevel_matchesPiecewiseFormula(int level, int expected) {
        assertEquals(expected, ExpManager.getExpAtLevel(level));
    }

    @Test
    void getPlayerExp_combinesLevelExpAndProgressTowardsNextLevel() {
        Player player = mock(Player.class);
        when(player.getLevel()).thenReturn(10);
        when(player.getExp()).thenReturn(0.5f);

        // getExpAtLevel(10) + round(getExpToLevelUp(10) * 0.5)
        int expected = ExpManager.getExpAtLevel(10) + Math.round(ExpManager.getExpToLevelUp(10) * 0.5f);

        assertEquals(expected, ExpManager.getPlayerExp(player));
    }

    @Test
    void getPlayerExp_atLevelZeroWithNoProgressIsZero() {
        Player player = mock(Player.class);
        when(player.getLevel()).thenReturn(0);
        when(player.getExp()).thenReturn(0f);

        assertEquals(0, ExpManager.getPlayerExp(player));
    }

    @Test
    void changePlayerExp_resetsThenGivesBackCurrentPlusDelta() {
        Player player = mock(Player.class);
        when(player.getLevel()).thenReturn(5);
        when(player.getExp()).thenReturn(0f);

        int currentExp = ExpManager.getPlayerExp(player);
        int result = ExpManager.changePlayerExp(player, 50);

        verify(player).setExp(0);
        verify(player).setLevel(0);
        verify(player).giveExp(currentExp + 50);
        assertEquals(currentExp + 50, result);
    }

    @Test
    void changePlayerExp_supportsNegativeDeltaToRemoveExp() {
        Player player = mock(Player.class);
        when(player.getLevel()).thenReturn(5);
        when(player.getExp()).thenReturn(0f);

        int currentExp = ExpManager.getPlayerExp(player);
        int result = ExpManager.changePlayerExp(player, -20);

        verify(player).giveExp(currentExp - 20);
        assertEquals(currentExp - 20, result);
    }
}
