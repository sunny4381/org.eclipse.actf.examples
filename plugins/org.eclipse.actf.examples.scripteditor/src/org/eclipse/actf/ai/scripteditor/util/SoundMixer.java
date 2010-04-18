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
package org.eclipse.actf.ai.scripteditor.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.eclipse.actf.ai.scripteditor.preferences.CapturePreferenceUtil;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;

public class SoundMixer implements IUNIT {

	/**
	 * Local data
	 */
	// own instance
	static private SoundMixer ownInst = null;
	static private int ownProcMode = SM_PMODE_CAPTURE;
	// main thread instance
	private Thread mainThreadCapture = null;
	// voice capture control
	protected boolean runningCapture = false;

	// Capture audio mode
	private DataLine.Info voiceInfo = null;
	private TargetDataLine voiceLine = null;
	private ByteArrayOutputStream voiceOutputStream = null;
	private byte voiceCaptureBuffer[];
	private byte voiceStoreBuffer[];
	private byte voiceClearData[];
	private float voiceSampleRate = SM_CAP_RATE_NOM;	// Default sampling rate : 22050Hz

	// Save file(.wav) mode
	private AudioInputStream voiceInputStream = null;
	private AudioFileFormat.Type voiceTargetType;
	private File saveFH;

	// current WAV header information
	private String currentWavFormatID;
	private int currentWavCh;
	private float currentWavFrameRate;
	private int currentWavFrameSize;
	private float currentWavSampRate;
	private int currentWavSampBit;
	private boolean currentWavBigEndian;
	private String currentWavFormat;
	private int currentWavBytePerSec;
	private int currentWavDurationTime;
	private int currentWavDataLength;

	// Play wav sound mode
	private Thread mainThreadPlayer = null;
	private boolean runningPlayer = false;
	private AudioInputStream audioInputStreamWavPlayer;
	private SourceDataLine lineWavPlayer;


	/**
	 * Constructor
	 */
	private SoundMixer() {
		// Initialize value by Preference setting
		setSampleRateCaptureAudio( CapturePreferenceUtil.getPreferenceSampleRate() );
	}

	/**
	 * 
	 * @return
	 */
	static public SoundMixer getInstance() {

		// 1st check current Own Instance
		if (ownInst == null) {
			synchronized (SoundMixer.class) {
				// 2nd check current Own instance
				if (ownInst == null) {
					// New own class at once
					ownInst = new SoundMixer();
				}
			}
		}
		// return current own instance
		return (ownInst);
	}

	public void dispose() {
		try {
			// clear own instance
			if(voiceOutputStream != null) voiceOutputStream.close();
			if(ownInst != null)ownInst = null;
		} catch (Exception e) {
			System.out.println("SoundMixer.dispose() : " + e);
		}
	}

/*******************************************************************
 * Capture audio control part
 * 
 ******************************************************************/
	/**
	 * Main thread class for Capture audio
	 */
	class CaptureVoiceThread extends Thread {
		/**
		 * run() method : for Capture audio
		 */
		public void run() {
			while (runningCapture) {
				// PickUP capture audio data from InputStream
				updateCaptureSound();
				// yield own thread
				Thread.yield();
			}
		}
	}

	/**
	 * Main thread class for Save to file from captured audio data
	 */
	class SaveFileCaptureAudioThread extends Thread {
		/**
		 * run() method : for Save file from capture audio data
		 */
		public void run() {
			while (runningCapture) {
				// Save to file from capture audio data from InputStream
				updateSaveFileCaptureSound();
				// yield own thread
				Thread.yield();
			}
		}
	}

	/**
	 * Setter methods
	 */
	public void startCaptureSound(int procMode) {
		// Set own process mode
		ownProcMode = procMode;

		//1)Capture audio mode
		if(ownProcMode == SM_PMODE_CAPTURE){
			startCaptureAudio();
		}
		//2)Save to file(.wav) mode
		else if(ownProcMode == SM_PMODE_FSAVE){
			startSaveFileCaptureAudio();
		}
	}

