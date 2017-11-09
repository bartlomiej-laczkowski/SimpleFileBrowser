package org.sds.file.browser.ui.remote;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.sds.file.browser.core.remote.ClientConfiguration;
import org.sds.file.browser.core.remote.ClientConfigurationManager;
import org.sds.file.browser.ui.ImageRegistry;
import org.sds.file.browser.ui.ImageRegistry.Images;

/**
 * Client configurations dialog.
 */
public class ClientConfigurationsDialog extends Dialog {

	private static class ConfigurationNameDialog extends Dialog {

		private String name;
		private Text configurationText;
		private Button okButton;
		private CLabel errorMessage;

		public ConfigurationNameDialog(Shell parent) {
			super(parent);
		}

		/**
		 * Makes the dialog visible.
		 * 
		 * @return
		 */
		public String open(final String nameProposal) {
			name = nameProposal;
			final Shell shell = new Shell(getParent(), SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL | SWT.CLOSE);
			shell.setText("FTP Session Name");
			shell.setImage(ImageRegistry.getImage(Images.IMG_FTP_SESSION));
			shell.setLayout(new GridLayout());
			shell.setLayoutData(new GridData(GridData.FILL_BOTH));
			final Label descriptionLabel = new Label(shell, SWT.NULL);
			descriptionLabel.setText("Please enter a session name:");
			configurationText = new Text(shell, SWT.SINGLE | SWT.BORDER);
			GridData dGridData = new GridData(GridData.FILL_HORIZONTAL);
			dGridData.widthHint = 300;
			configurationText.setLayoutData(dGridData);
			configurationText.setText(name);
			configurationText.selectAll();
			configurationText.addModifyListener((ModifyEvent event) -> {
				name = configurationText.getText();
				checkName();
			});
			errorMessage = new CLabel(shell, SWT.FLAT);
			errorMessage.setText("Configuration with that name already exists.");
			errorMessage.setImage(ImageRegistry.getImage(Images.IMG_ERROR));
			errorMessage.setVisible(false);
			okButton = new Button(shell, SWT.PUSH);
			okButton.setText("OK");
			GridData bOKGridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
			bOKGridData.widthHint = 50;
			okButton.setLayoutData(bOKGridData);
			okButton.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					shell.dispose();
				}
			});
			shell.setDefaultButton(okButton);
			shell.addListener(SWT.Close, new Listener() {
				public void handleEvent(Event event) {
					name = null;
					shell.dispose();
				}
			});
			shell.addListener(SWT.Traverse, new Listener() {
				public void handleEvent(Event event) {
					if (event.detail == SWT.TRAVERSE_ESCAPE)
						event.doit = false;
				}
			});
			checkName();
			shell.pack();
			shell.open();
			Rectangle shellBounds = getParent().getBounds();
			Point dialogSize = shell.getSize();
			shell.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
					shellBounds.y + (shellBounds.height - dialogSize.y) / 2);
			Display display = getParent().getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			return name;
		}
		
		private void checkName() {
			for (ClientConfiguration configuration : ClientConfigurationManager.INSTANCE.getAllConfigurations()) {
				if (configurationText.getText().equalsIgnoreCase(configuration.getName())) {
					okButton.setEnabled(false);
					errorMessage.setVisible(true);
					return;
				}
			}
			okButton.setEnabled(true);
			errorMessage.setVisible(false);
		}
	}

	private ClientConfiguration clientConfiguration;
	private Shell dialog;
	private Text portText;
	private Combo encryptionTypeCombo;
	private Text hostText;
	private Text userText;
	private Text passwdText;
	private int encryptionType = ClientConfiguration.ENCRYPTION_NONE;
	private Table configurationsTable;
	private String activeConfigurationId = null;
	private Button loginButton;
	private Button saveButton;
	private Button removeButton;

	private static final String FTP_DEFAULT_PORT = "21";
	private static final String FTP_IMPLICIT_DEFAULT_PORT = "990";

	private static final String NAME_PROPOSAL_PATTERN = "{0}@{1}";

	public ClientConfigurationsDialog(Shell parent) {
		super(parent);
	}

	public ClientConfiguration open() {
		Display display = getParent().getDisplay();
		Shell dialog = createContents();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return clientConfiguration;
	}

	private Shell createContents() {
		Shell parent = getParent();
		dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		dialog.setText("FTP Session");
		dialog.setImage(ImageRegistry.getImage(Images.IMG_FTP_FOLDER));
		dialog.setLayout(new GridLayout());
		// Create main composite
		Composite mainComposite = new Composite(dialog, SWT.NONE);
		GridData mcGridData = new GridData(GridData.FILL_BOTH);
		mcGridData.widthHint = 500;
		mcGridData.heightHint = 300;
		mainComposite.setLayoutData(mcGridData);
		GridLayout mcGridLayout = new GridLayout();
		mcGridLayout.numColumns = 3;
		mcGridLayout.makeColumnsEqualWidth = true;
		mcGridLayout.marginWidth = mcGridLayout.marginHeight = 0;
		mainComposite.setLayout(mcGridLayout);
		// Create list with available sessions
		createConfigurationsSection(mainComposite);
		// Create client configuration details section
		createDetailsSection(mainComposite);
		// Open dialog shell
		dialog.pack();
		dialog.open();
		// Align it to the center of parent shell
		Rectangle shellBounds = getParent().getBounds();
		Point dialogSize = dialog.getSize();
		dialog.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
				shellBounds.y + (shellBounds.height - dialogSize.y) / 2);
		return dialog;
	}

	private void createConfigurationsSection(Composite mainComposite) {
		Composite configurationsComposite = new Composite(mainComposite, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		configurationsComposite.setLayoutData(gd);
		configurationsComposite.setLayout(new FillLayout());

		configurationsTable = new Table(configurationsComposite,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		TableItem newConfigurationItem = new TableItem(configurationsTable, SWT.NONE);
		newConfigurationItem.setText("New Session");
		for (ClientConfiguration conf : ClientConfigurationManager.INSTANCE.getAllConfigurations()) {
			TableItem tableItem = new TableItem(configurationsTable, SWT.NONE);
			tableItem.setText(conf.getName());
			tableItem.setData(conf.getUniqueId());
			tableItem.setImage(ImageRegistry.getImage(Images.IMG_FTP_SESSION));
		}
		configurationsTable.setSelection(0);
		configurationsTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sessionSelectionChanged();
			}
		});

	}

	private void createDetailsSection(Composite mainComposite) {

		Composite detailsComposite = new Composite(mainComposite, SWT.NONE);
		final GridData tcGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tcGridData.horizontalSpan = 2;
		detailsComposite.setLayoutData(tcGridData);
		GridLayout tcGridLayout = new GridLayout();
		tcGridLayout.marginHeight = tcGridLayout.marginWidth = 0;
		detailsComposite.setLayout(tcGridLayout);

		Composite settingsComposite = new Composite(detailsComposite, SWT.NONE);
		final GridData scGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scGridData.horizontalSpan = 2;
		settingsComposite.setLayoutData(scGridData);
		final GridLayout scGridLayout = new GridLayout();
		scGridLayout.numColumns = 2;
		scGridLayout.marginHeight = scGridLayout.marginWidth = 0;
		settingsComposite.setLayout(scGridLayout);

		Composite buttonsComposite = new Composite(detailsComposite, SWT.RIGHT_TO_LEFT);
		final GridData bdGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		bdGridData.horizontalSpan = 2;
		buttonsComposite.setLayoutData(bdGridData);
		GridLayout bcGridLayout = new GridLayout();
		bcGridLayout.numColumns = 3;
		bcGridLayout.marginHeight = bcGridLayout.marginWidth = 0;
		buttonsComposite.setLayout(bcGridLayout);

		final Label protocolLabel = new Label(settingsComposite, SWT.NONE);
		protocolLabel.setText("Encryption:");
		protocolLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		encryptionTypeCombo = new Combo(settingsComposite, SWT.NONE);
		final GridData pcGridData = new GridData(GridData.BEGINNING);
		pcGridData.horizontalSpan = 2;
		encryptionTypeCombo.setLayoutData(pcGridData);
		encryptionTypeCombo.setItems("No encryption", "TLS/SSL Implicit encryption", "TLS/SSL Explicit encryption");
		encryptionTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				encryptionTypeChanged();
			}
		});
		encryptionTypeCombo.select(0);

		final Label hostLabel = new Label(settingsComposite, SWT.NONE);
		hostLabel.setText("Host name:");
		hostLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		final Label portLabel = new Label(settingsComposite, SWT.NONE);
		portLabel.setText("Port number:");
		portLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		hostText = new Text(settingsComposite, SWT.BORDER);
		hostText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		portText = new Text(settingsComposite, SWT.BORDER);
		portText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		encryptionTypeChanged();

		final Label userLabel = new Label(settingsComposite, SWT.NONE);
		userLabel.setText("User name:");
		userLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		final Label passwdLabel = new Label(settingsComposite, SWT.NONE);
		passwdLabel.setText("Password:");
		passwdLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		userText = new Text(settingsComposite, SWT.BORDER);
		userText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		passwdText = new Text(settingsComposite, SWT.BORDER | SWT.PASSWORD);
		passwdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		loginButton = new Button(buttonsComposite, SWT.PUSH);
		GridData lbGridData = new GridData(SWT.RIGHT, SWT.BOTTOM, false, true);
		lbGridData.widthHint = 120;
		loginButton.setLayoutData(lbGridData);
		loginButton.setText("Login");
		loginButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				runConfiguration();
			}
		});

		saveButton = new Button(buttonsComposite, SWT.PUSH);
		GridData sbGridData = new GridData(SWT.RIGHT, SWT.BOTTOM, false, true);
		sbGridData.widthHint = 80;
		saveButton.setLayoutData(sbGridData);
		saveButton.setText("Save");
		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				saveConfiguration();
			}
		});

		removeButton = new Button(buttonsComposite, SWT.PUSH);
		GridData rbGridData = new GridData(SWT.RIGHT, SWT.BOTTOM, false, true);
		rbGridData.widthHint = 80;
		removeButton.setLayoutData(rbGridData);
		removeButton.setText("Remove");
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				removeConfiguration();
			}
		});

		dialog.setDefaultButton(loginButton);
	}

	private void saveConfiguration() {
		String sessionName;
		ConfigurationNameDialog sessionNameDialog = new ConfigurationNameDialog(dialog);
		sessionName = sessionNameDialog
				.open(MessageFormat.format(NAME_PROPOSAL_PATTERN, userText.getText(), hostText.getText()));
		if (sessionName == null) {
			return;
		}
		ClientConfiguration sessionConfiguration = new ClientConfiguration(sessionName, encryptionType,
				hostText.getText(), Integer.valueOf(portText.getText()), userText.getText(), passwdText.getText());
		ClientConfigurationManager.INSTANCE.addConfiguration(sessionConfiguration);
		TableItem newTableItem = new TableItem(configurationsTable, SWT.NONE);
		newTableItem.setText(sessionConfiguration.getName());
		newTableItem.setData(sessionConfiguration.getUniqueId());
		newTableItem.setImage(ImageRegistry.getImage(Images.IMG_FTP_SESSION));
		configurationsTable.setSelection(newTableItem);
		sessionSelectionChanged();
		removeButton.setEnabled(true);
		dialog.setDefaultButton(loginButton);
	}

	private void removeConfiguration() {
		if (activeConfigurationId == null) {
			return;
		}
		ClientConfigurationManager.INSTANCE.removeConfiguration(activeConfigurationId);
		int itemToRemove = configurationsTable.getSelectionIndex();
		configurationsTable.remove(itemToRemove);
		configurationsTable.select(configurationsTable.getItemCount() - 1);
		sessionSelectionChanged();
	}

	private void runConfiguration() {
		if (activeConfigurationId == null) {
			String tmpSessionName = MessageFormat.format(NAME_PROPOSAL_PATTERN, userText.getText(),
					hostText.getText());
			clientConfiguration = new ClientConfiguration(tmpSessionName, encryptionType, hostText.getText(),
					Integer.valueOf(portText.getText()), userText.getText(), passwdText.getText());
		} else {
			clientConfiguration = ClientConfigurationManager.INSTANCE.findConfiguration(activeConfigurationId);
		}
		dialog.close();
	}

	private void updateDetailsSection() {
		if (activeConfigurationId == null) {
			encryptionTypeCombo.select(0);
			hostText.setText("");
			userText.setText("");
			passwdText.setText("");
			encryptionTypeChanged();
		} else {
			ClientConfiguration configuration = ClientConfigurationManager.INSTANCE
					.findConfiguration(activeConfigurationId);
			encryptionTypeCombo.select(configuration.getEncryptionType());
			hostText.setText(configuration.getHost());
			userText.setText(configuration.getUser());
			passwdText.setText(configuration.getPassword());
			portText.setText(String.valueOf(configuration.getPort()));
		}
	}

	private void encryptionTypeChanged() {
		switch (encryptionTypeCombo.getSelectionIndex()) {
		case 0: {
			encryptionType = ClientConfiguration.ENCRYPTION_NONE;
			portText.setText(FTP_DEFAULT_PORT);
			break;
		}
		case 1: {
			encryptionType = ClientConfiguration.ENCRYPTION_IMPLICIT_TLS_SSL;
			portText.setText(FTP_IMPLICIT_DEFAULT_PORT);
			break;
		}
		case 2: {
			encryptionType = ClientConfiguration.ENCRYPTION_EXPLICIT_TLS_SSL;
			portText.setText(FTP_DEFAULT_PORT);
			break;
		}
		default:
			break;
		}
	}

	private void sessionSelectionChanged() {
		activeConfigurationId = (String) ((TableItem) configurationsTable.getSelection()[0]).getData();
		if (activeConfigurationId != null) {
			removeButton.setEnabled(true);
			saveButton.setEnabled(false);
			encryptionTypeCombo.setEnabled(false);
			hostText.setEnabled(false);
			portText.setEnabled(false);
			userText.setEnabled(false);
			passwdText.setEnabled(false);
		} else {
			removeButton.setEnabled(false);
			saveButton.setEnabled(true);
			encryptionTypeCombo.setEnabled(true);
			hostText.setEnabled(true);
			portText.setEnabled(true);
			userText.setEnabled(true);
			passwdText.setEnabled(true);
		}
		updateDetailsSection();
	}

}
