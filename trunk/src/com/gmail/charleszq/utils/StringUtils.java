/*
 * Created on Jul 25, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.utils;

import android.text.Html;
import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.util.Log;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author charles
 */
public final class StringUtils {

	private static final String TAG = StringUtils.class.getName();

	/**
	 * Example: [http://www.flickr.com/photos/example/2910192942/]
	 */
	private static final String FILICK_URL_EXPRESSION = "(\\[http){1}+(s)?+(://){1}+.*\\]{1}+"; //$NON-NLS-1$

	/**
	 * Example: <a href="....">Link Text</a>
	 */
	private static final String A_HREF_STRING_EXP = "<a\\b[^>]*href=\"[^>]*>(.*?)</a>"; //$NON-NLS-1$

	/**
	 * The pattern to retrieve the real link part.
	 */
	private static Pattern hrefPattern = Pattern.compile("href=\"[^>]*\">"); //$NON-NLS-1$

	public static void formatHtmlString(String string, TextView textView) {

		Log.d(TAG, string);

		textView.setText(Html.fromHtml(string));
		// textView.setText(string);
		Linkify.addLinks(textView, Pattern.compile(FILICK_URL_EXPRESSION),
				"http://", new MatchFilter() { //$NON-NLS-1$

					@Override
					public boolean acceptMatch(CharSequence s, int start,
							int end) {
						return true;
					}

				}, new TransformFilter() {

					@Override
					public String transformUrl(Matcher matcher, String data) {
						if (data.length() > 2) {
							return data.substring(1, data.length() - 1);
						}
						return data;
					}

				});

		Linkify.addLinks(textView, Pattern.compile(A_HREF_STRING_EXP),
				"http://", new MatchFilter() { //$NON-NLS-1$

					@Override
					public boolean acceptMatch(CharSequence s, int start,
							int end) {
						return true;
					}
				}, new TransformFilter() {

					@Override
					public String transformUrl(Matcher match, String url) {
						Matcher matcher = hrefPattern.matcher(match.group());
						matcher.find();
						String s = matcher.group().replaceFirst("href=\"", "")  //$NON-NLS-1$//$NON-NLS-2$
								.replaceFirst("\">", "");  //$NON-NLS-1$//$NON-NLS-2$
						Log.d(TAG, "link = " + s); //$NON-NLS-1$
						return s;
					}
				});

	}

	private StringUtils() {
	}
}
