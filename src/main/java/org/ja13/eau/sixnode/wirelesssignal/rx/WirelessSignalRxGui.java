package org.ja13.eau.sixnode.wirelesssignal.rx;

import org.ja13.eau.gui.*;
import org.ja13.eau.gui.GuiButtonEln;
import org.ja13.eau.gui.GuiHelper;
import org.ja13.eau.gui.GuiScreenEln;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;

import static org.ja13.eau.i18n.I18N.tr;

public class WirelessSignalRxGui extends GuiScreenEln {

    GuiTextFieldEln channel;
    private final WirelessSignalRxRender render;

    AggregatorBt buttonBigger, buttonSmaller, buttonToogle;

    class AggregatorBt extends GuiButtonEln {
        byte id;

        public AggregatorBt(int x, int y, int width, int height, String str, byte id) {
            super(x, y, width, height, str);
            this.id = id;
        }

        @Override
        public void onMouseClicked() {
            render.clientSetByte(WirelessSignalRxElement.setSelectedAggregator, id);
            super.onMouseClicked();
        }

        @Override
        public void idraw(int x, int y, float f) {
            this.enabled = render.selectedAggregator != id;
            super.idraw(x, y, f);
        }
    }

    public WirelessSignalRxGui(WirelessSignalRxRender render) {
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();
        channel = newGuiTextField(6, 6, 220);
        channel.setText(render.channel);
        channel.setComment(0, I18N.tr("Specify the channel"));

        int w = 72;
        int x = 6;
        int y = 6 + 12 + 4;
        add(buttonBigger = new AggregatorBt(x, y, w, 20, I18N.tr("Biggest"), (byte) 0));
        x += 2 + w;
        add(buttonSmaller = new AggregatorBt(x, y, w, 20, I18N.tr("Smallest"), (byte) 1));
        x += 2 + w;
        add(buttonToogle = new AggregatorBt(x, y, w, 20, I18N.tr("Toggle"), (byte) 2));

        buttonBigger.setHelper(helper);
        int lineNumber = 0;
        for (String line : I18N.tr("Uses the biggest\nvalue on the channel.").split("\n"))
            buttonBigger.setComment(lineNumber++, line);

        buttonSmaller.setHelper(helper);
        lineNumber = 0;
        for (String line : I18N.tr("Uses the smallest\nvalue on the channel.").split("\n"))
            buttonSmaller.setComment(lineNumber++, line);

        buttonToogle.setHelper(helper);
        lineNumber = 0;
        for (String line : I18N.tr("Toggles the output each time\nan emitter's value rises.\nUseful to allow multiple buttons\nto control the same light.").split("\n"))
            buttonToogle.setComment(lineNumber++, line);
    }

    @Override
    protected GuiHelper newHelper() {
        return new GuiHelper(this, 220 + 12, 12 + 1 + 24 * 1 + 12);
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        if (render.connection)
            channel.setComment(1, "\u00a72" + I18N.tr("Connected"));
        else
            channel.setComment(1, "\u00a74" + I18N.tr("Not connected"));

        super.preDraw(f, x, y);
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        if (object == channel) {
            render.clientSetString(WirelessSignalRxElement.setChannelId, channel.getText());
        }
        super.guiObjectEvent(object);
    }
}
