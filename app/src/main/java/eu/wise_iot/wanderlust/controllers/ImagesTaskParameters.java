package eu.wise_iot.wanderlust.controllers;

import java.util.List;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;

public class ImagesTaskParameters {
    long id;
    List<ImageInfo> imageInfos;
    String route;
    FragmentHandler handler;

    ImagesTaskParameters(long id, List<ImageInfo> imageInfos, String route, FragmentHandler handler) {
        this.id = id;
        this.imageInfos = imageInfos;
        this.route = route;
        this.handler = handler;
    }
}