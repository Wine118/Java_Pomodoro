import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PomodoroWindow extends JFrame {
    private JLabel timerLabel;
    private Timer swingTimerWorking, swingTimerResting, swingTimerLongResting;
    private int remainingWorkingSeconds, remaingRestingSeconds, remainingLongRestingSeconds;
    private boolean isRunning,autoStart;
    private int workmin,sessions,focusSessions,breakmin,longbreakmin;
    private enum workingState {Focus, Rest, LongRest}
    private workingState currentMode;
    private JButton startPauseButton;
    private PomodoroSettings settings;
    private JLabel sessionLabel;
    private JRadioButton focusRadio, shortBreakRadio, longBreakRadio;
    private JPanel buttonPanel,mainPanel,topPanel, popupContentPanel,centerPanel,radioPanel,middlePanel;
    private ImageIcon settingsIcon;
    private Image scaledSettings;
    private JButton settingsButton;
    JPopupMenu settingsMenu;
    private String filepath;




    public PomodoroWindow() {
        LoadSetting();
        setTitle("Pomodoro Timer");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
        filepath = "D:\\Java\\JavaGreatProjects\\Pomodoro\\Pomodoro\\src\\school_bell.mp3";
        focusSessions = 1;
        addingMainPanel();

        addingTopPanel();

        addingCenterPanel();

        mainPanel.add(topPanel,BorderLayout.NORTH);
        mainPanel.add(centerPanel,BorderLayout.CENTER);

        //Set window icon
        ImageIcon icon = new ImageIcon("D:\\Java\\JavaGreatProjects\\Pomodoro\\Pomodoro\\src\\technique.png");
        setIconImage(icon.getImage());

        //Create a panel with custom background color

        setVisible(true);
        timerWorking();

    }







    private void addingTopPanel() {
        //top panel (for setting + pomodoro icon)
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.decode("#8f1402"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));

        addingSettingPanel();


        //Title label center
        JLabel titleLabel = new JLabel("Pomodoro for your focus", SwingConstants.CENTER);
        titleLabel.setForeground(Color.decode("#66cc99"));
        titleLabel.setFont(new Font("Serif",Font.BOLD | Font.ITALIC,40));

        topPanel.add(settingsButton, BorderLayout.EAST);
        topPanel.add(titleLabel,BorderLayout.CENTER);


        // Pomodoro Icon
        ImageIcon pomodoroIcon = new ImageIcon("D:\\Java\\JavaGreatProjects\\Pomodoro\\Pomodoro\\src\\pomodoromiddle.png");
        Image scaledPomodoro = pomodoroIcon.getImage().getScaledInstance(150,150,Image.SCALE_SMOOTH);
        JLabel pomodoroLabel = new JLabel(new ImageIcon(scaledPomodoro));

        JPanel pomodoroPanel = new JPanel();
        pomodoroPanel.setBackground(Color.decode("#8f1402"));
        pomodoroPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 30, 0)); // space below image
        pomodoroPanel.setLayout(new BorderLayout());
        pomodoroPanel.add(pomodoroLabel, BorderLayout.SOUTH);

        topPanel.add(pomodoroPanel, BorderLayout.SOUTH);

    }

    private void addingSettingPanel() {
        //Setting icon
        settingsIcon = new ImageIcon("D:\\Java\\JavaGreatProjects\\Pomodoro\\Pomodoro\\src\\setting.png");
        scaledSettings = settingsIcon.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH);
        settingsButton = new JButton(new ImageIcon(scaledSettings));

        //Make it look like just an icon
        settingsButton.setContentAreaFilled(false);
        settingsButton.setBorderPainted(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setOpaque(false);
        settingsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        //Create Settings Panel for Popup
        popupContentPanel = new JPanel();
        popupContentPanel.setLayout(new GridLayout(7, 5,10,5));
        popupContentPanel.setBackground(Color.WHITE); //Optional: to match menu look

        //Create components
        JTextField workField = new JTextField(String.valueOf(workmin));
        JTextField breakField = new JTextField(String.valueOf(breakmin));
        JTextField longBreakField = new JTextField(String.valueOf(longbreakmin));
        JTextField sessionsField = new JTextField(String.valueOf(sessions));
        JCheckBox autoStartCheck = new JCheckBox("Auto Start",autoStart);
        JButton restoreBtn = new JButton("Restore Default");
        JButton saveBtn = new JButton("Save");

        //Add labels + fields
        popupContentPanel.add(new JLabel("Focus (min):"));
        popupContentPanel.add(workField);
        popupContentPanel.add(new JLabel("Break (min):"));
        popupContentPanel.add(breakField);
        popupContentPanel.add(new JLabel("Long Break(min):"));
        popupContentPanel.add(longBreakField);
        popupContentPanel.add(new JLabel("Sessions:"));
        popupContentPanel.add(sessionsField);
        popupContentPanel.add(new JLabel("Autostart:"));
        popupContentPanel.add(autoStartCheck);
        popupContentPanel.add(restoreBtn);
        popupContentPanel.add(saveBtn);

        //Listening Restore Button
        restoreBtn.addActionListener(e -> {
            workField.setText("25");
            breakField.setText("5");
            longBreakField.setText("15");
            sessionsField.setText("4");
            autoStartCheck.setSelected(true);
        });

        //Save updated settings to JSON
        saveBtn.addActionListener(e -> {
            try{
                int work = Integer.parseInt(workField.getText());
                settings.setWorkMin(work);
                int breakfield = Integer.parseInt(breakField.getText());
                settings.setBreakMin(breakfield);
                int longBreakfield = Integer.parseInt(longBreakField.getText());
                settings.setLongBreakMin(longBreakfield);
                int sessionsfield = Integer.parseInt(sessionsField.getText());
                settings.setSessions(sessionsfield);
                boolean auto = autoStartCheck.isSelected();
                settings.setAutoStart(auto);
                LoadSetting();
                applySettingtoGUI();

            }catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,"Please enter valid numbers.");
            }
        });


        settingsMenu = new JPopupMenu();
        settingsMenu.setLayout(new BorderLayout());
        settingsMenu.add(popupContentPanel,BorderLayout.CENTER);

        //Show the popup menu when button is clicked
        settingsButton.addActionListener(e -> {
            settingsMenu.show(settingsButton,0,settingsButton.getHeight());
        });
    }

    private void addingCenterPanel() {
        //Center Panel
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.decode("#8f1402"));

        //Row 1: Mode selection with radio buttons
        focusRadio = new JRadioButton("Focus");
        shortBreakRadio = new JRadioButton("Short Break");
        longBreakRadio = new JRadioButton("Long Break");
        //Group the radio buttons so only one can be selected
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(focusRadio);
        modeGroup.add(shortBreakRadio);
        modeGroup.add(longBreakRadio);
        //Set default selection
        focusRadio.setSelected(true);
        currentMode = workingState.Focus;


        for (JRadioButton btn : new JRadioButton[]{focusRadio, shortBreakRadio, longBreakRadio}) {
            btn.setBackground(Color.decode("#8f1402"));
            btn.setForeground(Color.decode("#66cc99"));
            btn.setFont(new Font("Serif", Font.PLAIN, 22));
        }

        // Row 1: Radio buttons panel at the top
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        radioPanel.setBackground(Color.decode("#8f1402"));
        radioPanel.add(focusRadio);
        radioPanel.add(shortBreakRadio);
        radioPanel.add(longBreakRadio);

        centerPanel.add(radioPanel, BorderLayout.NORTH);

        focusRadio.addActionListener(e -> {
            if(currentMode != workingState.Focus){
                resetMode(currentMode);
                currentMode = workingState.Focus;
                updateTimerLabelMode(currentMode);
            }
        });

        shortBreakRadio.addActionListener(e -> {
            if(currentMode != workingState.Rest){
                resetMode(currentMode);
                currentMode = workingState.Rest;
                updateTimerLabelMode(currentMode);
            }
        });

        longBreakRadio.addActionListener(e -> {
            if(currentMode != workingState.LongRest){
                resetMode(currentMode);
                currentMode = workingState.LongRest;
                updateTimerLabelMode(currentMode);
            }
        });



        //Row 2: Session Display

        sessionLabel = new JLabel("#" +focusSessions,SwingConstants.CENTER);
        sessionLabel.setFont(new Font("Serif",Font.PLAIN,15));
        sessionLabel.setForeground(Color.decode("#cc6666"));
        sessionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);



        int mins = (workmin * 60) / 60;
        int secs = (workmin * 60) % 60;

        timerLabel = new JLabel(String.format("%02d:%02d", mins, secs));
        timerLabel.setFont(new Font("Arial",Font.BOLD,48));
        timerLabel.setForeground(Color.decode("#66cc99"));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Row 2 & 3: Session + Timer in the center
        middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        //middlePanel.setOpaque(false); // keep background transparent
        middlePanel.setBackground(Color.decode("#8f1402"));
        middlePanel.add(sessionLabel);
        middlePanel.add(Box.createVerticalStrut(5));
        middlePanel.add(timerLabel);
        centerPanel.add(middlePanel, BorderLayout.CENTER);



        // Row 4 : STart/ Pause Button
        startPauseButton = new JButton("Start");
        startPauseButton.setFont(new Font("Arial",Font.BOLD,24));



        startPauseButton.addActionListener(e -> {
            if (isRunning) {
                switch(currentMode){
                    case Focus -> {
                        pauseTimer(swingTimerWorking);
                        startPauseButton.setText("Start");
                    }
                    case Rest -> {
                        pauseTimer(swingTimerResting);
                        startPauseButton.setText("Start");
                    }
                    case LongRest -> {
                        pauseTimer(swingTimerLongResting);
                        startPauseButton.setText("Start");
                    }
                    default -> {
                        pauseTimer(swingTimerWorking);
                        startPauseButton.setText("Start");
                    }
                }

            } else {
                switch(currentMode){
                    case Focus -> {
                        startTimer(swingTimerWorking);
                        startPauseButton.setText("Pause");
                    }
                    case Rest -> {
                        startTimer(swingTimerResting);
                        startPauseButton.setText("Pause");
                    }
                    case LongRest -> {
                        startTimer(swingTimerLongResting);
                        startPauseButton.setText("Pause");
                    }
                    default -> {
                        startTimer(swingTimerWorking);
                        startPauseButton.setText("Pause");
                    }

                }

            }

        });

        buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.decode("#8f1402"));
        buttonPanel.add(startPauseButton);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


        middlePanel.add(Box.createVerticalStrut(100));
        middlePanel.add(buttonPanel);
    }

    private void applySettingtoGUI() {
        // Reset counters
        remainingWorkingSeconds = workmin * 60;
        remaingRestingSeconds = breakmin * 60;
        remainingLongRestingSeconds = longbreakmin * 60;
        focusSessions = 1;
        autoStart = settings.isAutoStart();

        // Update timer label and session label accordingly
        updateTimerLabel(remainingWorkingSeconds);
        sessionLabel.setText("#" + focusSessions);

        // Reset current mode and radio buttons
        currentMode = workingState.Focus;
        focusRadio.setSelected(true);

        // Stop any running timers
        swingTimerWorking.stop();
        swingTimerResting.stop();
        swingTimerLongResting.stop();

        isRunning = false;
        startPauseButton.setText("Start");
    }

    private void addingMainPanel() {
        //Main Background panel
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.decode("#8f1402"));
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);
    }

    private void startTimer(Timer timer) {
        isRunning = true;
        timer.start();
    }

    private void pauseTimer(Timer timer) {
        isRunning = false;
        timer.stop();
    }

    private void updateTimerLabel(int seconds) {

        int mins = seconds / 60;
        int secs = seconds % 60;
        timerLabel.setText(String.format("%02d:%02d", mins, secs));
    }

    private void updateTimerLabelMode(workingState currentMode) {
        int seconds = 0;
        switch (currentMode){
            case Focus -> seconds = workmin * 60;
            case Rest -> seconds = breakmin * 60;
            case LongRest -> seconds = longbreakmin * 60;
        }
        int mins = seconds / 60;
        int secs = seconds % 60;
        timerLabel.setText(String.format("%02d:%02d", mins, secs));
    }

    private void resetMode(workingState mode){
        switch(mode) {
            case Focus -> {
                swingTimerWorking.stop();
                remainingWorkingSeconds = workmin * 60;
                updateTimerLabel(remainingWorkingSeconds);
            }
            case Rest -> {
                swingTimerResting.stop();
                remaingRestingSeconds = breakmin * 60;
                updateTimerLabel(remaingRestingSeconds);
            }
            case LongRest -> {
                swingTimerLongResting.stop();
                remainingLongRestingSeconds = longbreakmin * 60;
                updateTimerLabel(remainingLongRestingSeconds);
            }
        }
        isRunning = false;
        startPauseButton.setText("Start");

    }

    public void LoadSetting(){
        settings = new PomodoroSettings().load();
        workmin = settings.getWorkMin();
        sessions = settings.getSessions();
        breakmin = settings.getBreakMin();
        longbreakmin = settings.getLongBreakMin();
        autoStart = settings.isAutoStart();
    }

    private void timerWorking() {
        // Initialize remainingSeconds from settings:
        // example for 25 minutes from settings
        remainingWorkingSeconds = workmin * 60;
        autoStart = settings.isAutoStart();
        // Setup the swingTimer with 1-second delay
        swingTimerWorking = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(sessions > 1){
                    if (remainingWorkingSeconds > 0) {
                        sessionLabel.setText("#"+focusSessions);
                        remainingWorkingSeconds--;
                        updateTimerLabel(remainingWorkingSeconds);
                        startPauseButton.setText("Pause");
                    }else {
                        alarmPlaying(filepath);
                        remainingWorkingSeconds = workmin * 60;
                        sessions--;
                        focusSessions++;
                        swingTimerWorking.stop();
                        isRunning = false;
                        startPauseButton.setText("Start");
                        // TODO: You can trigger next session or notify cycle end here
                        currentMode = workingState.Rest;
                        updateTimerLabelMode(currentMode);
                        shortBreakRadio.setSelected(true);
                        if(autoStart){
                            startTimer(swingTimerResting);
                        }

                    }
                }else {
                    if (remainingWorkingSeconds > 0) {
                        sessionLabel.setText("#"+focusSessions);
                        remainingWorkingSeconds--;
                        updateTimerLabel(remainingWorkingSeconds);
                        startPauseButton.setText("Pause");
                    }else {
                        alarmPlaying(filepath);
                        remainingWorkingSeconds = workmin * 60;
                        sessions--;
                        focusSessions++;
                        swingTimerWorking.stop();
                        isRunning = false;
                        startPauseButton.setText("Start");
                        // TODO: You can trigger next session or notify cycle end here
                        currentMode = workingState.LongRest;
                        longBreakRadio.setSelected(true);
                        updateTimerLabelMode(currentMode);
                        if(autoStart){
                            startTimer(swingTimerLongResting);
                        }


                    }


                }

            }
        });

        remaingRestingSeconds = breakmin * 60;
        swingTimerResting = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(sessions > 0){
                    if (remaingRestingSeconds> 0) {
                        remaingRestingSeconds--;
                        updateTimerLabel(remaingRestingSeconds);
                        startPauseButton.setText("Pause");
                    }else {
                        alarmPlaying(filepath);
                        remaingRestingSeconds = breakmin * 60;
                        swingTimerResting.stop();
                        isRunning = false;
                        startPauseButton.setText("Start");
                        // TODO: You can trigger next session or notify cycle end here
                        currentMode = workingState.Focus;
                        updateTimerLabelMode(currentMode);
                        focusRadio.setSelected(true);
                        if(autoStart){
                            startTimer(swingTimerWorking);
                        }

                    }
                }


            }
        });

        remainingLongRestingSeconds = longbreakmin * 60;
        swingTimerLongResting = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(sessions == 0){
                    if (remainingLongRestingSeconds> 0) {
                        remainingLongRestingSeconds--;
                        updateTimerLabel(remainingLongRestingSeconds);
                        startPauseButton.setText("Pause");
                    } else {
                        alarmPlaying(filepath);
                        LoadSetting();
                        swingTimerWorking.stop();
                        remainingLongRestingSeconds = longbreakmin * 60;

                        isRunning = false;
                        startPauseButton.setText("Start");
                        // TODO: You can trigger next session or notify cycle end here
                        currentMode = workingState.Focus;
                        updateTimerLabelMode(currentMode);
                        focusRadio.setSelected(true);

                        if(autoStart){
                            startTimer(swingTimerWorking);
                        }
                    }
                }

            }
        });
    }

    public void alarmPlaying(String filepath){
        MP3Player.play(filepath);

    }



    public static void main(String[] args) {
        new PomodoroWindow();
    }
}
