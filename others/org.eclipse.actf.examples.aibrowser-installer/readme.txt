############################################################################
# Copyright (c) 2007, 2008 IBM Corporation and others.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#     IBM Corporation - initial Documentation
############################################################################

1. Download this project into the workspace that has aiBrowser plugins.

2. Export aiBrowser into C:\build\aiBrowser directory by using 
   Eclipse Product export wizard.

 ** Before export, remove "eclipse" folder in C:\build\aiBrowser.
   
3. Run setup.bat from the Command Prompt (or Cygwin shell)

$ cd <eclipse_workspace>\org.eclipse.actf.examples.aibrowser-installer
$ setup.bat

 ** setup.bat can't work well by double clicking from the Eclipse workspace
 
4. Build installer by using aiBrowserInstaller.ism (InstallShield)
