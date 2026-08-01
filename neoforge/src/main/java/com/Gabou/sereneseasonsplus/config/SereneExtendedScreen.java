package com.Gabou.sereneseasonsplus.config;

import com.Gabou.sereneseasonsplus.client.config.SereneExtendedList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * Simple in-game configuration screen.
 */
public class SereneExtendedScreen extends Screen {
    private static final int MAX_PANEL_WIDTH = 440;
    private static final int PANEL_MARGIN = 16;
    private static final int PANEL_TOP = 28;
    private static final int PANEL_BOTTOM_MARGIN = 20;
    private static final int FOOTER_HEIGHT = 40;

    private final Screen parent;
    private boolean snowFeatureEnabled;
    private int tickSnowReplacerThreshold;
    private int maxSnowHeight;

    private EditBox maxReplacerBox;
    private EditBox maxSnowHeightBox;
    private EditBox dayLengthBox;

    private EditBox nightLengthBox;

    private boolean seasonalDaylightCycle;

    private boolean betterDaysDynamicTimeCompat;

    private boolean customDayCycle;

    private double customDayLength;

    private double customNightLength;

    private boolean grassFlowerGrowth;

    private Component replacerLabel = Component.literal("Common Feature Threshold:");
    private Component pillerLabel = Component.literal("");
    private Component snowHeightLabel = Component.literal("Max Snow Height (layers):");
    private Component nightLabel = Component.literal("Custom Night Speed:");
    private Component dayLabel = Component.literal("Custom Day Speed:");
    private Component grassFlowerLabel = Component.literal("Grass Flower:");

    private SereneExtendedList list;
    private Button seasonalCycleButton;
    private Button customCycleButton;
    private Component errorMessage;
    /**
     * Creates the configuration screen.
     *
     * @param parent parent screen to return to
     */
    public SereneExtendedScreen(Screen parent) {
        super(Component.literal("Serene Seasons Plus Config"));
        this.parent = parent;
    }

