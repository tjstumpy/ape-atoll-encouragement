package com.apeatollencouragement;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class EncouragementTest
{
    @Test
    public void readsGameCountWithFormattingAndCommas()
    {
        assertEquals(1234, ApeAtollEncouragementPlugin.parseLapCount(
            "Your Ape Atoll Agility lap count is: <col=ff0000>1,234</col>."));
        assertEquals(-1, ApeAtollEncouragementPlugin.parseLapCount(
            "Your Ardougne Agility lap count is: 100."));
        assertEquals(-1, ApeAtollEncouragementPlugin.parseLapCount("test"));
    }

    @Test
    public void handlesMilestoneBoundaries()
    {
        assertEquals("113 more laps until the Zombie monkey! Go! Go!",
            ApeAtollEncouragementPlugin.encouragement(137));
        assertEquals("1 more lap until the Karamjan monkey! Go! Go!",
            ApeAtollEncouragementPlugin.encouragement(99));
        int[] milestones = {100, 250, 500, 1000, 1500, 2000};
        String[] names = {"Karamjan monkey", "Zombie monkey", "Maniacal monkey", "Skeleton monkey", "Kruk jr", "Princely monkey"};
        for (int i = 0; i < milestones.length; i++)
        {
            assertEquals("1 more lap until " + (i == 4 ? "" : "the ") + names[i] + "! Go! Go!",
                ApeAtollEncouragementPlugin.encouragement(milestones[i] - 1));
            assertEquals(names[i] + " unlocked! Ook!",
                ApeAtollEncouragementPlugin.encouragement(milestones[i]));
        }
        assertEquals("2001 laps... Go home...",
            ApeAtollEncouragementPlugin.encouragement(2001));
    }
}
