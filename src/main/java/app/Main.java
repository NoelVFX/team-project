package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        final AppBuilder appBuilder;
        try {
            appBuilder = new AppBuilder();
        }
        catch (IllegalStateException ex) {
            // The database could not be reached; tell the user instead of failing silently.
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }
        JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addLoggedInView()
                .addCourseProgressView()
                .addEnrolView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addLogoutUseCase()
                .addChangePasswordUseCase()
                .addViewCoursesUseCase()
                .addViewCourseProgressUseCase()
                .addEnrolCourseUseCase()
                .addCalculateGradeUseCase()
                .addSetTargetGradeUseCase()
                .addRankSubjectsUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
