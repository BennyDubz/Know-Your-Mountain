import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public class LDIntroScreen extends JPanel{
    private MainGUI parentGUI;
    LearnLevel currLevel;
    private boolean isIntro;
    private JLabel subtitleLabel;
    private JLabel descriptionLabel;

    // Used for putting the subtitles/descriptions in the right place
    int subtitleX;
    int subtitleY;
    int descriptionX;
    int descriptionY;

    public LDIntroScreen(MainGUI parentGUI, LearnDisplay learnDisplay) throws IOException {
        this.parentGUI = parentGUI;
        subtitleX = parentGUI.getScreenWidth() * 2/3;
        subtitleY = parentGUI.getScreenHeight() / 3;
        descriptionX = parentGUI.getScreenWidth() * 2/3;
        descriptionY = parentGUI.getScreenHeight() / 3 + 100;

        this.setLayout(null);
        //learnIntro.setBackground(Color.LIGHT_GRAY);

        JLabel title = new JLabel("Learn", SwingConstants.CENTER);
        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 30));
        title.setBounds(parentGUI.getScreenWidth() / 2 - 100, parentGUI.getScreenHeight() / 10,200, 100);
        title.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
        title.setOpaque(true);
        title.setBackground(new Color(220,220,220));

        this.add(title);

        descriptionButtons();
        isIntro = true;

        JButton continueButton = new JButton("Continue");
        continueButton.setBounds(descriptionX - 200, subtitleY + 300, 400, 50);
        continueButton.setFont(new Font(continueButton.getFont().getFontName(), Font.BOLD,25));
        continueButton.addActionListener(e -> {
            isIntro = false;
            CardLayout cl = (CardLayout) parentGUI.getLearnContainer().getLayout();
            cl.show(parentGUI.getLearnContainer(), "learnDisplay");
        });
        this.add(continueButton);

    }

    private void descriptionButtons() {

        // Creating the subtitle label
        subtitleLabel = new JLabel("Description", SwingConstants.CENTER);
        subtitleLabel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        subtitleLabel.setFont(new Font(subtitleLabel.getFont().getFontName(), Font.BOLD, 25));
        subtitleLabel.setBounds(subtitleX - 200, subtitleY, 400, 50);
        subtitleLabel.setOpaque(true);
        subtitleLabel.setBackground(new Color(220,220,220));
        // Uses compound border to add spacing between the title and its border
        Border titleBorder = subtitleLabel.getBorder();
        Border margin = new EmptyBorder(0,10,0,10);
        subtitleLabel.setBorder(new CompoundBorder(titleBorder, margin));
        this.add(subtitleLabel);

        // Creating the description label
        descriptionLabel = new JLabel("<html><p>The purpose of this Mode is to help you learn all of the points on the mountain in manageable chunks. To do this, you will go through 10 \"areas\" using the different settings on the left. Click on those buttons to see their descriptions. When you are ready to begin, click \"Continue\".</p></html>");
        descriptionLabel.setFont(new Font(descriptionLabel.getFont().getFontName(), Font.PLAIN, 18));
        descriptionLabel.setBounds(descriptionX - 200, descriptionY, 400, 200);
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
        this.add(descriptionLabel);

        // Creating look description button
        JButton lookDescriptionButton = new JButton("Look Mode");
        lookDescriptionButton.setFont(new Font(lookDescriptionButton.getFont().getFontName(), Font.PLAIN, 20));
        lookDescriptionButton.setBounds(parentGUI.getScreenWidth() / 8, parentGUI.getScreenHeight() / 3, 300, 50);

        lookDescriptionButton.addActionListener(e -> {
            // HTML formatting allows for auto-line breaks
            descriptionLabel.setText("<html>"+ "<p>Simply look at the points that you will learn, familiarizing yourself with them. By default, all of the names will be above their respective points, but you can click \"Toggle Names\" to make the names disappear and only show when you hover over or click a point. </p>"+"</html>");
            subtitleLabel.setText("Look Mode");
            this.revalidate();
            this.repaint();
        });
        this.add(lookDescriptionButton);

        JButton easyMCDescriptionButton = new JButton("Easy Multiple Choice");
        easyMCDescriptionButton.setFont(new Font(easyMCDescriptionButton.getFont().getFontName(), Font.PLAIN, 20));
        easyMCDescriptionButton.setBounds(parentGUI.getScreenWidth() / 8, parentGUI.getScreenHeight() / 3 + 75, 300, 50);

        easyMCDescriptionButton.addActionListener(e -> {
            descriptionLabel.setText("<html>" + "<p>You will be prompted with the name of a point and shown four on the map. Click on the correct points until every point has been prompted. </p>" + "</html>");
            subtitleLabel.setText("Easy Multiple Choice");
            this.revalidate();
            this.repaint();
        });
        this.add(easyMCDescriptionButton);

        JButton difficultMCDescriptionButton = new JButton("Difficult Multiple Choice");
        difficultMCDescriptionButton.setFont(new Font(difficultMCDescriptionButton.getFont().getFontName(), Font.PLAIN, 20));
        difficultMCDescriptionButton.setBounds(parentGUI.getScreenWidth() / 8, parentGUI.getScreenHeight() / 3 + 150, 300, 50);

        difficultMCDescriptionButton.addActionListener(e -> {
            descriptionLabel.setText("<html>" + "<p>You will be prompted with the name of a point and shown all of the area's points. Click on the correct points until every point has been prompted.</p>" + "</html>");
            subtitleLabel.setText("Difficult Multiple Choice");
            this.revalidate();
            this.repaint();
        });
        this.add(difficultMCDescriptionButton);

        JButton cumulativeDescriptionButton = new JButton("Cumulative");

        cumulativeDescriptionButton.setFont(new Font(cumulativeDescriptionButton.getFont().getFontName(), Font.PLAIN, 20));
        cumulativeDescriptionButton.setBounds(parentGUI.getScreenWidth() / 8, parentGUI.getScreenHeight() / 3 + 225, 300, 50);

        cumulativeDescriptionButton.addActionListener(e -> {
            descriptionLabel.setText("<html>" + "<p>In this Mode, you will practice with this area's points as well as all the previous areas' points. Good for solidifying everything you know so far. </p>" + "</html>");
            subtitleLabel.setText("Cumulative Mode");
            this.revalidate();
            this.repaint();
        });
        this.add(cumulativeDescriptionButton);
    }

    private void modeButtons() {
        JPanel buttons = new JPanel();
        buttons.setBounds(0, 0, parentGUI.getScreenWidth(), parentGUI.getScreenHeight() / 20);

    }

    public JPanel getLearnIntro() {
        return this;
    }

    public boolean isIntro() {
        return isIntro;
    }

    public void reset() {
        isIntro = true;
        subtitleLabel.setText("Description");
        descriptionLabel.setText("<html><p>The purpose of this Mode is to help you learn all of the points on the mountain in manageable chunks. To do this, you will go through 10 \"areas\" using the different settings on the left. Click on those buttons to see their descriptions. When you are ready to begin, click \"Continue\".</p></html>");
        CardLayout cl = (CardLayout) parentGUI.getLearnContainer().getLayout();
        cl.show(parentGUI.getLearnContainer(), "intro");
    }
}
