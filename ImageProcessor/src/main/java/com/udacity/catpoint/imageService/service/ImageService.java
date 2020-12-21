package com.udacity.catpoint.imageService.service;

import java.awt.image.BufferedImage;

/**
 * @author shubham sharma
 *         <p>
 *         21/12/20
 */
public interface ImageService {
    boolean imageContainsCat(BufferedImage image, float confidenceThreshhold);
}
