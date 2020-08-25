package com.github.derrop.proxy.plugins.gommecw.image;

import com.github.derrop.proxy.plugins.gommecw.running.ClanWarMember;
import com.github.derrop.proxy.plugins.gommecw.running.ClanWarTeam;
import com.github.derrop.proxy.plugins.gommecw.running.RunningClanWar;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

// TODO Names with 16 Chars are too long for the GUI
public class Frame extends JFrame {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    private static final String AVATAR_URL = "https://minotar.net/helm/%s/%d.png";
    private static final Image BAC_IMAGE;
    private static final Image BACKGROUND_IMAGE;
    private static final Image GOMME_ICON;
    private static final Image LABYMOD_IMAGE;

    static {
        try {
            BAC_IMAGE = scale(ImageIO.read(Objects.requireNonNull(Frame.class.getClassLoader().getResourceAsStream("img/bac.png"))), 81, 27);

            Image x = ImageIO.read(Objects.requireNonNull(Frame.class.getClassLoader().getResourceAsStream("img/labymod.png")));
            LABYMOD_IMAGE = scale(x, 40, 42);

            BACKGROUND_IMAGE = scale(ImageIO.read(Objects.requireNonNull(Frame.class.getClassLoader().getResourceAsStream("img/background.jpg"))), WIDTH, HEIGHT);

            GOMME_ICON = ImageIO.read(Objects.requireNonNull(Frame.class.getClassLoader().getResourceAsStream("img/gomme.png")));
        } catch (IOException exception) {
            throw new Error("Cannot load images", exception);
        }
    }

    private static final int SIDE_DISTANCE = 20;
    private static final int HEAD_SIZE = 75;
    private static final int FONT_SIZE = 30;
    private static final int DISTANCE = 20;
    private static final int PLAYER_HEIGHT = HEAD_SIZE + DISTANCE;

    //private static final int ELO_FONT_SIZE = 50;
    //private static final String ELO_HEADER = "ELO";

    private static final int CLAN_ICON_WIDTH = 150;
    private static final int CLAN_ICON_HEIGHT = 150;
    private static final int CLAN_FONT_SIZE = 35;

    private final Robot robot;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final int height;
    private final int width;

    private JLabel playerCameraLabelHead;
    private JLabel playerCameraLabel;
    private final Collection<Container> playerCamera = new ArrayList<>(); // TODO add multiple elements to this and don't use the full screen for the camera but display two cameras at once (maybe damager and damaged when receiving damage)?
    private final Collection<Container> generalInformation = new ArrayList<>();

    private UUID currentDisplayedPlayer;
    private long lastSwitch = -1;
    private String lastSwitchReason;

    private final RunningClanWar clanWar;

    public Frame(RunningClanWar clanWar) {
        this.clanWar = clanWar;
        try {
            this.robot = new Robot();
        } catch (AWTException exception) {
            throw new Error(exception);
        }

        this.setContentPane(new JLabel(new ImageIcon(BACKGROUND_IMAGE)));

        super.setBounds(100, 100, WIDTH, HEIGHT);
        super.setUndecorated(true);

        super.setIconImage(GOMME_ICON);

        this.height = super.getHeight();
        this.width = super.getWidth();

        super.setLayout(null);
        super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.init();

        super.setVisible(true);
    }

    public UUID getCurrentDisplayedPlayer() {
        return this.currentDisplayedPlayer;
    }

    public long getLastSwitch() {
        return this.lastSwitch;
    }

    public String getLastSwitchReason() {
        return this.lastSwitchReason;
    }

    public boolean isLastSwitchLongerThan(long distance) {
        return this.lastSwitch != -1 && System.currentTimeMillis() - this.lastSwitch < distance;
    }

