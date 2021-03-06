package dev.frydae.emcutils.utils;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.frydae.emcutils.EmpireMinecraftUtilities;

public class ModMenuIntegration implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return parent -> MidnightConfig.getScreen(parent, EmpireMinecraftUtilities.MODID);
  }
}
