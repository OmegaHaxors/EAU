package org.ja13.eau.sixnode.electricalsource;

import org.ja13.eau.gui.GuiHelper;
import org.ja13.eau.gui.GuiScreenEln;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.gui.GuiHelper;
import org.ja13.eau.gui.GuiScreenEln;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.i18n.I18N;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalSourceGui extends GuiScreenEln {

    GuiTextFieldEln voltage;
    ElectricalSourceRender render;

    public ElectricalSourceGui(ElectricalSourceRender render) {
        this.render = render;
    }

    @Override
    protected GuiHelper newHelper() {
        return new GuiHelper(this, 50 + 12, 12 + 12);
    }

    @Override
    public void initGui() {
        super.initGui();

        voltage = newGuiTextField(6, 6, 50);
        voltage.setText((float) render.voltage);
        voltage.setObserver(this);
        voltage.setComment(new String[]{I18N.tr("Output voltage")});
    }

    @Override
    public void textFieldNewValue(GuiTextFieldEln textField, String value) {
        float newVoltage;

        try {
            newVoltage = NumberFormat.getInstance().parse(voltage.getText()).floatValue();
        } catch (ParseException e) {
            return;
        }

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(bos);

            render.preparePacketForServer(stream);

            stream.writeByte(ElectricalSourceElement.setVoltageId);
            stream.writeFloat(newVoltage);

            render.sendPacketToServer(bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