    @Override
    /**
     * Initializes widgets and loads values from the config.
     */
    protected void init() {
        this.snowFeatureEnabled = SereneExtendedConfig.SNOWSTORM_ENABLED.get();
        this.tickSnowReplacerThreshold = SereneExtendedConfig.TICK_SNOW_REPLACER.get();
        this.maxSnowHeight = SereneExtendedConfig.MAX_SNOW_ACCUMULATION_LAYERS.get();
        this.seasonalDaylightCycle = SereneExtendedConfig.ENABLE_SEASONAL_DAYLIGHT_CYCLE.get();
        this.betterDaysDynamicTimeCompat = SereneExtendedConfig.ENABLE_BETTER_DAYS_DYNAMIC_TIME_COMPAT.get();
        this.customDayCycle = SereneExtendedConfig.CUSTOM_CYCLE_LENGTH.get();
        this.customDayLength = SereneExtendedConfig.CUSTOM_DAY_LENGTH.get();
        this.customNightLength = SereneExtendedConfig.CUSTOM_NIGHT_LENGTH.get();
        this.grassFlowerGrowth = SereneExtendedConfig.GRASS_FLOWER_GROWTH_ENABLED.get();

        int panelW = Math.min(MAX_PANEL_WIDTH, Math.max(280, this.width - PANEL_MARGIN * 2));
        int panelX = (this.width - panelW) / 2;
        int top = PANEL_TOP;
        int bottom = this.height - PANEL_BOTTOM_MARGIN;
        int listTop = top + 24;
        int listHeight = Math.max(40, bottom - listTop - FOOTER_HEIGHT);

        this.list = new SereneExtendedList(this.minecraft, panelW - 16, listHeight, listTop, 24);
        this.list.setX(panelX + 8);
        this.addRenderableWidget(this.list);

        var snowFeatureBtn = Button.builder(toggleLabel(snowFeatureEnabled), b -> {
            snowFeatureEnabled = !snowFeatureEnabled;
            b.setMessage(toggleLabel(snowFeatureEnabled));
        }).bounds(0,0,200,20).build();
        this.list.addRow(Component.literal("Snow Features"), snowFeatureBtn);

        this.seasonalCycleButton = Button.builder(toggleLabel(seasonalDaylightCycle), b -> {
            seasonalDaylightCycle = !seasonalDaylightCycle;
            if (seasonalDaylightCycle) {
                customDayCycle = false;
                this.customCycleButton.setMessage(toggleLabel(false));
            }
            b.setMessage(toggleLabel(seasonalDaylightCycle));
        }).bounds(0,0,200,20).build();
        this.list.addRow(Component.literal("Seasonal Daylight Cycle"), this.seasonalCycleButton);

        var betterDaysCompatBtn = Button.builder(toggleLabel(betterDaysDynamicTimeCompat), b -> {
            betterDaysDynamicTimeCompat = !betterDaysDynamicTimeCompat;
            b.setMessage(toggleLabel(betterDaysDynamicTimeCompat));
        }).bounds(0,0,200,20).build();
        this.list.addRow(Component.literal("Better Days Time Compat"), betterDaysCompatBtn);

        var grassFlowerBtn = Button.builder(toggleLabel(grassFlowerGrowth), b -> {
            grassFlowerGrowth = !grassFlowerGrowth;
            b.setMessage(toggleLabel(grassFlowerGrowth));
        }).bounds(0,0,200,20).build();
        this.list.addRow(Component.literal("Grass and Flower Growth"), grassFlowerBtn);

        this.customCycleButton = Button.builder(toggleLabel(customDayCycle), b -> {
            customDayCycle = !customDayCycle;
            if (customDayCycle) {
                seasonalDaylightCycle = false;
                this.seasonalCycleButton.setMessage(toggleLabel(false));
            }
            b.setMessage(toggleLabel(customDayCycle));
        }).bounds(0,0,200,20).build();
        this.list.addRow(Component.literal("Custom Daylight Cycle"), this.customCycleButton);


        this.maxReplacerBox = new EditBox(this.font, 0, 0, 200, 20, Component.empty());
        this.maxReplacerBox.setValue(Integer.toString(tickSnowReplacerThreshold));
        this.list.addRow(Component.literal("Common Feature Threshold"), this.maxReplacerBox);

        this.maxSnowHeightBox = new EditBox(this.font, 0, 0, 200, 20, Component.empty());
        this.maxSnowHeightBox.setValue(Integer.toString(this.maxSnowHeight));
        this.list.addRow(Component.literal("Max Snow Height (layers)"), this.maxSnowHeightBox);

        this.nightLengthBox = new EditBox(this.font, 0, 0, 200, 20, Component.empty());
        this.nightLengthBox.setValue(Double.toString(customNightLength));
        this.list.addRow(Component.literal("Custom Night Speed"), this.nightLengthBox);

        this.dayLengthBox = new EditBox(this.font, 0, 0, 200, 20, Component.empty());
        this.dayLengthBox.setValue(Double.toString(customDayLength));
        this.list.addRow(Component.literal("Custom Day Speed"), this.dayLengthBox);


        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.done"), b -> {
                    if (saveChanges()) {
                        this.minecraft.setScreen(parent);
                    }
                }).bounds(panelX + (panelW - 200) / 2, bottom - 28, 200, 20).build()
        );
    }


    @Override
    /**
     * Draws background, panel chrome, and delegates to list/widgets.
     */
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        if (this.errorMessage != null) {
            g.drawCenteredString(this.font, this.errorMessage, this.width / 2, this.height - 47, 0xFFFF5555);
        }
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(g, mouseX, mouseY, partialTick);
        int panelW = Math.min(MAX_PANEL_WIDTH, Math.max(280, this.width - PANEL_MARGIN * 2));
        int panelX = (this.width - panelW) / 2;
        int bottom = this.height - PANEL_BOTTOM_MARGIN;
        g.fill(panelX, PANEL_TOP, panelX + panelW, bottom, 0xCC101010);
        g.drawCenteredString(this.font, this.title, this.width / 2, PANEL_TOP + 8, 0xFFFFFF);
    }

    /**
     * Formats a toggle label with ON/OFF state.
     */
    private Component toggleLabel(boolean enabled) {
        return Component.literal(enabled ? "ON" : "OFF");
    }

    /**
     * Validates inputs and writes changes to config, then persists them.
     */
    private boolean saveChanges() {
        int parsed2 = this.tickSnowReplacerThreshold;
        int parsedSnowHeight = this.maxSnowHeight;
        double parsed3 = this.customDayLength;
        double parsed4 = this.customNightLength;


        try {
            parsed2 = Integer.parseInt(this.maxReplacerBox.getValue());
            parsedSnowHeight = Integer.parseInt(this.maxSnowHeightBox.getValue());
        } catch (NumberFormatException ignored) {
            this.errorMessage = Component.literal("Snow settings must be whole numbers.");
            return false;
        }

        try {
            parsed3 = Double.parseDouble(this.dayLengthBox.getValue());
            parsed4 = Double.parseDouble(this.nightLengthBox.getValue());
        } catch (NumberFormatException ignored) {
            this.errorMessage = Component.literal("Day and night speeds must be numbers.");
            return false;
        }
        SereneExtendedConfig.TICK_SNOW_REPLACER.set(parsed2);
        SereneExtendedConfig.SNOWSTORM_ENABLED.set(snowFeatureEnabled);
        SereneExtendedConfig.MAX_SNOW_ACCUMULATION_LAYERS.set(parsedSnowHeight);
        SereneExtendedConfig.ENABLE_SEASONAL_DAYLIGHT_CYCLE.set(seasonalDaylightCycle);
        SereneExtendedConfig.ENABLE_BETTER_DAYS_DYNAMIC_TIME_COMPAT.set(betterDaysDynamicTimeCompat);
        SereneExtendedConfig.CUSTOM_CYCLE_LENGTH.set(customDayCycle);
        SereneExtendedConfig.CUSTOM_DAY_LENGTH.set(parsed3);
        SereneExtendedConfig.CUSTOM_NIGHT_LENGTH.set(parsed4);
        SereneExtendedConfig.GRASS_FLOWER_GROWTH_ENABLED.set(grassFlowerGrowth);

        try {
            SereneExtendedConfig.COMMON_SPEC.save();
            this.errorMessage = null;
            return true;
        } catch (RuntimeException exception) {
            this.errorMessage = Component.literal("Failed to save config: " + exception.getMessage());
            return false;
        }
    }


    @Override
    /**
     * Closes the screen and returns to the parent.
     */
    public void onClose() {
        this.setFocused(false);
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    /**
     * Delegates click handling to the list and then widgets.
     */
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.list != null && this.list.mouseClicked(event, doubleClick)) return true;
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.list != null && this.list.keyPressed(event)) return true;


        if (this.maxReplacerBox != null && this.maxReplacerBox.keyPressed(event)) return true;
        if (this.maxSnowHeightBox   != null && this.maxSnowHeightBox.keyPressed(event)) return true;
        if (this.nightLengthBox != null && this.nightLengthBox.keyPressed(event)) return true;
        if (this.dayLengthBox   != null && this.dayLengthBox.keyPressed(event)) return true;

        return super.keyPressed(event);
    }

    /**
     * Forwards typed characters to the list and text boxes.
     */
    @Override
    public boolean charTyped(CharacterEvent event) {
        if (this.list != null && this.list.charTyped(event)) return true;

        if (this.maxReplacerBox != null && this.maxReplacerBox.charTyped(event)) return true;
        if (this.maxSnowHeightBox   != null && this.maxSnowHeightBox.charTyped(event)) return true;
        if (this.nightLengthBox != null && this.nightLengthBox.charTyped(event)) return true;
        if (this.dayLengthBox   != null && this.dayLengthBox.charTyped(event)) return true;

        return super.charTyped(event);
    }


}

