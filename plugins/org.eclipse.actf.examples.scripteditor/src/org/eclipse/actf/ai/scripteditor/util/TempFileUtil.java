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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.actf.util.FileUtils;

public class TempFileUtil {

	static final int EOF = -1;

	// Local data
	static private FileInputStream fis = null;
	static private FileOutputStream fos = null;
	static private DataInputStream dis = null;
	static private DataOutputStream dos = null;
	static private CopyStreamTempFileThread instCopyThread = null;
	static private WriteStreamTempFileThread instWriteThread = null;
	static private boolean statusActiveThread = false;
	static private ArrayList<Integer> listStartTime = null;
	static private ArrayList<Integer> listVolLvl = null;

	/**
	 * @category Setter method : Create new file into system temporary directory
	 * @param parentDir
	 *            : parent directory into system temporary directory
	 * @param prefix
	 *            : prefix of new file
	 * @param suffix
	 *            : suffix of new file
	 * @return instance of File
	 */
	static public File createTempFile(String parentDir, String prefix,
			String suffix) {
		File tempFile = null;

		try {
			// Create parent directory
			File dh = createTempDirectory(parentDir);
			if (dh != null) {
				// Create new temporary file
				tempFile = File.createTempFile(prefix, suffix, dh);
			}
		} catch (IOException ee) {
			System.out.println("createTempFile() : " + ee);
		}

		// return result
		return (tempFile);
	}

	/**
	 * @category Getter method : Create temporary directory onto system
	 *           management area
	 * @return file handler of temporary directory
	 */
	private static File createTempDirectory(String parentDir) {
		File result = null;
		String tmpS = Activator.getDefault().getStateLocation().toOSString()
				+ File.separator + "tmp" + File.separator + parentDir
				+ File.separator; //$NON-NLS-1$

		if (FileUtils.isAvailableDirectory(tmpS)) {
			result = new File(tmpS);
		} else {
			System.err
					.println("createTempDirectory() : can't create tmp Directory"); //$NON-NLS-1$
			result = new File(System.getProperty("java.io.tmpdir") + parentDir); //$NON-NLS-1$
		}
		// return result
		return (result);
	}

	/**
	 * @category Getter method : Get absolute path of target file
	 * @param fh
	 *            : file handler
	 * @return string of absolute path of target file
	 */
	static public String getAbsTempFile(File fh) {
		// return result
		return (fh.getAbsolutePath());
	}

	/**
	 * @category Copy binary stream from source file to destination file
	 * @param srcFilePath
	 *            : string of source file path
	 * @param desFilePath
	 *            : string of destination file path
	 */
	static public void copyStreamTempFile(String srcFilePath, String desFilePath) {
		// Check current status of copy thread
		if (!statusActiveThread) {
			// SetUP in/out stream
			fis = null;
			fos = null;
			try {
				// Open source/destination file stream
				fis = new FileInputStream(srcFilePath);
				fos = new FileOutputStream(desFilePath);
				// Start copy thread
				if ((fis != null) && (fos != null)) {
					statusActiveThread = true;
					instCopyThread = new CopyStreamTempFileThread();
					instCopyThread.start();
				}
			} catch (Exception ee) {

			}
		}
	}

	/**
	 * @category Write binary stream to destination file
	 * @param srcData1
	 *            : source data 1 (Start Time)
	 * @param srcData2
	 *            : source data 2 (Volume Level)
	 * @param desFilePath
	 *            : string of destination file path
	 */
	static public void writeStreamTempFile(ArrayList<Integer> srcData1,
			ArrayList<Integer> srcData2, String desFilePath) {

		// Check current status of copy thread
		if (!statusActiveThread) {
			// SetUP out stream
			fos = null;
			dos = null;
			try {
				// Open destination file stream
				fos = new FileOutputStream(desFilePath);
				// Start write thread
				if (fos != null) {
					// Open data out stream
					dos = new DataOutputStream(fos);
					// Deep copy from source data list
					listStartTime = new ArrayList<Integer>(srcData1);
					listVolLvl = new ArrayList<Integer>(srcData2);
					// start write thread
					statusActiveThread = true;
					instWriteThread = new WriteStreamTempFileThread();
					instWriteThread.start();
				}
			} catch (Exception ee) {

			}
		}
	}

