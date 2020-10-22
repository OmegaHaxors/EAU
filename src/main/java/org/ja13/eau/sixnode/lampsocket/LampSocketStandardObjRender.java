package org.ja13.eau.sixnode.lampsocket;

import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.UtilsClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.UtilsClient;
import org.lwjgl.opengl.GL11;

public class LampSocketStandardObjRender implements LampSocketObjRender {

    private final Obj3D obj;
    private Obj3D.Obj3DPart socket, socket_unlightable, socket_lightable, lampOn, lampOff, lightAlphaPlane, lightAlphaPlaneNoDepth;
    ResourceLocation tOn, tOff;
    private final boolean onOffModel;

    public LampSocketStandardObjRender(Obj3D obj, boolean onOffModel) {
        this.obj = obj;
        this.onOffModel = onOffModel;
        if (obj != null) {
            socket = obj.getPart("socket");
            lampOn = obj.getPart("lampOn");
            lampOff = obj.getPart("lampOff");
            socket_unlightable = obj.getPart("socket_unlightable");
            socket_lightable = obj.getPart("socket_lightable");
            lightAlphaPlane = obj.getPart("lightAlpha");
            lightAlphaPlaneNoDepth = obj.getPart("lightAlphaNoDepth");
            tOff = obj.getModelResourceLocation(obj.getString("tOff"));
            tOn = obj.getModelResourceLocation(obj.getString("tOn"));
        }
    }

    @Override
    public void draw(LampSocketDescriptor descriptor, ItemRenderType type, double distanceToPlayer) {
        if (type == ItemRenderType.INVENTORY) {
            if (descriptor.hasGhostGroup()) {
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                GL11.glRotatef(90, 0, -1, 0);
                GL11.glTranslatef(-1.5f, 0f, 0f);
            }
        } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            if (descriptor.hasGhostGroup()) {
                GL11.glScalef(0.3f, 0.3f, 0.3f);
                GL11.glRotatef(90, 0, -1, 0);
                GL11.glTranslatef(-0.5f, 0f, -1f);
            }
        }
        draw(LRDU.Up, 0, (byte) 0, true, 15, distanceToPlayer);
    }

    @Override
    public void draw(LampSocketRender render, double distanceToPlayer) {
        int color = 15;
        if (render.descriptor.paintable)
            color = render.paintColor;
        draw(render.front, render.alphaZ, render.light, render.lampDescriptor != null, color, distanceToPlayer);
    }

    public void draw(LRDU front, float alphaZ, byte light, boolean hasBulb, int color, double distanceToPlayer) {
        front.glRotateOnX();

        UtilsClient.disableCulling();

        Utils.setGlColorFromLamp(color);
        if (!onOffModel) {
            if (socket != null) socket.draw();
        } else {
            //
            if (light > 8) {
                UtilsClient.bindTexture(tOn);
            } else {
                UtilsClient.bindTexture(tOff);
            }
            if (socket_unlightable != null) socket_unlightable.drawNoBind();

            if (light > 8) {
                UtilsClient.disableLight();
                float l = (light) / 14f;
                //GL11.glColor3f(l, l, l);
                if (socket_lightable != null) socket_lightable.drawNoBind();
                //GL11.glColor3f(1f, 1f, 1f);
            }

            if (hasBulb) {
                if (light > 8) {
                    if (lampOn != null) lampOn.draw();
                } else {
                    if (lampOff != null) lampOff.draw();
                }
            }
            if (socket != null) socket.drawNoBind();

            if (light > 8)
                UtilsClient.enableLight();
            //
        }

        UtilsClient.enableBlend();
        UtilsClient.disableLight();

        if (lightAlphaPlaneNoDepth != null) {
            float coeff = /*1.5f*/2.0f - (float) distanceToPlayer;
            if (coeff > 0.0f) {
                UtilsClient.enableCulling();
                UtilsClient.disableDepthTest(); //Beautiful effect, but overlay the whole render (i.e. through wall) : so distance limited.
                GL11.glColor4f(1.f, 1.f, 1.f, light * 0.06667f * coeff);

                lightAlphaPlaneNoDepth.draw();
                UtilsClient.enableDepthTest();
                UtilsClient.disableCulling();
            }
        }

        if (lightAlphaPlane != null) {
            GL11.glColor4f(1.f, 0.98f, 0.92f, light * 0.06667f);
            lightAlphaPlane.draw();
        }

        UtilsClient.enableLight();
        UtilsClient.disableBlend();

        UtilsClient.enableCulling();
        /*
		 * GL11.glLineWidth(2f); GL11.glDisable(GL11.GL_TEXTURE_2D); GL11.glDisable(GL11.GL_LIGHTING); GL11.glColor3f(1f,1f,1f); GL11.glBegin(GL11.GL_LINES); GL11.glVertex3d(0f, 0f, 0f); GL11.glVertex3d(Math.cos(alphaZ*Math.PI/180.0), Math.sin(alphaZ*Math.PI/180.0),0.0); GL11.glEnd(); GL11.glEnable(GL11.GL_TEXTURE_2D); GL11.glEnable(GL11.GL_LIGHTING);
		 */
    }
}
