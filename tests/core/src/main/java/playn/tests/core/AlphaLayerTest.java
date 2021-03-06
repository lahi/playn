/**
 * Copyright 2011 The PlayN Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package playn.tests.core;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.SurfaceLayer;
import playn.core.util.Callback;
import static playn.core.PlayN.*;

public class AlphaLayerTest extends Test {

  static float width = 100, height = 100;
  static int offset = 5;

  @Override
  public String getName() {
    return "AlphaLayerTest";
  }

  @Override
  public String getDescription() {
    return "Test that the alpha value on layers works the same on all layer types and that alpha is 'additive'. Left-to-right: ImageLayer, SurfaceLayer, CanvasImage (layer alpha), CanvasImage (canvas alpha), ground truth (expected). The first three layers all have alpha 50% and are in a grouplayer with alpha 50% (should result in a 25% opaque image).";
  }

  @Override
  public void init() {
    final GroupLayer rootLayer = graphics().rootLayer();
    final float fullWidth = 6*width, fullHeight = 3*height;

    // add a half white, half blue background
    SurfaceLayer bg = graphics().createSurfaceLayer((int) fullWidth, (int) fullHeight);
    bg.surface().setFillColor(Color.rgb(255, 255, 255));
    bg.surface().fillRect(0, 0, bg.surface().width(), bg.surface().height());
    bg.surface().setFillColor(Color.rgb(0, 0, 255));
    bg.surface().fillRect(0, 2*height, bg.surface().width(), height);
    rootLayer.add(bg);

    addDescrip("all layers contained in group layer with a=0.5\n" +
               "thus, fully composited a=0.25", offset, fullHeight+5, fullWidth);

    // add a 50% transparent group layer
    final GroupLayer groupLayer = graphics().createGroupLayer();
    groupLayer.setAlpha(0.5f);
    rootLayer.add(groupLayer);

    assets().getImage("images/alphalayertest.png").addCallback(new Callback<Image>() {
      @Override
      public void onSuccess(Image image) {
        // add the layers over the white background
        float x = offset;

        groupLayer.addAt(graphics().createImageLayer(image).setAlpha(0.5f), x, offset);
        addDescrip("image\nimg layer a=0.5", x, offset + height, width);
        x += width;

        SurfaceLayer surf1 = graphics().createSurfaceLayer(image.width(), image.height());
        surf1.surface().setAlpha(0.5f).drawImage(image, 0, 0);
        groupLayer.addAt(surf1, x, offset);
        addDescrip("image a=0.5\nsurf layer a=1", x, offset + height, width);
        x += width;

        SurfaceLayer surf2 = graphics().createSurfaceLayer(image.width(), image.height());
        surf2.surface().drawImage(image, 0, 0);
        groupLayer.addAt(surf2.setAlpha(0.5f), x, offset);
        addDescrip("image a=1\nsurf layer a=0.5", x, offset + height, width);
        x += width;

        CanvasImage canvas1 = graphics().createImage(image.width(), image.height());
        canvas1.canvas().drawImage(image, 0, 0);
        groupLayer.addAt(graphics().createImageLayer(canvas1).setAlpha(0.5f), x, offset);
        addDescrip("canvas a=1\nimg layer a=0.5", x, offset + height, width);
        x += width;

        CanvasImage canvas2 = graphics().createImage(image.width(), image.height());
        canvas2.canvas().setAlpha(0.5f).drawImage(image, 0, 0);
        groupLayer.addAt(graphics().createImageLayer(canvas2), x, offset);
        addDescrip("canvas a=0.5\nimg layer a=1", x, offset + height, width);
        x += width;

        // add the same layers over the blue background
        x = offset;

        groupLayer.addAt(graphics().createImageLayer(image).setAlpha(0.5f), x, offset + 2 * height);
        x += width;

        SurfaceLayer surf1b = graphics().createSurfaceLayer(image.width(), image.height());
        surf1b.surface().setAlpha(0.5f).drawImage(image, 0, 0);
        groupLayer.addAt(surf1b, x, offset + 2 * height);
        x += width;

        SurfaceLayer surf2b = graphics().createSurfaceLayer(image.width(), image.height());
        surf2b.surface().drawImage(image, 0, 0);
        groupLayer.addAt(surf2b.setAlpha(0.5f), x, offset + 2 * height);
        x += width;

        CanvasImage canvas1b = graphics().createImage(image.width(), image.height());
        canvas1b.canvas().drawImage(image, 0, 0);
        groupLayer.addAt(graphics().createImageLayer(canvas1b).setAlpha(0.5f),
                         x, offset + 2 * height);
        x += width;

        CanvasImage canvas2b = graphics().createImage(image.width(), image.height());
        canvas2b.canvas().setAlpha(0.5f).drawImage(image, 0, 0);
        groupLayer.addAt(graphics().createImageLayer(canvas2b), x, offset + 2 * height);

        // add some copies of the image at 1, 0.5, 0.25 and 0.125 alpha
        x = offset + width;
        for (float alpha : new float[] { 1, 1/2f, 1/4f, 1/8f }) {
          float y = fullHeight+50;
          rootLayer.addAt(graphics().createImageLayer(image).setAlpha(alpha), x, y);
          addDescrip("image a=" + alpha, x, y+height/2, width/2);
          x += width;
        }
      }

      @Override
      public void onFailure(Throwable err) {
        log().error("Error loading image", err);
      }
    });

    // add ground truth of 25% opaque image
    assets().getImage("images/alphalayertest_expected.png").addCallback(new Callback<Image>() {
      @Override
      public void onSuccess(Image image) {
        rootLayer.addAt(graphics().createImageLayer(image), 5*width, 0);
        addDescrip("ground truth", 5*width, offset+height, width);
      }

      @Override
      public void onFailure(Throwable err) {
        log().error("Error loading image", err);
      }
    });
  }
}
