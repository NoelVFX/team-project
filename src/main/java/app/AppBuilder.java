package app;

import com.mongodb.client.MongoDatabase;
import data_access.MongoConfig;
import data_access.MongoCourseDataAccessObject;
import data_access.MongoUserDataAccessObject;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.course_progress.CourseProgressViewModel;
import interface_adapter.course_progress.ViewCourseProgressController;
import interface_adapter.course_progress.ViewCourseProgressPresenter;
import entity.CourseFactory;
import entity.StandardConfidenceScoring;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.calculate_grade.CalculateGradeController;
import interface_adapter.calculate_grade.CalculateGradePresenter;
import interface_adapter.calculate_grade.GradeCalculatorViewModel;
import interface_adapter.rank_subjects.RankSubjectsController;
import interface_adapter.rank_subjects.RankSubjectsPresenter;
import interface_adapter.rank_subjects.SubjectRankingViewModel;
import interface_adapter.set_target_grade.SetTargetGradeController;
import interface_adapter.set_target_grade.SetTargetGradePresenter;
import interface_adapter.update_topic_confidence.UpdateTopicConfidenceController;
import interface_adapter.update_topic_confidence.UpdateTopicConfidencePresenter;
import interface_adapter.enrol_course.EnrolCourseController;
import interface_adapter.enrol_course.EnrolCoursePresenter;
import interface_adapter.enrol_course.EnrolViewModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordPresenter;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.view_courses.ViewCoursesController;
import interface_adapter.view_courses.ViewCoursesPresenter;
import use_case.calculate_grade.CalculateGradeInputBoundary;
import use_case.calculate_grade.CalculateGradeInteractor;
import use_case.calculate_grade.CalculateGradeOutputBoundary;
import use_case.rank_subjects.RankSubjectsInputBoundary;
import use_case.rank_subjects.RankSubjectsInteractor;
import use_case.rank_subjects.RankSubjectsOutputBoundary;
import use_case.set_target_grade.SetTargetGradeInputBoundary;
import use_case.set_target_grade.SetTargetGradeInteractor;
import use_case.set_target_grade.SetTargetGradeOutputBoundary;
import use_case.update_topic_confidence.UpdateTopicConfidenceInputBoundary;
import use_case.update_topic_confidence.UpdateTopicConfidenceInteractor;
import use_case.update_topic_confidence.UpdateTopicConfidenceOutputBoundary;
import use_case.enrol_course.EnrolCourseInputBoundary;
import use_case.enrol_course.EnrolCourseInteractor;
import use_case.enrol_course.EnrolCourseOutputBoundary;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.view_course_progress.ViewCourseProgressInputBoundary;
import use_case.view_course_progress.ViewCourseProgressInteractor;
import use_case.view_course_progress.ViewCourseProgressOutputBoundary;
import use_case.view_courses.ViewCoursesInputBoundary;
import use_case.view_courses.ViewCoursesInteractor;
import use_case.view_courses.ViewCoursesOutputBoundary;
import view.CourseProgressView;
import view.EnrolView;
import view.GradeCalculatorDialog;
import view.LoggedInView;
import view.LoginView;
import view.SignupView;
import view.SubjectRankingDialog;
import view.TargetGradePanel;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // Users and courses are stored in MongoDB. The connection is read from the MONGODB_URI and
    // MONGODB_DATABASE environment variables (see MongoConfig and the README).
    // Throws IllegalStateException if the database cannot be reached.
    final MongoDatabase database = MongoConfig.connectFromEnvironment();

    final MongoUserDataAccessObject userDataAccessObject =
            new MongoUserDataAccessObject(database, userFactory);

    final CourseFactory courseFactory = new CourseFactory();
    final MongoCourseDataAccessObject courseDataAccessObject = new MongoCourseDataAccessObject(database);

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private LoggedInView loggedInView;
    private LoginView loginView;
    private CourseProgressView courseProgressView;
    private EnrolViewModel enrolViewModel;
    private EnrolView enrolView;

    // The main window. It is created early so that pop-ups can use it as their owner.
    private final JFrame application = new JFrame("University Learning Progress Tracker");
    // One view model shared by the progress page and the use cases that update it (3, 5, 6).
    private final CourseProgressViewModel courseProgressViewModel = new CourseProgressViewModel();
    private GradeCalculatorDialog gradeCalculatorDialog;
    private TargetGradePanel targetGradePanel;
    private SubjectRankingDialog subjectRankingDialog;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel);
        cardPanel.add(signupView, SignupViewModel.VIEW_NAME);
        return this;
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel);
        cardPanel.add(loginView, LoginViewModel.VIEW_NAME);
        return this;
    }

    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel);
        cardPanel.add(loggedInView, LoggedInViewModel.VIEW_NAME);
        return this;
    }

    public AppBuilder addCourseProgressView() {
        courseProgressView = new CourseProgressView(courseProgressViewModel);
        cardPanel.add(courseProgressView, CourseProgressViewModel.VIEW_NAME);
        return this;
    }

    public AppBuilder addEnrolView() {
        enrolViewModel = new EnrolViewModel();
        enrolView = new EnrolView(enrolViewModel);
        cardPanel.add(enrolView, EnrolViewModel.VIEW_NAME);
        return this;
    }

    /**
     * Adds the Enrol Course Use Case to the application.
     * Call after addLoggedInView and addEnrolView.
     * @return this builder
     */
    public AppBuilder addEnrolCourseUseCase() {
        final EnrolCourseOutputBoundary enrolCourseOutputBoundary =
                new EnrolCoursePresenter(viewManagerModel, enrolViewModel);
        final EnrolCourseInputBoundary enrolCourseInteractor = new EnrolCourseInteractor(
                courseDataAccessObject, enrolCourseOutputBoundary, courseFactory);

        final EnrolCourseController enrolCourseController =
                new EnrolCourseController(enrolCourseInteractor, loggedInViewModel);
        enrolView.setEnrolCourseController(enrolCourseController);
        loggedInView.setEnrolCourseController(enrolCourseController);
        return this;
    }

    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(viewManagerModel,
                signupViewModel, loginViewModel);
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject, signupOutputBoundary, userFactory);

        SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(loggedInViewModel);

        final ChangePasswordInputBoundary changePasswordInteractor =
                new ChangePasswordInteractor(userDataAccessObject, changePasswordOutputBoundary, userFactory);

        ChangePasswordController changePasswordController = new ChangePasswordController(changePasswordInteractor);
        loggedInView.setChangePasswordController(changePasswordController);
        return this;
    }

    /**
     * Adds the Logout Use Case to the application.
     * @return this builder
     */
    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary logoutOutputBoundary = new LogoutPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);

        final LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);

        final LogoutController logoutController = new LogoutController(logoutInteractor);
        loggedInView.setLogoutController(logoutController);
        return this;
    }

    /**
     * Adds the View Enrolled Courses Use Case (use case 2) to the application.
     * The home page reloads its course list each time it is shown.
     * Call after addLoggedInView.
     * @return this builder
     */
    public AppBuilder addViewCoursesUseCase() {
        final ViewCoursesOutputBoundary viewCoursesOutputBoundary =
                new ViewCoursesPresenter(loggedInViewModel);
        final ViewCoursesInputBoundary viewCoursesInteractor =
                new ViewCoursesInteractor(courseDataAccessObject, viewCoursesOutputBoundary);

        final ViewCoursesController viewCoursesController =
                new ViewCoursesController(viewCoursesInteractor);
        loggedInView.setViewCoursesController(viewCoursesController);
        return this;
    }

    /**
     * Adds the View Course Progress Use Case to the application.
     * @return this builder
     */
    public AppBuilder addViewCourseProgressUseCase() {
        final ViewCourseProgressOutputBoundary viewCourseProgressOutputBoundary =
                new ViewCourseProgressPresenter(viewManagerModel, courseProgressViewModel,
                        LoggedInViewModel.VIEW_NAME);

        final ViewCourseProgressInputBoundary viewCourseProgressInteractor =
                new ViewCourseProgressInteractor(courseDataAccessObject, viewCourseProgressOutputBoundary);

        final ViewCourseProgressController viewCourseProgressController =
                new ViewCourseProgressController(viewCourseProgressInteractor);
        loggedInView.setViewCourseProgressController(viewCourseProgressController);
        courseProgressView.setViewCourseProgressController(viewCourseProgressController);
        return this;
    }

    /**
     * Adds the Update Topic Confidence Use Case to the application.
     * @return this builder
     */
    public AppBuilder addUpdateTopicConfidenceUseCase() {
        final UpdateTopicConfidenceOutputBoundary outputBoundary =
                new UpdateTopicConfidencePresenter(courseProgressViewModel);
        final UpdateTopicConfidenceInputBoundary interactor =
                new UpdateTopicConfidenceInteractor(courseDataAccessObject, outputBoundary);
        final UpdateTopicConfidenceController controller =
                new UpdateTopicConfidenceController(interactor);
        courseProgressView.setUpdateTopicConfidenceController(controller);
        return this;
    }

    /**
     * Adds the Calculate Grade Use Case (use case 5) to the application.
     * The course progress page opens the pop-up with getGradeCalculatorDialog().open().
     * @return this builder
     */
    public AppBuilder addCalculateGradeUseCase() {
        final GradeCalculatorViewModel gradeCalculatorViewModel = new GradeCalculatorViewModel();
        gradeCalculatorDialog = new GradeCalculatorDialog(application, gradeCalculatorViewModel,
                courseProgressViewModel);
        courseProgressView.setOpenGradeCalculatorAction(() -> gradeCalculatorDialog.open());

        final CalculateGradeOutputBoundary calculateGradeOutputBoundary =
                new CalculateGradePresenter(gradeCalculatorViewModel, courseProgressViewModel);
        final CalculateGradeInputBoundary calculateGradeInteractor =
                new CalculateGradeInteractor(courseDataAccessObject, calculateGradeOutputBoundary);

        final CalculateGradeController calculateGradeController =
                new CalculateGradeController(calculateGradeInteractor);
        gradeCalculatorDialog.setCalculateGradeController(calculateGradeController);
        return this;
    }

    public CourseProgressViewModel getCourseProgressViewModel() {
        return courseProgressViewModel;
    }

    public GradeCalculatorDialog getGradeCalculatorDialog() {
        return gradeCalculatorDialog;
    }

    /**
     * Adds the Set Target Grade Use Case (use case 6) to the application.
     * The course progress page embeds the panel from getTargetGradePanel().
     * @return this builder
     */
    public AppBuilder addSetTargetGradeUseCase() {
        targetGradePanel = new TargetGradePanel(courseProgressViewModel);

        final SetTargetGradeOutputBoundary setTargetGradeOutputBoundary =
                new SetTargetGradePresenter(courseProgressViewModel);
        final SetTargetGradeInputBoundary setTargetGradeInteractor =
                new SetTargetGradeInteractor(courseDataAccessObject, setTargetGradeOutputBoundary);

        final SetTargetGradeController setTargetGradeController =
                new SetTargetGradeController(setTargetGradeInteractor);
        targetGradePanel.setSetTargetGradeController(setTargetGradeController);
        return this;
    }

    public TargetGradePanel getTargetGradePanel() {
        return targetGradePanel;
    }

    /**
     * Adds the Rank Subjects Use Case (use case 7) to the application.
     * The home page opens the pop-up with getSubjectRankingDialog().open(username).
     * @return this builder
     */
    public AppBuilder addRankSubjectsUseCase() {
        final SubjectRankingViewModel subjectRankingViewModel = new SubjectRankingViewModel();
        subjectRankingDialog = new SubjectRankingDialog(application, subjectRankingViewModel);

        final RankSubjectsOutputBoundary rankSubjectsOutputBoundary =
                new RankSubjectsPresenter(subjectRankingViewModel);
        final RankSubjectsInputBoundary rankSubjectsInteractor =
                new RankSubjectsInteractor(courseDataAccessObject, rankSubjectsOutputBoundary,
                        new StandardConfidenceScoring());

        final RankSubjectsController rankSubjectsController =
                new RankSubjectsController(rankSubjectsInteractor);
        subjectRankingDialog.setRankSubjectsController(rankSubjectsController);
        return this;
    }

    public SubjectRankingDialog getSubjectRankingDialog() {
        return subjectRankingDialog;
    }

    public JFrame build() {
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        viewManagerModel.setState(SignupViewModel.VIEW_NAME);
        viewManagerModel.notifyViewChanged();

        return application;
    }


}
