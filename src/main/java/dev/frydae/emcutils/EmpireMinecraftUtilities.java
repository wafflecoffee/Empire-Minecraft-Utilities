package dev.frydae.emcutils;

import dev.frydae.emcutils.containers.EmpireServer;
import dev.frydae.emcutils.features.UsableItems;
import dev.frydae.emcutils.features.VaultButtons;
import dev.frydae.emcutils.features.VoxelMapIntegration;
import dev.frydae.emcutils.features.vaultButtons.VaultScreen;
import dev.frydae.emcutils.listeners.ChatListener;
import dev.frydae.emcutils.listeners.CommandListener;
import dev.frydae.emcutils.listeners.ServerListener;
import dev.frydae.emcutils.tasks.GetChatAlertPitchTask;
import dev.frydae.emcutils.tasks.GetChatAlertSoundTask;
import dev.frydae.emcutils.tasks.GetLocationTask;
import dev.frydae.emcutils.tasks.Tasks;
import dev.frydae.emcutils.utils.Config;
import dev.frydae.emcutils.utils.MidnightConfig;
import dev.frydae.emcutils.utils.Util;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@SuppressWarnings("InstantiationOfUtilityClass")
public class EmpireMinecraftUtilities implements ClientModInitializer {
  public static final String MODID = "emcutils";
  @Getter private static EmpireMinecraftUtilities instance;
  private static boolean online = false;

  public static void onJoinEmpireMinecraft() {
    if (!online) {
      new ChatListener();
      new CommandListener();
      new ServerListener();
      new UsableItems();

      online = true;
    }
  }

  public static void onPostJoinEmpireMinecraft() {
    if (Util.getInstance().isShouldRunTasks()) {
      Tasks.runTasks(
              new GetChatAlertPitchTask(),
              new GetChatAlertSoundTask(),
              () -> Util.getInstance().setShouldRunTasks(false));
    }

    if (Util.HAS_VOXELMAP) {
      Tasks.runTasks(
              new GetLocationTask(),
              new VoxelMapIntegration()
      );
    }
  }

  @Override
  public void onInitializeClient() {
    instance = this;

    ExecutorService executor = Executors.newCachedThreadPool();
    IntStream.rangeClosed(1, 10).forEach(i -> executor.submit(() -> EmpireServer.getById(i).collectResidences()));
    executor.shutdown();

    HandledScreens.register(VaultButtons.GENERIC_9X7, VaultScreen::new);

    Util.getOnJoinCommandQueue();
    Util.hasVoxelMap();

    MidnightConfig.init(MODID, Config.class);

    LogManager.getLogger(MODID).info("Initialized " + MODID);
  }
}
