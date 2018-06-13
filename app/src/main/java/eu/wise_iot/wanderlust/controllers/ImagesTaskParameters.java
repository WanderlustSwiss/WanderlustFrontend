package eu.wise_iot.wanderlust.controllers;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;

/**
 * Represents Tasks related to images TODO ??
 *
 * @author TODO ??
 * @license MIT
 */
public class ImagesTaskParameters {
    final long id;
    final List<ImageInfo> imageInfos;
    final String route;
    final FragmentHandler handler;

    ImagesTaskParameters(long id, List<ImageInfo> imageInfos, String route, FragmentHandler handler) {
        this.id = id;
        this.imageInfos = imageInfos;
        this.route = route;
        this.handler = handler;
    }
}