package org.ja13.eau.node.transparent;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.IFluidHandler;
import org.ja13.eau.EAU;
import org.ja13.eau.item.IConfigurable;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.Node;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class TransparentNode extends Node {

    public TransparentNodeElement element;
    public int elementId;
    public EntityPlayerMP removedByPlayer;

    @Override
    public void onNeighborBlockChange() {
        super.onNeighborBlockChange();
        element.onNeighborBlockChange();
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt.getCompoundTag("node"));
        elementId = nbt.getShort("eid");
        try {
            TransparentNodeDescriptor descriptor = EAU.transparentNodeItem.getDescriptor(elementId);
            element = (TransparentNodeElement) descriptor.ElementClass.getConstructor(TransparentNode.class, TransparentNodeDescriptor.class).newInstance(this, descriptor);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        element.readFromNBT(nbt.getCompoundTag("element"));
    }

    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(Utils.newNbtTagCompund(nbt, "node"));

        nbt.setShort("eid", (short) elementId);

        element.writeToNBT(Utils.newNbtTagCompund(nbt, "element"));

    }

    @Override
    public void onBreakBlock() {

        element.onBreakElement();
        super.onBreakBlock();
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu, int mask) {
        return element.getElectricalLoad(side, lrdu);
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu, int mask) {
        return element.getThermalLoad(side, lrdu);
    }

    @Override
    public int getSideConnectionMask(Direction side, LRDU lrdu) {
        return element.getConnectionMask(side, lrdu);
    }

    @Override
    public String multiMeterString(Direction side) {
        return element.multiMeterString(side);
    }

    @Override
    public String thermoMeterString(Direction side) {
        return element.thermoMeterString(side);
    }

    @Override
    public boolean readConfigTool(Direction side, NBTTagCompound tag, EntityPlayer invoker) {
        if(element instanceof IConfigurable) {
            ((IConfigurable) element).readConfigTool(tag, invoker);
            return true;
        }
        return false;
    }

    @Override
    public boolean writeConfigTool(Direction side, NBTTagCompound tag, EntityPlayer invoker) {
        if(element instanceof IConfigurable) {
            ((IConfigurable) element).writeConfigTool(tag, invoker);
            return true;
        }
        return false;
    }

    public IFluidHandler getFluidHandler() {
        return element.getFluidHandler();
    }

    @Override
    public void publishSerialize(DataOutputStream stream) {

        super.publishSerialize(stream);

        try {
            stream.writeShort(this.elementId);
            element.networkSerialize(stream);
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public enum FrontType {
        BlockSide, PlayerView, PlayerViewHorizontal, BlockSideInv
    }

    @Override
    public void initializeFromThat(Direction side, EntityLivingBase entityLiving, ItemStack itemStack) {
        try {
            TransparentNodeDescriptor descriptor = EAU.transparentNodeItem.getDescriptor(itemStack);
            int metadata = itemStack.getItemDamage();
            elementId = metadata;
            element = (TransparentNodeElement) descriptor.ElementClass.getConstructor(TransparentNode.class, TransparentNodeDescriptor.class).newInstance(this, descriptor);
            element.initializeFromThat(side, entityLiving, itemStack.getTagCompound());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        Utils.println("TN.iFT element = " + element + " elId = " + elementId);

    }

    @Override
    public void initializeFromNBT() {
        element.initialize();
    }

    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        if (element.onBlockActivated(entityPlayer, side, vx, vy, vz)) return true;
        return super.onBlockActivated(entityPlayer, side, vx, vy, vz);
    }

    @Override
    public boolean hasGui(Direction side) {
        if (element == null) return false;
        return element.hasGui();
    }

    public IInventory getInventory(Direction side) {
        if (element == null) return null;
        return element.getInventory();
    }

    public Container newContainer(Direction side, EntityPlayer player) {
        if (element == null) return null;
        return element.newContainer(side, player);
    }

    @Override
    public int getBlockMetadata() {
        Utils.println("TN.gBM");
        Utils.println(element);
        Utils.println(element.transparentNodeDescriptor);
        return element.transparentNodeDescriptor.tileEntityMetaTag.meta;
    }

    @Override
    public void networkUnserialize(DataInputStream stream, EntityPlayerMP player) {
        super.networkUnserialize(stream, player);
        try {
            if (elementId == stream.readShort()) {
                element.networkUnserialize(stream, player);
            } else {
                Utils.println("Transparent node unserialize miss");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectJob() {
        super.connectJob();
        element.connectJob();
    }

    @Override
    public void disconnectJob() {
        super.disconnectJob();
        element.disconnectJob();
    }

    @Override
    public void checkCanStay(boolean onCreate) {

        super.checkCanStay(onCreate);
        element.checkCanStay(onCreate);
    }

    public void dropElement(EntityPlayerMP entityPlayer) {
        if (element != null)
            if (Utils.mustDropItem(entityPlayer))
                dropItem(element.getDropItemStack());
    }

    @Override
    public String getNodeUuid() {
        return EAU.transparentNodeBlock.getNodeUuid();
    }

    @Override
    public void unload() {
        super.unload();
        if (element != null)
            element.unload();
    }

}