    public boolean togglePlayerCamera(ClanWarTeam team, ClanWarMember member, String reason) {
        if (member != null && !this.isLastSwitchLongerThan(10000)) {
            return false;
        }

        boolean visible = team != null && member != null;
        if (visible) {
            try {
                this.playerCameraLabelHead.setIcon(new ImageIcon(new URL(String.format(AVATAR_URL, member.getUniqueId().toString().replace("-", ""), HEAD_SIZE)))); // TODO cache icon
            } catch (MalformedURLException exception) {
                exception.printStackTrace();
            }
            this.playerCameraLabel.setText(member.getName());
            this.playerCameraLabel.setForeground(team.getColor().getColor());
        }

        for (Container container : this.generalInformation) {
            container.setVisible(!visible);
        }
        for (Container container : this.playerCamera) {
            container.setVisible(visible);
        }

        this.currentDisplayedPlayer = member == null ? null : member.getUniqueId();
        this.lastSwitch = System.currentTimeMillis();
        this.lastSwitchReason = reason;

        super.invalidate();

        return true;
    }

    private void init() {
        this.generalInformation.clear();
        this.playerCamera.clear();
        this.initGeneralInformation(this.generalInformation);
        this.initPlayerCamera(this.playerCamera);

        for (Container container : this.generalInformation) {
            super.add(container);
        }
        for (Container container : this.playerCamera) {
            super.add(container);
        }
    }

    private void initPlayerCamera(Collection<Container> output) {
        this.playerCameraLabel = new JLabel();
        this.playerCameraLabel.setBounds(SIDE_DISTANCE + HEAD_SIZE + DISTANCE, SIDE_DISTANCE + (HEAD_SIZE / 2) - (FONT_SIZE / 2), 100, FONT_SIZE + 5);
        this.playerCameraLabel.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
        this.playerCameraLabel.setVisible(false);
        output.add(this.playerCameraLabel);

        this.playerCameraLabelHead = new JLabel();
        this.playerCameraLabelHead.setBounds(SIDE_DISTANCE, SIDE_DISTANCE, HEAD_SIZE, HEAD_SIZE);
        this.playerCameraLabelHead.setVisible(false);
        output.add(this.playerCameraLabelHead);

        Container video = this.addVideo(new Rectangle(2565, 662, 4095 - 2565, 1546 - 670), new Rectangle(0, 0, this.width, this.height));
        video.setVisible(false);
        output.add(video);
    }

    private void initGeneralInformation(Collection<Container> output) {
        Iterator<ClanWarTeam> teams = this.clanWar.getTeams().iterator();
        ClanWarTeam left = teams.next();
        ClanWarTeam right = teams.next();

        try {
            this.addCenteredIcon(output, GOMME_ICON);
            this.drawTeam(output, 12345, left.getClanName(), left, left.getColor().getColor(), SIDE_DISTANCE, 20, false);
            this.drawTeam(output, 12345, right.getClanName(), right, right.getColor().getColor(), this.width - HEAD_SIZE - SIDE_DISTANCE, this.height - (PLAYER_HEIGHT * right.getMembers().size()), true);
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }

        //this.drawELO(1234, Color.BLUE, (this.width / 2) - 50, (this.height / 4) - ELO_FONT_SIZE, true);
        //this.drawELO(1235, Color.RED, (this.width / 2) + 50, ((this.height / 4) * 3) - ELO_FONT_SIZE, false);
    }

    private Container addVideo(Rectangle videoPosition, Rectangle targetPosition) {
        JLabel label = new JLabel();
        label.setBounds(targetPosition);

        this.executorService.execute(() -> {
            while (!Thread.interrupted()) {
                if (!label.isVisible()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                    continue;
                }
                Image image = scale(robot.createScreenCapture(videoPosition), (int) targetPosition.getWidth(), (int) targetPosition.getHeight());
                label.setIcon(new ImageIcon(image));
            }
        });

        return label;
    }

