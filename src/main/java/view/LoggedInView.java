package view;

    import interface_adapter.course_progress.ViewCourseProgressController;
    import interface_adapter.enrol_course.EnrolCourseController;
    import interface_adapter.logged_in.ChangePasswordController;
    import interface_adapter.logged_in.LoggedInState;
    import interface_adapter.logged_in.LoggedInViewModel;
    import interface_adapter.logged_in.LoggedInViewModelListener;
    import interface_adapter.logout.LogoutController;
    import interface_adapter.view_courses.CourseRow;
    import interface_adapter.view_courses.ViewCoursesController;

    import javax.swing.DefaultListModel;
    import javax.swing.JButton;
    import javax.swing.JLabel;
    import javax.swing.JList;
    import javax.swing.JOptionPane;
    import javax.swing.JPanel;
    import javax.swing.JScrollPane;
    import javax.swing.ListSelectionModel;
    import javax.swing.JTextField;
    import javax.swing.SwingUtilities;
    import javax.swing.event.DocumentEvent;
    import javax.swing.event.DocumentListener;
    import java.awt.Component;
    import java.awt.Dimension;
    import java.awt.event.ComponentAdapter;
    import java.awt.event.ComponentEvent;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;
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
        private ViewCoursesController viewCoursesController;

        private final JLabel username;
        private final JButton logOut;
        private final JTextField passwordInputField = new JTextField(15);
        private final JButton changePassword;

        // The user's enrolled courses (use case 2). Selecting one opens its progress page (use case 3).
        private final DefaultListModel<CourseRow> courseListModel = new DefaultListModel<>();
        private final JList<CourseRow> courseList = new JList<>(courseListModel);
        private final JLabel courseListMessage = new JLabel();
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

            final JLabel coursesTitle = new JLabel("My Courses");
            coursesTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            courseListMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

            courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            courseList.setVisibleRowCount(6);
            final JScrollPane courseScrollPane = new JScrollPane(courseList);
            courseScrollPane.setPreferredSize(new Dimension(520, 140));

            viewProgress = new JButton("View Progress");
            viewProgress.setAlignmentX(Component.CENTER_ALIGNMENT);
            viewProgress.addActionListener(evt -> openSelectedCourse());

            // Double-clicking a course also opens its progress page.
            courseList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        openSelectedCourse();
                    }
                }
            });

            // Reload the course list every time the home page is shown: after logging in,
            // after enrolling and after coming back from a course's progress page.
            this.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent evt) {
                    refreshCourses();
                }
            });

            this.add(title);
            this.add(usernameInfo);
            this.add(username);
            this.add(passwordInfo);
            this.add(passwordErrorField);
            this.add(buttons);
            this.add(coursesTitle);
            this.add(courseListMessage);
            this.add(courseScrollPane);
            this.add(viewProgress);
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

        public void setViewCoursesController(ViewCoursesController viewCoursesController) {
            this.viewCoursesController = viewCoursesController;
        }

        private void refreshCourses() {
            if (viewCoursesController != null) {
                viewCoursesController.execute(loggedInViewModel.getState().getUsername());
            }
        }

        private void openSelectedCourse() {
            final CourseRow selected = courseList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please choose a course.");
            }
            else {
                viewCourseProgressController.execute(
                        loggedInViewModel.getState().getUsername(),
                        selected.getCourseCode()
                );
            }
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

            courseListModel.clear();
            for (CourseRow row : state.getCourses()) {
                courseListModel.addElement(row);
            }
            courseListMessage.setText(state.getCourseListMessage());
            viewProgress.setEnabled(!courseListModel.isEmpty());
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