	/**
	 * Setter methods
	 */
	private void startCaptureAudio() {
		try {
			// SetUP Input stream (wave mix)
			final AudioFormat format = getFormat();
			voiceInfo = new DataLine.Info(TargetDataLine.class, format);
			voiceLine = (TargetDataLine)AudioSystem.getLine(voiceInfo);

			// Initialize captured voice data(wave format)
			int bufferSize = format.getFrameSize();
			voiceCaptureBuffer = new byte[bufferSize];		// Capture buffer for Thread process
			voiceStoreBuffer = new byte[bufferSize];		// Store buffer for latest captured data
			voiceClearData = new byte[bufferSize];			// Clear data for stored buffer
			for(int i = 0; i < voiceClearData.length; i++){	// SetUP zero data
				voiceClearData[i] = 0;
			}

			// Setup output stream
			if(voiceOutputStream == null){
				voiceOutputStream = new ByteArrayOutputStream();
			}
			else {
				// reset data
				if(voiceOutputStream.size() > 0){
					voiceOutputStream.reset();
				}
			}

			// Spawn main thread class
			mainThreadCapture = new CaptureVoiceThread();

			// Open Input&Output stream
			runningCapture = true;
			voiceLine.open(format);
			voiceLine.start();
			mainThreadCapture.start();
		}
		catch (LineUnavailableException e) {
			System.err.println("Line unavailable: " + e);
		}
	}

	/**
	 * Setter methods
	 */
	public void startSaveFileCaptureAudio() {
		try {
			// SetUP Input stream (wave mix)
			final AudioFormat format = getFormat();
			voiceInfo = new DataLine.Info(TargetDataLine.class, format);
			voiceLine = (TargetDataLine)AudioSystem.getLine(voiceInfo);

			// Create & Open file for save voice data
			saveFH = new File("c:\\temp\\dummy.wav");
			saveFH.createNewFile();
			voiceTargetType = AudioFileFormat.Type.WAVE;
			voiceInputStream = new AudioInputStream(voiceLine);

			// Spawn main thread class
			mainThreadCapture = new SaveFileCaptureAudioThread();

			// Open Input&Output stream
			runningCapture = true;
			voiceLine.open(format);
			voiceLine.start();
			mainThreadCapture.start();
		}
		catch (LineUnavailableException e) {
			System.err.println("Line unavailable: " + e);
		}
		catch(IOException ee){
			System.err.println("File IO : " +ee);
		}
	}

	public void updateCaptureSound() {
		try {
			// Only Playing
			if( runningCapture ){
				// PickUP capture data(PCM)
				int count = voiceLine.read(voiceCaptureBuffer, 0, voiceCaptureBuffer.length);
				if(count > 0){
					// Buffering capture data
					System.arraycopy(voiceCaptureBuffer, 0, voiceStoreBuffer, 0, count);
				}
			}
		}
		catch (Exception e) {
			System.err.println("I/O problems: " + e);
		}
	}

	public void updateSaveFileCaptureSound() {
		try {
			// Only Playing
			if( runningCapture ){
				// Write data to .wav file
				AudioSystem.write(voiceInputStream, voiceTargetType, saveFH);
			}
		}
		catch (Exception e) {
			System.err.println("I/O problems: " + e);
		}
	}

	/**
	 * Setter methods
	 */
	public void stopCaptureSound() {
		try {
			if (runningCapture) {
				// Stop control
				runningCapture = false;
				mainThreadCapture = null;

				// Stop&Close InputStream for Save file mode
				if(voiceInputStream != null){
					voiceInputStream.close();
				}
				// Stop&Close InputStream for Capture audio mode
				if(voiceLine != null){
					voiceLine.stop();
					voiceLine.close();
				}
			}
		} catch (Exception e) {
			System.out.println("stopSoundMixer() : " + e);
		}
	}

	/**
	 * Getter method : Get current sampling rate for capture audio
	 * @return Current sampling rate(Hz)
	 */
	public float getSampleRateCaptureAudio() {
		// return current value of sampling rate for capture audio
		return(voiceSampleRate);
	}
	/**
	 * Setter method : Set sampling rate for capture audio
	 * @param newSampleRate : new sampling rate for capture audio
	 */
	public void setSampleRateCaptureAudio(float newSampleRate) {
		// Update new sampling rate for capture audio
		voiceSampleRate = newSampleRate;
	}

	/**
	 * Getter method
	 */
	public int getSizeCaptureData(int scaleTime) {
		// return size of captured data
		byte audio[] = voiceOutputStream.toByteArray();
		// Get current format of Audio
		final AudioFormat format = getFormat();

		// Calculate current size of audio data
		int len = audio.length / format.getFrameSize();

		// return result
		return (len);
	}

