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

package org.eclipse.actf.examples.emine.ui.internal;

import org.eclipse.swt.graphics.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.actf.examples.emine.vips.types.Fixation;
import org.eclipse.actf.examples.emine.vips.types.Recording;

public class CsvReader {
	
    public static ArrayList<Recording> readData(String fileName){
        HashMap<String, Recording> map = new HashMap<String, Recording>();
        
        if(fileName == null)
        	return new ArrayList<Recording>();
        
        File file = new File(fileName);
        BufferedReader bufreader;
        try {
            bufreader = new BufferedReader(new FileReader(file));
            String line = bufreader.readLine();
            while((line = bufreader.readLine()) != null) {
                String[] values = line.split(";");
                int x = Integer.parseInt(values[1]);
                int y = Integer.parseInt(values[2]);
                String recId = values[3];
                Fixation fix = new Fixation(new Point(x,y));
                if(!map.containsKey(recId)){
                    Recording rec = new Recording(recId);
                    map.put(recId, rec);
                }
                map.get(recId).addPoint(fix);
            }
        } catch (FileNotFoundException e) {
        	return new ArrayList<Recording>();
        } catch (IOException e) {
        	return new ArrayList<Recording>();
        }
        ArrayList<Recording> list = new ArrayList<Recording>();
        for(String key : map.keySet()){
        	list.add(map.get(key));
        }
        Collections.sort(list);
        return list;
    }
}
