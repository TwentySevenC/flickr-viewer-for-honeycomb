/**
 * 
 */

package com.gmail.charleszq.event;

/**
 * Represents the listener to handle the flickr authentication process.
 * 
 * @author charles
 */
public interface IAuthDoneListener {

    /**
     * Handles the auth process.
     * 
     * @param type The flickr auth process includes 2 steps, the first one is to
     *            get the 'frob', then, with the 'frob' we can get the auth
     *            token, so, <code>type</code> here is to differ the 2 steps.
     * @param result for the 1st step mentioned above, the <code>result</code>
     *            is the 'frob'; for the 2nd step, the <code>result</code> will
     *            be the 'auth' object which contains 'token' and other
     *            information.
     */
    void onAuthDone(int type, Object result);
}
