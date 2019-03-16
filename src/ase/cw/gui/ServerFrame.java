package ase.cw.gui;

import ase.cw.control.OrderController;
import ase.cw.log.Log;
import ase.cw.model.Order;
import ase.cw.interfaces.OrderConsumer;
import ase.cw.view.ServerFrameView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Bartosz on 03.03.2019.
 */

public class ServerFrame extends JFrame implements ActionListener, ServerFrameView {

    private JTextArea textArea = new JTextArea("");
    private JButton breakButton = new JButton("On-Break");
    private JButton restartButton = new JButton("Restart");
    private int serverId;
    private OrderController oc;


    // Frame constructor
    public ServerFrame(int id, JFrame parentFrame, OrderController controller) {
        this.serverId = id;
        this.textArea.setEditable(false);
        this.oc = controller;
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("coffee.png")));
        this.setTitle("Server "+serverId);
        this.setName("QueueFrame "+this.getTitle());
        this.setPreferredSize(new Dimension(300, 200));
        this.setLocation(parentFrame.getX()+parentFrame.getWidth()+(id*10), parentFrame.getY()+(id*10));
        this.setResizable(false);
        this.buildFrame();
        this.pack();
        this.setVisible(true);
        this.addWindowListener(new exitButtonPress());


        Log.getLogger().log("GUI: "+this.getName()+" opened.");
    }

    private void buildFrame() {
        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        textArea.setOpaque(false);
        top.add(textArea, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new GridLayout(1, 2, 5, 5));
        restartButton.setEnabled(false);
        breakButton.addActionListener(this);
        restartButton.addActionListener(this);
        bottom.add(breakButton);
        bottom.add(restartButton);
        top.add(bottom, BorderLayout.PAGE_END);

        this.add(top);
    }

    private void logButtonPress(JButton button) {
        Log.getLogger().log("GUI: "+this.getName()+" "+button.getText()+" button pressed.");
    }

    @Override
    public int getServerId() {
        return this.serverId;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == breakButton) {
            logButtonPress(breakButton);
            breakButton.setEnabled(false);
            restartButton.setEnabled(true);
            oc.pauseOrderProcess(this.serverId);
        }

        if (e.getSource() == restartButton) {
            logButtonPress(restartButton);
            restartButton.setEnabled(false);
            breakButton.setEnabled(true);
            oc.restartOrderProcess(this.serverId);
        }
    }

    @Override
    public void updateView(OrderConsumer server, Order order) {
        this.textArea.setText(
                "Status: "+(server.getStatus()
                        +"\nOrder: "+order.getCustomerId()+ (order.isPriorityOrder() ? " (priority)" : "")
                        +"\nItems: "+order.getOrderItems().size()
                        +"\nSubtotal: £"+order.getBill().getSubtotal()
                        +"\nTotal: £"+order.getBill().getTotal()
                ));
        }

    @Override
    public void closeFrame() {
        dispose();
    }

    @Override
    public void updateView(OrderConsumer server) {
        this.textArea.setText(
                "Status: "+(server.getStatus()
                ));
    }

    private class exitButtonPress extends WindowAdapter {
        public void windowClosing(WindowEvent evt) {
            Log.getLogger().log("GUI: ServerFrame "+serverId+" window close button pressed.");
            oc.removeServer(oc.getServerById(serverId));
        }
    }
}
