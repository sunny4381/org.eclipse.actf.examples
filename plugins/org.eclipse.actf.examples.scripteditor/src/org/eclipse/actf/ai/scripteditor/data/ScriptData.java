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

import org.eclipse.actf.ai.scripteditor.util.TimeFormatUtil;

public class ScriptData implements IScriptData {

	private String description = "";
	private String lang = "en-US"; //TODO use locale

	private int startTime = 0;
	private int startTimeOrg = 0;
	private int endTime = 0;
	private int wavEndTime = 0;
	private int maxEndTime = 0;

	private boolean isEndTimeAccurate = false;

	private boolean vgExtended = false;
	private boolean vgGender = true; // mail
	private int vgPitch = 50;
	private int vgVolume = 50;

	private int vgPlaySpeed = 50;
	private float wavPlaySpeed = 1.0F;

	private String vgCharacter = "";

	private URI wavURI = null;
	private boolean wavEnabled = false;

	private String scriptComment = "";

	private int mark = -1;

	private boolean commit = false;
	private int type;

	private String scenario = "";
	private int comboIndex = 0;
	private String caption = "";

	protected ScriptData clone = null;

	// private UUID id = null;

	protected ScriptData() {
		// id = UUID.randomUUID();
	}

	public String getCaption() {
		return caption;
	}

	public String getCharacter() {
		return vgCharacter;
	}

	public int getComboIndex() {
		return comboIndex;
	}

	public String getDescription() {
		return description;
	}

	public int getEndTime() {
		return this.endTime;
	}

	public int getEndTimeMax() {

		int rtnEndTime = 0;

		rtnEndTime = endTime;
		if (rtnEndTime < wavEndTime) {
			rtnEndTime = wavEndTime;
		}

		return rtnEndTime;
	}

	public String getEndTimeString() {
		return TimeFormatUtil.makeFormatHHMMSSMS(this.endTime);
	}

	// vgExtended
	public boolean isExtended() {
		return this.vgExtended;
	}

	public URI getWavURI() {
		return this.wavURI;
	}

	public void setLang(String lang){
		//TODO validate
		this.lang = lang;
	}
	
	public String getLang() {
		return lang;
	}

	public int getMark() {
		return mark;
	}

	public String getScenario() {
		return scenario;
	}

	public String getScriptComment() {
		if (scriptComment == null) {
			return "";
		}
		return scriptComment.toString();
	}

	public int getStartTime() {
		return this.startTime;
	}

	public int getStartTimeOrg() {
		return this.startTimeOrg;
	}

	public String getStartTimeOrgString() {
		return TimeFormatUtil.makeFormatHHMMSSMS(this.startTimeOrg);
	}

	public String getStartTimeString() {
		return TimeFormatUtil.makeFormatHHMMSSMS(this.startTime);
	}

	public int getType() {
		return type;
	}

	public boolean getVgGender() {
		return this.vgGender;
	}

	public int getVgPitch() {
		return this.vgPitch;
	}

	public int getVgPlaySpeed() {
		return this.vgPlaySpeed;
	}

	public int getVgVolume() {
		return this.vgVolume;
	}

	public int getWavEndTime() {
		return wavEndTime;
	}

	public String getWavEndTimeString() {
		return TimeFormatUtil.makeFormatHHMMSSMS(this.wavEndTime);
	}

	public Float getWavPlaySpeed() {
		return this.wavPlaySpeed;
	}

	public boolean isDataCommit() {
		return this.commit;
	}

	public boolean isWavEnabled() {
		return this.wavEnabled;
	}

	public void setCaption(String Caption) {
		this.caption = Caption;
	}

	public void setCharacter(String Character) {
		this.vgCharacter = Character;
	}

	public void setComboIndex(int comboIndex) {
		this.comboIndex = comboIndex;
	}

	public void setDataCommit(boolean commit) {
		this.commit = commit;
	}

	public void setDescription(String description) {
		if (description != null) {
			this.description = description;
		}
	}

	public boolean setWavEnabled(boolean enabled) {
		this.wavEnabled = enabled && (wavURI != null);
		return this.wavEnabled;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
		if (this.maxEndTime < this.endTime) {
			this.maxEndTime = this.endTime;
		}
	}

	public void setEndTimeString(String endTime) {
		this.endTime = TimeFormatUtil.parseIntStartTime(endTime);
	}

	public void setExtended(boolean vgExtended) {
		this.vgExtended = vgExtended;
	}

	public void setWavURI(URI fileName) {
		// TODO existence/read check
		this.wavURI = fileName;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public void setScenario(String Scenario) {
		this.scenario = Scenario;
	}

	public void setScriptComment(String scriptComment) {
		this.scriptComment = scriptComment;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public void setStartTimeOrg(int startTimeOrg) {
		this.startTimeOrg = startTimeOrg;
	}

	public void setStartTimeOrgString(String startTimeOrg) {
		this.startTimeOrg = TimeFormatUtil.parseIntStartTime(startTimeOrg);
	}

	public void setStartTimeString(String startTime) {
		this.startTime = TimeFormatUtil.parseIntStartTime(startTime);
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setVgGender(boolean vgGender) {
		this.vgGender = vgGender;
	}

	public void setVgPitch(int vgPitch) {
		this.vgPitch = vgPitch;
	}

	public void setVgPlaySpeed(int vgPlaySpeed) {
		this.vgPlaySpeed = vgPlaySpeed;
	}

	public void setVgVolume(int vgVolume) {
		this.vgVolume = vgVolume;
	}

	public void setWavEndTime(int wavEndTime) {
		this.wavEndTime = wavEndTime;
	}

	public void setWavEndTimeString(String wavEndTime) {
		this.wavEndTime = TimeFormatUtil.parseIntStartTime(wavEndTime);
	}

	public void setWavPlaySpeed(Float wavPlaySpeed) {
		this.wavPlaySpeed = wavPlaySpeed;
	}

	public void setEndTimeAccurate(boolean accurate) {
		isEndTimeAccurate = accurate;
	}

	public boolean isEndTimeAccurate() {
		return isEndTimeAccurate;
	}

}