	/**
	 * @category Open input stream for read data from temporary file
	 * @param srcFilePath
	 *            : source file path
	 * @return status : TRUE:success process, FALSE:occured exception
	 */
	static public boolean openInputStreamTempFile(String srcFilePath) {
		// Check current status of copy thread
		if (!statusActiveThread) {
			// SetUP in stream
			fis = null;
			dis = null;
			try {
				// Open source file stream
				fis = new FileInputStream(srcFilePath);
				dis = new DataInputStream(fis);
				// Start copy thread
				if ((fis != null) && (dis != null)) {
					// Success process
					statusActiveThread = true;
				}
			} catch (Exception ee) {

			}
		}
		// return result
		return (statusActiveThread);
	}

	/**
	 * @category Read four bytes data from current input data stream
	 * @return next four bytes data
	 */
	static public int readIntValueTempFile() {
		int result = EOF;
		try {
			// Read integer data from input stream
			result = dis.readInt();
		} catch (EOFException ef) {
			result = EOF;
		} catch (Exception ee) {
			result = EOF;
		}
		// return result
		return (result);
	}

	/**
	 * @category Setter method : Close all input stream for read data from
	 *           temporary file
	 */
	static public void closeInputStreamTempFile() {
		try {
			// Close all file stream
			dis.close();
			fis.close();
		} catch (Exception ee) {

		} finally {
			// drop status flag
			statusActiveThread = false;
		}
	}

	/**
	 * @category Getter method : Get active status of copy thread
	 * @return status of thread
	 */
	static public boolean isActiveTempFileThread() {
		// return result
		return (statusActiveThread);
	}

	/**
	 * Getter method : Get resource URL string
	 */
	static public URI getResource(String fpath) {
		URI result = null;

		// exchange type from String to URI
		fpath = fpath.replace("file:/", "");
		File fh = new File(fpath);
		result = fh.toURI();

		// return result
		return (result);
	}

	/**
	 * @category Local class : Thread of copy temporary file
	 * 
	 */
	static private class CopyStreamTempFileThread extends Thread {
		/**
		 * @category Thread{@link #run()}
		 */
		public void run() {
			int len;
			byte copyBuffer[] = new byte[4096];
			try {
				while (statusActiveThread) {
					// check exist source file
					if (fis.available() > 0) {
						// Read binary stream from source file
						len = fis.read(copyBuffer);
						if (len == -1) {
							// End of source File
							closeThread();
							break;
						}
						// Write binary stream to destination file
						fos.write(copyBuffer, 0, len);
					} else {
						// forced close thread
						closeThread();
						break;
					}
					// Thread yield
					Thread.yield();
				}
			} catch (Exception ee) {

			}
		}

		/**
		 * @category Setter method : Close run() method
		 */
		private void closeThread() {
			// Reset status flag
			statusActiveThread = false;
			instCopyThread = null;
			// Close all file stream
			try {
				fis.close();
				fos.close();
			} catch (Exception ee) {

			}
		}
	}

	/**
	 * @category Local class : Thread of write temporary file
	 * 
	 */
	static private class WriteStreamTempFileThread extends Thread {
		/**
		 * @category Thread{@link #run()}
		 */
		public void run() {
			int location = 0;
			int max = listStartTime.size();
			try {
				while (statusActiveThread) {
					// Check exist data
					location++;
					if (location < max) {
						// write start time to target file
						dos.writeInt(listStartTime.get(location));
						// write volume level to target file
						dos.writeInt(listVolLvl.get(location));
					} else {
						// forced close thread
						closeThread();
						break;
					}
					// Thread yield
					Thread.yield();
				}
			} catch (Exception ee) {

			}
		}

		/**
		 * @category Setter method : Close run() method
		 */
		private void closeThread() {
			// Reset status flag
			statusActiveThread = false;
			instWriteThread = null;
			// Close all file stream
			try {
				dos.close();
				fos.close();
				listStartTime.clear();
				listVolLvl.clear();
				listStartTime = null;
				listVolLvl = null;
			} catch (Exception ee) {

			}
		}
	}

}