	/**
	 * Getter method
	 */
	public int pickupCaptureAudioLevel() {
		// If return data is minus, then End of Data
		int rawData = -1;

		// check running
		if( runningCapture ){
			// check current process mode
			if(ownProcMode == SM_PMODE_CAPTURE){
				// Exchange data type from byte[] to integer
				rawData = ((int)voiceStoreBuffer[1] << 8) + (int)voiceStoreBuffer[0];
				if (rawData < 0) rawData = -1 * rawData;

				// Clear current stored buffer
				System.arraycopy(voiceClearData, 0, voiceStoreBuffer, 0, voiceClearData.length);
			}
		}

		// return raw data
		return (rawData);
	}

	private AudioFormat getFormat() {
		// MakeUP PCM type format
		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
		float sampleRate = voiceSampleRate;
		int sampleSizeInBits = 16;
		int channels = 2;
		int frameSize = 4;
		float frameRate = voiceSampleRate;
		boolean bigEndian = false;

		// return format type(WAV)
		return (new AudioFormat(encoding, sampleRate, sampleSizeInBits,
				channels, frameSize, frameRate, bigEndian));
	}

/*******************************************************************
 * Play audio control part
 * 
 ******************************************************************/
	/**
	 * Main thread class for Play sound
	 */
	class PlaySoundThread extends Thread {
		/**
		 * run() method : for Capture audio
		 */
		public void run() {
			try {
				int size = 0;
				float frameRate = audioInputStreamWavPlayer.getFormat().getFrameRate();
				int frameSize = audioInputStreamWavPlayer.getFormat().getFrameSize();
				byte[] inStream = new byte[(int)(frameRate * frameSize)];

				// Play sound stream
				while (runningPlayer) {
					// Read WAV data from input stream
					size = audioInputStreamWavPlayer.read(inStream, 0, inStream.length);
					if(size >= 0){
						// put WAV data to source line
						int len = lineWavPlayer.write(inStream, 0, size);
					}
					else {
						// End of WAV data
						stopPlaySound();
						break;
					}
					// yield own thread
					Thread.yield();
				}
			}
			catch (IOException e) {
				//System.out.println("IOException : " +e);

				// Forced stop player
				stopPlaySound();
			}
		}
	}

	/**
	 * Local method : Re-make Audio Format of current WAV file for Adjust Sampling Rate
	 * @param orgForm
	 * @param newSampleRate
	 * @return New AudioFormat
	 */
	private AudioFormat adjustSampleRateWav(AudioFormat orgForm, float newSampleRate) {
		// MakeUP new sampling rate
		float sampleRate = newSampleRate;

		// And Copy otherwise PCM parameters from original audio format
		AudioFormat.Encoding encoding = orgForm.getEncoding();
		int sampleSizeInBits = orgForm.getSampleSizeInBits();
		int channels = orgForm.getChannels();
		int frameSize = orgForm.getFrameSize();
		float frameRate = orgForm.getFrameRate();
		boolean bigEndian = orgForm.isBigEndian();

		// return format type(WAV)
		return (new AudioFormat(encoding, sampleRate, sampleSizeInBits,
				channels, frameSize, frameRate, bigEndian));
	}

	/**
	 * Setter method : Start play WAV sound
	 * @throws LineUnavailableException 
	 */
	public void startPlaySound(URI uriWavFileName, float competitiveRatioWav) {
		if(uriWavFileName != null){
			try {
				// Check current status of Player thread
				if( runningPlayer ){
					// Forced stop player
					stopPlaySound();
				}

				// Open WAV file
				File soundFile = new File(uriWavFileName);
				// Create Input stream
				audioInputStreamWavPlayer = AudioSystem.getAudioInputStream(soundFile);
				// Get audio format from current input stream
				AudioFormat audioFormat = audioInputStreamWavPlayer.getFormat();

				// Adjust play speed parameter(sampling rate)
				float samplerate = audioFormat.getSampleRate();
				samplerate = samplerate * competitiveRatioWav;
				audioFormat = adjustSampleRateWav(audioFormat, samplerate);

				// Get data line from current input stream
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
				// Get source line from current data line
				lineWavPlayer = (SourceDataLine) AudioSystem.getLine(info);
				// Open source line
				lineWavPlayer.open(audioFormat);
				// Start source line
				lineWavPlayer.start();

				// Spawn player thread & Run
				mainThreadPlayer = new PlaySoundThread();
				runningPlayer = true;
				mainThreadPlayer.start();

			}
			catch (UnsupportedAudioFileException e1) {
				//System.out.println("UnsupportedAudioFileException : "+e1);
			}
			catch (IOException e2) {
				//System.out.println("IOException : "+e2);
			}
			catch (LineUnavailableException e3) {
				//System.out.println("LineUnavailableException : "+e3);
			}
		}
	}

