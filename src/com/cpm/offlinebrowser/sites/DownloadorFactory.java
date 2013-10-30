package com.cpm.offlinebrowser.sites;

import android.content.Context;

public class DownloadorFactory {
	public static AbstractDownloador getDownloador(Context ctx, int siteId){
		switch(siteId) {
			case 1:
				return new Other(ctx);
				
			default: return null;
		}
	}
}
