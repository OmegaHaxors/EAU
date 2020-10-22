package org.ja13.eau.sixnode.electricalvumeter;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.*;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalVuMeterDescriptor extends SixNodeDescriptor {

    Obj3D obj;

    enum ObjType {Rot, LedOnOff}

    ObjType objType;

    Obj3D.Obj3DPart vumeter, pointer, led, halo, main;

    public boolean onOffOnly;

    public float[] pinDistance;

    public ElectricalVuMeterDescriptor(String name, String objName, boolean onOffOnly) {
        super(name, ElectricalVuMeterElement.class, ElectricalVuMeterRender.class);
        this.onOffOnly = onOffOnly;
        obj = EAU.obj.getObj(objName);

        if (obj != null) {
            if (obj.getString("type").toLowerCase().equals("rot")) {
                objType = ObjType.Rot;
                vumeter = obj.getPart("Vumeter");
                pointer = obj.getPart("Pointer");
                pinDistance = Utils.getSixNodePinDistance(vumeter);
            }
            if (obj.getString("type").equals("LedOnOff")) {
                objType = ObjType.LedOnOff;
                main = obj.getPart("main");
                halo = obj.getPart("halo");
                led = obj.getPart("Led");
                pinDistance = Utils.getSixNodePinDistance(main);
            }
        }

        voltageTier = VoltageTier.TTL;
    }

    void draw(float factor, float distance, TileEntity entity) {
        if (factor < 0.0) factor = 0.0f;
        if (factor > 1.0) factor = 1.0f;
        switch (objType) {
            case LedOnOff:
                main.draw();
                boolean s = factor > 0.5;
                Color c = UtilsClient.ledOnOffColorC(s);
                GL11.glColor3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
                UtilsClient.drawLight(led);
                //Utils.enableBilinear();
                if (entity != null)
                    UtilsClient.drawHalo(halo, c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, entity, false);
                else
                    UtilsClient.drawLight(halo);
                //Utils.disableBilinear();
                break;
            case Rot:
                vumeter.draw();
                float alphaOff, alphaOn;
                alphaOff = pointer.getFloat("alphaOff");
                alphaOn = pointer.getFloat("alphaOn");
                pointer.draw((factor * (alphaOn - alphaOff) + alphaOff), 1.0f, 0, 0);
                break;
            default:
                break;
        }
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        list.add(I18N.tr("Displays the value of a signal."));
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelperEln(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, data);
        } else {
            draw(0.0f, 1f, null);
        }
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).inverse();
    }
}