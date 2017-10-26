package org.sds.file.browser.ui;

import static org.sds.file.browser.core.Messages.MSG_TABLE_COLUMN_FILE_MODIFIED;
import static org.sds.file.browser.core.Messages.MSG_TABLE_COLUMN_FILE_NAME;
import static org.sds.file.browser.core.Messages.MSG_TABLE_COLUMN_FILE_SIZE;
import static org.sds.file.browser.core.Messages.MSG_TABLE_COLUMN_FILE_TYPE;
import static org.sds.file.browser.core.Messages.MSG_TABLE_FILE_SIZE_KB;
import static org.sds.file.browser.ui.ImageRegistry.Images.IMG_ARROW_UP;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.sds.file.browser.core.Comparators;
import org.sds.file.browser.core.IContainer;
import org.sds.file.browser.core.IFile;
import org.sds.file.browser.core.ISession;
import org.sds.file.browser.core.Messages;
import org.sds.file.browser.ui.preview.PreviewViewer;

/**
 * Browsing session viewer.
 */
class SessionViewer extends Composite {

	private Tree containerTree;
	private ISessionViewerLabelProvider labelProvider;
	private Map<IContainer, TreeItem> containerTreeItems;
	private Table contentsTable;
	private CLabel labelLocation;
	private ToolItem buttonLevelUp;
	private PreviewViewer previewViewer;
	private ISession session;
	private Label totalCountLabel;
	private Label selectedItemsLabel;

	public SessionViewer(Composite parent, ISession session, ISessionViewerLabelProvider sessionLabelProvider) {
		super(parent, SWT.NONE);
		this.session = session;
		this.labelProvider = sessionLabelProvider;
		containerTreeItems = new HashMap<>();
		createContents();
		setData(session);
		updateTree(session.getRoots());
	}