    private Container addVideo(boolean left, Rectangle videoPosition) {
        /*int videoHeight = (this.height / 2) - 30;
        int videoWidth = (int) ((16D * (double) videoHeight) / 9D);

        int xDiff = (this.width / 2) - videoWidth - 30;
        int yDiff = (this.height / 2) - videoHeight - 10;*/

        int videoHeight = this.height / 2;
        int videoWidth = (int) ((16D * (double) videoHeight) / 9D);

        int xDiff = 10;
        int yDiff = 10;

        int x = left ? xDiff * 2 : (this.width / 2) - xDiff;
        int y = left ? (this.height / 2) - yDiff : yDiff * 2;
        //this.addVideo(videoPosition,new Rectangle(0, (this.height / 2) - 100, (this.width / 2) + 100, (this.height / 2) + 100));
        return this.addVideo(videoPosition, new Rectangle(x, y, videoWidth, videoHeight));
    }

    /*private void drawELO(int eloPoints, Color color, int x, int y, boolean left) {
        JLabel label = new JLabel("<html>" + ELO_HEADER + "<br>" + eloPoints + "</html>");
        label.setFont(new Font("ARIAL", Font.BOLD, ELO_FONT_SIZE));

        if (left) {
            FontMetrics metrics = label.getFontMetrics(label.getFont());
            int width = Math.max(metrics.stringWidth(ELO_HEADER), metrics.stringWidth(String.valueOf(eloPoints)));
            x -= width;
        }

        label.setBounds(x, y - (ELO_FONT_SIZE / 4), 500, (ELO_FONT_SIZE * 2) + 10);
        label.setForeground(color);

        output.add(label);
    }*/

    private void addCenteredIcon(Collection<Container> output, Image image) {
        JLabel icon = new JLabel(new ImageIcon(image));

        int width = icon.getIcon().getIconWidth();
        int height = icon.getIcon().getIconHeight();

        icon.setBounds((this.width / 2) - (width / 2), (this.height / 2) - (height / 2), width, height);
        output.add(icon);
    }

