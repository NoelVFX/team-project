package view;

    import interface_adapter.course_progress.ViewCourseProgressController;
    import interface_adapter.enrol_course.EnrolCourseController;
    import interface_adapter.logged_in.ChangePasswordController;
    import interface_adapter.logged_in.LoggedInState;
    import interface_adapter.logged_in.LoggedInViewModel;
    import interface_adapter.logged_in.LoggedInViewModelListener;
    import interface_adapter.logout.LogoutController;

    import javax.swing.JButton;
    import javax.swing.JLabel;
    import javax.swing.JOptionPane;
    import javax.swing.JPanel;
    import javax.swing.JTextField;
    import javax.swing.event.DocumentEvent;
    import javax.swing.event.DocumentListener;
    import java.awt.Component;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;

    /**
     * The View for when the user is logged into the program.
     */
    public class LoggedInView extends JPanel implements ActionListener, LoggedInViewModelListener {

        private final LoggedInViewModel loggedInViewModel;

        private final JLabel passwordErrorField = new JLabel();

        private ChangePasswordController changePasswordController;
        private LogoutController logoutController;
        private ViewCourseProgressController viewCourseProgressController;
        private EnrolCourseController enrolCourseController;

        private final JLabel username;
        private final JButton logOut;
        private final JTextField passwordInputField = new JTextField(15);
        private final JButton changePassword;

        // Temporary way to open a course until the home page lists the enrolled courses.
        private final JTextField courseCodeInputField = new JTextField(15);
        private final JButton viewProgress;

        public LoggedInView(LoggedInViewModel loggedInViewModel) {
            this.loggedInViewModel = loggedInViewModel;
            this.loggedInViewModel.addListener(this);

            final JLabel title = new JLabel("Logged In Screen");
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            final LabelTextPanel passwordInfo = new LabelTextPanel(
                    new JLabel("Password"),
                    passwordInputField
            );

            final JLabel usernameInfo = new JLabel("Currently logged in: ");
            username = new JLabel();

            final JPanel buttons = new JPanel();

            logOut = new JButton("Log Out");
            buttons.add(logOut);

            changePassword = new JButton("Change Password");
            buttons.add(changePassword);

            final JButton enrol = new JButton("Enrol");
            buttons.add(enrol);
            enrol.addActionListener(evt -> enrolCourseController.switchToEnrolView());

            logOut.addActionListener(this);

            this.setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

            passwordInputField.getDocument().addDocumentListener(new DocumentListener() {

                private void documentListenerHelper() {
                    final LoggedInState currentState = loggedInViewModel.getState();
                    currentState.setPassword(passwordInputField.getText());
                    loggedInViewModel.setState(currentState);
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    documentListenerHelper();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    documentListenerHelper();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    documentListenerHelper();
                }
            });

            changePassword.addActionListener(evt -> {
                if (evt.getSource().equals(changePassword)) {
                    final LoggedInState currentState = loggedInViewModel.getState();

                    this.changePasswordController.execute(
                            currentState.getUsername(),
                            currentState.getPassword()
                    );
                }
            });

            final LabelTextPanel courseCodeInfo = new LabelTextPanel(
                    new JLabel("Course code"),
                    courseCodeInputField
            );

            viewProgress = new JButton("View Progress");
            courseCodeInfo.add(viewProgress);

            viewProgress.addActionListener(evt ->
                    viewCourseProgressController.execute(
                            loggedInViewModel.getState().getUsername(),
                            courseCodeInputField.getText()
                    )
            );

            this.add(title);
            this.add(usernameInfo);
            this.add(username);
            this.add(passwordInfo);
            this.add(passwordErrorField);
            this.add(buttons);
            this.add(courseCodeInfo);
        }

        /**
         * React to a button click.
         *
         * @param evt the ActionEvent to react to
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            logoutController.execute();
        }

        public void setChangePasswordController(
                ChangePasswordController changePasswordController) {
            this.changePasswordController = changePasswordController;
        }

        public void setLogoutController(LogoutController logoutController) {
            this.logoutController = logoutController;
        }

        public void setViewCourseProgressController(
                ViewCourseProgressController viewCourseProgressController) {
            this.viewCourseProgressController = viewCourseProgressController;
        }

        public void setEnrolCourseController(
                EnrolCourseController enrolCourseController) {
            this.enrolCourseController = enrolCourseController;
        }

        /**
         * The logged-in state was updated.
         */
        @Override
        public void stateChanged() {
            final LoggedInState state = loggedInViewModel.getState();
            username.setText(state.getUsername());
        }

        /**
         * A password change attempt occurred.
         */
        @Override
        public void passwordChanged() {
            final LoggedInState state = loggedInViewModel.getState();

            if (state.getPasswordError() == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "password updated for " + state.getUsername()
                );
                passwordInputField.setText("");
            }
            else {
                JOptionPane.showMessageDialog(this, state.getPasswordError());
            }
        }
    }