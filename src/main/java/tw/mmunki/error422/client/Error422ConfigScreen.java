package tw.mmunki.error422.client;

import tw.mmunki.error422.Error422Config;
import tw.mmunki.error422.Error422Settings;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class Error422ConfigScreen extends Screen {
    private static final int BUTTON_WIDTH = 232;
    private static final int[] SESSION_MINUTES = {15, 30, 45, 60, 90, 120, 240};

    private final Screen parent;
    private GridLayout layout;

    public Error422ConfigScreen(Screen parent) {
        super(Component.translatable("error422.config.screen_title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        layout = new GridLayout();
        layout.rowSpacing(4);
        layout.columnSpacing(4);
        layout.defaultCellSetting().padding(1).alignHorizontallyCenter();
        GridLayout.RowHelper helper = layout.createRowHelper(1);
        Error422Settings settings = Error422Config.get();

        helper.addChild(toggle("error422.config.enabled", settings.enabled, value -> settings.enabled = value));
        helper.addChild(toggle("error422.config.autostart", settings.clientAutoStart, value -> settings.clientAutoStart = value));
        helper.addChild(toggle("error422.config.hud", settings.corruptHud, value -> settings.corruptHud = value));
        helper.addChild(toggle("error422.config.corrupt_title", settings.corruptTitleScreen, value -> settings.corruptTitleScreen = value));
        helper.addChild(toggle("error422.config.static", settings.playStatic, value -> settings.playStatic = value));
        helper.addChild(toggle("error422.config.world", settings.allowWorldChanges, value -> settings.allowWorldChanges = value));
        helper.addChild(toggle("error422.config.inventory", settings.allowInventoryScramble, value -> settings.allowInventoryScramble = value));
        helper.addChild(toggle("error422.config.damage", settings.allowDamage, value -> settings.allowDamage = value));
        helper.addChild(toggle("error422.config.creative", settings.disableInCreative, value -> settings.disableInCreative = value));

        helper.addChild(Button.builder(Component.translatable("error422.config.minutes", settings.sessionMinutes), button -> {
            settings.sessionMinutes = nextMinutes(settings.sessionMinutes);
            button.setMessage(Component.translatable("error422.config.minutes", settings.sessionMinutes));
            Error422Config.save();
        }).width(BUTTON_WIDTH).build());

        helper.addChild(Button.builder(Component.translatable("error422.config.intensity", settings.intensity), button -> {
            settings.intensity = settings.intensity >= 3 ? 1 : settings.intensity + 1;
            button.setMessage(Component.translatable("error422.config.intensity", settings.intensity));
            Error422Config.save();
        }).width(BUTTON_WIDTH).build());

        helper.addChild(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .width(BUTTON_WIDTH).build());

        layout.arrangeElements();
        layout.setX((width - layout.getWidth()) / 2);
        layout.setY((height - layout.getHeight()) / 2);
        layout.visitWidgets(this::addRenderableWidget);
    }

    private Button toggle(String key, boolean initial, BooleanSetter setter) {
        return Button.builder(stateLabel(key, initial), button -> {
            boolean value = !readState(key);
            setter.set(value);
            button.setMessage(stateLabel(key, value));
            Error422Config.save();
        }).width(BUTTON_WIDTH).build();
    }

    private static boolean readState(String key) {
        Error422Settings settings = Error422Config.get();
        return switch (key) {
            case "error422.config.enabled" -> settings.enabled;
            case "error422.config.autostart" -> settings.clientAutoStart;
            case "error422.config.hud" -> settings.corruptHud;
            case "error422.config.corrupt_title" -> settings.corruptTitleScreen;
            case "error422.config.static" -> settings.playStatic;
            case "error422.config.world" -> settings.allowWorldChanges;
            case "error422.config.inventory" -> settings.allowInventoryScramble;
            case "error422.config.damage" -> settings.allowDamage;
            case "error422.config.creative" -> settings.disableInCreative;
            default -> false;
        };
    }

    private static int nextMinutes(int current) {
        for (int index = 0; index < SESSION_MINUTES.length; index++) {
            if (SESSION_MINUTES[index] == current) {
                return SESSION_MINUTES[(index + 1) % SESSION_MINUTES.length];
            }
        }
        return SESSION_MINUTES[0];
    }

    private static Component stateLabel(String key, boolean value) {
        return Component.translatable(key, value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(font, title, width / 2, 15, 0xFFFF2222);
        graphics.centeredText(font, Component.translatable("error422.config.note"),
                width / 2, height - 24, 0xA0A0A0);
    }

    @Override
    public void onClose() {
        Error422Config.save();
        minecraft.gui.setScreen(parent);
    }

    @FunctionalInterface
    private interface BooleanSetter {
        void set(boolean value);
    }
}
