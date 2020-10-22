package org.ja13.eau.sixnode.thermalsensor;

import org.ja13.eau.gui.*;
import org.ja13.eau.sim.PhysicalConstant;
import org.ja13.eau.sixnode.electricalsensor.ElectricalSensorElement;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.gui.HelperStdContainer;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.sim.PhysicalConstant;

import java.text.NumberFormat;
import java.text.ParseException;

import static org.ja13.eau.i18n.I18N.tr;

public class ThermalSensorGui extends GuiContainerEln {

    GuiButton validate, temperatureType, powerType;
    GuiTextFieldEln lowValue, highValue;
    ThermalSensorRender render;

    public ThermalSensorGui(EntityPlayer player, IInventory inventory, ThermalSensorRender render) {
        super(new ThermalSensorContainer(player, inventory, render.descriptor.temperatureOnly));
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();

        if (!render.descriptor.temperatureOnly) {
            powerType = newGuiButton(8, 8, 70, I18N.tr("Power"));
            temperatureType = newGuiButton(176 - 8 - 70, 8, 70, I18N.tr("Temperature"));

            int x = -15, y = 13;
            validate = newGuiButton(x + 8 + 50 + 4 + 50 + 4 - 26, y + (166 - 84) / 2 - 8, 50, I18N.tr("Validate"));

            lowValue = newGuiTextField(x + 8 + 50 + 4 - 26, y + (166 - 84) / 2 + 3, 50);
            lowValue.setText(render.lowValue);
            lowValue.setComment(I18N.tr("Measured value\ncorresponding\nto 0% output").split("/n"));

            highValue = newGuiTextField(x + 8 + 50 + 4 - 26, y + (166 - 84) / 2 - 12, 50);
            highValue.setText(render.highValue);
            highValue.setComment(I18N.tr("Measured value\ncorresponding\nto 100% output").split("\n"));
        } else {
            int x = 0, y = 0;
            validate = newGuiButton(x + 8 + 50 + 4 + 50 + 4 - 26, y + (166 - 84) / 2 - 8, 50, I18N.tr("Validate"));

            lowValue = newGuiTextField(x + 8 + 50 + 4 - 26, y + (166 - 84) / 2 + 3, 50);
            lowValue.setText(render.lowValue);
            lowValue.setComment(I18N.tr("Measured temperature\ncorresponding\nto 0% output").split("/n"));

            highValue = newGuiTextField(x + 8 + 50 + 4 - 26, y + (166 - 84) / 2 - 12, 50);
            highValue.setText(render.highValue);
            highValue.setComment(I18N.tr("Measured temperature\ncorresponding\nto 100% output").split("/n"));
        }
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);
        if (object == validate) {
            float lowVoltage, highVoltage;

            try {
                lowVoltage = NumberFormat.getInstance().parse(lowValue.getText()).floatValue();
                highVoltage = NumberFormat.getInstance().parse(highValue.getText()).floatValue();
                render.clientSetFloat(ElectricalSensorElement.setValueId, lowVoltage - (float) PhysicalConstant.Tamb, highVoltage - (float) PhysicalConstant.Tamb);
            } catch (ParseException e) {
            }
        } else if (object == temperatureType) {
            render.clientSetByte(ThermalSensorElement.setTypeOfSensorId, ThermalSensorElement.temperatureType);
        } else if (object == powerType) {
            render.clientSetByte(ThermalSensorElement.setTypeOfSensorId, ThermalSensorElement.powerType);
        }
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
        if (!render.descriptor.temperatureOnly) {
            if (render.typeOfSensor == ThermalSensorElement.temperatureType) {
                powerType.enabled = true;
                temperatureType.enabled = false;
            } else if (render.typeOfSensor == ThermalSensorElement.powerType) {
                powerType.enabled = false;
                temperatureType.enabled = true;
            }
        }
    }

    @Override
    protected GuiHelperContainer newHelper() {
        return new HelperStdContainer(this);
    }
}
