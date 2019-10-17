package capm.installer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import java.awt.Font;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.codehaus.groovy.control.CompilationFailedException;
import org.ini4j.Wini;

import com.jcraft.jsch.JSchException;

import capm.installer.INTERFACE.Monitoring;
import capm.installer.MODEL.ShellCommandException;
import capm.installer.MODEL.ShellSSH;
import capm.installer.SHARE.SharedResources;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.ListSelectionModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class CAPMInstallerGUI {

	private boolean isConnecting = false;
	private Thread thread;
	private LinkedHashMap<String, String> resource_groovy = new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> resource_function = new LinkedHashMap<String, String>();
	private LinkedHashMap<String, JTable> resource_table = new LinkedHashMap<String, JTable>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CAPMInstallerGUI window = new CAPMInstallerGUI();
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					SwingUtilities.updateComponentTreeUI(window.frmInstaller);
					window.frmInstaller.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CAPMInstallerGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frmInstaller = new JFrame();
		frmInstaller.setTitle("Remote Installer");
		frmInstaller.setBounds(100, 100, 450, 523);
		frmInstaller.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmInstaller.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		frmInstaller.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(1, 1, 0, 0));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 434, 0 };
		gbl_panel_1.rowHeights = new int[] { 33, 0, 33, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JPanel panel_8 = new JPanel();
		GridBagConstraints gbc_panel_8 = new GridBagConstraints();
		gbc_panel_8.fill = GridBagConstraints.BOTH;
		gbc_panel_8.gridheight = 2;
		gbc_panel_8.insets = new Insets(0, 0, 5, 0);
		gbc_panel_8.gridx = 0;
		gbc_panel_8.gridy = 0;
		panel_1.add(panel_8, gbc_panel_8);
		panel_8.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblHost = new JLabel("Host");
		panel_8.add(lblHost);

		textFieldHost = new JTextField();
		textFieldHost.setText("192.168.3.82");
		panel_8.add(textFieldHost);
		textFieldHost.setColumns(10);

		JLabel lblId = new JLabel("Username");
		panel_8.add(lblId);

		textFieldID = new JTextField();
		textFieldID.setText("root");
		panel_8.add(textFieldID);
		textFieldID.setColumns(10);

		JLabel lblPassword = new JLabel("Password");
		panel_8.add(lblPassword);

		textFieldPw = new JTextField();
		textFieldPw.setText("root");
		panel_8.add(textFieldPw);
		textFieldPw.setColumns(10);

		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 2;
		panel_1.add(panel_3, gbc_panel_3);

		JLabel lblResources = new JLabel("Resource:");
		panel_3.add(lblResources);
		comboBoxRS = new JComboBox<String>();
		comboBoxRS.setModel(new DefaultComboBoxModel<String>(
				new String[] { "Vertica_DB", "Data_Aggregation", "Data_Collectors", "Performance_Center" }));
		panel_3.add(comboBoxRS);
		JLabel lblFunction = new JLabel("Function:");
		panel_3.add(lblFunction);
		comboBoxFunc = new JComboBox<String>();
		comboBoxFunc.setModel(new DefaultComboBoxModel<String>(new String[] { "install", "uninstall" }));
		panel_3.add(comboBoxFunc);
		comboBoxRS.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					textFieldFile.setText(resource_groovy.get(arg0.getItem()));
					String funcs[] = resource_function.get(arg0.getItem()).split(",");
					comboBoxFunc.removeAllItems();
					for (String funcName : funcs) {
						comboBoxFunc.addItem(funcName);
					}
					tabbedPane.setSelectedIndex(comboBoxRS.getSelectedIndex());
				}
			}
		});

		JLabel lblFile = new JLabel("File:");
		panel_3.add(lblFile);

		textFieldFile = new JTextField();
		textFieldFile.setText("dr.groovy");
		panel_3.add(textFieldFile);
		textFieldFile.setColumns(10);

		JPanel panel_5 = new JPanel();
		frmInstaller.getContentPane().add(panel_5, BorderLayout.CENTER);
		panel_5.setLayout(new BorderLayout(0, 0));

		JPanel panel_6 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_6.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		panel_5.add(panel_6, BorderLayout.NORTH);

		final JButton btnGo = new JButton("Go!");

		panel_6.add(btnGo);

		JPanel panel_7 = new JPanel();
		panel_5.add(panel_7, BorderLayout.CENTER);
		panel_7.setLayout(new BoxLayout(panel_7, BoxLayout.X_AXIS));

		JScrollPane scrollPane = new JScrollPane();
		panel_7.add(scrollPane);

		textPane = new JTextPane();
		textPane.setForeground(new Color(192, 192, 192));
		textPane.setFont(new Font("Consolas", Font.PLAIN, 16));
		textPane.setBackground(new Color(0, 0, 0));
		scrollPane.setViewportView(textPane);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		scrollPane.setColumnHeaderView(tabbedPane);

		JPanel panel_9 = new JPanel();
		tabbedPane.addTab("Vertica_DB", null, panel_9, null);
		panel_9.setLayout(new BorderLayout(0, 0));

		tableDR = new JTable();
		panel_9.add(tableDR, BorderLayout.NORTH);
		tableDR.setModel(new DefaultTableModel(
				new Object[][] { { "*.bin path", "/root/installDR.bin" },
						{ "extracted path", "/opt/CA/IMDataRepository_vertica9" }, { "database name", "polaris" },
						{ "database admin user", "vertica" }, { "database admin pwd", "polaris" }, },
				new String[] { "New column", "New column" }));
		tableDR.getColumnModel().getColumn(0).setPreferredWidth(126);
		tableDR.getColumnModel().getColumn(1).setPreferredWidth(237);
		tableDR.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableDR.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		tableDR.setBackground(SystemColor.menu);

		JPanel panel_11 = new JPanel();
		tabbedPane.addTab("Data_Aggregation", null, panel_11, null);
		panel_11.setLayout(new BorderLayout(0, 0));

		tableDA = new JTable();
		tableDA.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		tableDA.setBackground(SystemColor.control);
		tableDA.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableDA.setModel(new DefaultTableModel(
				new Object[][] { { "*.bin path", "/root/installDA.bin" }, { "extracted path", "/opt/IMDataAggregator" },
						{ "database name", "polaris" }, { "database user", "dbuser" }, { "database pwd", "dbpass" },
						{ "database admin user", "vertica" }, { "database admin pwd", "polaris" },
						{ "root username", "root" }, { "vertica ip", "127.0.0.1" }, },
				new String[] { "Attribute", "Value" }));
		tableDA.getColumnModel().getColumn(0).setPreferredWidth(126);
		tableDA.getColumnModel().getColumn(1).setPreferredWidth(237);
		panel_11.add(tableDA);

		JPanel panel_12 = new JPanel();
		tabbedPane.addTab("Data_Collectors", null, panel_12, null);
		panel_12.setLayout(new BorderLayout(0, 0));

		tableDC = new JTable();
		tableDC.setModel(new DefaultTableModel(
				new Object[][] { { "*.bin path", "/root/installDC.bin" }, { "extracted path", "/opt/IMDataCollector" },
						{ "root username", "root" }, { "data aggregator ip", "127.0.0.1" }, },
				new String[] { "New column", "New column" }));
		tableDC.getColumnModel().getColumn(0).setPreferredWidth(126);
		tableDC.getColumnModel().getColumn(1).setPreferredWidth(237);
		tableDC.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableDC.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		tableDC.setBackground(SystemColor.menu);
		panel_12.add(tableDC, BorderLayout.CENTER);

		JPanel panel_13 = new JPanel();
		tabbedPane.addTab("Performance_Center", null, panel_13, null);
		panel_13.setLayout(new BorderLayout(0, 0));

		tablePC = new JTable();
		tablePC.setModel(new DefaultTableModel(
				new Object[][] { { "*.bin path", "/root/CAPerfCenterSetup.bin" }, { "extracted path", "/opt/CA" }, },
				new String[] { "New column", "New column" }));
		tablePC.getColumnModel().getColumn(0).setPreferredWidth(126);
		tablePC.getColumnModel().getColumn(1).setPreferredWidth(237);
		tablePC.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablePC.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		tablePC.setBackground(SystemColor.menu);
		panel_13.add(tablePC, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		frmInstaller.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmLoadConfig = new JMenuItem("Load Config");
		mntmLoadConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser c = new JFileChooser();
				c.setFileFilter(INIFilterJC);

				int rVal = c.showOpenDialog(null);
				if (rVal != JFileChooser.APPROVE_OPTION)
					return;
				File f = new File(c.getSelectedFile().getAbsolutePath());
				try {
					Wini ini = new Wini(f);
					textFieldHost.setText(ini.get("SSH", "host"));
					textFieldID.setText(ini.get("SSH", "username"));
					textFieldPw.setText(ini.get("SSH", "password"));

					resource_groovy.clear();
					resource_function.clear();
					resource_table.clear();
					comboBoxRS.removeAllItems();
					tabbedPane.removeAll();

					String resources[] = ini.get("Initiation", "Resources").split(",");
					for (String resourceName : resources) {
						System.out.println(resourceName);
						resource_groovy.put(resourceName, ini.get("Groovy", resourceName));
						resource_function.put(resourceName, ini.get("Functions", resourceName));
						JTable table = newTableTab(resourceName,
								new LinkedHashMap<String, String>(ini.get(resourceName + "_Variables")));
						resource_table.put(resourceName, table);
						comboBoxRS.addItem(resourceName);
					}
					comboBoxRS.setSelectedIndex(0);

				} catch (IOException e) {
					log(Color.RED, e.getMessage() + "\n\r");
					e.printStackTrace();
				}

			}
		});
		mnFile.add(mntmLoadConfig);

		JMenuItem mntmSaveConfig = new JMenuItem("Save Config");
		mntmSaveConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser c = new JFileChooser();
				c.setFileFilter(INIFilterJC);

				int rVal = c.showSaveDialog(null);
				if (rVal != JFileChooser.APPROVE_OPTION)
					return;
				File f = new File(c.getSelectedFile().getAbsolutePath());
				if (f.exists())
					f.delete();
				try {
					f.createNewFile();
					Wini ini = new Wini(f);
					ini.put("SSH", "host", textFieldHost.getText());
					ini.put("SSH", "username", textFieldID.getText());
					ini.put("SSH", "password", textFieldPw.getText());

					String orderResources = "";
					for (Map.Entry<String, String> entry : resource_groovy.entrySet()) {
						if (!orderResources.isEmpty())
							orderResources = orderResources + ",";
						orderResources = orderResources + entry.getKey();
					}
					ini.put("Initiation", "Resources", orderResources);

					for (Map.Entry<String, String> entry : resource_groovy.entrySet()) {
						ini.put("Groovy", entry.getKey(), entry.getValue());
					}

					for (Map.Entry<String, String> entry : resource_function.entrySet()) {
						ini.put("Functions", entry.getKey(), entry.getValue());
					}
					for (Map.Entry<String, JTable> entry : resource_table.entrySet()) {
						String resourceName = entry.getKey();
						TableModel model = resource_table.get(resourceName).getModel();
						int n = model.getRowCount();
						for (int i = 0; i < n; i++) {
							String key = (String) model.getValueAt(i, 0);
							String value = (String) model.getValueAt(i, 1);
							ini.put(resourceName + "_Variables", key, value);
						}

					}
					ini.store();
				} catch (IOException e) {
					log(Color.RED, e.getMessage() + "\n\r");
					e.printStackTrace();
				}

			}
		});
		mnFile.add(mntmSaveConfig);

		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!isConnecting) {

					thread = new Thread(new Runnable() {
						public void run() {

							if (JOptionPane.showConfirmDialog(null, "Are you sure?", "",
									JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
								return;
							isConnecting = true;
							btnGo.setText("Connecting..");
							// shell connecting
							log(Color.GREEN, "Connecting..\n\r");
							final String host = textFieldHost.getText();
							final String id = textFieldID.getText();
							final String pw = textFieldPw.getText();
							ShellSSH shell = new ShellSSH(host, id, pw);
							try {
								shell.connect(15000);
								log(Color.GREEN, "Connected!\n\r");
							} catch (JSchException e) {
								log(Color.RED, e.getMessage() + "\n\r");
								return;
							} catch (IOException e) {
								log(Color.RED, e.getMessage() + "\n\r");
								return;
							}

							// installation
							btnGo.setText("Installing..");
							btnGo.setEnabled(false);

							String resourceName = (String) comboBoxRS.getSelectedItem();
							String funcName = (String) comboBoxFunc.getSelectedItem();
							String groovyName = textFieldFile.getText();
							Installer ins = new Installer(shell);
							TableModel model = resource_table.get(resourceName).getModel();

							ins.setMonitor(guiMonitor);
							SharedResources.putResource("installer", ins);
							SharedResources.setStep(SharedResources.Step.NEW);

							log(Color.GREEN, "running " + funcName + "..\n\r");

							boolean isLooping = true;
							while (isLooping) {
								int n = model.getRowCount();
								for (int i = 0; i < n; i++) {
									String key = (String) model.getValueAt(i, 0);
									String value = (String) model.getValueAt(i, 1);
									SharedResources.putResource(key, value);
								}
								try {
									ins.run(funcName, groovyName);
									isLooping = false;
								} catch (IOException e) {
									log(Color.RED, e.getMessage() + "\n\r");
								} catch (ShellCommandException e) {
									log(Color.RED, "an installing error occurred\n\r");
									JOptionPane p = new JOptionPane(
											"'Yes' to retry the current command, 'No' to ignore the current command or 'Cancel' to abort the process",
											JOptionPane.ERROR_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
									JDialog k = p.createDialog("An installing error occurred");
									k.setModal(false); // Makes the dialog not modal
									k.setVisible(true);
									Object selectedValue;
									while ((selectedValue = p.getValue()) == JOptionPane.UNINITIALIZED_VALUE) {
										try {
											Thread.sleep(77);
										} catch (InterruptedException e1) {
											e1.printStackTrace();
											return;
										}
									}

									int c = JOptionPane.CANCEL_OPTION;
									if (selectedValue != null) {
										c = (Integer) selectedValue;
									}
									switch (c) {
									case JOptionPane.YES_OPTION:// retry
										SharedResources.setStep(SharedResources.Step.RETRY);
										break;
									case JOptionPane.NO_OPTION:// ignore
										SharedResources.setStep(SharedResources.Step.IGNORE);
										break;
									case JOptionPane.CANCEL_OPTION:// abort
										SharedResources.setStep(SharedResources.Step.NEW);
										isLooping = false;
										break;
									}
								} catch (CompilationFailedException e) {
									log(Color.RED, e.getMessage() + "\n\r");
								} catch (URISyntaxException e) {
									log(Color.RED, e.getMessage() + "\n\r");
								}
							}
							try {
								shell.close();
							} catch (IOException e) {
								log(Color.RED, e.getMessage() + "\n\r");
							}
							btnGo.setEnabled(true);
							btnGo.setText("Go!");
							isConnecting = false;
						}
					});
					thread.start();
				} else {
					thread.interrupt();
					btnGo.setText("Go!");
					isConnecting = false;
				}
			}
		});

		resource_groovy.put("Vertica_DB", "dr.groovy");
		resource_groovy.put("Data_Aggregation", "da.groovy");
		resource_groovy.put("Data_Collectors", "dc.groovy");
		resource_groovy.put("Performance_Center", "pc.groovy");
		resource_function.put("Vertica_DB", "install,uninstall");
		resource_function.put("Data_Aggregation", "install,uninstall");
		resource_function.put("Data_Collectors", "install,uninstall");
		resource_function.put("Performance_Center", "install,uninstall");
		resource_table.put("Vertica_DB", tableDR);
		resource_table.put("Data_Aggregation", tableDA);
		resource_table.put("Data_Collectors", tableDC);
		resource_table.put("Performance_Center", tablePC);

	}

	public JTable newTableTab(String tabName, LinkedHashMap<String, String> map) {
		JPanel panel = new JPanel();
		tabbedPane.addTab(tabName, null, panel, null);
		panel.setLayout(new BorderLayout(0, 0));
		JTable table = new JTable();
		panel.add(table, BorderLayout.CENTER);
		DefaultTableModel model = new DefaultTableModel(new Object[][] {}, new String[] { "Key", "Value" });
		table.setModel(model);
		table.getColumnModel().getColumn(0).setPreferredWidth(126);
		table.getColumnModel().getColumn(1).setPreferredWidth(237);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		table.setBackground(SystemColor.menu);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			model.addRow(new String[] { entry.getKey(), entry.getValue() });
		}
		return table;
	}

	public void log(Color c, String s) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		int len = textPane.getDocument().getLength();
		textPane.setCaretPosition(len);
		textPane.setCharacterAttributes(aset, false);
		textPane.replaceSelection(s);
	}

	public Monitoring guiMonitor = new Monitoring() {

		public void println(String line) {
			log(Color.LIGHT_GRAY, line + "\n\r");
		}

		public void error(String message) {
			log(Color.RED, message + "\n\r");
		}

		public void append(String str) {
			log(Color.LIGHT_GRAY, str);
		}
	};
	private JTable tableDA;
	private JTable tableDR;
	private JTable tableDC;
	private JTable tablePC;
	private JFrame frmInstaller;
	private JTextField textFieldHost;
	private JTextField textFieldID;
	private JTextField textFieldPw;
	private JTextField textFieldFile;
	private JTextPane textPane;
	private JTabbedPane tabbedPane;
	private JComboBox<String> comboBoxRS;
	private JComboBox<String> comboBoxFunc;

	private FileFilter INIFilterJC = new FileFilter() {

		@Override
		public String getDescription() {
			return "INI File (*.ini)";
		}

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return false;
			} else {
				String filename = f.getName().toLowerCase();
				return filename.endsWith(".ini");
			}
		}
	};
}
