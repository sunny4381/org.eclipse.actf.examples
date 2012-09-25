/*******************************************************************************
 * Copyright (c) 2012 Middle East Technical University Northern Cyprus Campus and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elgin Akpinar (METU) - initial API and implementation
 *    Sukru Eraslan (METU NCC) - Eye Tracking Data Handling Implementation
 *******************************************************************************/

package org.eclipse.actf.examples.emine.vips.types;

import java.util.ArrayList;

public class Recording implements Comparable<Recording>{
    String id;
    String scanPath;
    boolean scanPathDetected;
    ArrayList<Fixation> pointList;
   
    public Recording(String id){
        this.id = id;
        scanPathDetected = false;
        pointList = new ArrayList<Fixation>();
    }
    
    public String getId(){
    	return id;
    }
    
    public boolean isScanPathDetected(){
    	return this.scanPathDetected;
    }
   
    public void addPoint(Fixation p){
        pointList.add(p);
    }

    public int getPointCount() {
        return pointList.size();
    }

	public Fixation getPointAt(int pointIndex) {
		return pointList.get(pointIndex);
	}

	public ArrayList<Fixation> getFixations() {
		return pointList;
	}

	public String getScanPath() {
		if(!scanPathDetected){
			StringBuilder builder = new StringBuilder();
			for(Fixation fix : pointList){
				if(fix.getBlock() != null)
					builder.append(fix.getBlock().getBlockName() + " * ");
			}
			
			scanPath = builder.toString();
			if(!scanPath.equals("")){
				scanPathDetected = true;
			}
		}
		return scanPath;
	}

	public int compareTo(Recording recording) {
		int rec1, rec2;
		try {
			rec1 = Integer.parseInt(this.getId());
		} catch(ClassCastException ex){
			rec1 = 0;
		}
		try {
			rec2 = Integer.parseInt(recording.getId());
		} catch(ClassCastException ex){
			rec2 = 0;
		}
		return (rec1 == rec2 ? 0 : (rec1 < rec2 ? -1 : 1));
	}
   
}


