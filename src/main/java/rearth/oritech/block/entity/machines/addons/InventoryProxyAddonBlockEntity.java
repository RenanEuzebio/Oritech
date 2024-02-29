package rearth.oritech.block.entity.machines.addons;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import rearth.oritech.block.base.entity.UpgradableMachineBlockEntity;
import rearth.oritech.block.blocks.machines.addons.MachineAddonBlock;
import rearth.oritech.client.ui.InventoryProxyScreenHandler;
import rearth.oritech.init.BlockEntitiesContent;
import rearth.oritech.util.ImplementedInventory;

import java.util.Objects;

public class InventoryProxyAddonBlockEntity extends AddonBlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory {
    
    private UpgradableMachineBlockEntity cachedController;
    private int targetSlot = 0;
    
    public InventoryProxyAddonBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesContent.INVENTORY_PROXY_ADDON_ENTITY, pos, state);
    }
    
    private boolean isConnected() {
        var isUsed = this.getCachedState().get(MachineAddonBlock.ADDON_USED);
        return isUsed && getCachedController() != null;
    }
    
    private UpgradableMachineBlockEntity getCachedController() {
        
        if (cachedController != null && !cachedController.isRemoved())
            return cachedController;
        
        cachedController = (UpgradableMachineBlockEntity) Objects.requireNonNull(world).getBlockEntity(getControllerPos());
        return cachedController;
    }
    
    @Override
    public DefaultedList<ItemStack> getItems() {
        if (!isConnected())
            return DefaultedList.of();
        
        return getCachedController().getItems();
    }
    
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return slot == targetSlot;
    }
    
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return slot == targetSlot;
    }
    
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.getPos());
        buf.writeBlockPos(getControllerPos());
        buf.writeInt(targetSlot);
    }
    
    @Override
    public Text getDisplayName() {
        return Text.literal("Machine Inventory Access");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new InventoryProxyScreenHandler(syncId, playerInventory, this, getCachedController(), targetSlot);
    }
    
    public int getTargetSlot() {
        return targetSlot;
    }
    
    public void setTargetSlot(int targetSlot) {
        this.targetSlot = targetSlot;
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("target_slot", targetSlot);
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        targetSlot = nbt.getInt("target_slot");
    }
}
