package dev.frydae.emcutils.features.vaultButtons;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class VaultScreen extends HandledScreen<VaultScreenHandler> implements ScreenHandlerProvider<VaultScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("emcutils", "textures/gui/container/generic_63.png");
    private final int rows;
    private final int vaultPage;
    private boolean shouldCallClose = true;

    private final int[] slotOffsets = {8, 26, 44, 62, 80, 98, 116, 134, 152};

    public VaultScreen(VaultScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.passEvents = false;
        this.rows = 6;
        this.backgroundHeight = 114 + 7 * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;

        String page = title.getString().split(" ")[1];
        this.vaultPage = NumberUtils.isParsable(page) ? Integer.parseInt(page) : 1;
    }

    /**
     * @param amount the amount of pages to go back
     * @return an {@link ItemStack player head} with a left arrow
     */
    private ItemStack getPreviousHead(int amount) {
        ItemStack stack = Items.PLAYER_HEAD.getDefaultStack();

        stack.setCustomName(new LiteralText("Go back " + amount + " page" + (amount > 1 ? "s" : "")).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GREEN)).withItalic(false)));

        GameProfile profile = new GameProfile(UUID.fromString("1f961930-4e97-47b7-a5a1-2cc5150f3764"), "");
        profile.getProperties().put("textures", new Property("Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzNlOTE5MTlkYjBhY2VmZGMyNzJkNjdmZDg3YjRiZTg4ZGM0NGE5NTg5NTg4MjQ0NzRlMjFlMDZkNTNlNiJ9fX0="));

        stack.getTag().put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), profile));

        return stack;
    }

    private ItemStack getNextHead(int amount) {
        ItemStack stack = Items.PLAYER_HEAD.getDefaultStack();

        stack.setCustomName(new LiteralText("Go forward " + amount + " page" + (amount > 1 ? "s" : "")).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GREEN)).withItalic(false)));

        GameProfile profile = new GameProfile(UUID.fromString("1f961930-4e97-47b7-a5a1-2cc5150f3764"), "");
        profile.getProperties().put("textures", new Property("Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNmYzUyMjY0ZDhhZDllNjU0ZjQxNWJlZjAxYTIzOTQ3ZWRiY2NjY2Y2NDkzNzMyODliZWE0ZDE0OTU0MWY3MCJ9fX0="));

        stack.getTag().put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), profile));

        return stack;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        for (int i = 4; i > 0; i--) {
            if (vaultPage > i) {
                drawButton(matrices, getPreviousHead(i), mouseX, mouseY, slotOffsets[4 - i], i + "");
            }
        }

        ItemStack chest = Items.CHEST.getDefaultStack();
        chest.setCustomName(new LiteralText("View your vaults").setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GREEN)).withItalic(false)));
        drawButton(matrices, chest, mouseX, mouseY, slotOffsets[4], "");

        for (int i = 1; i <= 4; i++) {
            drawButton(matrices, getNextHead(i), mouseX, mouseY, slotOffsets[4 + i], i + "");
        }

        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    private void drawButton(MatrixStack matrices, ItemStack button, int mouseX, int mouseY, int buttonX, String amountText) {
        int midWidth = (this.width - this.backgroundWidth) / 2;
        int midHeight = (this.height - this.backgroundHeight) / 2;

        this.drawItem(button, midWidth + buttonX, midHeight + 125, amountText);

        if (mouseX >= midWidth + buttonX && mouseX <= midWidth + buttonX + 15) {
            if (mouseY >= midHeight + 126 && mouseY <= midHeight + 141) {
                matrices.translate(0, 0, 225);
                this.fillGradient(matrices, midWidth + buttonX, midHeight + 125, midWidth + buttonX + 16, midHeight + 125 + 16, 0x80ffffff, 0x80ffffff);
                this.renderTooltip(matrices, button, mouseX, mouseY);
                matrices.translate(0, 0, -225);
            }
        }
    }

    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, (rows + 1) * 18 + 17);
        this.drawTexture(matrices, i, j + this.rows * 18 + 17, 0, 126, this.backgroundWidth, 128);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 4; i > 0; i--) {
            handleClick(slotOffsets[4 - i], mouseX, mouseY, "/vault " + (vaultPage - i));
        }

        handleClick(slotOffsets[4], mouseX, mouseY, "/vaults");

        for (int i = 1; i <= 4; i++) {
            handleClick(slotOffsets[4 + i], mouseX, mouseY, "/vault " + (vaultPage + i));
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleClick(int buttonX, double mouseX, double mouseY, String command) {
        int midWidth = (this.width - this.backgroundWidth) / 2;
        int midHeight = (this.height - this.backgroundHeight) / 2;

        if (mouseX >= midWidth + buttonX && mouseX < midWidth + buttonX + 16) {
            if (mouseY >= midHeight + 126 && mouseY <= midHeight + 141) {
                this.shouldCallClose = false;
                MinecraftClient.getInstance().player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_SNARE, 4F, 1F);
                MinecraftClient.getInstance().player.sendChatMessage(command);
            }
        }
    }

    @Override
    public void onClose() {
        if (shouldCallClose) {
            super.onClose();
        } else {
            shouldCallClose = true;
        }
    }
}