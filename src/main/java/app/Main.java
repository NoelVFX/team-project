package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
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
                .addUpdateTopicConfidenceUseCase()
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
