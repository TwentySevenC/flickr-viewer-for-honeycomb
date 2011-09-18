
package com.gmail.charleszq.event;

import com.aetrion.flickr.photos.Exif;

import java.util.Collection;

/**
 * @author charles
 */
public interface IExifListener {

    /**
     * After exif inforamtion got
     * 
     * @param bitmap the photo image bitmap
     * @param exifs the exif information.
     */
    void onExifInfoFetched(Collection<Exif> exifs);
}
