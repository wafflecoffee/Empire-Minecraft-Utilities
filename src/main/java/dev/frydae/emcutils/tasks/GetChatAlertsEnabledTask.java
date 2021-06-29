package dev.frydae.emcutils.tasks;

import dev.frydae.emcutils.listeners.ChatListener;
import dev.frydae.emcutils.utils.Util;
import net.minecraft.client.MinecraftClient;

public class GetChatAlertsEnabledTask implements Task {
  @Override
  public void execute() {
    Util.getInstance().setHideFeatureMessages(true);
    ChatListener.currentMessage = ChatListener.ChatMessage.CHAT_SOUND_ALERTS;
    assert MinecraftClient.getInstance().player != null;
    MinecraftClient.getInstance().player.sendChatMessage("/ps set chatalerts");
  }

  @Override
  public String getDescription() {
    return "Getting Chat Alerts Enabled";
  }
}
