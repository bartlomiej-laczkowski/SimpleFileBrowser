package org.sds.file.browser.ui;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;
import static org.sds.file.browser.core.Messages.MSG_APP_TITLE;
import static org.sds.file.browser.core.Messages.MSG_DIALOG_ABOUT_DESCRIPTION;
import static org.sds.file.browser.core.Messages.MSG_DIALOG_ABOUT_TITLE;
import static org.sds.file.browser.core.Messages.MSG_MENU_FILE_CLOSE;
import static org.sds.file.browser.core.Messages.MSG_MENU_FILE_TITLE;
import static org.sds.file.browser.core.Messages.MSG_MENU_HELP_ABOUT;
import static org.sds.file.browser.core.Messages.MSG_MENU_HELP_TITLE;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.sds.file.browser.core.ISession;
import org.sds.file.browser.core.Messages;
import org.sds.file.browser.core.Status;
import org.sds.file.browser.core.local.LocalSession;
import org.sds.file.browser.core.remote.ClientConfiguration;
import org.sds.file.browser.core.remote.ClientConfigurationManager;
import org.sds.file.browser.core.remote.ClientHandlerBuilder;
import org.sds.file.browser.core.remote.IClientHandler;
import org.sds.file.browser.core.remote.RemoteSession;
import org.sds.file.browser.ui.ImageRegistry.Images;
import org.sds.file.browser.ui.local.LocalSessionLabelProvider;
import org.sds.file.browser.ui.preview.PreviewExecutor;
import org.sds.file.browser.ui.remote.ClientConfigurationsDialog;
import org.sds.file.browser.ui.remote.RemoteSessionLabelProvider;

/**
 * SImple file browser main UI class.
 */
public final class FileBrowserUI {

	private Shell shell;
	private CTabFolder sessionViewersContainer;