    private void drawTeam(Collection<Container> output, int eloPoints, String clanName, ClanWarTeam team, Color color, int x, int y, boolean left) throws MalformedURLException {
        final String displayName = "Elo: " + eloPoints + " / " + clanName;
        final int initialY = y;

        Font font = new Font("ARIAL", Font.PLAIN, FONT_SIZE);
        FontMetrics metrics = super.getFontMetrics(font);

        Font clanNameFont = new Font("ARIAL", Font.BOLD, CLAN_FONT_SIZE);
        final int clanNameWidth = super.getFontMetrics(clanNameFont).stringWidth(displayName);

        int longestText = team.getMembers().stream().map(ClanWarMember::getName).mapToInt(metrics::stringWidth).max().orElse(0);
        int highestX = 0;

        int factor = left ? -1 : 1;
        boolean anyBacUser = false;
        boolean anyLabyUser = false;
        int highestStatsWidth = 0;

        for (ClanWarMember member : team.getMembers()) {
            JLabel label = new JLabel(new ImageIcon(new URL(String.format(AVATAR_URL, member.getUniqueId().toString().replace("-", ""), HEAD_SIZE))));
            label.setBounds(x, y, HEAD_SIZE, HEAD_SIZE);
            output.add(label);

            JLabel text = new JLabel(member.getName());
            text.setForeground(color);
            text.setFont(font);

            int textWidth = metrics.stringWidth(member.getName());
            int height = text.getFont().getSize();

            int distance = (left ? textWidth : HEAD_SIZE) + SIDE_DISTANCE;

            int textX = x + (factor * distance);
            int textY = y + (HEAD_SIZE / 2) - (height / 2);
            text.setBounds(textX, textY, textWidth + 10, height);

            output.add(text);

            JLabel stats = new JLabel("Kills: 10 / Deaths: 10");
            int statsWidth = metrics.stringWidth(stats.getText());
            int statsX = x + (factor * (HEAD_SIZE + longestText + (left ? statsWidth : HEAD_SIZE) - 20));

            stats.setForeground(color);
            stats.setFont(font);

            stats.setBounds(statsX, textY, statsWidth + 10, height);
            output.add(stats);

            boolean hasBac = ThreadLocalRandom.current().nextInt(100) < 25; // TODO use the data out of the player
            boolean hasLaby = ThreadLocalRandom.current().nextInt(100) < 25 && !hasBac; // TODO use the data out of the player
            final int newX = statsX + (left ? 0 : statsWidth);

            if (hasBac) {
                JLabel bac = new JLabel(new ImageIcon(BAC_IMAGE));

                int bacX = statsX + (!left ? (statsWidth + DISTANCE) : -(BAC_IMAGE.getWidth(null) + DISTANCE));

                bac.setBounds(bacX, textY, BAC_IMAGE.getWidth(null), BAC_IMAGE.getHeight(null));

                output.add(bac);
            } else if (hasLaby) {
                JLabel laby = new JLabel(new ImageIcon(LABYMOD_IMAGE));

                int labyX = statsX + (!left ? (statsWidth + DISTANCE) : -(LABYMOD_IMAGE.getWidth(null) + DISTANCE));

                laby.setBounds(labyX, textY, LABYMOD_IMAGE.getWidth(null), LABYMOD_IMAGE.getHeight(null));

                output.add(laby);
            }

            anyBacUser = anyBacUser || hasBac;
            anyLabyUser = anyLabyUser || hasLaby;

            if (highestX == 0 || (!left ? highestX < newX : highestX > newX)) {
                highestX = newX;
            }
            if (statsWidth > highestStatsWidth) {
                highestStatsWidth = statsWidth;
            }

            y += PLAYER_HEIGHT;
        }

        JLabel clanNameLabel = new JLabel(displayName);
        clanNameLabel.setFont(clanNameFont);
        clanNameLabel.setForeground(color);
        clanNameLabel.setBounds(!left ? SIDE_DISTANCE : this.width - SIDE_DISTANCE - clanNameWidth, left ? initialY - 70 : y + 10, clanNameWidth + 10, font.getSize() + 10);
        // clanNameLabel.setBounds((this.width / 2) - (factor * ((this.gommeIconWidth / 2) + DISTANCE)) - (left ? 0 : clanNameWidth), (this.height / 2) - (factor * 75), clanNameWidth + 10, font.getSize() + 10); <--- Right/Left from the Center
        output.add(clanNameLabel);

        if (anyBacUser) {
            highestX += factor * (BAC_IMAGE.getWidth(null) + DISTANCE);
        } else if (anyLabyUser) {
            highestX += factor * (LABYMOD_IMAGE.getWidth(null) + DISTANCE);
        }

        if (left) {
            highestX -= CLAN_ICON_WIDTH;
        }

        try {
            ImageIcon icon = new ImageIcon(scale(ImageIO.read(this.getClanImageURL(clanName)), CLAN_ICON_WIDTH, CLAN_ICON_HEIGHT));
            int height = CLAN_ICON_HEIGHT;
            int width = CLAN_ICON_WIDTH;

            JLabel iconLabel = new JLabel(icon);

            //iconLabel.setBounds(left ? 10 : this.width - 10 - width, left ? this.height - 10 - height : 10, width, height);
            int iconY = this.height / 4;
            iconLabel.setBounds(highestX + (factor * DISTANCE), !left ? iconY - height : (iconY * 3), width, height);

            output.add(iconLabel);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        output.add(this.addVideo(left, new Rectangle(2565, 662, 4095 - 2565, 1546 - 670)));
    }

    @Override
    public void dispose() {
        this.executorService.shutdown();
        super.dispose();
    }

    private URL getClanImageURL(String clanName) throws IOException {
        Document document = Jsoup.connect("https://www.gommehd.net/clan-profile?name=" + URLEncoder.encode(clanName, "UTF-8")).get();
        Elements images = document.getElementsByClass("avatarContainer").get(0).getElementsByTag("img");
        String url = images.get(images.size() - 1).attr("src");
        // TODO if the clan has no image, none should be displayed instead of the gomme logo (default image)
        if (!url.startsWith("http")) {
            url = url.startsWith("/") ? "https://www.gommehd.net" + url : "https://www.gommehd.net/" + url;
        }
        return new URL(url);
    }

    private static Image scale(Image source, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(source, 0, 0, width, height, null);
        g2.dispose();

        return resized;
    }

}
