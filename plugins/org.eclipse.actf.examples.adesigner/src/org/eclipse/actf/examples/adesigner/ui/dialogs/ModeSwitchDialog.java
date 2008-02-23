/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Norimasa HAYASHIDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.adesigner.ui.dialogs;

import java.util.EventObject;

import org.eclipse.actf.examples.adesigner.ADesignerPlugin;
import org.eclipse.actf.examples.adesigner.internal.Messages;
import org.eclipse.actf.examples.adesigner.ui.perspectives.FlashPerspective;
import org.eclipse.actf.examples.adesigner.ui.preferences.IPreferenceConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;




public class ModeSwitchDialog extends TitleAreaDialog {

    private static final String MODE_SELECTED = "MODE_SELECTED";

    private IWorkbenchWindow _window;

    private String _selectedPerspectiveID = ADesignerPlugin.getDefault().getPreferenceStore().getString(
            IPreferenceConstants.SELECTED_MODE_PERSPECTIVE_ID);

    private Composite _selectedComp = null;

    private Button _okButton;

    private Button _startupCheckButton;

    private Image[] modeIcons;

    public ModeSwitchDialog(IWorkbenchWindow window) {
        super(window.getShell());
        this._window = window;
    }

    public String getSelectedPerspective() {
        return this._selectedPerspectiveID;
    }

    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        this._okButton.setFocus();
        return contents;
    }

    protected Control createDialogArea(Composite parent) {

        Composite dialogArea = (Composite) super.createDialogArea(parent);

        IPerspectiveDescriptor[] perspectiveDesc = _window.getWorkbench().getPerspectiveRegistry().getPerspectives();
        int length = perspectiveDesc.length;
        String[] modePerspectiveIDs = new String[length];
        String[] modeNames = new String[length];
        modeIcons = new Image[length];
        for (int i = 0; i < length; i++) {
            IPerspectiveDescriptor desc = perspectiveDesc[i];
            modePerspectiveIDs[i] = desc.getId();
            modeNames[i] = desc.getLabel().replaceAll("  ", System.getProperty("line.separator"));
            modeIcons[i] = desc.getImageDescriptor().createImage();
            // System.out.println(desc.getId()+":"+desc.getLabel());
        }

        int modeNum = modeNames.length;

        final Composite modeBaseComp = new Composite(dialogArea, SWT.NONE);
        modeBaseComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        modeBaseComp.setLayoutData(gridData);
        GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = gridLayout.verticalSpacing = gridLayout.marginWidth = gridLayout.marginHeight = 0;
        gridLayout.marginBottom = 5;
        gridLayout.numColumns = modeNum;
        modeBaseComp.setLayout(gridLayout);

        for (int i = 0; i < modeNum; i++) {
            // if (_window.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(modePerspectiveIDs[i]) != null)
            // {
            final Composite modeComp = new Composite(modeBaseComp, SWT.NONE);
            modeComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
            gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            modeComp.setLayoutData(gridData);
            gridData.minimumWidth = 100;
            gridLayout = new GridLayout();
            gridLayout.horizontalSpacing = 0; 
            gridLayout.verticalSpacing = 0;
            gridLayout.marginWidth = 5;
            gridLayout.marginHeight = 0;
            modeComp.setLayout(gridLayout);

            createModeComposite(modeComp, modeNames[i], modeIcons[i], modePerspectiveIDs[i]);
            // }
        }

        Composite startupCheckComp = new Composite(dialogArea, SWT.NONE);
        startupCheckComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        gridLayout = new GridLayout();
        gridLayout.marginRight = 5;
        gridLayout.marginTop = 5;
        gridLayout.marginHeight = 0;
        startupCheckComp.setLayout(gridLayout);

        this._startupCheckButton = new Button(startupCheckComp, SWT.CHECK);
        this._startupCheckButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false));
        this._startupCheckButton.setText(Messages.ModeSwitchDialog_checkShowDialog);
        this._startupCheckButton.setSelection(ADesignerPlugin.getDefault().getPreferenceStore().getString(
                IPreferenceConstants.STARTUP_OPTION_ID) == IPreferenceConstants.CHOICE_SHOW_MODE_DIALOG);

        setMessage(Messages.ModeSwitchDialog_message);
        setTitle(Messages.ModeSwitchDialog_title);

        return dialogArea;
    }

    private void createModeComposite(Composite parent, String modeName, Image iconImage, final String perspectiveID) {

        Composite iconComp = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.heightHint = 100;
        iconComp.setLayoutData(gridData);
        GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = gridLayout.verticalSpacing = gridLayout.marginWidth = gridLayout.marginHeight = 0;
        iconComp.setLayout(gridLayout);

        Label iconImageLabel = new Label(iconComp, SWT.NONE);
        iconImageLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
        iconImageLabel.setImage(iconImage);

        // Label modeNameLabel = new Label(parent, SWT.NONE);
        Link modeNameLabel = new Link(parent, SWT.NONE);
        modeNameLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
        // modeNameLabel.setText(modeName);
        modeNameLabel.setText("<a>" + modeName + "</a>");
        // modeNameLabel.setAlignment(SWT.CENTER);
        modeNameLabel.addSelectionListener(new ModeSwitchMouseAdapter(perspectiveID));
        modeNameLabel.addFocusListener(new ModeSwitchMouseAdapter(perspectiveID));
        modeNameLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        iconComp.addMouseListener(new ModeSwitchMouseAdapter(perspectiveID));
        iconComp.addMouseTrackListener(new ModeSwitchMouseTrackAdapter());

        iconImageLabel.addMouseListener(new ModeSwitchMouseAdapter(perspectiveID));
        iconImageLabel.addMouseTrackListener(new ModeSwitchMouseTrackAdapter());

        boolean isSelected = this._selectedPerspectiveID.equals(perspectiveID);
        iconComp.setData(MODE_SELECTED, new Boolean(isSelected));
        if (isSelected) {
            changeModeCompColor(iconComp, SWT.COLOR_LIST_SELECTION_TEXT, SWT.COLOR_LIST_SELECTION);
            this._selectedComp = iconComp;
        } else {
            changeModeCompColor(iconComp, SWT.COLOR_BLACK, SWT.COLOR_WHITE);
        }

    }

    protected void createButtonsForButtonBar(Composite parent) {
        this._okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(ADesignerPlugin.getResourceString("adesigner.window.title"));
    }

    
    @Override
    protected void handleShellCloseEvent() {
        checkCache();
        super.handleShellCloseEvent();
    }

    @Override
    protected void cancelPressed() {
        checkCache();
        super.cancelPressed();
    }
    
    protected void checkCache(){
        IPreferenceStore store = ADesignerPlugin.getDefault().getPreferenceStore();
        if(store.getString(IPreferenceConstants.SELECTED_MODE_PERSPECTIVE_ID).equals(FlashPerspective.ID)){
            //ProxyPlugin.getDefault().checkCache();
        }
    }

    protected void okPressed() {

        try {
            _window.getWorkbench().showPerspective(getSelectedPerspective(), _window);
        } catch (WorkbenchException we) {
        	//we.printStackTrace();
        }

        IPreferenceStore store = ADesignerPlugin.getDefault().getPreferenceStore();
        if (this._startupCheckButton.getSelection()) {
            store.setValue(IPreferenceConstants.STARTUP_OPTION_ID, IPreferenceConstants.CHOICE_SHOW_MODE_DIALOG);
        } else {
            store.setValue(IPreferenceConstants.STARTUP_OPTION_ID, IPreferenceConstants.CHOICE_DONOT_SHOW_MODE_DIALOG);
        }

        store.setValue(IPreferenceConstants.SELECTED_MODE_PERSPECTIVE_ID, this._selectedPerspectiveID);

        checkCache();
        
        super.okPressed();
    }

    private class ModeSwitchMouseAdapter extends MouseAdapter implements SelectionListener, FocusListener {

        private String _perspectiveID;

        ModeSwitchMouseAdapter(String perspectiveID) {
            this._perspectiveID = perspectiveID;
        }

        public void mouseUp(MouseEvent evt) {
            select(evt);
        }

        public void select(EventObject evt) {

            _selectedPerspectiveID = this._perspectiveID;

            Composite selectedComp = getSelectedComp(evt);
            if (null != selectedComp) {
                changeModeCompColor(selectedComp, SWT.COLOR_LIST_SELECTION_TEXT, SWT.COLOR_LIST_SELECTION);
                selectedComp.setData(MODE_SELECTED, Boolean.TRUE);

                if (null != _selectedComp && !_selectedComp.equals(selectedComp)) {
                    _selectedComp.setData(MODE_SELECTED, Boolean.FALSE);
                    changeModeCompColor(_selectedComp, SWT.COLOR_BLACK, SWT.COLOR_WHITE);
                }

                _selectedComp = selectedComp;
            }
        }

        public void widgetSelected(SelectionEvent e) {
            select(e);
            // okPressed();
        }

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void focusGained(FocusEvent e) {
            select(e);
        }

        public void focusLost(FocusEvent e) {
        }
    }

    private class ModeSwitchMouseTrackAdapter extends MouseTrackAdapter {

        public void mouseEnter(MouseEvent evt) {
            select(evt);
        }

        public void select(EventObject evt) {
            Composite selectedComp = getSelectedComp(evt);
            if (null != selectedComp) {
                changeModeCompColor(selectedComp, SWT.COLOR_LIST_SELECTION_TEXT, SWT.COLOR_LIST_SELECTION);
            }
        }

        public void mouseExit(MouseEvent evt) {
            deselect(evt);
        }

        public void deselect(EventObject evt) {
            Composite selectedComp = getSelectedComp(evt);
            if (null != selectedComp) {
                Boolean isSelected = (Boolean) selectedComp.getData(MODE_SELECTED);
                if (null != isSelected) {
                    if (!isSelected.booleanValue()) {
                        changeModeCompColor(selectedComp, SWT.COLOR_BLACK, SWT.COLOR_WHITE);
                    }
                }
            }
        }
    }

    protected void changeModeCompColor(Composite selectedComp, int iFgColor, int iBgColor) {
        Composite selectedCompParent = selectedComp;// selectedComp.getParent();
        selectedCompParent.setBackground(Display.getCurrent().getSystemColor(iBgColor));
        Control[] children = selectedCompParent.getChildren();
        for (int i = 0; i < children.length; i++) {
            children[i].setBackground(Display.getCurrent().getSystemColor(iBgColor));
            children[i].setForeground(Display.getCurrent().getSystemColor(iFgColor));
        }
    }

    protected Composite getSelectedComp(EventObject evt) {
        Composite selectedComp = null;

        Object evtSrc = evt.getSource();
        if (evtSrc instanceof Label) {
            selectedComp = ((Label) evtSrc).getParent();
        } else if (evtSrc instanceof Link) {
            selectedComp = (Composite) ((Link) evtSrc).getParent().getChildren()[0];
        } else {
            selectedComp = (Composite) evtSrc;
        }

        return selectedComp;
    }
}
