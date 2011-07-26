
package com.charles.dataprovider;

import com.aetrion.flickr.photos.PhotoList;
import java.io.Serializable;

/**
 * Represents the data provider to provide a list of photos.
 * <p>
 * Make it extend from <code>Serializable</code> is to save this when fragment
 * is to be destroyed.
 * 
 * @author charles
 */
public interface IPhotoListDataProvider extends Serializable {

    /**
     * Returns a list of photos.
     * 
     * @return
     * @throws Exception
     */
    PhotoList getPhotoList() throws Exception;
    
    /**
     * The photo list will be cached, to invalidate the cache, call this method.
     */
    void invalidatePhotoList();
}
