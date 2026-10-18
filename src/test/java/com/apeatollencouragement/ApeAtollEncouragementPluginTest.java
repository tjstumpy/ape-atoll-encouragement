package com.apeatollencouragement;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ApeAtollEncouragementPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(ApeAtollEncouragementPlugin.class);
        RuneLite.main(args);
    }
}
