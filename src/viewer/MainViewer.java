package viewer;

import fileReader.EFileDecoder;
import register.MapObjRegister;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;


/**
 * This is the main viewer of the program
 * @author Zhanghaoji
 * @date 2021/6/21 20:54
 * @author Zhengrundong
 * @date 2021/6/24 20:58
 */
public class MainViewer extends JFrame implements Runnable{

    private SimulationModel model;

    private boolean is_run = false;

    private JPanel panelBase; // all

    private JPanel panel1; // left up

    private JPanel panel2; // right up

    private JPanel panel3; // left down

    private JPanel panel4; // right down

    private JTextArea textArea1; // left down

    private JTextArea textArea2; // right up

    private JTextArea textArea3; // right down

    private JButton playButton; // play button

    private JButton pauseButton; // pause button

    private JButton endButton; // end button

    private JButton modifyButton; // modify info.txt

    public void run() {
        while(true) {
            try {
                while(is_run) {
                    textArea1.setText(model.getCarText());
                    textArea2.setText(model.getStationText());
                    textArea3.setText(model.getPlaceText());
                    Thread.sleep(1000);
                    model.simulate();
                }
            } catch(InterruptedException e) {
                System.err.println(e);
				    }
		    }
		}
    
    private void setButton(MapObjRegister register,MainViewer t1) {
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!is_run) {
                    is_run = true;
                    Thread th = new Thread(t1);
                    th.start();
                } else {
                    is_run = true;
                }
            }
        });
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!is_run) {
                    is_run = true;
                    Thread th = new Thread(t1);
                    th.start();
                } else {
                    is_run = false;
                }
            }
        });
        endButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model = new SimulationModel(register);
                textArea1.setText(model.getCarText());
                textArea2.setText(model.getStationText());
                textArea3.setText(model.getPlaceText());
                is_run = false;
            }
        });
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().open(new File("lib" + File.separator + "info.txt"));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }
    
    public static void main(String[] args) {
        EFileDecoder decoder = new EFileDecoder();
        decoder.setConfigFile("lib" + File.separator + "NariEFormatReader.properties");
        decoder.setEPath(System.getProperty("user.dir"));
        decoder.setEFile("lib" + File.separator + "info.txt");
        try{
            decoder.decodeEfile();
            MapObjRegister register = decoder.getObjRegister();
            MainViewer frame = new MainViewer(register);
            frame.setContentPane(frame.panelBase);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setSize(1400, 800 + 28);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Insets insets = frame.getInsets();// ??????????????????????????????
            System.out.println("???????????????"+insets.top);//???
            System.out.println("???????????????"+insets.bottom);//???
            System.out.println("???????????????"+insets.left);//???
            System.out.println("???????????????"+insets.right);//???

            Dimension di = frame.getContentPane().getSize();//?????????????????????
            System.out.println("??????????????????"+di.width);//???
            System.out.println("?????????????????????"+di.height);//???
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public MainViewer(MapObjRegister register) {
        super("????????????????????????");
        model = new SimulationModel(register);
        /**
         * panelBase look like
         * -------------------
         * panel1   |   panel2
         * panel3   |   panel4
         * -------------------
         */
        panelBase = new JPanel();
        panelBase.setLayout(new GridBagLayout());

        /**
         * panel 1 at (0, 0)
         */
        panel1 = new DisplayPanel(model.getPlaceMap());
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelBase.add(panel1, gbc);

        /**
         * panel 2 at (1, 0)
         */
        panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelBase.add(panel2, gbc);

        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(scrollPane1, gbc);

        textArea2 = new JTextArea();
        scrollPane1.setViewportView(textArea2);

        /**
         * panel 3 at (0, 1)
         */
        panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelBase.add(panel3, gbc);

        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        panel3.add(toolBar1, gbc);

        playButton = new JButton();
        playButton.setText("????????????");
        toolBar1.add(playButton);

        pauseButton = new JButton();
        pauseButton.setText("??????/??????");
        toolBar1.add(pauseButton);

        endButton = new JButton();
        endButton.setText("????????????");
        toolBar1.add(endButton);

        modifyButton = new JButton();
        modifyButton.setText("??????????????????");
        toolBar1.add(modifyButton);

        final JScrollPane scrollPane2 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(scrollPane2, gbc);

        textArea1 = new JTextArea();
        scrollPane2.setViewportView(textArea1);

        /**
         * panel 4 at (1, 1)
         */
        panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelBase.add(panel4, gbc);

        final JScrollPane scrollPane3 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(scrollPane3, gbc);

        textArea3 = new JTextArea();
        textArea3.setText("");
        scrollPane3.setViewportView(textArea3);
        setButton(register,this);
    }

}