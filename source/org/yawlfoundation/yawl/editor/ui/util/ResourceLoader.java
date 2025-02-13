/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.editor.ui.util;

import org.apache.jena.base.Sys;
import org.apache.logging.log4j.LogManager;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/** loads images & icons */
public class ResourceLoader {

    private static final String resourcesPath =
            "/org/yawlfoundation/yawl/editor/ui/resources/";


    public static ImageIcon getMenuIcon(String name) {
        return getImageAsIcon(resourcesPath + "menuicons/" + name + ".png");
    }


    public static ImageIcon getPaletteIcon(String name) {
        return getImageAsIcon(resourcesPath + "paletteicons/" + name + ".png");
    }


    public static ImageIcon getMiniToolIcon(String name) {
        return getImageAsIcon(resourcesPath + "minitoolicons/" + name + ".png");
    }


    public static Image getCursorIcon(String name) {
        return getImageAsIcon(resourcesPath + "cursors/" + name + ".gif").getImage();
    }


    public static ImageIcon getApplicationIcon() {
        return getImageAsIcon(resourcesPath + "applicationIcon.png");
    }


    public static ImageIcon getIcon(String file) {
        return getImageAsIcon(resourcesPath + file);
    }


    public static ImageIcon getExternalImageAsIcon(String imageFile) {
        return getExternalImageAsIcon(imageFile, -1);
    }


    private static ImageIcon getImageAsIcon(String imageFile) {
        try {
            InputStream in = ResourceLoader.class.getResourceAsStream(imageFile);
            final byte[] imageByteBuffer = convertToByteArray(in);
            in.close();
            return new ImageIcon(imageByteBuffer);
        } catch (Exception e) {
            LogManager.getLogger(ResourceLoader.class)
                    .warn("Unable to load icon from file: " + imageFile + ": " +
                    e.getMessage());
            return null;
        }
    }


    private static ImageIcon getExternalImageAsIcon(String imageFile, int scale) {
        try {
            BufferedImage b = ImageIO.read(new File(imageFile));

            // scale to fit inside tasks
            if (b != null) {
                if (scale > 0 && (b.getHeight() > scale || b.getWidth() > scale)) {
                    b = Scalr.resize(b, scale);
                }
                return new ImageIcon(b);
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }


    private static byte[] convertToByteArray(final InputStream is) throws IOException {
        final int BUF_SIZE = 16384;
        BufferedInputStream inStream = new BufferedInputStream(is);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(BUF_SIZE);
        byte[] buffer = new byte[BUF_SIZE];

        // read chunks from the input stream and write them out
        int bytesRead = 0;
        while ((bytesRead = inStream.read(buffer, 0, BUF_SIZE)) > 0) {
            outStream.write(buffer, 0, bytesRead);
        }
        outStream.flush();
        return outStream.toByteArray();
    }
}