	public static void main(String[] args) {
		Display display = Display.getDefault();
		FileBrowserUI app = new FileBrowserUI();
		Shell shell = app.open(display);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public Shell open(Display display) {
		ImageRegistry.init(display);
		shell = new Shell();
		createContents();
		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.addListener(SWT.Close, (Event event) -> {
			close();
		});
		shell.open();
		return shell;
	}

	private void close() {
		for (CTabItem tabItem : sessionViewersContainer.getItems()) {
			SessionViewer sessionViewer = (SessionViewer) tabItem.getControl();
			ISession session = (ISession) sessionViewer.getData();
			session.shutdown();
		}
		ImageRegistry.dispose();
		PreviewExecutor.INSTANCE.shutdown();
	}

	private void createContents() {
		shell.setText(Messages.get(MSG_APP_TITLE));
		shell.setImage(ImageRegistry.getImage(Images.IMG_FILE_BROWSER));
		GridLayout mainLayout = new GridLayout();
		shell.setLayout(mainLayout);
		// Create main menu bar
		createMenuBar(shell);
		// Create main tool bar
		createToolBar(shell);
		// Create sessions tab panel
		createSessionViewersContainer(shell);
	}

	private void createMenuBar(Shell parent) {
		Menu menuBar = new Menu(parent, SWT.BAR);
		parent.setMenuBar(menuBar);
		// File menu control
		final Menu fileMenu = new Menu(menuBar);
		final MenuItem fileMenuTitle = new MenuItem(menuBar, SWT.CASCADE);
		fileMenuTitle.setText(Messages.get(MSG_MENU_FILE_TITLE));
		fileMenuTitle.setMenu(fileMenu);
		// File menu items
		final MenuItem itemClose = new MenuItem(fileMenu, SWT.PUSH);
		itemClose.setText(Messages.get(MSG_MENU_FILE_CLOSE));
		itemClose.addSelectionListener(widgetSelectedAdapter(e -> shell.close()));
		// Help menu control
		final Menu helpMenu = new Menu(menuBar);
		final MenuItem helpMenuTitle = new MenuItem(menuBar, SWT.CASCADE);
		helpMenuTitle.setText(Messages.get(MSG_MENU_HELP_TITLE));
		helpMenuTitle.setMenu(helpMenu);
		// Menu items
		final MenuItem itemAbout = new MenuItem(helpMenu, SWT.PUSH);
		itemAbout.setText(Messages.get(MSG_MENU_HELP_ABOUT));
		itemAbout.addSelectionListener(widgetSelectedAdapter(e -> {
			MessageBox aboutDialog = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
			aboutDialog.setText(Messages.get(MSG_DIALOG_ABOUT_TITLE));
			aboutDialog.setMessage(Messages.get(MSG_DIALOG_ABOUT_DESCRIPTION, System.getProperty("os.name")));
			aboutDialog.open();
		}));
	}

	private void createToolBar(Composite parent) {
		ToolBar toolBar = new ToolBar(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		toolBar.setLayout(gridLayout);
		toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolBar.setLayout(gridLayout);
		createLocalSessionItem(toolBar);
		createRemoteSessionItem(toolBar);
	}

	private void createLocalSessionItem(ToolBar parent) {
		final ToolItem localSessionButton = new ToolItem(parent, SWT.PUSH);
		localSessionButton.setImage(ImageRegistry.getImage(Images.IMG_LOCAL_SESSION));
		localSessionButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				openLocalSession();
			}
		});
	}

	private void createRemoteSessionItem(ToolBar parent) {
		final ToolItem dropDown = new ToolItem(parent, SWT.DROP_DOWN);
		dropDown.setImage(ImageRegistry.getImage(Images.IMG_FTP_FOLDER));
		dropDown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.ARROW) {
					// Show menu
					Point location = dropDown.getParent().toDisplay(event.x, event.y);
					showRemoteSessionsMenu(dropDown, location);
				} else {
					ClientConfigurationsDialog remoteSessionDialog = new ClientConfigurationsDialog(shell);
					ClientConfiguration clientConfiguration = remoteSessionDialog.open();
					if (clientConfiguration == null) {
						return;
					}
					openRemoteSession(clientConfiguration);
				}
			}
		});
	}

	private void showRemoteSessionsMenu(ToolItem dropDown, Point location) {
		// Get saved remote configurations
		final Collection<ClientConfiguration> savedConfigurations = ClientConfigurationManager.INSTANCE
				.getAllConfigurations();
		// Create menu
		final Menu menu = new Menu(dropDown.getParent().getShell(), SWT.POP_UP);
		for (final ClientConfiguration configuration : savedConfigurations) {
			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(configuration.getName());
			item.setImage(ImageRegistry.getImage(Images.IMG_FTP_SESSION));
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					openRemoteSession(configuration);
				}
			});
		}
		// Separator
		new MenuItem(menu, SWT.SEPARATOR);
		// Add "New Session..." action
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("New Session...");
		// Fill menu with saved sessions
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				ClientConfiguration configuration = (new ClientConfigurationsDialog(shell)).open();
				if (configuration == null) {
					return;
				}
				openRemoteSession(configuration);
			}
		});
		// Show menu
		menu.setLocation(location);
		menu.setVisible(true);
	}

	private void createSessionViewersContainer(Composite parent) {
		sessionViewersContainer = new CTabFolder(parent, SWT.BORDER);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
		sessionViewersContainer.setLayout(gridLayout);
		sessionViewersContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		sessionViewersContainer.setMinimumCharacters(20);
		sessionViewersContainer.addCTabFolder2Listener(new CTabFolder2Adapter() {
			@Override
			public void close(CTabFolderEvent event) {
				CTabItem tabItem = (CTabItem) event.item;
				SessionViewer sessionViewer = (SessionViewer) tabItem.getControl();
				ISession session = (ISession) sessionViewer.getData();
				session.shutdown();
			}
		});
		// Open local file session as a default
		openLocalSession();
	}

	private void openLocalSession() {
		ISession session = new LocalSession();
		session.startup();

		final CTabItem sessionViewerTabItem = new CTabItem(sessionViewersContainer, SWT.CLOSE);

		sessionViewerTabItem.setText(LocalSession.LOCAL_SESSION_NAME);
		sessionViewerTabItem.setImage(ImageRegistry.getImage(Images.IMG_LOCAL_SESSION));

		SessionViewer fileSessionViewer = new SessionViewer(sessionViewersContainer, session,
				new LocalSessionLabelProvider());

		sessionViewerTabItem.setControl(fileSessionViewer);
		sessionViewersContainer.setSelection(sessionViewerTabItem);
	}

	private void openRemoteSession(final ClientConfiguration configuration) {
		ClientHandlerBuilder clientHandlerBuilder = new ClientHandlerBuilder();
		IClientHandler clientHandler = clientHandlerBuilder.build(configuration);
		ISession session = new RemoteSession(clientHandler);
		final Status[] sessionStatus = new Status[1];
		// Should be done in a better way with progress reporting etc.
		BusyIndicator.showWhile(shell.getDisplay(), () -> {
			Status connectionStatus = session.startup();
			sessionStatus[0] = connectionStatus;
			if (!connectionStatus.isOK()) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
				messageBox.setText("FTP Session Error");
				messageBox.setMessage(connectionStatus.getMessage());
				messageBox.open();
				return;
			}
		});
		if (!sessionStatus[0].isOK()) {
			return;
		}
		final CTabItem sessionViewerTabItem = new CTabItem(sessionViewersContainer, SWT.CLOSE);

		sessionViewerTabItem.setText(configuration.getName());
		sessionViewerTabItem.setImage(ImageRegistry.getImage(Images.IMG_FTP_SESSION));

		SessionViewer fileSessionViewer = new SessionViewer(sessionViewersContainer, session, new RemoteSessionLabelProvider());

		sessionViewerTabItem.setControl(fileSessionViewer);
		sessionViewersContainer.setSelection(sessionViewerTabItem);
	}

}