	/**
	 * Setter methods
	 */
	public void stopPlaySound() {
		try {
			if (runningPlayer) {
				// Stop control
				runningPlayer = false;
				mainThreadPlayer = null;

				// Stop&Close InputStream for Save file mode
				if(audioInputStreamWavPlayer != null){
					audioInputStreamWavPlayer.close();
				}
				// Stop&Close InputStream for Capture audio mode
				if(lineWavPlayer != null){
					lineWavPlayer.drain();
					lineWavPlayer.close();
				}
			}
		}
		catch (Exception e) {
			System.out.println("stopPlaySound() : " + e);
		}
	}

	/**
	 * Getter method : Get current status of Play sound
	 */
	public boolean isRunningPlaySound() {
		// return current status
		return(runningPlayer);
	}

	/**
	 * Getter method : Check type WAV format of current file
	 * @throws IOException 
	 * @throws UnsupportedAudioFileException 
	 */
	public boolean isWavFormat(String fname) throws FileNotFoundException {
		boolean result = false;
		File fh;

		try {
			// open target file
			fh = new File(fname);
			// Get audio format from target file
			AudioFileFormat aff = AudioSystem.getAudioFileFormat(fh);
			// check audio format as WAV?
			if(AudioFileFormat.Type.WAVE == aff.getType()){
				// target file type is WAV format
				result = true;
			}
		}
		catch(UnsupportedAudioFileException e1){
			//System.out.println("UnsupportedAudioFileException : "+e1);
		}
		catch(IOException e2){
			//System.out.println("IOException : "+e2);
		}

		// return result
		return(result);
	}

	/**
	 * Getter method : MakeUP string of WAV header information
	 */
	public String makeFormatWavInfo(String wfname) {
		// local parameters
		String strWavInfo = "";
		String strSeparator = "\n\r";
		String strFileName = "WAV file name : ";
		String strFormatID = "Format ID : ";
		String strSampRate = "Sampling rate : ";
		String strSampBit = "Sampling bit : ";
		String strChNum = "Channel : ";
		String strFrameRate = "Frame rate : ";
		String strEndian = "Endian : ";

		// Cat string for WAV header information
		strWavInfo =   strFileName  + wfname + strSeparator
		             + strFormatID  + currentWavFormatID + strSeparator
		             + strSampRate  + Integer.toString((int)currentWavSampRate) + " Hz" + strSeparator
		             + strSampBit   + Integer.toString(currentWavSampBit) + " bit" + strSeparator
		             + strChNum     + ((currentWavCh == 2) ? "Stereo" : "Mono") + strSeparator
		             + strFrameRate + Integer.toString((int)currentWavFrameRate) + " bytes/frame" + strSeparator
		             + strEndian   + (currentWavBigEndian ? "Big Endian" : "Littele Endian") + strSeparator;

		// return result
		return(strWavInfo);
	}

	/**
	 * Getter method : Get end time of WAV data
	 */
	public int getDurationTimeWav() {
		// return current end time of WAV data
		return(currentWavDurationTime);
	}

	/**
	 * Getter method : Read WAV file header information
	 */
	public void storeWavHeader(URI fname) throws Exception {
		File fh;

		// open target file
		fh = new File(fname);
		// Get audio format from target file
		AudioFileFormat aff = AudioSystem.getAudioFileFormat(fh);
		// check audio format as WAV?
		if(AudioFileFormat.Type.WAVE == aff.getType()){
			// Get current WAV header information
			AudioFormat af = aff.getFormat();
			currentWavFormatID = af.getEncoding().toString();
			currentWavCh = af.getChannels();
			currentWavFrameRate = af.getFrameRate();
			currentWavFrameSize = af.getFrameSize();
			currentWavSampRate = af.getSampleRate();
			currentWavSampBit = af.getSampleSizeInBits();
			currentWavBigEndian = af.isBigEndian();
			currentWavFormat = af.toString();

			// Calculate byte per second of current WAV file
			currentWavBytePerSec = (int)(currentWavSampRate * currentWavCh * (currentWavSampBit / 8));
			// Calculate total data length of current WAV file
			currentWavDataLength = (int)(aff.getFrameLength() * currentWavFrameSize);
			// Calculate duration time of current WAV file
			currentWavDurationTime = (int)(((float)currentWavDataLength / (float)currentWavBytePerSec) * (float)MSEC);
		}
	}

}
