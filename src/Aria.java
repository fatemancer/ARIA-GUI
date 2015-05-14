import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by hauu on 12.05.2015.
 */
public class Aria {
    private JTextField URL;
    private JCheckBox multithreadedCheckBox;

    public JTextField getThreads() {
        return threads;
    }

    private JTextField threads;
    private JRadioButton HTTPRadioButton;
    private JRadioButton FTPRadioButton;
    private JPanel Protocol;
    private JTextField login;
    private JPasswordField password;
    private JButton chooseDownloadFolderButton;
    private JPanel Window;
    private JButton downloadButton;
    private JRadioButton noAuthRadioButton;
    static String protocol;
    static int thread_n = 1;
    static String THREAD;
    static String URL_text;
    static String LOGIN;
    static String PASS;
    static String DESTINATION;

    public Aria() {

        // listener of download button. launches Aria using launch() method
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] arr = {"--max-connection-per-server=16", "--min-split-size=1M", LOGIN, PASS, URL_text, THREAD, DESTINATION};
                launch(arr);
            }
        });

        // choose destination directory listener
        chooseDownloadFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION)
                {
                    File file = fileChooser.getCurrentDirectory();
                    DESTINATION = ("--dir=" + file.getAbsolutePath());
                }
            }
        });

        // grouping protocol buttons into group
        ButtonGroup protocols = new ButtonGroup();
        protocols.add(HTTPRadioButton);
        protocols.add(FTPRadioButton);
        protocols.add(noAuthRadioButton);
        FTPRadioButton.setSelected(true);
        protocol = "FTP";
        HTTPRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                protocol = "HTTP";
            }
        });
        FTPRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                protocol = "FTP";
            }
        });

        // multithreading handlers (button + text field)
        multithreadedCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (multithreadedCheckBox.isSelected())
                {
                    if (threads.getText().isEmpty())
                    {
                        THREAD = null;
                    } else THREAD = "--split=" + thread_n;
                } else
                {
                    THREAD = null;
                    thread_n = 1;
                }


            }
        });

        threads.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

                try {
                    thread_n = Integer.parseInt(threads.getText());
                    if (thread_n > 16)
                    {
                        thread_n = 16;
                    }
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Вы пытаетесь превратить букву в цифру. Не надо так.", "Error!", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                thread_n = 1;
                THREAD = null;

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    thread_n = Integer.parseInt(threads.getText());
                    if (thread_n > 16)
                    {
                        thread_n = 16;
                    }
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Вы пытаетесь превратить букву в цифру. Не надо так.", "Error!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //  URL text handler

        URL.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    URL_text = "\"" + URL.getText() + "\"";
                } catch (RuntimeException e1) {
                    JOptionPane.showMessageDialog(null, "С этим урлом что-то не в порядке", "Error!", JOptionPane.ERROR_MESSAGE);

                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                URL_text = null;

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    URL_text = "\"" + URL.getText() + "\"";
                } catch (RuntimeException e1) {
                    JOptionPane.showMessageDialog(null, "С этим урлом что-то не в порядке", "Error!", JOptionPane.ERROR_MESSAGE);

                }

            }
        });

        login.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                LOGIN = "";
                if (protocol.equals("FTP"))
                {
                    LOGIN = "--ftp-user=" + getLogin();
                } else if (protocol.equals("HTTP"))
                {
                    LOGIN = "--http-user=" + getLogin();
                } else LOGIN="";
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                LOGIN = "";
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        password.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

                PASS = "";
                if (protocol.equals("FTP"))
                {
                    PASS = "--ftp-passwd=" + String.valueOf(getPass());
                } else if (protocol.equals("HTTP"))
                {
                    PASS = "--http-passwd=" + String.valueOf(getPass());
                } else PASS="";
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                PASS = "";
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

    }

    // login and password fields.
    private char[] getPass()
    {
        return password.getPassword();
    }

    public String getLogin()
    {
        return login.getText();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Aria");
        frame.setContentPane(new Aria().Window);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    // download_button getter.
    // TODO: check if really needed?
    public JButton getDownloadButton()
    {
        return downloadButton;
    }

    // generic launcher. for now only on WIN
    // TODO: read this: http://stackoverflow.com/questions/17148033/executing-os-dependent-commands
    public static void launch(String[] args)
    {
        launch_WIN(filter(args));
    }

    // filtering nulls before sending parameters to Aria
    public static String[] filter(String[] args)
    {
        ArrayList<String> output = new ArrayList<String>();

        for (String s : args)
        {
            if (s != null)
            {
                output.add(s);
            }
        }

        return output.toArray(new String[output.size()]);
    }


    // launcher. takes array of parameters.
    public static void launch_WIN(String[] args)
    {
        String str_args = "";
        for (int i = 0; i < args.length; i++)
    {
        str_args = str_args + " " + args[i];
    }
        try {
            Runtime.getRuntime().exec("cmd.exe /c start cmd /k aria2c.exe" + " " + str_args);
            System.out.println("Aria launched");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

// TODO: compile exe: http://www.excelsior-usa.com/articles/java-to-exe.html
// TODO: add remembering parameters (except URL)
