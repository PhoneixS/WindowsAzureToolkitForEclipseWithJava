/**
 * Copyright (c) Microsoft Corporation
 * 
 * All rights reserved. 
 * 
 * MIT License
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR 
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH 
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gigaspaces.azure.runnable;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

import waeclipseplugin.Activator;
import com.gigaspaces.azure.propertypage.Messages;
import com.gigaspaces.azure.util.PreferenceUtil;
import com.microsoftopentechnologies.azurecommons.deploy.util.PublishData;
import com.microsoftopentechnologies.azurecommons.exception.RestAPIException;
import com.persistent.util.MessageUtil;

public class LoadAccountWithProgressWindow extends AccountActionRunnable implements Runnable {
	
	public LoadAccountWithProgressWindow(PublishData data, Shell shell) {
		super(data, shell);
	}

	@Override
	public void run() {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, true, this);
			dialog.close();
			PreferenceUtil.setLoaded(true);
		} catch (InvocationTargetException e) {
            // special check for Java 1.6 and bouncycastle not in classpath error
            if (com.gigaspaces.azure.util.Messages.importDlgMsgJavaVersion.equals(e.getMessage())) {
                MessageUtil.displayErrorDialog(shell, com.gigaspaces.azure.propertypage.Messages.loadingCred,
                        com.gigaspaces.azure.util.Messages.importDlgMsgJavaVersion);
            } else {
                MessageUtil.displayErrorDialog(shell, com.gigaspaces.azure.propertypage.Messages.loadingCred, Messages.loadingAccountError);
            }
			Activator.getDefault().log(Messages.error, e);
			PreferenceUtil.setLoaded(false);
		} catch (InterruptedException e) {
			// cancel pressed, so user might not be interested in loading data again
			PreferenceUtil.setLoaded(true);
		}
	}
	
	@Override
	public void doTask() {
		try {
			PreferenceUtil.load(this);
		} catch (RestAPIException e) {
			Activator.getDefault().log(Messages.error, e);
		}
	}

}