	private void createContents() {
		setLayoutData(new GridData(GridData.FILL_BOTH));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		setLayout(gridLayout);

		final SashForm sashForm = new SashForm(this, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		sashForm.setLayout(new FillLayout());

		final Composite structureComposite = new Composite(sashForm, SWT.NONE);
		GridLayout scGridLayout = new GridLayout();
		scGridLayout.numColumns = 1;
		scGridLayout.marginHeight = scGridLayout.marginWidth = 0;
		scGridLayout.horizontalSpacing = scGridLayout.verticalSpacing = 0;
		structureComposite.setLayout(scGridLayout);

		final Composite contentsComposite = new Composite(sashForm, SWT.NONE);
		GridLayout ccGridLayout = new GridLayout();
		ccGridLayout.numColumns = 1;
		ccGridLayout.marginHeight = ccGridLayout.marginWidth = 0;
		ccGridLayout.verticalSpacing = 2;
		ccGridLayout.horizontalSpacing = 0;
		contentsComposite.setLayout(ccGridLayout);

		sashForm.setWeights(new int[] { 25, 75 });

		final Composite statusComposite = new Composite(this, SWT.NONE);
		statusComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout stcGridLayout = new GridLayout();
		stcGridLayout.numColumns = 2;
		stcGridLayout.marginHeight = stcGridLayout.marginWidth = 0;
		statusComposite.setLayout(stcGridLayout);

		createStructureSection(structureComposite);
		createContentsSection(contentsComposite);
		createStatusSection(statusComposite);

	}

	private void createStatusSection(Composite parent) {
		totalCountLabel = new Label(parent, SWT.FLAT);
		GridData tcGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		tcGridData.widthHint = 100;
		totalCountLabel.setLayoutData(tcGridData);

		selectedItemsLabel = new Label(parent, SWT.FLAT);
		selectedItemsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}

	private void createStructureSection(Composite parent) {
		containerTree = new Tree(parent, SWT.BORDER);
		containerTree.setLayoutData(new GridData(GridData.FILL_BOTH));
		containerTree.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Point point = new Point(event.x, event.y);
				TreeItem treeItem = containerTree.getItem(point);
				if (treeItem != null) {
					treeSelectionChanged(treeItem);
				}
			}
		});
		containerTree.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				final TreeItem[] selection = containerTree.getSelection();
				if (selection != null && selection.length != 0) {
					TreeItem treeItem = selection[0];
					if (!treeItem.getExpanded()) {
						treeExpandItem(treeItem);
					} else {
						treeCollapseItem(treeItem);
					}
					event.doit = false;
				}
			}
		});
		containerTree.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					TreeItem[] item = containerTree.getSelection();
					if (item != null && item.length != 0) {
						treeSelectionChanged(item[0]);
					}
				}
			}
		});
		containerTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				//
			}
		});
		containerTree.addTreeListener(new TreeAdapter() {
			@Override
			public void treeExpanded(TreeEvent event) {
				treeExpandItem((TreeItem) event.item);
			}

			@Override
			public void treeCollapsed(TreeEvent event) {
				treeCollapseItem((TreeItem) event.item);
			}
		});
	}

	private void createContentsSection(Composite parent) {

		Composite locationComposite = new Composite(parent, SWT.NONE);
		locationComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout lcGridLayout = new GridLayout();
		lcGridLayout.numColumns = 2;
		lcGridLayout.marginHeight = lcGridLayout.marginWidth = 0;
		lcGridLayout.verticalSpacing = lcGridLayout.horizontalSpacing = 0;
		lcGridLayout.horizontalSpacing = 0;
		locationComposite.setLayout(lcGridLayout);

		ToolBar locationToolBar = new ToolBar(locationComposite, SWT.NONE);
		locationToolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		GridLayout lctbGridLayout = new GridLayout();
		lctbGridLayout.numColumns = 1;
		lctbGridLayout.marginHeight = lctbGridLayout.marginWidth = 0;
		lctbGridLayout.verticalSpacing = lctbGridLayout.horizontalSpacing = 0;
		locationToolBar.setLayout(lctbGridLayout);

		buttonLevelUp = new ToolItem(locationToolBar, SWT.PUSH);
		buttonLevelUp.setImage(ImageRegistry.getImage(IMG_ARROW_UP));
		buttonLevelUp.setEnabled(false);
		buttonLevelUp.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				IContainer container = (IContainer) contentsTable.getData();
				Path parent = container.getPath().getParent();
				if (parent != null) {
					updateTable(session.getContainer(parent));
				}
			}
		});

		labelLocation = new CLabel(locationComposite, SWT.BORDER);
		GridData tllGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		labelLocation.setLayoutData(tllGridData);

		SashForm sashForm = new SashForm(parent, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		sashForm.setLayout(new FillLayout());

		contentsTable = new Table(sashForm, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);

		TableColumn columnName = new TableColumn(contentsTable, SWT.NONE);
		columnName.setText(Messages.get(MSG_TABLE_COLUMN_FILE_NAME));
		columnName.setWidth(200);

		TableColumn columnType = new TableColumn(contentsTable, SWT.NONE);
		columnType.setText(Messages.get(MSG_TABLE_COLUMN_FILE_TYPE));
		columnType.setWidth(100);

		TableColumn columnSize = new TableColumn(contentsTable, SWT.NONE);
		columnSize.setText(Messages.get(MSG_TABLE_COLUMN_FILE_SIZE));
		columnSize.setWidth(80);

		TableColumn columnModified = new TableColumn(contentsTable, SWT.NONE);
		columnModified.setText(Messages.get(MSG_TABLE_COLUMN_FILE_MODIFIED));
		columnModified.setWidth(160);

		contentsTable.setHeaderVisible(true);
		contentsTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				tableSelectionChanged();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				TableItem item = (TableItem) event.item;
				IFile file = (IFile) item.getData();
				if (!file.isDirectory()) {
					Program.launch(file.getPath().toString());
				} else {
					updateTable(session.getContainer(file.getPath()));
				}
			}
		});
		// Add file preview section
		previewViewer = new PreviewViewer(sashForm, session);

		sashForm.setWeights(new int[] { 70, 30 });
	}

	private void treeExpandItem(final TreeItem treeItem) {
		treeItem.setExpanded(true);
		IContainer folder = (IContainer) treeItem.getData();
		treeItem.setImage(labelProvider.getImage(folder, true));
		TreeItem[] items = treeItem.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getData() != null)
				return;
			items[i].dispose();
		}
		BusyIndicator.showWhile(getDisplay(), () -> {
			List<IContainer> children = session.getChildren(folder);
			Collections.sort(children, new Comparator<IContainer>() {
				@Override
				public int compare(IContainer o1, IContainer o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			for (IContainer child : children) {
				TreeItem childItem = new TreeItem(treeItem, SWT.NONE);
				childItem.setData(child);
				childItem.setText(labelProvider.getText(child));
				childItem.setImage(labelProvider.getImage(child, false));
				containerTreeItems.put(child, childItem);
				if (session.hasChildren(child)) {
					new TreeItem(childItem, 0);
				}
			}
		});
	}

	private void treeCollapseItem(final TreeItem treeItem) {
		treeItem.setExpanded(false);
		IContainer element = (IContainer) treeItem.getData();
		treeItem.setImage(labelProvider.getImage(element, false));
	}

	private void treeSelectionChanged(final TreeItem item) {
		IContainer container = (IContainer) item.getData();
		updateTable(container);
		previewViewer.preview(null);
	}

	private void tableSelectionChanged() {
		updatePreviewViewer();
		updateSelectedItemsLabel();
	}

	private void updateTable(final IContainer container) {
		BusyIndicator.showWhile(getDisplay(), () -> {
			List<IFile> contents = session.getContents(container);
			labelLocation.setText(container.getPath().toString());
			buttonLevelUp.setEnabled(container.getPath().getParent() != null ? true : false);
			contentsTable.setData(container);
			Collections.sort(contents, new Comparators.FileNameKindComparator());
			getDisplay().syncExec(() -> {
				if (!contentsTable.isDisposed()) {
					contentsTable.removeAll();
				}
				// guard against the shell being closed before this runs
				if (Display.getDefault().isDisposed())
					return;
				for (IFile file : contents) {
					TableItem tableItem = new TableItem(contentsTable, 0);
					tableItem.setImage(labelProvider.getImage(file));
					tableItem.setData(file);
					for (int i = 0; i < contentsTable.getColumnCount(); i++) {
						tableItem.setText(i, labelProvider.getText(file, i));
					}
				}
			});
		});
		selectTreeElement(container);
		tableSelectionChanged();
		updateTotalCountLabel();
	}

	private void selectTreeElement(IContainer container) {
		TreeItem treeItem = containerTreeItems.get(container);
		if (treeItem == null) {
			return;
		}
		boolean isVisible = true;
		TreeItem parentTreeItem = treeItem.getParentItem();
		while (isVisible && parentTreeItem != null) {
			isVisible = parentTreeItem.getExpanded();
			parentTreeItem = parentTreeItem.getParentItem();
		}
		if (!isVisible) {
			return;
		}
		containerTree.select(treeItem);
	}

	private void updateTree(final List<IContainer> roots) {
		Collections.sort(roots, new Comparators.ContainerNameComparator());
		for (IContainer root : roots) {
			TreeItem childItem = new TreeItem(containerTree, SWT.NONE);
			childItem.setText(labelProvider.getText(root));
			childItem.setImage(labelProvider.getImage(root, false));
			childItem.setData(root);
			containerTreeItems.put(root, childItem);
			// Lazy loading ugly SWT hack
			new TreeItem(childItem, 0);
		}
	}

	private void updateTotalCountLabel() {
		totalCountLabel.setText(contentsTable.getItemCount() + " items");
	}

	private void updateSelectedItemsLabel() {
		TableItem[] selectedItems = contentsTable.getSelection();
		if (selectedItems == null || selectedItems.length == 0) {
			selectedItemsLabel.setText("");
			return;
		}
		long size = 0;
		for (TableItem tableItem : selectedItems) {
			IFile file = (IFile) tableItem.getData();
			if (file.isDirectory()) {
				continue;
			}
			size += file.getSize();
		}
		String message = selectedItems.length + (selectedItems.length == 1 ? " item selected " : " items selected ");
		if (size > 0)
			message += Messages.get(MSG_TABLE_FILE_SIZE_KB, new Long((size + 512) / 1024));
		selectedItemsLabel.setText(message);
	}

	private void updatePreviewViewer() {
		final TableItem[] selection = contentsTable.getSelection();
		if (selection != null && selection.length == 1) {
			IFile file = (IFile) selection[0].getData();
			previewViewer.preview(file);
		} else {
			previewViewer.preview(null);
		}
	}

}
