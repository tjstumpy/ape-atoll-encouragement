package com.apeatollencouragement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@PluginDescriptor(
    name = "Ape Atoll Encouragement",
    description = "Bobawu cheers you on after each Ape Atoll agility lap",
    tags = {"agility", "ape atoll", "bobawu"}
)
public class ApeAtollEncouragementPlugin extends Plugin
{
    private static final Pattern LAP_COUNT_PATTERN = Pattern.compile(
        "Your Ape Atoll(?: Agility)? lap count is: ([0-9,]+)\\.?", Pattern.CASE_INSENSITIVE);
    private static final int[] BACKPACK_LAPS = {100, 250, 500, 1000, 1500, 2000};
    private static final String[] MONKEY_NAMES = {"Karamjan monkey", "Zombie monkey", "Maniacal monkey", "Skeleton monkey", "Kruk jr", "Princely monkey"};
    private static final int SPEECH_CYCLES = 250;

    @Inject
    private Client client;

    private int lastLapCount = -1;

    @Override
    protected void startUp()
    {
        lastLapCount = -1;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOGIN_SCREEN
            || event.getGameState() == GameState.HOPPING)
        {
            lastLapCount = -1;
        }
    }

    static int parseLapCount(String message)
    {
        Matcher matcher = LAP_COUNT_PATTERN.matcher(Text.removeTags(message));
        if (!matcher.matches())
        {
            return -1;
        }
        try
        {
            return Integer.parseInt(matcher.group(1).replace(",", ""));
        }
        catch (NumberFormatException ignored)
        {
            return -1;
        }
    }

    static String encouragement(int laps)
    {
        for (int i = 0; i < BACKPACK_LAPS.length; i++)
        {
            int milestone = BACKPACK_LAPS[i];
            if (laps == milestone)
            {
                return MONKEY_NAMES[i] + " unlocked! Ook!";
            }
            if (laps < milestone)
            {
                int remaining = milestone - laps;
                String target = MONKEY_NAMES[i].equals("Kruk jr")
                    ? MONKEY_NAMES[i] : "the " + MONKEY_NAMES[i];
                return remaining + (remaining == 1 ? " more lap" : " more laps")
                    + " until " + target + "! Go! Go!";
            }
        }
        return laps + " laps... Go home...";
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE
            && event.getType() != ChatMessageType.SPAM)
        {
            return;
        }
        int laps = parseLapCount(event.getMessage());
        if (laps < 0 || laps == lastLapCount || client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        lastLapCount = laps;
        for (NPC npc : client.getTopLevelWorldView().npcs())
        {
            if ("Bobawu".equalsIgnoreCase(npc.getName()))
            {
                npc.setOverheadText(encouragement(laps));
                npc.setOverheadCycle(SPEECH_CYCLES);
                break;
            }
        }
    }
}
