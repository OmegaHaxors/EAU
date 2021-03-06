package org.ja13.eau.sixnode.resistor;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.misc.series.ISeriesMapping;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.ja13.eau.EAU;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.misc.series.ISeriesMapping;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.lwjgl.opengl.GL11;

/**
 * Created by svein on 05/08/15.
 */
public class ResistorDescriptor extends SixNodeDescriptor {

    public final boolean isRheostat;
    public double thermalCoolLimit = -100;
    public double thermalWarmLimit = EAU.cableWarmLimit;
    public double thermalMaximalPowerDissipated = 1000;
    public double thermalNominalHeatTime = 120;
    public double thermalConductivityTao = EAU.cableThermalConductionTao;
    public double tempCoef;
    Obj3D.Obj3DPart ResistorBaseExtension, ResistorCore, ResistorTrack, ResistorWiper, Base, Cables;
    ISeriesMapping series;
    private final Obj3D obj;


    public ResistorDescriptor(String name,
                              Obj3D obj,
                              ISeriesMapping series,
                              double tempCoef,
                              boolean isRheostat) {
        super(name, ResistorElement.class, ResistorRender.class);
        this.obj = obj;
        this.series = series;
        this.tempCoef = tempCoef;
        this.isRheostat = isRheostat;
        if (obj != null) {
            ResistorBaseExtension = obj.getPart("ResistorBaseExtention");
            ResistorCore = obj.getPart("ResistorCore");
            ResistorTrack = obj.getPart("ResistorTrack");
            ResistorWiper = obj.getPart("ResistorWiper");
            Base = obj.getPart("Base");
            Cables = obj.getPart("CapacitorCables");
        }
        voltageTier = VoltageTier.NEUTRAL;
    }

    public double getRsValue(IInventory inventory) {
        ItemStack core = inventory.getStackInSlot(ResistorContainer.coreId);

        if (core == null) return series.getValue(0);
        return series.getValue(core.stackSize);
    }

    void draw(float wiperPos) {
        //UtilsClient.disableCulling();
        //UtilsClient.disableTexture();
        //GL11.glRotatef(90, 1, 0, 0);


        if (null != Base) Base.draw();
        if (null != ResistorBaseExtension) ResistorBaseExtension.draw();
        if (null != ResistorCore) ResistorCore.draw();
        if (null != Cables) Cables.draw();

        if (isRheostat) {
            final float wiperSpread = 0.238f;
            wiperPos = (wiperPos - 0.5f) * wiperSpread * 2;
            ResistorTrack.draw();
            GL11.glTranslatef(0, 0, wiperPos);
            ResistorWiper.draw();
            // GL11.glTranslatef(-wiperPos, 0, 0);
        }
    }

    @Override
    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return true;
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        if (type != ItemRenderType.INVENTORY) {
            GL11.glTranslatef(0.0f, 0.0f, -0.2f);
            GL11.glScalef(1.25f, 1.25f, 1.25f);
            GL11.glRotatef(-90.f, 0.f, 1.f, 0.f);
            draw(0);
        } else {
            super.renderItem(type, item, data);
        }
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).left();
    }
}
