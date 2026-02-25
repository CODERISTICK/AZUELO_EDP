/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package azuelorhoderick;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;

public class SlidePanelController {

    private final JFrame frame;
    private final JLayeredPane layeredPane;

    private final int panelWidth = 420;
    private final int animStep = 28;
    private final int timerDelay = 10;

    // ✅ Adjust these to match your dashboard header/footer sizes
    private final int topGap = 0;
    private final int bottomGap = 55; // reserve space for footer so it won't overlap

    private final JPanel dimOverlay;
    private final NotificationSlidePanel slidePanel;

    private boolean isShown = false;
    private Timer animTimer;

    public SlidePanelController(JFrame frame, int currentUserId, String currentRole, Connection conn) {
        this.frame = frame;
        this.layeredPane = frame.getRootPane().getLayeredPane();

        NotificationDAO dao = new NotificationDAO(conn);

        dimOverlay = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 110));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dimOverlay.setOpaque(false);
        dimOverlay.setVisible(false);
        dimOverlay.setLayout(null);

        slidePanel = new NotificationSlidePanel(currentUserId, currentRole, dao);
        slidePanel.setVisible(false);

        dimOverlay.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                hide();
            }
        });
        slidePanel.getCloseButton().addActionListener(e -> hide());

        frame.getRootPane().registerKeyboardAction(
                e -> hide(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        layeredPane.add(dimOverlay, JLayeredPane.MODAL_LAYER);
        layeredPane.add(slidePanel, JLayeredPane.POPUP_LAYER);

        frame.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                layoutComponents(isShown);
            }
        });

        layoutComponents(false);
    }

    public void toggle() {
        if (isShown) hide();
        else show();
    }

    public void show() {
        if (isShown) return;
        isShown = true;

        layoutComponents(false);
        dimOverlay.setVisible(true);
        slidePanel.setVisible(true);
        slidePanel.refreshFromDB();

        animate(true);
    }

    public void hide() {
        if (!isShown) return;
        isShown = false;
        animate(false);
    }

    private void layoutComponents(boolean shown) {
    // Use contentPane size instead of frame size (prevents overshoot)
    int w = frame.getRootPane().getWidth();
    int h = frame.getRootPane().getHeight();

    dimOverlay.setBounds(0, 0, w, h);

    // Reserve footer space (tune this)
    int footerReserve = 70; // ✅ change if your footer is taller (try 70–90)

    int usableY = 0;
    int usableH = Math.max(250, h - footerReserve);

    int targetX = w - panelWidth;
    int startX = w;

    int x = shown ? targetX : startX;
    slidePanel.setBounds(x, usableY, panelWidth, usableH);

    slidePanel.revalidate();
    slidePanel.repaint();
}

    private void animate(boolean opening) {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();

        int w = frame.getWidth();
        int targetX = w - panelWidth;
        int startX = w;

        animTimer = new Timer(timerDelay, null);
        animTimer.addActionListener(e -> {
            Rectangle b = slidePanel.getBounds();
            int x = b.x;

            if (opening) {
                int nx = Math.max(targetX, x - animStep);
                slidePanel.setLocation(nx, b.y);
                if (nx <= targetX) {
                    slidePanel.setLocation(targetX, b.y);
                    animTimer.stop();
                }
            } else {
                int nx = Math.min(startX, x + animStep);
                slidePanel.setLocation(nx, b.y);
                if (nx >= startX) {
                    slidePanel.setLocation(startX, b.y);
                    slidePanel.setVisible(false);
                    dimOverlay.setVisible(false);
                    animTimer.stop();
                }
            }
        });
        animTimer.start();
    }
    
    public void setUnreadCount(int count) {
    slidePanel.setUnreadCount(count);
}
}
