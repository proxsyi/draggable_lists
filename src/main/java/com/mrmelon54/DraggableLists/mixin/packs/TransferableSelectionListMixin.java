package com.mrmelon54.DraggableLists.mixin.packs;

import com.mrmelon54.DraggableLists.DragItem;
import com.mrmelon54.DraggableLists.DragList;
import com.mrmelon54.DraggableLists.DragManager;
import com.mrmelon54.DraggableLists.DraggableLists;
import com.mrmelon54.DraggableLists.duck.AbstractPackDuckProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TransferableSelectionList.class)
@Environment(EnvType.CLIENT)
public abstract class TransferableSelectionListMixin extends ObjectSelectionList<TransferableSelectionList.Entry> implements DragList<PackSelectionModel.Entry, TransferableSelectionList.PackEntry> {
    @Unique
    private final DragManager<PackSelectionModel.Entry, TransferableSelectionList.PackEntry> draggable_lists$dragManager = new DragManager<>(this);

    public TransferableSelectionListMixin(Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l);
    }

    @Override
    protected void extractListItems(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        draggable_lists$dragManager.renderListItems(guiGraphics, mouseX, mouseY, tickDelta);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (draggable_lists$dragManager.mouseReleased(event.x(), event.y(), event.button())) return true;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (DraggableLists.CONFIG.resourcePackDraggingEnabled.isEnabled() && !isOverScrollbar(event.x(), event.y()) && draggable_lists$dragManager.mouseDragged(event.x(), event.y(), event.button(), deltaX, deltaY)) return true;
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (draggable_lists$dragManager.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, delta);
        draggable_lists$dragManager.renderWidget(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public DragItem<PackSelectionModel.Entry, TransferableSelectionList.PackEntry> draggable_lists$getEntryAtPosition(double mouseX, double mouseY) {
        TransferableSelectionList.Entry entryAtPosition = getEntryAtPosition(mouseX, mouseY);
        if (entryAtPosition instanceof TransferableSelectionList.PackEntry packEntry) {
            return (DragItem<PackSelectionModel.Entry, TransferableSelectionList.PackEntry>) packEntry;
        }
        return null;
    }

    @Override
    public int draggable_lists$getIndexOfEntry(DragItem<PackSelectionModel.Entry, TransferableSelectionList.PackEntry> selectedItem) {
        return children().indexOf(selectedItem.draggable_lists$getUnderlyingEntry());
    }

    @Override
    public void draggable_lists$setDragging(boolean b) {
        super.setDragging(b);
    }

    @Override
    public int draggable_lists$getY() {
        return getY();
    }

    @Override
    public int draggable_lists$getBottom() {
        return getBottom();
    }

    @Override
    public int draggable_lists$getItemHeight() {
        return defaultEntryHeight;
    }

    @Override
    public int draggable_lists$getRowTop(int i) {
        return getRowTop(i);
    }

    @Override
    public int draggable_lists$getRowBottom(int i) {
        return getRowBottom(i);
    }

    @Override
    public double draggable_lists$getRowLeft() {
        return getRowLeft();
    }

    @Override
    public int draggable_lists$getRowWidth() {
        return getRowWidth();
    }

    @Override
    public double draggable_lists$getScrollAmount() {
        return scrollAmount();
    }

    @Override
    public void draggable_lists$setScrollAmount(double v) {
        setScrollAmount(v);
    }

    @Override
    public void draggable_lists$moveEntry(DragItem<PackSelectionModel.Entry, TransferableSelectionList.PackEntry> item, int n) {
        if (item.draggable_lists$getUnderlyingData() instanceof AbstractPackDuckProvider duckProvider) {
            duckProvider.draggable_lists$moveTo(n);
        }
    }

    @Override
    public int draggable_lists$getItemCount() {
        return getItemCount();
    }

    @Override
    public void draggable_lists$renderItem(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta, int i, int rowLeft, int rowTop, int rowWidth, int rowHeight) {
        TransferableSelectionList.Entry entry = children().get(i);
        int oldX = entry.getX();
        int oldY = entry.getY();
        int oldWidth = entry.getWidth();
        int oldHeight = entry.getHeight();
        entry.setX(rowLeft);
        entry.setY(rowTop);
        entry.setWidth(rowWidth);
        entry.setHeight(rowHeight);
        extractItem(guiGraphics, mouseX, mouseY, tickDelta, entry);
        entry.setX(oldX);
        entry.setY(oldY);
        entry.setWidth(oldWidth);
        entry.setHeight(oldHeight);
    }
}
