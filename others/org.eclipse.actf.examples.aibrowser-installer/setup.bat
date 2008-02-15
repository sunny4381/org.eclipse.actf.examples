::############################################################################
::# Copyright (c) 2007, 2008 IBM Corporation and others.
::# All rights reserved. This program and the accompanying materials
::# are made available under the terms of the Eclipse Public License v1.0
::# which accompanies this distribution, and is available at
::# http://www.eclipse.org/legal/epl-v10.html
::#
::# Contributors:
::#     IBM Corporation - initial API and implementation
::############################################################################
REM aiBrowser installer setup script
::
setlocal
if not defined BUILDDIR set BUILDDIR=c:\build\aiBrowser

copy /Y "..\org.eclipse.actf.examples.aibrowser-feature\epl-v10.html" %BUILDDIR%\eclipse
copy /Y "..\org.eclipse.actf.examples.aibrowser-feature\license.html" %BUILDDIR%\eclipse\notice.html

echo osgi.configuration.area=@user.home/Application Data/ACTF/aiBrowser/1.0/configuration >> %BUILDDIR%\eclipse\configuration\config.ini
echo osgi.instance.area=@user.home/Application Data/ACTF/aiBrowser/1.0/workspace >> %BUILDDIR%\eclipse\configuration\config.ini

mkdir %BUILDDIR%\Scripts\jaws
copy /Y "..\org.eclipse.actf.ai.screenreader.jaws\script\*" %BUILDDIR%\Scripts\jaws

mkdir %BUILDDIR%\img
copy /Y "..\org.eclipse.actf.examples.aibrowser\splash.bmp" %BUILDDIR%\img
copy /Y "..\org.eclipse.actf.examples.aibrowser\icons\aiBrowser.ico" %BUILDDIR%\img
