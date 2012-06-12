/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.scripteditor.data;

import java.net.URI;

/**
 * @category Script data
 * 
 */
public interface IScriptData {

	public static final int TYPE_UNDEFINED = -1;
	public static final int TYPE_SCENARIO = 0;
	public static final int TYPE_AUDIO = 1;
	public static final int TYPE_CAPTION = 2;

	public String getDescription();

	public void setDescription(String scriptText);

	public int getStartTime();

	public void setStartTime(int startTime);

	public String getStartTimeString();

	public void setStartTimeString(String startTime);

	public int getStartTimeOrg();

	public void setStartTimeOrg(int startTimeOrg);

	public String getStartTimeOrgString();

	public void setStartTimeOrgString(String startTimeOrg);

	public int getEndTime();

	public void setEndTime(int endTime);

	public String getEndTimeString();

	public void setEndTimeString(String endTime);

	public void setEndTimeAccurate(boolean accurate);

	public boolean isEndTimeAccurate();

	public int getWavEndTime();

	public void setWavEndTime(int wavEndTime);

	public String getWavEndTimeString();

	public void setWavEndTimeString(String wavEndTime);

	public boolean isExtended();

	public void setExtended(boolean vgExtended);

	public boolean getVgGender();

	public void setVgGender(boolean vgGender);

	public int getVgPitch();

	public void setVgPitch(int vgPitch);

	public int getVgVolume();

	public void setVgVolume(int vgVolume);

//	public int getVgLang();
//
//	public void setVgLang(int vgLang);

	public URI getWavURI();

	public void setWavURI(URI fileName);

	public boolean isWavEnabled();

	public boolean setWavEnabled(boolean enable);

	public String getScriptComment();

	public void setScriptComment(String scriptComment);

	public int getVgPlaySpeed();

	public void setVgPlaySpeed(int vgPlaySpeed);

	public Float getWavPlaySpeed();

	public void setWavPlaySpeed(Float wavPlaySpeed);

	public int getMark();

	public void setMark(int mark);

	public int getEndTimeMax();

	public boolean isDataCommit();

	public void setDataCommit(boolean commit);

	public int getType();

	public void setType(int type);

	public void setLang(String lang);
	
	public String getLang();//

	public String getCaption();//

	public void setCaption(String Caption);//

	public String getCharacter();//

	public void setCharacter(String Character);//

	public String getScenario();//

	public void setScenario(String Scenario);//

	public int getComboIndex();//

	public void setComboIndex(int scComboIndex);//

}
